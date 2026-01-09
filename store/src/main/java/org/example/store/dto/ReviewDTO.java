package org.example.store.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long userId;
    private Integer rating;
    private String content;
}
