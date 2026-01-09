package org.example.store.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.entity.Review;
import org.example.store.mapper.ReviewMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/reviews")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class UserReviewController {

    private final ReviewMapper reviewMapper;

    public UserReviewController(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    @GetMapping
    public Result<IPage<Review>> myReviews(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(defaultValue = "latest") String sort) {
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");

        LambdaQueryWrapper<Review> qw = new LambdaQueryWrapper<>();
        qw.eq(Review::getUserId, uid);

        if ("oldest".equalsIgnoreCase(sort)) {
            qw.orderByAsc(Review::getCreatedAt).orderByAsc(Review::getId);
        } else {
            qw.orderByDesc(Review::getCreatedAt).orderByDesc(Review::getId);
        }

        return Result.success(reviewMapper.selectPage(new Page<>(page, size), qw));
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");

        Review r = reviewMapper.selectById(id);
        if (r == null || !uid.equals(r.getUserId())) return Result.error("评论不存在或无权限操作");

        reviewMapper.deleteById(id);
        return Result.success("已删除");
    }
}