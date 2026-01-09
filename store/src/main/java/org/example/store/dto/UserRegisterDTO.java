package org.example.store.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email; // 选填

    @NotBlank(message = "Phone is required")
    private String phone; // 必填

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Verification code is required")
    private String code;
}