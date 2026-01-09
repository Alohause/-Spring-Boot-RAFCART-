package org.example.store.dto;

import lombok.Data;

@Data
public class CreatePaymentRequest {
    private Long orderId;
    private String method;
}
