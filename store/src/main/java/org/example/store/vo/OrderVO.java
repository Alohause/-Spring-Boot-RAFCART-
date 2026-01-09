package org.example.store.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime updatedAt;
    private List<OrderItemVO> items;
}
