package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.Favorite;
import com.campus.secondhandplatform.entity.Product;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.repository.FavoriteRepository;
import com.campus.secondhandplatform.service.FavoriteService;
import com.campus.secondhandplatform.service.ProductService;
import com.campus.secondhandplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, 
                              UserService userService, 
                              ProductService productService) {
        this.favoriteRepository = favoriteRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @Override
    public Favorite addToFavorites(Long userId, Long productId) {
        // 检查是否已经收藏
        if (isProductFavoritedByUser(userId, productId)) {
            return null; // 或者可以抛出异常表示已经收藏
        }

        // 获取用户和商品
        Optional<User> userOpt = userService.getUserById(userId);
        Optional<Product> productOpt = productService.getProductById(productId);

        if (userOpt.isPresent() && productOpt.isPresent()) {
            Favorite favorite = new Favorite();
            favorite.setUser(userOpt.get());
            favorite.setProduct(productOpt.get());
            favorite.setCreatedAt(LocalDateTime.now());
            return favoriteRepository.save(favorite);
        }

        return null;
    }

    @Override
    public boolean removeFromFavorites(Long userId, Long productId) {
        Optional<Favorite> favoriteOpt = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (favoriteOpt.isPresent()) {
            favoriteRepository.delete(favoriteOpt.get());
            return true;
        }
        return false;
    }

    @Override
    public List<Product> getUserFavoriteProducts(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
    }

    @Override
    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Override
    public boolean isProductFavoritedByUser(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }
}