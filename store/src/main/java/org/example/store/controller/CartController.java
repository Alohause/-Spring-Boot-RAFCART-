package org.example.store.controller;

import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.dto.CartAddDTO;
import org.example.store.dto.CartUpdateDTO;
import org.example.store.service.ICartService;
import org.example.store.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cart")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CartController {
    @Autowired
    private ICartService cartService;

    // 获取购物车
    @GetMapping
    public Result<CartVO> getCart() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.error("请先登录");
        return Result.success(cartService.getMyCart(userId));
    }

    // 获取角标
    @GetMapping("/count")
    public Result<Integer> getCartCount() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.success(0);
        return Result.success(cartService.getCartCount(userId));
    }

    // 添加商品
    @PostMapping("/add")
    public Result<String> add(@RequestBody @Validated CartAddDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.error("请先登录");

        cartService.addToCart(userId, dto);
        return Result.success("已加入购物车");
    }

    // 更新数量
    @PostMapping("/update")
    public Result<String> update(@RequestBody @Validated CartUpdateDTO  dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.error("请先登录");
        cartService.updateQuantity(userId, dto);
        return Result.success("更新成功");
    }

    // 删除
    @DeleteMapping("/{itemId}")
    public Result<String> remove(@PathVariable Long itemId) {
        Long userId = UserContext.getUserId();
        if (userId == null) return Result.error("请先登录");

        cartService.removeCartItem(userId, itemId);
        return Result.success("删除成功");
    }

}
