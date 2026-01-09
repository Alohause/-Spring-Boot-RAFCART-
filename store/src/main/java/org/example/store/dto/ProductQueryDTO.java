package org.example.store.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductQueryDTO {
    private Integer page = 1;
    private Integer size = 12;
    private String keyword; // 关键字
    private Long categoryId;
    private Long brandId;
    private String sort;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer offset;
}
