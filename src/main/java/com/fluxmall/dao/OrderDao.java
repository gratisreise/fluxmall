package com.fluxmall.dao;

import com.fluxmall.domain.enums.OrderStatus;
import com.fluxmall.domain.mapper.OrderItemRowMapper;
import com.fluxmall.domain.mapper.OrderRowMapper;
import com.fluxmall.domain.vo.OrderItemVO;
import com.fluxmall.domain.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 주문 생성 (헤더 + 아이템들)
     * @return 생성된 주문 ID
     */
    public Long createOrder(OrderVO order, List<OrderItemVO> orderItems) {
        // 1. 주문 헤더 INSERT (자동 생성된 ID 받아오기)
        String orderSql = "INSERT INTO orders " +
            "(member_id, order_number, total_price, order_status, shipping_address) " +
            "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getMemberId());
            ps.setString(2, order.getOrderNumber());
            ps.setInt(3, order.getTotalPrice());
            ps.setString(4, order.getStatus().name());
            ps.setString(5, order.getShippingAddress());
            return ps;
        }, keyHolder);

        Long orderId = keyHolder.getKey().longValue();

        // 2. 주문 아이템들 INSERT
        String itemSql = "INSERT INTO order_item " +
            "(order_id, product_id, quantity, price) " +
            "VALUES (?, ?, ?, ?)";

        for (OrderItemVO item : orderItems) {
            jdbcTemplate.update(itemSql,
                    orderId,
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPrice());
        }

        return orderId;
    }

    /**
     * 회원별 주문 목록 조회 (페이징)
     */
    public List<OrderVO> findOrdersByMemberId(Long memberId, int offset, int size) {
        String sql = "SELECT * FROM orders " +
            "WHERE member_id = ? " +
            "ORDER BY created_at DESC " +
            "LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, new OrderRowMapper(), memberId, size, offset);
    }

    /**
     * 주문 상세 조회 (헤더)
     */
    public OrderVO findOrderById(Long orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new OrderRowMapper(), orderId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 주문 아이템 목록 조회 (상품 정보 JOIN)
     */
    public List<OrderItemVO> findOrderItemsByOrderId(Long orderId) {
        String sql = "SELECT oi.*, p.name AS product_name, p.category AS product_category " +
            "FROM order_item oi " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE oi.order_id = ?";

        return jdbcTemplate.query(sql, new OrderItemRowMapper(), orderId);
    }

    /**
     * 주문 상태 업데이트 (예: PENDING → PAID)
     */
    public int updateOrderStatus(Long orderId, OrderStatus newStatus) {
        String sql = "UPDATE orders SET order_status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newStatus.name(), orderId);
    }
}