package org.example.store.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderResponse {
    private Long orderId;
    private String orderNo;
    private BigDecimal payAmount;
    private String status;
}
