package org.example.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WishlistAddDTO {
    @NotNull(message = "productId 不能为空")
    private Long productId;
}
