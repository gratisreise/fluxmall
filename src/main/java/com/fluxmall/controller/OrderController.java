package com.fluxmall.controller;

import com.fluxmall.dao.OrderDao;
import com.fluxmall.domain.vo.*;
import com.fluxmall.service.CartService;
import com.fluxmall.service.OrderService;
import com.fluxmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderDao orderDao;

    /**
     * 주문서 작성 페이지 - 장바구니 기반
     */
    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        List<CartItemVO> cartItems = cartService.getCartItems(session);
        if (cartItems == null || cartItems.isEmpty()) {
            model.addAttribute("error", "장바구니가 비어 있습니다.");
            return "redirect:/cart";
        }

        int totalPrice = cartService.calculateTotalPrice(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("member", loginMember);

        return "order/checkout";
    }

    /**
     * 바로구매 주문서 작성 페이지
     */
    @GetMapping("/direct")
    public String directCheckout(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session,
            Model model) {

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        ProductVO product = productService.getProductById(productId);
        if (product == null || product.getStockQuantity() < quantity) {
            model.addAttribute("error", "상품 재고가 부족하거나 판매 중이 아닙니다.");
            return "redirect:/products/" + productId;
        }

        // 임시 OrderItemVO 생성
        CartItemVO tempItem = CartItemVO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .quantity(quantity)
                .build();

        List<CartItemVO> tempCartItems = List.of(tempItem);
        int totalPrice = product.getPrice() * quantity;

        model.addAttribute("cartItems", tempCartItems);  // checkout.jsp 재사용 위해 같은 이름
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("member", loginMember);
        model.addAttribute("isDirect", true);

        return "order/checkout";
    }

    /**
     * 주문 처리 (장바구니 또는 바로구매)
     */
    @PostMapping("/create")
    public String createOrder(
            @RequestParam String shippingAddress,
            @RequestParam(required = false, defaultValue = "false") boolean isDirectBuy,
            HttpSession session,
            Model model) {

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        Long orderId;
        try {
            if (isDirectBuy) {
                model.addAttribute("error", "바로구매는 별도 처리 필요");
                return "order/checkout";
            } else {
                List<CartItemVO> cartItems = cartService.getCartItems(session);
                orderId = orderService.createOrder(shippingAddress, false, cartItems, null, session);
            }
        } catch (Exception e) {
            model.addAttribute("error", "주문 실패: " + e.getMessage());
            return "order/checkout";
        }

        if (orderId == null) {
            model.addAttribute("error", "주문 처리 중 오류가 발생했습니다.");
            return "order/checkout";
        }

        return "redirect:/order/success?orderId=" + orderId;
    }

    /**
     * 주문 완료 페이지
     */
    @GetMapping("/success")
    public String orderSuccess(@RequestParam Long orderId, Model model, HttpSession session) {
        OrderVO order = orderService.getOrderDetail(orderId, session);
        if (order == null) {
            return "redirect:/";
        }

        List<OrderItemVO> items = orderDao.findOrderItemsByOrderId(orderId);  // 필요시 주입
        model.addAttribute("order", order);
        model.addAttribute("items", items);

        return "order/success";
    }

    /**
     * 주문 내역 목록
     */
    @GetMapping("/list")
    public String orderList(
            @RequestParam(defaultValue = "1") int page,
            HttpSession session,
            Model model) {

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        int size = 10;
        List<OrderVO> orders = orderService.getOrdersByMember(loginMember.getId(), page, size);

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);

        return "order/list";
    }

    /**
     * 주문 상세 조회
     */
    @GetMapping("/{orderId}")
    public String orderDetail(@PathVariable Long orderId, HttpSession session, Model model) {
        OrderVO order = orderService.getOrderDetail(orderId, session);
        if (order == null) {
            model.addAttribute("error", "주문을 찾을 수 없거나 권한이 없습니다.");
            return "error/404";
        }

        List<OrderItemVO> items = orderDao.findOrderItemsByOrderId(orderId);
        model.addAttribute("order", order);
        model.addAttribute("items", items);

        return "order/detail";
    }
}