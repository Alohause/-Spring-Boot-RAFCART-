package org.example.store.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_address")
public class UserAddress {
    private Long id;
    private Long userId;
    private String receiverName;
    private String phone;
    private String region;
    private String detail;
    private Integer isDefault;
    private Integer isDeleted;
    private java.util.Date createdAt;
    private java.util.Date updatedAt;
}
