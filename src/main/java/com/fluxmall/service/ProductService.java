package com.fluxmall.service;


import com.fluxmall.dao.ProductDao;
import com.fluxmall.domain.enums.ProductCategory;
import com.fluxmall.domain.enums.ProductStatus;
import com.fluxmall.domain.vo.MemberVO;
import com.fluxmall.domain.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductDao productDao;

    /**
     * 상품 등록
     * @param product 등록할 상품 정보 (name, description, category, price, stockQuantity)
     * @param session 로그인 세션
     * @return 등록 성공 여부
     */
    @Transactional
    public boolean registerProduct(ProductVO product, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return false;  // 로그인 안 하면 등록 불가
        }

        // 판매자 ID 자동 주입
        product.setMemberId(loginMember.getId());
        // 기본 상태: 판매중
        product.setStatus(ProductStatus.ON_SALE);

        return productDao.insertProduct(product) > 0;
    }

    /**
     * 상품 상세 조회
     * 존재검증 먼저 그 다음에 반환하는 형식으로
     */
    public ProductVO getProductById(Long id) {
        ProductVO product = productDao.findById(id);
        if (product != null && product.getStatus() == ProductStatus.ON_SALE) {
            return product;
        }
        return null;
    }

    /**
     * 상품 목록 조회 - 페이징 + 카테고리 필터
     * @param page 현재 페이지 (1부터 시작)
     * @param size 한 페이지당 상품 수
     * @param category 필터링할 카테고리 (null이면 전체)
     * @return 상품 리스트
     */
    public List<ProductVO> getProductList(int page, int size, ProductCategory category) {
        int offset = (page - 1) * size;
        return productDao.findAll(offset, size, category);
    }

    /**
     * 상품 키워드 검색 (상품명 OR 설명)
     * 페이징 형태 추상화로 분리 필요
     */
    public List<ProductVO> searchProducts(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getProductList(page, size, null);  // 키워드 없으면 전체 목록
        }

        // ProductDao에 검색 메서드 추가 필요 → 아래에 추가 제안
        int offset = (page - 1) * size;
        return productDao.searchByKeyword(keyword.trim(), offset, size);
    }

    /**
     * 전체 상품 수 조회 (페이징용)
     */
    public int getTotalProductCount(ProductCategory category) {
        return productDao.findAll(0, Integer.MAX_VALUE, category).size();
    }

    /**
     * 재고 차감 (주문 확정 시 OrderService에서 호출)
     * @return 차감 성공 여부 (재고 부족 시 false)
     */
    @Transactional
    public boolean decreaseStock(Long productId, int quantity) {
        if (quantity <= 0) return false;

        int updatedRows = productDao.updateStock(productId, quantity);
        if (updatedRows > 0) {
            ProductVO product = productDao.findById(productId);
            if (product != null && product.getStockQuantity() <= 0) {
                product.setStatus(ProductStatus.SOLD_OUT);
            }
            return true;
        }
        return false;
    }
}