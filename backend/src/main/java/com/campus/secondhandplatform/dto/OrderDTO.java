package com.campus.secondhandplatform.dto;

import com.campus.secondhandplatform.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long productId;
    private String productName;
    private String productImage;
    private Long buyerId;
    private String buyerName;
    private Long sellerId;
    private String sellerName;
    private BigDecimal price;
    private BigDecimal finalPrice;
    private Order.OrderStatus status;
    private String contactInfo;
    private String deliveryAddress;
    private String deliveryMethod;
    private String paymentMethod;
    private String paymentStatus;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}