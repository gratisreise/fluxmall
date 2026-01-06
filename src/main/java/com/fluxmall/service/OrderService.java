package com.fluxmall.service;

import com.fluxmall.dao.CartDao;
import com.fluxmall.dao.OrderDao;
import com.fluxmall.dao.ProductDao;
import com.fluxmall.domain.dto.OrderItemRequest;
import com.fluxmall.domain.enums.OrderStatus;
import com.fluxmall.domain.enums.ProductStatus;
import com.fluxmall.domain.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDao orderDao;
    private final CartDao cartDao;
    private final ProductDao productDao;

    /**
     * 주문 생성 (장바구니 전체 주문 또는 바로구매)
     * @param shippingAddress 배송지
     * @param isDirectBuy 바로구매 여부 (true면 cartItems 무시하고 directItems 사용)
     * @param cartItems 장바구니 아이템들 (바로구매 아니면 사용)
     * @param directItems 바로구매 아이템들 (상품ID + 수량)
     * @param session 로그인 세션
     * @return 생성된 주문 ID (실패 시 null)
     */
    @Transactional
    public Long createOrder(String shippingAddress,
                            boolean isDirectBuy,
                            List<CartItemVO> cartItems,
                            List<OrderItemRequest> directItems,
                            HttpSession session) {

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return null;  // 로그인 안 됨
        }

        // 1. 주문 아이템 목록 준비
        List<OrderItemVO> orderItems = new ArrayList<>();

        if (isDirectBuy) {
            // 바로구매 로직
            for (OrderItemRequest item : directItems) {
                ProductVO product = productDao.findById(item.getProductId());
                if (product == null || product.getStatus() != ProductStatus.ON_SALE || product.getStockQuantity() < item.getQuantity()) {
                    throw new RuntimeException("상품 재고 부족 또는 판매 불가: " + item.getProductId());
                }

                OrderItemVO orderItem = new OrderItemVO();
                orderItem.setProductId(item.getProductId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(product.getPrice());  // 주문 시점 가격 스냅샷
                orderItems.add(orderItem);
            }
        } else {
            // 장바구니 기반 주문
            if (cartItems == null || cartItems.isEmpty()) {
                return null;
            }

            for (CartItemVO cartItem : cartItems) {
                ProductVO product = productDao.findById(cartItem.getProductId());
                if (product == null || product.getStockQuantity() < cartItem.getQuantity()) {
                    throw new RuntimeException("재고 부족: " + cartItem.getProductName());
                }

                OrderItemVO orderItem = new OrderItemVO();
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getProductPrice());  // 장바구니에 저장된 가격 사용
                orderItems.add(orderItem);
            }
        }

        // 2. 총 금액 계산
        int totalPrice = orderItems.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();

        // 3. 주문번호 생성 (고유 + 가독성 좋게)
        String orderNumber = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                             "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 4. 주문 헤더 생성
        OrderVO order = OrderVO.builder()
                .memberId(loginMember.getId())
                .orderNumber(orderNumber)
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)  // 초기 상태: 주문접수
                .shippingAddress(shippingAddress)
                .build();

        // 5. 재고 차감 시도
        for (OrderItemVO item : orderItems) {
            int updated = productDao.updateStock(item.getProductId(), item.getQuantity());
            if (updated == 0) {
                throw new RuntimeException("재고 차감 실패 (동시성 문제): " + item.getProductId());
            }
        }

        // 6. 주문 저장 (성공 시 커밋)
        Long orderId = orderDao.createOrder(order, orderItems);

        // 7. 장바구니 비우기 (장바구니 주문인 경우에만)
        if (!isDirectBuy) {
            CartVO cart = cartDao.getOrCreateCart(loginMember.getId());
            cartDao.clearCart(cart.getId());
        }

        return orderId;
    }

    /**
     * 회원별 주문 목록 조회 (페이징)
     */
    public List<OrderVO> getOrdersByMember(Long memberId, int page, int size) {
        int offset = (page - 1) * size;
        return orderDao.findOrdersByMemberId(memberId, offset, size);
    }

    /**
     * 주문 상세 조회
     */
    public OrderVO getOrderDetail(Long orderId, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return null;
        }

        OrderVO order = orderDao.findOrderById(orderId);
        if (order == null || !order.getMemberId().equals(loginMember.getId())) {
            return null;  // 본인 주문만 조회 가능
        }

        List<OrderItemVO> items = orderDao.findOrderItemsByOrderId(orderId);
        // items를 order에 담거나 별도로 반환 (컨트롤러에서 처리)
        // 여기서는 order에 items 리스트 추가하는 방식은 VO에 필드 없으니 컨트롤러에서 별도 처리 추천

        return order;
    }

    /**
     * 주문 상태 변경 (예: 결제 완료 처리)
     */
    @Transactional
    public boolean updateOrderStatus(Long orderId, OrderStatus newStatus, HttpSession session) {
        OrderVO order = orderDao.findOrderById(orderId);
        if (order == null) return false;

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null || !order.getMemberId().equals(loginMember.getId())) {
            return false;  // 본인 주문만
        }

        // 간단한 상태 전이 검증 (실무에선 더 엄격하게)
        if (order.getStatus() == OrderStatus.PENDING && newStatus == OrderStatus.PAID) {
            return orderDao.updateOrderStatus(orderId, newStatus) > 0;
        }

        return false;
    }
}

