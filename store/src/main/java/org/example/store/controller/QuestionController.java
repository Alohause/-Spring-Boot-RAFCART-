package org.example.store.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.dto.QuestionDTO;
import org.example.store.entity.ProductQuestion;
import org.example.store.mapper.ProductQuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class QuestionController {

    @Autowired
    private ProductQuestionMapper questionMapper;

    // 获取列表
    @GetMapping("/{productId}/questions")
    public Result<List<ProductQuestion>> list(@PathVariable Long productId) {
        List<ProductQuestion> list = questionMapper.selectList(
                new LambdaQueryWrapper<ProductQuestion>()
                        .eq(ProductQuestion::getProductId, productId)
                        .orderByDesc(ProductQuestion::getCreatedAt)
        );
        return Result.success(list);
    }

    // 提交提问
    @PostMapping("/{productId}/questions")
    public Result<Long> create(@PathVariable Long productId,
                               @Validated @RequestBody QuestionDTO dto) { // 建议加上 @Validated

        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            return Result.error("未登录或登录已过期");
        }

        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return Result.error("提问内容不能为空");
        }

        // 构建实体
        ProductQuestion q = new ProductQuestion();
        q.setUserId(currentUserId);
        q.setProductId(productId);
        q.setContent(dto.getContent().trim());
        q.setCreatedAt(LocalDateTime.now());

        questionMapper.insert(q);

        return Result.success(q.getId());
    }

}
