package org.example.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.store.entity.Cart;
import org.example.store.vo.CartItemVO;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
    List<CartItemVO> selectCartItems(@Param("cartId") Long cartId);
}
