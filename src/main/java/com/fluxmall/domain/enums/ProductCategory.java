package com.fluxmall.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {
    ELECTRONICS("전자제품"),      // 스마트폰, 노트북, 가전 등
    FASHION("패션"),             // 의류, 신발, 가방, 액세서리
    BEAUTY("뷰티"),              // 화장품, 스킨케어, 향수
    FOOD("식품"),                // 가공식품, 신선식품, 건강식품
    BOOKS("도서"),               // 책, 만화, 잡지, 전자책
    SPORTS("스포츠/레저"),        // 운동복, 운동기구, 아웃도어 용품
    HOME("홈/인테리어"),          // 가구, 조명, 생활용품, 주방용품
    OTHERS("기타");              // 위 카테고리에 속하지 않는 상품

    private final String displayName;

}