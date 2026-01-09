package org.example.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.store.entity.Orders;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
