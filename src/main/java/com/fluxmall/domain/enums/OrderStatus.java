package com.fluxmall.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("주문접수"),
    PAID("결제완료"),
    PREPARING("상품준비중"),
    SHIPPED("배송중"),
    DELIVERED("배송완료"),
    CANCELLED("주문취소"),
    REFUNDED("환불완료");

    private final String displayName;

    public boolean isCompleted() {
        return this == DELIVERED || this == REFUNDED;
    }

    public boolean isCancelled() {
        return this == CANCELLED || this == REFUNDED;
    }
}