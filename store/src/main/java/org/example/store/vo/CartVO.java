package org.example.store.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVO {
    private Long cartId;
    private List<CartItemVO> items;
    private BigDecimal totalAmount;
    private Integer totalQuantity;
}
