package org.example.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.store.dto.ProductQueryDTO;
import org.example.store.entity.Product;
import org.example.store.vo.ProductCardVO;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    List<ProductCardVO> selectProductList(ProductQueryDTO dto);

}
