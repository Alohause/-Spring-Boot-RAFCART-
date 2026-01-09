package org.example.store.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {
    private Long id;
    private Long skuId;
    private Long productId;
    private String productName;
    private String skuCode;
    private String size;
    private String color;
    private String coverImage;// 封面图
    private BigDecimal price; // 当前价格
    private Integer quantity;
    private Integer stock;    // 库存
    private BigDecimal subTotal;
}
