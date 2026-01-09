package org.example.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;
import org.example.store.dto.*;
import org.example.store.entity.User;
import org.springframework.stereotype.Service;

public interface UserService extends IService<User> {
    void register(UserRegisterDTO dto);
    User login(UserLoginDTO dto, HttpSession session);
    User loginByCode(UserLoginByCodeDTO dto, HttpSession session);

    void resetPassword(ResetPasswordDTO dto);

}
