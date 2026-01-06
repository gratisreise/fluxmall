package com.fluxmall.controller;

import com.fluxmall.domain.enums.ProductCategory;
import com.fluxmall.domain.vo.MemberVO;
import com.fluxmall.domain.vo.ProductVO;
import com.fluxmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 메인 상품 목록 (카테고리 필터 + 페이징)
     */
    @GetMapping
    public String productList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) ProductCategory category,
            Model model) {

        int size = 12;  // 한 페이지당 12개 상품 (조정 가능)
        List<ProductVO> products = productService.getProductList(page, size, category);

        // 전체 상품 수 (페이징 계산용)
        int totalCount = productService.getTotalProductCount(category);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("products", products);
        model.addAttribute("category", category);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("categories", ProductCategory.values());  // JSP에서 카테고리 선택용

        return "product/list";  // /WEB-INF/views/product/list.jsp
    }

    /**
     * 상품 검색
     */
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        int size = 12;
        List<ProductVO> products = productService.searchProducts(keyword, page, size);

        int totalCount = products.size();  // 정확한 count는 별도 메서드 필요시 추가
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "product/search";  // 또는 list.jsp 재사용 가능
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        ProductVO product = productService.getProductById(id);
        if (product == null) {
            // 상품 없거나 판매중이 아닐 때
            model.addAttribute("error", "상품을 찾을 수 없습니다.");
            return "error/404";  // 나중에 에러 페이지 만들기
        }

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        model.addAttribute("product", product);
        model.addAttribute("loginMember", loginMember);  // 본인 상품인지 확인용 (수정/삭제 나중에)

        return "product/detail";  // /WEB-INF/views/product/detail.jsp
    }

    /**
     * 상품 등록 폼 (로그인한 회원만 접근)
     */
    @GetMapping("/register")
    public String registerForm(HttpSession session, Model model) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("categories", ProductCategory.values());
        return "product/register";  // 등록 폼 JSP
    }

    /**
     * 상품 등록 처리
     */
    @PostMapping("/register")
    public String registerProduct(ProductVO product, HttpSession session, Model model) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        boolean success = productService.registerProduct(product, session);
        if (success) {
            return "redirect:/products/" + product.getId();  // 등록 후 상세 페이지로
        } else {
            model.addAttribute("error", "상품 등록 실패");
            model.addAttribute("categories", ProductCategory.values());
            return "product/register";
        }
    }
}