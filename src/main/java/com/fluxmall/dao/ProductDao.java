package com.fluxmall.dao;

import com.fluxmall.domain.enums.ProductCategory;
import com.fluxmall.domain.enums.ProductStatus;
import com.fluxmall.domain.mapper.ProductRowMapper;
import com.fluxmall.domain.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductDao {

    private final JdbcTemplate jdbcTemplate;

    // 상품 등록
    public int insertProduct(ProductVO product) {
        String sql = "INSERT INTO products " +
            "(member_id, name, description, category, price, stock_quantity, product_status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
            product.getMemberId(),
            product.getName(),
            product.getDescription(),
            product.getCategory().name(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getStatus().name());
    }

    // 상품 상세 조회
    public ProductVO findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ProductRowMapper(), id);
        } catch (Exception e) {
            return null;
        }
    }

    // 상품 목록 조회 (페이징 + 카테고리 필터)
    public List<ProductVO> findAll(int offset, int size, ProductCategory category) {
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE product_status = 'ON_SALE'");

        if (category != null) {
            sql.append(" AND category = ?");
        }
        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");

        Object[] params = category == null ?
            new Object[]{size, offset} :
            new Object[]{category.name(), size, offset};

        return jdbcTemplate.query(sql.toString(), new ProductRowMapper(), params);
    }

    // 재고 업데이트 (주문 시 차감용)
    public int updateStock(Long productId, int quantityToSubtract) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";
        return jdbcTemplate.update(sql, quantityToSubtract, productId, quantityToSubtract);
    }

    public List<ProductVO> searchByKeyword(String keyword, int offset, int size) {
        String sql = "SELECT * FROM products " +
            "WHERE product_status = 'ON_SALE' " +
            "AND (name LIKE CONCAT('%', ?, '%') OR description LIKE CONCAT('%', ?, '%')) " +
            "ORDER BY id DESC LIMIT ? OFFSET ?";

        String likeKeyword = keyword;
        return jdbcTemplate.query(sql, new ProductRowMapper(), likeKeyword, likeKeyword, size, offset);
    }

}