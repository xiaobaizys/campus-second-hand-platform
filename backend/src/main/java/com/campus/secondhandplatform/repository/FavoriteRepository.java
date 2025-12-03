package com.campus.secondhandplatform.repository;

import com.campus.secondhandplatform.entity.Favorite;
import com.campus.secondhandplatform.entity.Product;
import com.campus.secondhandplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * 根据用户ID和商品ID查找收藏记录
     */
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 检查用户是否已收藏该商品
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 获取用户的所有收藏记录
     */
    List<Favorite> findByUserId(Long userId);
    
    /**
     * 获取商品的所有收藏记录
     */
    List<Favorite> findByProductId(Long productId);
}