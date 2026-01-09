package org.example.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.store.dto.WishlistAddDTO;
import org.example.store.entity.Wishlist;
import org.example.store.mapper.WishlistMapper;
import org.example.store.service.IWishlistService;
import org.example.store.vo.WishlistVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WishlistServiceImpl implements IWishlistService {
    @Autowired
    private WishlistMapper wishlistMapper;

    @Override
    public List<WishlistVO> getMyWishlist(Long userId) {
        List<WishlistVO> list = wishlistMapper.selectWishlistDetails(userId);
        // 库存状态
        for (WishlistVO vo : list) {
            vo.setStockStatus(vo.getMinPrice() != null ? 1 : 0);
        }
        return list;
    }

    @Override
    public void addWishlist(Long userId, WishlistAddDTO dto) {
        // 查重
        Long count = wishlistMapper.selectCount(
                new LambdaQueryWrapper<Wishlist>()
                        .eq(Wishlist::getUserId, userId)
                        .eq(Wishlist::getProductId, dto.getProductId())
        );

        if (count == 0) {
            Wishlist w = new Wishlist();
            w.setUserId(userId);
            w.setProductId(dto.getProductId());
            w.setCreatedAt(LocalDateTime.now());
            wishlistMapper.insert(w);
        }
    }

    @Override
    public void removeWishlist(Long userId, Long productId) {
        wishlistMapper.delete(
                new LambdaQueryWrapper<Wishlist>()
                        .eq(Wishlist::getUserId, userId)
                        .eq(Wishlist::getProductId, productId)
        );
    }
}
