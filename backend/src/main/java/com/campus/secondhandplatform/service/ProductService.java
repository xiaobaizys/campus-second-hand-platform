package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    /**
     * 创建商品
     * @param product 商品信息
     * @return 创建后的商品
     */
    Product createProduct(Product product);
    
    /**
     * 更新商品信息
     * @param id 商品ID
     * @param product 更新的商品信息
     * @return 更新后的商品
     */
    Optional<Product> updateProduct(Long id, Product product);
    
    /**
     * 根据ID获取商品
     * @param id 商品ID
     * @return 商品信息
     */
    Optional<Product> getProductById(Long id);
    
    /**
     * 获取所有商品（分页）
     * @param pageable 分页参数
     * @return 商品列表
     */
    Page<Product> getAllProducts(Pageable pageable);
    
    /**
     * 获取指定状态的商品（分页）
     * @param status 商品状态
     * @param pageable 分页参数
     * @return 商品列表
     */
    Page<Product> getProductsByStatus(Product.ProductStatus status, Pageable pageable);
    
    /**
     * 根据卖家ID获取商品
     * @param sellerId 卖家ID
     * @return 商品列表
     */
    List<Product> getProductsBySellerId(Long sellerId);
    
    /**
     * 根据分类ID获取商品
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<Product> getProductsByCategoryId(Long categoryId);
    
    /**
     * 搜索商品
     * @param keyword 关键词
     * @param categoryId 分类ID（可选）
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Product> searchProducts(String keyword, Long categoryId, Pageable pageable);
    
    /**
     * 高级搜索商品
     * @param keyword 关键词
     * @param categoryId 分类ID
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param location 地区
     * @param isNegotiable 是否可议价
     * @param isNew 是否全新
     * @param deliveryMethod 交易方式
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Product> advancedSearchProducts(
            String keyword, Long categoryId, Double minPrice, Double maxPrice, 
            String location, Boolean isNegotiable, Boolean isNew, 
            String deliveryMethod, Pageable pageable);
    
    /**
     * 获取热门商品
     * @param limit 限制数量
     * @return 热门商品列表
     */
    List<Product> getHotProducts(int limit);
    
    /**
     * 获取最新商品
     * @param limit 限制数量
     * @return 最新商品列表
     */
    List<Product> getLatestProducts(int limit);
    
    /**
     * 删除商品
     * @param id 商品ID
     * @return 是否删除成功
     */
    boolean deleteProduct(Long id);
    
    /**
     * 增加商品浏览量
     * @param id 商品ID
     */
    void incrementViewCount(Long id);
    
    /**
     * 增加商品点赞数
     * @param id 商品ID
     */
    void incrementLikeCount(Long id);
    
    /**
     * 减少商品点赞数
     * @param id 商品ID
     */
    void decrementLikeCount(Long id);
    
    /**
     * 更新商品状态
     * @param id 商品ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateProductStatus(Long id, Product.ProductStatus status);
    
    /**
     * 检查商品是否属于指定用户
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 是否属于该用户
     */
    boolean isProductOwner(Long productId, Long userId);
    
    /**
     * 获取商品总数量
     * @return 商品总数量
     */
    long getProductCount();
}