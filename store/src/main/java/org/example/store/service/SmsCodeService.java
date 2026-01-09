package org.example.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.store.dto.SmsSendDTO;
import org.example.store.entity.SmsCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface SmsCodeService extends IService<SmsCode> {
    // 生成
    String sendCode(SmsSendDTO dto);

    // 校验
    void verifyOrThrow(String phone, String scene, String code);

    @Transactional
    void validateCode(String phone, String code, String scene);
}