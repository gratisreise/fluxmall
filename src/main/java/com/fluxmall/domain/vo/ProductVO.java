package com.fluxmall.domain.vo;


import com.fluxmall.domain.enums.ProductCategory;
import com.fluxmall.domain.enums.ProductStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductVO {
    private Long id;
    private Long memberId;          // 판매자 ID
    private String name;
    private String description;
    private ProductCategory category;
    private int price;
    private int stockQuantity;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}