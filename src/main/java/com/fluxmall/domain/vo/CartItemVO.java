package com.fluxmall.domain.vo;

import com.fluxmall.domain.enums.ProductStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CartItemVO {
    private Long id;
    private Long cartId;
    private Long productId;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // JOIN해서 화면에 바로 쓰기 위한 추가 필드
    private String productName;
    private int productPrice;
    private String productCategory;  // 또는 ProductCategory Enum 써도 됨
    private ProductStatus productStatus;
}