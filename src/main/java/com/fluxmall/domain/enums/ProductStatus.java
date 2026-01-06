package com.fluxmall.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductStatus {
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    DISCONTINUED("판매중지"),
    HIDDEN("숨김");

    private final String displayName;

}