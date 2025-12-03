package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = { "http://localhost:*" }, maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("获取当前登录用户ID，authentication: " + authentication);
        
        if (authentication == null) {
            System.out.println("用户未认证：authentication为null");
            throw new RuntimeException("用户未认证");
        }
        
        String name = authentication.getName();
        System.out.println("authentication.getName(): " + name);
        
        if (name == null || name.equals("anonymousUser")) {
            System.out.println("用户未认证：name为null或anonymousUser");
            throw new RuntimeException("用户未认证");
        }

        // 尝试先将name作为用户ID处理
        try {
            Long userId = Long.parseLong(name);
            System.out.println("成功将name转换为用户ID: " + userId);
            return userId;
        } catch (NumberFormatException e) {
            System.out.println("name不是数字ID，尝试根据用户名查找用户: " + name);
            // 如果不是数字，说明是用户名，需要根据用户名查找用户ID
            User user = userService.getUserByUsername(name)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + name));
            System.out.println("根据用户名找到用户，用户ID: " + user.getId());
            return user.getId();
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<User> getCurrentUserProfile() {
        Long userId = getCurrentUserId();
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 不返回密码
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<User> updateCurrentUserProfile(@RequestBody User userDetails) {
        Long userId = getCurrentUserId();
        User updatedUser = userService.updateUser(userId, userDetails);

        // 不返回密码
        updatedUser.setPassword(null);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        Long userId = getCurrentUserId();
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        try {
            userService.changePassword(userId, oldPassword, newPassword);
            Map<String, String> response = new HashMap<>();
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 注销账号
     */
    @DeleteMapping("/account")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteAccount() {
        Long userId = getCurrentUserId();

        try {
            userService.deleteUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "账号已注销");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = getCurrentUserId();

        try {
            String avatarUrl = userService.uploadAvatar(userId, file);
            Map<String, String> response = new HashMap<>();
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 管理员获取用户列表
     */
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> users = userService.getAllUsers();
        // 不返回密码
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    /**
     * 管理员获取用户详情
     */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 不返回密码
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    /**
     * 管理员禁用/启用用户
     */
    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean status) {

        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setIsActive(status);
        userService.updateUser(id, user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户状态已更新");
        response.put("isActive", status);

        return ResponseEntity.ok(response);
    }

    /**
     * 管理员获取用户数量
     */
    @GetMapping("/admin/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        long count = userService.getUserCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * 管理员搜索用户
     */
    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Iterable<User>> searchUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role) {
        List<User> users = userService.searchUsers(search, role);
        // 不返回密码
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    /**
     * 管理员修改用户角色
     */
    @PatchMapping("/admin/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> roleData) {
        String role = roleData.get("role");
        if (role == null || role.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "角色不能为空");
            return ResponseEntity.badRequest().body(error);
        }

        User user = userService.updateUserRole(id, role);
        // 不返回密码
        user.setPassword(null);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户角色已更新");
        response.put("user", user);

        return ResponseEntity.ok(response);
    }
}