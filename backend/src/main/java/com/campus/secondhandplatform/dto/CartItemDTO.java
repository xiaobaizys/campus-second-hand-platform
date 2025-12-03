package com.campus.secondhandplatform.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String title;
    private Double price;
    private Integer quantity;
    private Integer stock;
    private String status;
    private List<String> images;
    private String sellerAvatar;
    private String sellerName;
}