package com.campus.secondhandplatform.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String realName;
    private String role = "USER"; // 默认为普通用户
}