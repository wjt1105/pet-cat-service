package com.petsitter.user.service;

import com.petsitter.user.dto.LoginDTO;
import com.petsitter.user.dto.RegisterDTO;
import com.petsitter.user.vo.LoginVO;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @return JWT Token 与用户信息
     */
    LoginVO login(LoginDTO loginDTO);
}
