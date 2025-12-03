package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.entity.CartItem;
import com.campus.secondhandplatform.dto.CartItemDTO;
import com.campus.secondhandplatform.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 将CartItem实体转换为CartItemDTO
    private CartItemDTO convertToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setTitle(cartItem.getProduct().getTitle());
        dto.setPrice(cartItem.getProduct().getPrice().doubleValue());
        dto.setQuantity(cartItem.getQuantity());
        dto.setStock(100); // 临时设置默认库存为100
        dto.setStatus(cartItem.getProduct().getStatus().name().toLowerCase());
        // 处理图片URL，将逗号分隔的字符串转换为列表
        String imageUrls = cartItem.getProduct().getImageUrls();
        List<String> images = imageUrls != null && !imageUrls.isEmpty()
                ? Arrays.asList(imageUrls.split("\\s*,\\s*"))
                : Collections.emptyList();
        dto.setImages(images);
        dto.setSellerAvatar(cartItem.getProduct().getSeller().getAvatar());
        dto.setSellerName(cartItem.getProduct().getSeller().getNickname());
        return dto;
    }

    // 获取购物车列表
    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCartItems(@RequestParam Long userId) {
        List<CartItem> cartItems = cartService.getCartItemsByUser(userId);
        List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(cartItemDTOs, HttpStatus.OK);
    }

    // 添加商品到购物车
    @PostMapping
    public ResponseEntity<CartItemDTO> addToCart(@RequestParam Long userId, @RequestParam Long productId,
            @RequestParam Integer quantity) {
        CartItem cartItem = cartService.addToCart(userId, productId, quantity);
        CartItemDTO cartItemDTO = convertToDTO(cartItem);
        return new ResponseEntity<>(cartItemDTO, HttpStatus.CREATED);
    }

    // 更新购物车商品数量
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemDTO> updateCartItemQuantity(@PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        CartItem cartItem = cartService.updateCartItemQuantity(cartItemId, quantity);
        CartItemDTO cartItemDTO = convertToDTO(cartItem);
        return new ResponseEntity<>(cartItemDTO, HttpStatus.OK);
    }

    // 移除购物车商品
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 清空购物车
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 检查商品是否已经在购物车中
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkCartStatus(@RequestParam Long userId, @RequestParam Long productId) {
        boolean isInCart = cartService.isProductInCart(userId, productId);
        return new ResponseEntity<>(isInCart, HttpStatus.OK);
    }
}