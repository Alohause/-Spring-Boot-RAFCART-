package org.example.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.store.entity.Review;
import org.example.store.vo.MyReviewVO;
import org.example.store.vo.ReviewAggVO;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    List<ReviewAggVO> selectAggByProductIds(@Param("ids") String ids);
    IPage<MyReviewVO> selectMyReviews(Page<?> page, @Param("userId") Long userId);
}

