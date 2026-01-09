package org.example.store.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wishlist")
public class Wishlist {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long productId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
