package com.fluxmall.domain.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CartVO {
    private Long id;
    private Long memberId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}