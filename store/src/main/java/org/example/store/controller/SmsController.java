package org.example.store.controller;

import org.example.store.common.result.Result;
import org.example.store.dto.SmsSendDTO;
import org.example.store.service.SmsCodeService; // 确保引用正确
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
@CrossOrigin(originPatterns = "*",allowCredentials = "true")
public class SmsController {

    @Autowired
    private SmsCodeService SmsCodeService;

    @PostMapping("/send")
    public Result<String> sendCode(@RequestBody @Validated SmsSendDTO dto) {
        // 获取
        String code = SmsCodeService.sendCode(dto);
        // 返回前端
        return Result.success(code);
    }
}