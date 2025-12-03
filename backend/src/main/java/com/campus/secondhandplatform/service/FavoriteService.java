package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.Favorite;
import com.campus.secondhandplatform.entity.Product;

import java.util.List;

public interface FavoriteService {
    
    /**
     * 添加收藏
     */
    Favorite addToFavorites(Long userId, Long productId);
    
    /**
     * 取消收藏
     */
    boolean removeFromFavorites(Long userId, Long productId);
    
    /**
     * 获取用户收藏的商品列表
     */
    List<Product> getUserFavoriteProducts(Long userId);
    
    /**
     * 获取用户收藏记录列表
     */
    List<Favorite> getUserFavorites(Long userId);
    
    /**
     * 检查用户是否已收藏该商品
     */
    boolean isProductFavoritedByUser(Long userId, Long productId);
}