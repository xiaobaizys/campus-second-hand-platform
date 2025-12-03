package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.CartItem;
import java.util.List;

public interface CartService {
    List<CartItem> getCartItemsByUser(Long userId);

    CartItem addToCart(Long userId, Long productId, Integer quantity);

    CartItem updateCartItemQuantity(Long cartItemId, Integer quantity);

    void removeCartItem(Long cartItemId);

    void clearCart(Long userId);
    
    /**
     * 检查商品是否已经在购物车中
     */
    boolean isProductInCart(Long userId, Long productId);
}