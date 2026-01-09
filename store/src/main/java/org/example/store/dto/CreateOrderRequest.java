package org.example.store.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long addressId;
    private String remark;
}
