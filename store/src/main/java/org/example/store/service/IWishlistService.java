package org.example.store.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.store.dto.WishlistAddDTO;
import org.example.store.entity.Wishlist;
import org.example.store.mapper.WishlistMapper;
import org.example.store.vo.WishlistVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IWishlistService {
    List<WishlistVO> getMyWishlist(Long userId);
    void addWishlist(Long userId, WishlistAddDTO dto);
    void removeWishlist(Long userId, Long productId);
}
