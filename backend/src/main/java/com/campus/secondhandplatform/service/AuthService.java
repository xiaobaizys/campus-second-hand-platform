package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.dto.LoginRequest;
import com.campus.secondhandplatform.dto.RegisterRequest;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.repository.UserRepository;
import com.campus.secondhandplatform.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> authenticateUser(LoginRequest loginRequest) {
        logger.info("尝试登录用户: {}", loginRequest.getUsername());
        logger.info("登录密码: {}", loginRequest.getPassword());

        try {
            // 直接使用用户名进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            logger.info("认证成功，authentication: {}", authentication);
            logger.info("认证成功，authentication.getName(): {}", authentication.getName());
            logger.info("认证成功，authentication.getAuthorities(): {}", authentication.getAuthorities());

            // 从数据库获取用户信息
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

            logger.info("从数据库获取用户信息成功: {}, ID: {}, 角色: {}, 密码: {}",
                    user.getUsername(), user.getId(), user.getRole(), user.getPassword());

            // 确保管理员用户能被正确识别为管理员
            String role = "ROLE_USER";
            // 检查用户是否为admin用户
            if ("admin".equals(user.getUsername())
                    || user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()))) {
                role = "ROLE_ADMIN";
            }

            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(String.valueOf(user.getId())) // 使用用户ID作为用户名
                    .password(user.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                    .build();

            logger.info("创建UserDetails成功: {}, 用户名: {}, 密码: {}, 权限: {}",
                    userDetails, userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());

            String jwt = jwtUtil.generateToken(userDetails);
            logger.info("JWT生成成功: {}", jwt);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());

            return response;
        } catch (Exception e) {
            logger.error("登录失败: {}", e.getMessage());
            logger.error("登录失败异常堆栈:", e);
            throw e;
        }
    }

    @Autowired
    private com.campus.secondhandplatform.repository.RoleRepository roleRepository;

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        logger.info("开始注册用户: {}", registerRequest.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            logger.warn("用户名已存在: {}", registerRequest.getUsername());
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.warn("邮箱已被注册: {}", registerRequest.getEmail());
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhone());
        user.setRealName(registerRequest.getRealName());

        // 设置角色
        String roleName = "ROLE_" + registerRequest.getRole().toUpperCase();
        com.campus.secondhandplatform.entity.Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        java.util.Set<com.campus.secondhandplatform.entity.Role> roles = new java.util.HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        logger.info("准备保存用户: {}", user);
        User savedUser = userRepository.save(user);
        logger.info("用户保存成功: {}", savedUser.getId());

        return savedUser;
    }
}