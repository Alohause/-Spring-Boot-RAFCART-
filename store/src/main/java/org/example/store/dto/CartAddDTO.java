package org.example.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class CartAddDTO {
    @NotNull(message = "skuId 不能为空")
    private Long skuId;
    @Min(value = 1, message = "商品数量不能小于 1")
    private Integer quantity;

}
