package com.campus.secondhandplatform.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class OrderCreateDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;
    
    private String contactInfo;
    private String deliveryAddress;
    private String deliveryMethod;
    private String paymentMethod;
    private String remarks;
}