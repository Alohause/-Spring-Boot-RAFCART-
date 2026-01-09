package org.example.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.store.entity.UserAddress;

@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
