package org.example.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.store.entity.Wishlist;
import org.example.store.vo.WishlistVO;

import java.util.List;

@Mapper
public interface WishlistMapper extends BaseMapper<Wishlist> {
    List<WishlistVO> selectWishlistDetails(@Param("userId") Long userId);
}
