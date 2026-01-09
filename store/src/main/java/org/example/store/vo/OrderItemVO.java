package org.example.store.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Long id;
    private Long skuId;
    private String productName;
    private String skuSpec;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}