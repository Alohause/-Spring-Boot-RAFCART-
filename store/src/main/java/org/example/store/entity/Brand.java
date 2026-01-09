package org.example.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("brand")
public class Brand {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
}
