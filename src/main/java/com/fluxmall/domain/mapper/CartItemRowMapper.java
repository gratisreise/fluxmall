package com.fluxmall.domain.mapper;

import com.fluxmall.domain.enums.ProductStatus;
import com.fluxmall.domain.vo.CartItemVO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CartItemRowMapper implements RowMapper<CartItemVO> {
        @Override
        public CartItemVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return CartItemVO.builder()
                    .id(rs.getLong("id"))
                    .cartId(rs.getLong("cart_id"))
                    .productId(rs.getLong("product_id"))
                    .quantity(rs.getInt("quantity"))
                    .productName(rs.getString("product_name"))
                    .productPrice(rs.getInt("product_price"))
                    .productCategory(rs.getString("product_category"))
                    .productStatus(ProductStatus.valueOf(rs.getString("product_status")))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                    .build();
        }
    }