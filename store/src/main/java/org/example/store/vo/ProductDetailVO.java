package org.example.store.vo;

import lombok.Data;
import org.example.store.entity.ProductSku;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailVO {
    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private Integer reviewCount;
    private Double rating;

    private List<ProductSku> skus;
}
