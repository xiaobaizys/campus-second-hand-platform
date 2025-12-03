package com.campus.secondhandplatform.repository;

import com.campus.secondhandplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findUserById(Long id);

    /**
     * 搜索用户，支持按用户名、邮箱或角色搜索
     */
    @Query("SELECT u FROM User u WHERE (:search IS NULL OR u.username LIKE %:search% OR u.email LIKE %:search%) AND (:role IS NULL OR EXISTS (SELECT r FROM u.roles r WHERE r.name = :role))")
    List<User> searchUsers(@Param("search") String search, @Param("role") String role);

    /**
     * 获取最近7天的用户增长数据
     */
    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count FROM users WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY DATE(created_at) ORDER BY date", nativeQuery = true)
    List<Object[]> countUsersByLast7Days();

    /**
     * 获取最近30天的用户增长数据
     */
    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count FROM users WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY DATE(created_at) ORDER BY date", nativeQuery = true)
    List<Object[]> countUsersByLast30Days();

    /**
     * 获取按月份统计的用户增长数据
     */
    @Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(*) as count FROM users GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY month", nativeQuery = true)
    List<Object[]> countUsersByMonth();

    /**
     * 获取按角色统计的用户数量
     */
    @Query(value = "SELECT r.name as role, COUNT(u.id) as count FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id GROUP BY r.name", nativeQuery = true)
    List<Object[]> countUsersByRole();

    /**
     * 获取按校园统计的用户数量
     */
    @Query(value = "SELECT campus_name as campus, COUNT(*) as count FROM users WHERE campus_name IS NOT NULL AND campus_name != '' GROUP BY campus_name ORDER BY count DESC", nativeQuery = true)
    List<Object[]> countUsersByCampus();
}