package org.example.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsSendDTO {
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * REGISTER / LOGIN / RESET_PWD / CHANGE_PWD
     */
    @NotBlank(message = "场景不能为空")
    private String scene;
}
