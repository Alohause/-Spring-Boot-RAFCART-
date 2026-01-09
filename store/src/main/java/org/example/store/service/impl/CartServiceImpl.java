package org.example.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.store.dto.CartAddDTO;
import org.example.store.dto.CartUpdateDTO;
import org.example.store.entity.Cart;
import org.example.store.entity.CartItem;
import org.example.store.mapper.CartItemMapper;
import org.example.store.mapper.CartMapper;
import org.example.store.service.ICartService;
import org.example.store.vo.CartItemVO;
import org.example.store.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartItemMapper cartItemMapper;

    // 获取详情
    @Override
    public CartVO getMyCart(Long userId) {
        Cart cart = getOrCreatCart(userId);
        List<CartItemVO> items = cartMapper.selectCartItems(cart.getId());

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItemVO item : items) {
            if (item.getPrice() == null)
                item.setPrice(BigDecimal.ZERO);

            // 小计
            BigDecimal sub = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            item.setSubTotal(sub);
            totalAmount = totalAmount.add(sub);
            totalQuantity += item.getQuantity();

        }

        CartVO cartVO = new CartVO();
        cartVO.setCartId(cart.getId());
        cartVO.setItems(items);
        cartVO.setTotalAmount(totalAmount);
        cartVO.setTotalQuantity(totalQuantity);
        return cartVO;
    }

    // 添加
    @Override
    @Transactional
    public void addToCart(Long userId, CartAddDTO dto) {
        Cart cart = getOrCreatCart(userId);

        CartItem exist = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
                .eq(CartItem::getSkuId, dto.getSkuId()));

        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + dto.getQuantity());
            exist.setUpdatedAt(LocalDateTime.now());
            cartItemMapper.updateById(exist);
        } else {
            CartItem item = new CartItem();
            item.setCartId(cart.getId());
            item.setSkuId(dto.getSkuId());
            item.setQuantity(dto.getQuantity());
            item.setCreatedAt(LocalDateTime.now());
            item.setUpdatedAt(LocalDateTime.now());
            cartItemMapper.insert(item);
        }
    }

    // 数量
    @Override
    public void updateQuantity(Long userId, CartUpdateDTO dto) {
        Cart cart = getOrCreatCart(userId);
        CartItem item = cartItemMapper.selectById(dto.getItemId());

        if (item != null && item.getCartId().equals(cart.getId())) {
            if (dto.getQuantity() <= 0) {
                cartItemMapper.deleteById(item);
            } else {
                item.setQuantity(dto.getQuantity());
                item.setUpdatedAt(LocalDateTime.now());
                cartItemMapper.updateById(item);
            }
        }
    }

    // 删除
    @Override
    public void removeCartItem(Long userId, Long itemId) {
        Cart cart = getOrCreatCart(userId);
        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, itemId)
                .eq(CartItem::getCartId, cart.getId())
        );
    }

    // 获取角标
    @Override
    public Integer getCartCount(Long userId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
        );
        if (cart == null) return 0;

        // 求和
        Long count = cartItemMapper.selectCount(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
        );
        return count.intValue();
    }

    // 获取购物车
    private Cart getOrCreatCart(Long userId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
        );
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setCreatedAt(LocalDateTime.now());
            cartMapper.insert(cart);
        }
        return cart;
    }

    // 清空购物车
    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreatCart(userId);

        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
        );
    }
}