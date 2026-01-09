package org.example.store.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class BrandCountVO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private Integer count;
}
