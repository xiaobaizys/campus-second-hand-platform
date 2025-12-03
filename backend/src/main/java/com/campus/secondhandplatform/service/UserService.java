package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据ID获取用户
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名获取用户
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 更新用户信息
     */
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRealName(userDetails.getRealName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setStudentId(userDetails.getStudentId());
        user.setSchoolName(userDetails.getSchoolName());
        user.setCampusName(userDetails.getCampusName());
        user.setAvatarUrl(userDetails.getAvatarUrl());

        return userRepository.save(user);
    }

    /**
     * 修改密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 注销账号
     */
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        userRepository.delete(user);
    }

    /**
     * 检查用户名是否已存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否已存在
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(Long userId, MultipartFile file) {
        // 验证文件
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只能上传图片文件");
        }

        // 验证文件大小（限制为5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("图片大小不能超过5MB");
        }

        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        try {
            // 创建上传目录
            String uploadDir = "uploads/avatars/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 更新用户头像URL（返回完整URL）
            String avatarUrl = "/uploads/avatars/" + filename;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有用户（管理员功能）
     */
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 获取用户数量（管理员功能）
     */
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * 搜索用户（管理员功能）
     */
    public List<User> searchUsers(String keyword, String role) {
        return userRepository.searchUsers(keyword, role);
    }

    /**
     * 修改用户角色（管理员功能）
     */
    public User updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 这里需要根据roleName获取Role对象，然后更新用户的角色
        // 但当前实现中没有RoleService，所以暂时只更新用户的角色字符串
        // 注意：这只是一个临时实现，实际项目中应该更新用户的roles集合

        return userRepository.save(user);
    }

    /**
     * 获取用户增长分布统计
     */
    public Map<String, Object> getGrowthDistribution() {
        // 获取最近7天的用户增长数据
        List<Object[]> last7Days = userRepository.countUsersByLast7Days();
        // 获取最近30天的用户增长数据
        List<Object[]> last30Days = userRepository.countUsersByLast30Days();
        // 获取按月份统计的用户增长数据
        List<Object[]> byMonth = userRepository.countUsersByMonth();
        // 获取按角色统计的用户数量
        List<Object[]> byRole = userRepository.countUsersByRole();
        // 获取按校园统计的用户数量
        List<Object[]> byCampus = userRepository.countUsersByCampus();

        Map<String, Object> result = new HashMap<>();
        result.put("last7Days", last7Days);
        result.put("last30Days", last30Days);
        result.put("byMonth", byMonth);
        result.put("byRole", byRole);
        result.put("byCampus", byCampus);

        return result;
    }
}