package org.example.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.store.entity.Brand;

@Mapper
public interface BrandMapper extends BaseMapper<Brand> {
}
