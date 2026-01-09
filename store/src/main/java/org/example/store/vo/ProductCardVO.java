package org.example.store.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCardVO {
    private Long id;
    private String name;
    private String coverImage;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer reviewCount;       // 评价数
    private Double rating;             // 评分
}
