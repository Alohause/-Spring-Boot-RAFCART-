package org.example.store.controller;

import jakarta.servlet.http.HttpSession;
import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.dto.*;
import org.example.store.entity.User;
import org.example.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping({"/users", "/api/users"})
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody @Validated UserRegisterDTO dto) {
        try {
            userService.register(dto);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody UserLoginDTO dto, HttpSession session) {
        try {
            User user = userService.login(dto, session);
            user.setPasswordHash(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login/code")
    public Result<User> loginByCode(@RequestBody @Validated UserLoginByCodeDTO dto, HttpSession session) {
        try {
            User user = userService.loginByCode(dto, session);
            user.setPasswordHash(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/password/reset")
    public Result<String> resetPassword(@RequestBody @Validated ResetPasswordDTO dto) {
        try {
            userService.resetPassword(dto);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        return Result.success("注销成功");
    }

    @GetMapping({"/me", "/current"})
    public Result<User> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            Long headerUserId = UserContext.getUserId();
            if (headerUserId != null) user = userService.getById(headerUserId);
        }

        if (user == null) return Result.error("请先登录");

        user.setPasswordHash(null);
        return Result.success(user);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody UpdateProfileDTO dto) {
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");

        String name = dto.getName() == null ? "" : dto.getName().trim();
        String email = dto.getEmail() == null ? null : dto.getEmail().trim();

        if (name.isEmpty()) return Result.error("用户名不能为空");
        if (name.length() > 50) return Result.error("用户名过长");

        if (email != null && !email.isEmpty()) {
            if (email.length() > 100) return Result.error("邮箱过长");
            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return Result.error("邮箱格式不正确");
        } else {
            email = null;
        }

        User update = new User();
        update.setId(uid);
        update.setName(name);
        update.setEmail(email);
        update.setUpdatedAt(LocalDateTime.now());
        userService.updateById(update);

        return Result.success("OK");
    }
}
