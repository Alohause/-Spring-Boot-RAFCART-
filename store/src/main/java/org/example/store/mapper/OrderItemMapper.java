package org.example.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.store.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
