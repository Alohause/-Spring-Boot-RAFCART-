package org.example.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "联系方式不能为空")
    private String phone;
    @NotBlank(message = "密码不能为空")
    private String password;
}
