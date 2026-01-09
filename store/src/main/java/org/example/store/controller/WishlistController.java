package org.example.store.controller;

import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.dto.WishlistAddDTO;
import org.example.store.service.IWishlistService;
import org.example.store.vo.WishlistVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wishlist")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class WishlistController {
    @Autowired
    private IWishlistService wishlistService;

    @GetMapping
    public Result<List<WishlistVO>> getList() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.error("请先登录");
        return Result.success(wishlistService.getMyWishlist(userId));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody @Validated WishlistAddDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.error("请先登录");

        wishlistService.addWishlist(userId, dto);
        return Result.success("已收藏");
    }

    @DeleteMapping("/{productId}")
    public Result<String> remove(@PathVariable Long productId) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.error("请先登录");

        wishlistService.removeWishlist(userId, productId);
        return Result.success("已取消收藏");
    }
}
