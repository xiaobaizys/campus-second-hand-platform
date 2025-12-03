package com.campus.secondhandplatform.security;

import com.campus.secondhandplatform.entity.Role;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        boolean isUserId = false;
        
        System.out.println("CustomUserDetailsService.loadUserByUsername，username: " + username);
        
        // 尝试先按用户名查找
        user = userRepository.findByUsername(username).orElse(null);
        System.out.println("按用户名查找结果: " + (user != null ? "找到用户" : "未找到用户"));
        
        // 如果按用户名找不到，尝试按ID查找（因为JWT中存储的是用户ID）
        if (user == null) {
            try {
                Long userId = Long.parseLong(username);
                user = userRepository.findById(userId).orElse(null);
                isUserId = true;
                System.out.println("按ID查找结果: " + (user != null ? "找到用户" : "未找到用户") + ", userId: " + userId);
            } catch (NumberFormatException e) {
                // 不是数字ID，忽略错误
                System.out.println("不是数字ID，忽略错误: " + e.getMessage());
            }
        }
        
        if (user == null) {
            System.out.println("用户不存在: " + username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户是否激活
        if (!user.getIsActive()) {
            System.out.println("用户账号已被禁用: " + username);
            throw new RuntimeException("用户账号已被禁用");
        }

        // 对于登录认证，使用用户名作为用户名
        // 对于JWT验证，使用用户ID作为用户名
        String authUsername = isUserId ? String.valueOf(user.getId()) : user.getUsername();
        System.out.println("返回的UserDetails用户名: " + authUsername + ", 角色: " + user.getRole());
        
        // 构建权限列表，确保每个权限都带有ROLE_前缀
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        boolean isAdmin = false;
        
        // 检查用户是否为admin用户
        if ("admin".equals(user.getUsername())) {
            isAdmin = true;
        }
        
        // 检查用户角色集合
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (Role role : user.getRoles()) {
                String roleName = role.getName();
                // 确保角色名称带有ROLE_前缀
                if (!roleName.startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }
                authorities.add(new SimpleGrantedAuthority(roleName));
                
                // 如果用户有ADMIN角色，标记为管理员
                if (roleName.equals("ROLE_ADMIN")) {
                    isAdmin = true;
                }
            }
        }
        
        // 确保管理员用户有ROLE_ADMIN权限
        if (isAdmin && !authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        
        // 如果没有任何权限，添加默认角色ROLE_USER
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(authUsername)
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}