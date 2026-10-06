package com.petsitter.user.vo;

import com.petsitter.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录成功响应
 */
@Data
@AllArgsConstructor
public class LoginVO {

    /** JWT Token */
    private String token;

    /** 用户信息（密码已通过 @JsonIgnore 排除） */
    private User userInfo;
}
