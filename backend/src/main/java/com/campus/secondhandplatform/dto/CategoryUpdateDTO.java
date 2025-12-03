package com.campus.secondhandplatform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryUpdateDTO {
    
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String name;
    
    @Size(max = 200, message = "分类描述长度不能超过200个字符")
    private String description;
    
    @Size(max = 255, message = "图标URL长度不能超过255个字符")
    private String iconUrl;
    
    private Integer sortOrder;
    
    private Boolean isActive;
}