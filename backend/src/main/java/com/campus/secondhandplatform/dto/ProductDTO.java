package com.campus.secondhandplatform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private List<String> imageUrls;
    private Long categoryId;
    private String categoryName;
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    private Boolean isNegotiable;
    private Boolean isNew;
    private String deliveryMethod;
    private String location;
    private String contactInfo;
    private String status;
    private Integer viewCount;
    private Integer likeCount;
    private LocalDateTime favoriteCreatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}