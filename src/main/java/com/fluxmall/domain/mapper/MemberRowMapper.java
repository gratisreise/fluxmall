package com.fluxmall.domain.mapper;

import com.fluxmall.domain.vo.MemberVO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;


public class MemberRowMapper implements RowMapper<MemberVO> {
    @Override
    public MemberVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MemberVO.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .nickname(rs.getString("nickname"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }
}