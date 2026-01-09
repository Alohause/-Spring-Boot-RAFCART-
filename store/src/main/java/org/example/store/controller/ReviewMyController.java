package org.example.store.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.mapper.ReviewMapper;
import org.example.store.vo.MyReviewVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ReviewMyController {

    private final ReviewMapper reviewMapper;

    public ReviewMyController(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    @GetMapping("/my")
    public Result<IPage<MyReviewVO>> my(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");

        Page<MyReviewVO> p = new Page<>(page, size);
        return Result.success(reviewMapper.selectMyReviews(p, uid));
    }
}
