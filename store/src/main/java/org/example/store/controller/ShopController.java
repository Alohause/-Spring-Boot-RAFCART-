package org.example.store.controller;

import jakarta.validation.Valid;
import org.example.store.dto.CartAddDTO;
import org.example.store.dto.CartUpdateDTO;
import org.example.store.dto.WishlistAddDTO;
import org.example.store.service.impl.CartServiceImpl;
import org.example.store.service.IWishlistService;
import org.example.store.vo.CartVO;
import org.example.store.vo.WishlistVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShopController {
    @Autowired
    private CartServiceImpl cartService;
    @Autowired private IWishlistService wishlistService;

    private Long getCurrentUserId() {
        return 1L;
    }

    // 购物车接口
    @GetMapping("/cart")
    public CartVO getCart() {
        return cartService.getMyCart(getCurrentUserId());
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestBody @Valid CartAddDTO dto) {
        cartService.addToCart(getCurrentUserId(), dto);
        return "success";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestBody @Valid CartUpdateDTO dto) {
        cartService.updateQuantity(getCurrentUserId(), dto);
        return "success";
    }

    @DeleteMapping("/cart/remove/{itemId}")
    public String removeCartItem(@PathVariable Long itemId) {
        cartService.removeCartItem(getCurrentUserId(), itemId);
        return "success";
    }

    // 收藏夹接口
    @GetMapping("/wishlist")
    public List<WishlistVO> getWishlist() {
        return wishlistService.getMyWishlist(getCurrentUserId());
    }

    @PostMapping("/wishlist/add")
    public String addWishlist(@RequestBody @Valid WishlistAddDTO dto) {
        wishlistService.addWishlist(getCurrentUserId(), dto);
        return "success";
    }

    @DeleteMapping("/wishlist/remove/{productId}")
    public String removeWishlist(@PathVariable Long productId) {
        wishlistService.removeWishlist(getCurrentUserId(), productId);
        return "success";
    }
}
