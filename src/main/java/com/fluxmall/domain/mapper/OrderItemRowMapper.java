package com.fluxmall.domain.mapper;

import com.fluxmall.domain.vo.OrderItemVO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class OrderItemRowMapper implements RowMapper<OrderItemVO> {
    @Override
    public OrderItemVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return OrderItemVO.builder()
                .id(rs.getLong("id"))
                .orderId(rs.getLong("order_id"))
                .productId(rs.getLong("product_id"))
                .quantity(rs.getInt("quantity"))
                .price(rs.getInt("price"))
                .productName(rs.getString("product_name"))
                .productCategory(rs.getString("product_category"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }
}