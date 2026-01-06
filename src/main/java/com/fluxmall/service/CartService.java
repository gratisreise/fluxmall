package com.fluxmall.service;

import com.fluxmall.dao.CartDao;
import com.fluxmall.dao.ProductDao;
import com.fluxmall.domain.vo.CartItemVO;
import com.fluxmall.domain.vo.CartVO;
import com.fluxmall.domain.vo.MemberVO;
import com.fluxmall.domain.vo.ProductVO;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartDao cartDao;
    private final ProductDao productDao;

    /**
     * 현재 로그인한 회원의 장바구니 조회
     * @return 장바구니 아이템 리스트 (상품 정보 포함)
     */
    public List<CartItemVO> getCartItems(HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return null;  // 로그인 안 된 상태
        }

        CartVO cart = cartDao.getOrCreateCart(loginMember.getId());
        return cartDao.findCartItemsByCartId(cart.getId());
    }

    /**
     * 장바구니에 상품 추가 또는 수량 증가
     * @param productId 추가할 상품 ID
     * @param quantity 추가할 수량 (기본 1)
     * @return 성공 여부
     */
    @Transactional
    public boolean addToCart(Long productId, int quantity, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return false;
        }

        // 상품 존재 및 판매 가능 여부 확인
        ProductVO product = productDao.findById(productId);
        if (product == null || product.getStatus() != com.fluxmall.domain.enums.ProductStatus.ON_SALE) {
            return false;
        }

        CartVO cart = cartDao.getOrCreateCart(loginMember.getId());

        // ON DUPLICATE KEY UPDATE로 수량 자동 증가
        cartDao.addOrUpdateCartItem(cart.getId(), productId, quantity);

        return true;
    }

    /**
     * 장바구니 아이템 수량 직접 수정
     * @param cartItemId 수정할 장바구니 아이템 ID
     * @param newQuantity 새로운 수량 (1 이상)
     * @return 성공 여부
     */
    @Transactional
    public boolean updateQuantity(Long cartItemId, int newQuantity, HttpSession session) {
        if (newQuantity < 1) {
            return deleteCartItem(cartItemId, session);  // 0 이하면 삭제
        }

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return false;
        }

        // 해당 아이템이 본인 장바구니에 있는지 확인 (보안)
        List<CartItemVO> cartItems = getCartItems(session);
        boolean isMine = cartItems.stream()
                .anyMatch(item -> item.getId().equals(cartItemId));

        if (!isMine) {
            return false;
        }

        // 직접 UPDATE 쿼리 필요 → CartDao에 추가 메서드
        return cartDao.updateCartItemQuantity(cartItemId, newQuantity) > 0;
    }

    /**
     * 장바구니에서 개별 아이템 삭제
     */
    @Transactional
    public boolean deleteCartItem(Long cartItemId, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return false;
        }

        List<CartItemVO> cartItems = getCartItems(session);
        boolean isMine = cartItems.stream()
                .anyMatch(item -> item.getId().equals(cartItemId));

        if (!isMine) {
            return false;
        }

        return cartDao.deleteCartItem(cartItemId) > 0;
    }

    /**
     * 선택된 여러 아이템 일괄 삭제
     * @param cartItemIds 삭제할 아이템 ID 리스트
     */
    @Transactional
    public boolean deleteSelectedItems(List<Long> cartItemIds, HttpSession session) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return false;
        }

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return false;
        }

        List<CartItemVO> cartItems = getCartItems(session);
        // 본인 소유 아이템만 필터링
        List<Long> validIds = cartItems.stream()
                .filter(item -> cartItemIds.contains(item.getId()))
                .map(CartItemVO::getId)
                .collect(Collectors.toList());

        if (validIds.isEmpty()) {
            return false;
        }

        // CartDao에 bulk delete 메서드 필요시 추가
        int deletedCount = 0;
        for (Long id : validIds) {
            deletedCount += cartDao.deleteCartItem(id);
        }

        return deletedCount == validIds.size();
    }

    /**
     * 장바구니 전체 비우기 (주문 완료 후 사용)
     */
    @Transactional
    public void clearCart(HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return;
        }

        CartVO cart = cartDao.getOrCreateCart(loginMember.getId());
        cartDao.clearCart(cart.getId());
    }

    /**
     * 장바구니 예상 총 결제 금액 계산
     */
    public int calculateTotalPrice(List<CartItemVO> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0;
        }

        return cartItems.stream()
                .mapToInt(item -> item.getProductPrice() * item.getQuantity())
                .sum();
    }
}