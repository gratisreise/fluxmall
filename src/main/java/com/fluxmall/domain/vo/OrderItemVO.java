package com.fluxmall.domain.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OrderItemVO {
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private int price;               // 주문 시점 가격 스냅샷
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // JOIN용 추가 필드
    private String productName;
    private String productCategory;
    private int currentPrice;        // 현재 상품 가격 (비교용, 선택사항)
}