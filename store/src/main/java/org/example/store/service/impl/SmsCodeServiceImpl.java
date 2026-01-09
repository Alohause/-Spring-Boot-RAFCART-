package org.example.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.store.dto.SmsSendDTO;
import org.example.store.entity.SmsCode;
import org.example.store.mapper.SmsCodeMapper;
import org.example.store.service.SmsCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SmsCodeServiceImpl extends ServiceImpl<SmsCodeMapper, SmsCode> implements SmsCodeService {

    @Override
    @Transactional
    public String sendCode(SmsSendDTO dto) {
        SmsCode lastCode = baseMapper.selectOne(new LambdaQueryWrapper<SmsCode>()
                .eq(SmsCode::getPhone, dto.getPhone())
                .eq(SmsCode::getScene, dto.getScene())
                .orderByDesc(SmsCode::getId)
                .last("LIMIT 1"));

        if (lastCode != null) {
            LocalDateTime now = LocalDateTime.now();
            if (lastCode.getCreatedAt().plusSeconds(60).isAfter(now)) {
                throw new RuntimeException("发送太频繁，请稍后再试");
            }
        }

        String code = String.valueOf(new Random().nextInt(899999) + 100000);

        // 存库
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(dto.getPhone());
        smsCode.setCode(code);
        smsCode.setScene(dto.getScene());
        smsCode.setExpiredAt(LocalDateTime.now().plusMinutes(5)); // 5分钟有效
        smsCode.setUsed(0);
        smsCode.setCreatedAt(LocalDateTime.now());

        save(smsCode);

        // 返回验证码
        System.out.println(">>> Mock SMS: Phone=" + dto.getPhone() + ", Code=" + code);
        return code;
    }

    @Transactional
    @Override
    public void validateCode(String phone, String code, String scene) {
        // 查询
        SmsCode smsCode = baseMapper.selectOne(new LambdaQueryWrapper<SmsCode>()
                .eq(SmsCode::getPhone, phone)
                .eq(SmsCode::getScene, scene)
                .eq(SmsCode::getUsed, 0) // 未使用
                .gt(SmsCode::getExpiredAt, LocalDateTime.now()) // 未过期
                .orderByDesc(SmsCode::getId) // 最新
                .last("LIMIT 1"));

        if (smsCode == null) {
            throw new RuntimeException("验证码无效或已过期，请重新获取");
        }

        if (!smsCode.getCode().equals(code)) {
            throw new RuntimeException("验证码错误");
        }

        // 校验通过
        smsCode.setUsed(1);
        updateById(smsCode);
    }

    @Override
    @Transactional
    public void verifyOrThrow(String phone, String code, String scene) {
        // 查询
        SmsCode smsCode = baseMapper.selectOne(new LambdaQueryWrapper<SmsCode>()
                .eq(SmsCode::getPhone, phone)
                .eq(SmsCode::getScene, scene)
                .eq(SmsCode::getUsed, 0) // 未使用
                .gt(SmsCode::getExpiredAt, LocalDateTime.now()) // 未过期
                .orderByDesc(SmsCode::getId) // 最新
                .last("LIMIT 1"));

        if (smsCode == null) {
            throw new RuntimeException("验证码无效或已过期，请重新获取");
        }

        if (!smsCode.getCode().equals(code)) {
            throw new RuntimeException("验证码错误");
        }

        // 校验通过
        smsCode.setUsed(1);
        updateById(smsCode);
    }
}
