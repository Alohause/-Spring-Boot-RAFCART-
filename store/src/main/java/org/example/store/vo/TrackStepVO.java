package org.example.store.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrackStepVO {
    private String title;
    private LocalDateTime time;
    private String status; // DONE / PENDING
}
