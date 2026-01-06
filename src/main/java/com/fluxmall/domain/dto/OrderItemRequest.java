package com.fluxmall.domain.dto;

import lombok.Data;

@Data
class OrderItemRequest {
    private Long productId;
    private int quantity;
}