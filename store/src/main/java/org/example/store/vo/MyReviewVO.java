package org.example.store.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyReviewVO {
    private Long id;
    private Long productId;
    private String productName;
    private String coverImage;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}
