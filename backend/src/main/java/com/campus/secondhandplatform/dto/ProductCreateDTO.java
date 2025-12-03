package com.campus.secondhandplatform.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductCreateDTO {
    
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 100, message = "商品标题长度不能超过100个字符")
    private String title;
    
    @NotBlank(message = "商品描述不能为空")
    @Size(max = 2000, message = "商品描述长度不能超过2000个字符")
    private String description;
    
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @Digits(integer = 10, fraction = 2, message = "商品价格格式不正确")
    private BigDecimal price;
    
    @DecimalMin(value = "0.01", message = "商品原价必须大于0")
    @Digits(integer = 10, fraction = 2, message = "商品原价格式不正确")
    private BigDecimal originalPrice;
    
    @NotEmpty(message = "商品图片不能为空")
    @Size(max = 9, message = "商品图片数量不能超过9张")
    private List<String> imageUrls;
    
    @NotNull(message = "商品分类不能为空")
    private Long categoryId;
    
    private Boolean isNegotiable = false;
    
    private Boolean isNew = false;
    
    @NotBlank(message = "交易方式不能为空")
    private String deliveryMethod;
    
    private String location;
    
    @NotBlank(message = "联系方式不能为空")
    private String contactInfo;
}