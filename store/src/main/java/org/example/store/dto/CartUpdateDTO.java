package org.example.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartUpdateDTO {
    @NotNull(message = "购物车项ID不能为空")
    private Long itemId;
    @NotNull(message = "数量不能为空")
    private Integer quantity;
}
