package org.example.store.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
