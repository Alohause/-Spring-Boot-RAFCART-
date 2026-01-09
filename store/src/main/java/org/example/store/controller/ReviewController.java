package org.example.store.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.entity.Review;
import org.example.store.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ReviewController {

    @Autowired
    private ReviewMapper reviewMapper;

    @GetMapping("/{productId}/reviews")
    public Result<IPage<Review>> listByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "false") Boolean badOnly,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        LambdaQueryWrapper<Review> qw = new LambdaQueryWrapper<>();
        qw.eq(Review::getProductId, productId);

        // 差评筛选：<=3 星
        if (Boolean.TRUE.equals(badOnly)) {
            qw.le(Review::getRating, 3);
        }

        // 排序
        if ("oldest".equalsIgnoreCase(sort)) {
            qw.orderByAsc(Review::getCreatedAt);
        } else {
            qw.orderByDesc(Review::getCreatedAt).orderByDesc(Review::getId);
        }

        IPage<Review> res = reviewMapper.selectPage(new Page<>(page, size), qw);
        return Result.success(res);
    }

    // 提交
    @PostMapping("/{productId}/reviews")
    public Result<String> create(@RequestBody Map<String, Object> body) {
        // 获取当前登录用户 ID
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }

        Long productId = body.get("productId") == null ? null : Long.valueOf(String.valueOf(body.get("productId")));
        Integer rating = body.get("rating") == null ? null : Integer.valueOf(String.valueOf(body.get("rating")));
        String content = body.get("content") == null ? null : String.valueOf(body.get("content")).trim();

        if (productId == null || rating == null || rating < 1 || rating > 5) {
            return Result.error("参数错误：评分必须在 1~5 之间");
        }

        Review r = new Review();
        r.setProductId(productId);
        r.setRating(rating);
        r.setContent(content);

        r.setUserId(currentUserId);
        r.setCreatedAt(LocalDateTime.now());

        reviewMapper.insert(r);
        return Result.success("评论成功");
    }
}
