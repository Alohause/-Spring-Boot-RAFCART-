package org.example.store.service;

import org.example.store.dto.CartAddDTO;
import org.example.store.dto.CartUpdateDTO;
import org.example.store.vo.CartVO;

public interface ICartService {
    CartVO getMyCart(Long userId);
    void addToCart(Long userId, CartAddDTO dto);
    void updateQuantity(Long userId, CartUpdateDTO dto);
    void removeCartItem(Long userId, Long itemId);
    Integer getCartCount(Long userId);
    void clearCart(Long userId);
}
