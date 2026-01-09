package org.example.store.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String method;
    private String status;
    private BigDecimal amount;
    private String tradeNo;
    private LocalDateTime paidAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
