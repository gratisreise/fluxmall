package com.fluxmall.domain.vo;

import com.fluxmall.domain.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OrderVO {
    private Long id;
    private Long memberId;
    private String orderNumber;
    private int totalPrice;
    private OrderStatus status;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}