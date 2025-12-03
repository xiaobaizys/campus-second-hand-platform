package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.dto.LoginRequest;
import com.campus.secondhandplatform.dto.RegisterRequest;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = {"http://localhost:*"}, maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("收到登录请求");
        System.out.println("用户名: " + loginRequest.getUsername());
        System.out.println("密码: " + loginRequest.getPassword());
        
        try {
            Map<String, Object> response = authService.authenticateUser(loginRequest);
            System.out.println("登录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("登录失败，异常消息: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "用户名或密码错误");
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        logger.info("收到注册请求: {}", registerRequest.getUsername());
        try {
            User user = authService.registerUser(registerRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "注册成功");
            response.put("userId", user.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("注册失败", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}