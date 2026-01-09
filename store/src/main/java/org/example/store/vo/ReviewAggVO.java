package org.example.store.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReviewAggVO {
    private Long productId;
    private Long reviewCount;
    private BigDecimal avgRating;
}
