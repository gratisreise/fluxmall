package com.fluxmall.domain.mapper;

import com.fluxmall.domain.enums.OrderStatus;
import com.fluxmall.domain.vo.OrderVO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class OrderRowMapper implements RowMapper<OrderVO> {
    @Override
    public OrderVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return OrderVO.builder()
                .id(rs.getLong("id"))
                .memberId(rs.getLong("member_id"))
                .orderNumber(rs.getString("order_number"))
                .totalPrice(rs.getInt("total_price"))
                .status(OrderStatus.valueOf(rs.getString("order_status")))
                .shippingAddress(rs.getString("shipping_address"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }
}