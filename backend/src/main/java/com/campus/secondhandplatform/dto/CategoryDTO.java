package com.campus.secondhandplatform.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private Integer sortOrder;
    private Boolean isActive;
    private Long productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}