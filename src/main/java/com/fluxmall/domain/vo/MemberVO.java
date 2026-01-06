package com.fluxmall.domain.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MemberVO {
    private Long id;
    private String username;     // 로그인 아이디
    private String password;     // 해시된 값 저장 (회원가입/로그인 시만 사용)
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}