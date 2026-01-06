package com.fluxmall.controller;

import com.fluxmall.domain.vo.CartItemVO;
import com.fluxmall.domain.vo.MemberVO;
import com.fluxmall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 조회 페이지
     */
    @GetMapping
    public String cartView(HttpSession session, Model model) {
        List<CartItemVO> cartItems = cartService.getCartItems(session);
        
        if (cartItems == null) {
            return "redirect:/member/login";  // 로그인 안 됐으면 로그인 페이지로
        }

        int totalPrice = cartService.calculateTotalPrice(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "cart/view";  // /WEB-INF/views/cart/view.jsp
    }

    /**
     * 장바구니에 상품 추가 (AJAX)
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session) {

        boolean success = cartService.addToCart(productId, quantity, session);

        if (success) {
            // 추가 후 현재 장바구니 총 아이템 수 반환 (헤더 장바구니 아이콘용)
            List<CartItemVO> updatedCart = cartService.getCartItems(session);
            int cartItemCount = updatedCart != null ? updatedCart.size() : 0;

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "장바구니에 담겼습니다.",
                    "cartItemCount", cartItemCount
            ));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "상품을 담을 수 없습니다. (판매중지 또는 로그인 필요)"));
        }
    }

    /**
     * 장바구니 수량 수정 (AJAX)
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateQuantity(
            @RequestParam Long cartItemId,
            @RequestParam int quantity,
            HttpSession session) {

        boolean success = cartService.updateQuantity(cartItemId, quantity, session);

        if (success) {
            // 수정 후 총 금액 재계산
            List<CartItemVO> cartItems = cartService.getCartItems(session);
            int totalPrice = cartService.calculateTotalPrice(cartItems);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "totalPrice", totalPrice
            ));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "수량 수정 실패"));
        }
    }

    /**
     * 개별 아이템 삭제 (AJAX)
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteItem(
            @RequestParam Long cartItemId,
            HttpSession session) {

        boolean success = cartService.deleteCartItem(cartItemId, session);

        if (success) {
            List<CartItemVO> cartItems = cartService.getCartItems(session);
            int totalPrice = cartService.calculateTotalPrice(cartItems);
            int cartItemCount = cartItems != null ? cartItems.size() : 0;

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "totalPrice", totalPrice,
                    "cartItemCount", cartItemCount
            ));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "삭제 실패"));
        }
    }

    /**
     * 선택 아이템 일괄 삭제 (AJAX)
     */
    @PostMapping("/deleteSelected")
    @ResponseBody
    public ResponseEntity<?> deleteSelected(
            @RequestBody List<Long> cartItemIds,
            HttpSession session) {

        boolean success = cartService.deleteSelectedItems(cartItemIds, session);

        if (success) {
            List<CartItemVO> cartItems = cartService.getCartItems(session);
            int totalPrice = cartService.calculateTotalPrice(cartItems);
            int cartItemCount = cartItems != null ? cartItems.size() : 0;

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "totalPrice", totalPrice,
                    "cartItemCount", cartItemCount
            ));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "선택 삭제 실패"));
        }
    }
}