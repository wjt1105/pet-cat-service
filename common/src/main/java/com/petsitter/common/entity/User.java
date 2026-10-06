package com.petsitter.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 用户名 */
    private String username;

    /** 密码（BCrypt 加密），永不返回给前端 */
    @JsonIgnore
    private String password;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 头像地址 */
    private String avatar;

    /** 角色：0-管理员 1-普通用户 2-上门铲屎官 */
    private Integer role;

    /** 状态：0-禁用 1-正常 */
    private Integer status;
}
