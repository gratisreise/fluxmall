package com.fluxmall.domain.mapper;

import com.fluxmall.domain.enums.ProductCategory;
import com.fluxmall.domain.enums.ProductStatus;
import com.fluxmall.domain.vo.ProductVO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ProductRowMapper implements RowMapper<ProductVO> {
    @Override
    public ProductVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProductVO.builder()
                .id(rs.getLong("id"))
                .memberId(rs.getLong("member_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .category(ProductCategory.valueOf(rs.getString("category")))
                .price(rs.getInt("price"))
                .stockQuantity(rs.getInt("stock_quantity"))
                .status(ProductStatus.valueOf(rs.getString("product_status")))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }
}