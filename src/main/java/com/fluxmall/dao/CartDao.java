package com.fluxmall.dao;

import com.fluxmall.domain.enums.ProductStatus;
import com.fluxmall.domain.mapper.CartItemRowMapper;
import com.fluxmall.domain.mapper.CartRowMapper;
import com.fluxmall.domain.vo.CartItemVO;
import com.fluxmall.domain.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartDao {

    private final JdbcTemplate jdbcTemplate;

    // 회원의 장바구니 조회 or 생성 (1:1 관계)
    public CartVO getOrCreateCart(Long memberId) {
        String sql = "SELECT * FROM carts WHERE member_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new CartRowMapper(), memberId);
        } catch (Exception e) {
            // 없으면 새로 생성
            String insertSql = "INSERT INTO carts (member_id) VALUES (?)";
            jdbcTemplate.update(insertSql, memberId);
            return getOrCreateCart(memberId); // 재귀 호출
        }
    }

    // 장바구니 아이템 추가/업데이트 (이미 있으면 수량 증가)
    public void addOrUpdateCartItem(Long cartId, Long productId, int quantity) {
        String sql = "INSERT INTO cart_item (cart_id, product_id, quantity) " +
            "VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE quantity = quantity + ?";

        jdbcTemplate.update(sql, cartId, productId, quantity, quantity);
    }

    // 장바구니 아이템 목록 조회 (상품 정보 JOIN)
    public List<CartItemVO> findCartItemsByCartId(Long cartId) {
        String sql = "SELECT ci.*, p.name AS product_name, p.price AS product_price, " +
            "       p.category AS product_category, p.product_status " +
            "FROM cart_item ci " +
            "JOIN products p ON ci.product_id = p.id " +
            "WHERE ci.cart_id = ?";

        return jdbcTemplate.query(sql, new CartItemRowMapper(), cartId);
    }

    // 장바구니 아이템 삭제
    public int deleteCartItem(Long cartItemId) {
        String sql = "DELETE FROM cart_item WHERE id = ?";
        return jdbcTemplate.update(sql, cartItemId);
    }

    // 장바구니 비우기
    public int clearCart(Long cartId) {
        String sql = "DELETE FROM cart_item WHERE cart_id = ?";
        return jdbcTemplate.update(sql, cartId);
    }

    // CartDao.java에 추가
    public int updateCartItemQuantity(Long cartItemId, int newQuantity) {
        String sql = "UPDATE cart_item SET quantity = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newQuantity, cartItemId);
    }


}