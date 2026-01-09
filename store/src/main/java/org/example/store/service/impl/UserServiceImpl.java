package org.example.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.example.store.dto.*;
import org.example.store.entity.User;
import org.example.store.mapper.UserMapper;
import org.example.store.service.SmsCodeService;
import org.example.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private SmsCodeService smsCodeService;

    private String md5(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void register(UserRegisterDTO dto) {
        // 验证码校验
        smsCodeService.verifyOrThrow(dto.getPhone(), dto.getCode(), "REGISTER");
        // 用户校验
        long count = count(new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
        if (count > 0) {
            throw new RuntimeException("该手机号已注册");
        }

        // 用户
        User user = new User();
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword());
        user.setRole("USER");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        save(user);
    }

    @Override
    public User login(UserLoginDTO dto, HttpSession session) {
        // 查询用户
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 校验密码
        if (!user.getPasswordHash().equals(dto.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被封禁");
        }
        session.setAttribute("currentUser", user);
        return user;
    }
    @Override
    public User loginByCode(UserLoginByCodeDTO dto, HttpSession session) {
        // 校验验证码
        smsCodeService.validateCode(dto.getPhone(), dto.getCode(), "LOGIN");
        // 查询用户
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));

        if (user == null) {
            // 抛出异常
            throw new RuntimeException("用户不存在，请先注册");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被封禁");
        }
        session.setAttribute("currentUser", user);
        return user;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        // 校验验证码
        smsCodeService.validateCode(dto.getPhone(), dto.getCode(), "RESET_PWD");
        // 查询用户
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 更新密码
        user.setPasswordHash(dto.getNewPassword());
        user.setUpdatedAt(LocalDateTime.now());

        updateById(user);
    }
}
