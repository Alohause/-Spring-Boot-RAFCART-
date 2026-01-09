package org.example.store.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentResponse {
    private Long paymentId;
    private Long orderId;
    private String method;
    private String status;
    private BigDecimal amount;
    private String tradeNo;
}
