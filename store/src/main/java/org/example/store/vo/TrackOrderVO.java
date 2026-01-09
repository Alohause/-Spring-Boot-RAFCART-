package org.example.store.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrackOrderVO {
    private String orderNo;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime updatedAt;
    private List<TrackStepVO> steps;
}