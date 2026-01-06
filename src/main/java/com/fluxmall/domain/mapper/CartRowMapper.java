package com.fluxmall.domain.mapper;

import com.fluxmall.domain.vo.CartVO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CartRowMapper implements RowMapper<CartVO> {
        @Override
        public CartVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return CartVO.builder()
                    .id(rs.getLong("id"))
                    .memberId(rs.getLong("member_id"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                    .build();
        }
    }