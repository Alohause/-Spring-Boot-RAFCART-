package org.example.store.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WishlistVO {
    private Long id;
    private Long productId;
    private String productName;
    private String coverImage;
    private BigDecimal minPrice;
    private Integer stockStatus; // 0:缺货, 1:有货
    private LocalDateTime createdAt;
}
