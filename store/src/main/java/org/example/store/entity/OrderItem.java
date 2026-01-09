package org.example.store.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long skuId;
    private String productName;
    private String skuSpec;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
