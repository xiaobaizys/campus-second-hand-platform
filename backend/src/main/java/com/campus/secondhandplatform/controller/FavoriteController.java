package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.entity.Favorite;
import com.campus.secondhandplatform.entity.Product;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.service.FavoriteService;
import com.campus.secondhandplatform.service.ProductService;
import com.campus.secondhandplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = { "http://localhost:*" }, maxAge = 3600)
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("用户未登录");
        }

        String name = authentication.getName();

        // 尝试先将name作为用户ID处理
        try {
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            // 如果不是数字，说明是用户名，需要根据用户名查找用户ID
            User user = userService.getUserByUsername(name)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + name));
            return user.getId();
        }
    }

    /**
     * 添加收藏
     */
    @PostMapping("/products/{productId}/favorite")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> addFavorite(@PathVariable Long productId) {
        try {
            Long userId = getCurrentUserId();
            Favorite favorite = favoriteService.addToFavorites(userId, productId);
            Map<String, Object> response = new HashMap<>();
            if (favorite != null) {
                response.put("success", true);
                response.put("message", "添加收藏成功");
                response.put("favorite", favorite);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "该商品已在收藏列表中");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "添加收藏失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/products/{productId}/favorite")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> removeFavorite(@PathVariable Long productId) {
        try {
            Long userId = getCurrentUserId();
            boolean result = favoriteService.removeFromFavorites(userId, productId);
            Map<String, Object> response = new HashMap<>();
            if (result) {
                response.put("success", true);
                response.put("message", "取消收藏成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "该商品不在收藏列表中");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "取消收藏失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/products/{productId}/favorite/check")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> checkFavorite(@PathVariable Long productId) {
        try {
            Long userId = getCurrentUserId();
            boolean isFavorited = favoriteService.isProductFavoritedByUser(userId, productId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isFavorited", isFavorited);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("isFavorited", false);
            response.put("message", "检查收藏状态失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/favorites")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserFavorites() {
        try {
            Long userId = getCurrentUserId();
            List<Product> favoriteProducts = favoriteService.getUserFavoriteProducts(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", favoriteProducts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取收藏列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量取消收藏
     */
    @PostMapping("/favorites/batch-delete")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> batchRemoveFavorites(@RequestBody Map<String, List<Long>> requestBody) {
        try {
            Long userId = getCurrentUserId();
            List<Long> productIds = requestBody.get("productIds");
            if (productIds == null || productIds.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "请选择要取消收藏的商品");
                return ResponseEntity.badRequest().body(response);
            }

            // 批量取消收藏
            for (Long productId : productIds) {
                favoriteService.removeFromFavorites(userId, productId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量取消收藏成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量取消收藏失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
