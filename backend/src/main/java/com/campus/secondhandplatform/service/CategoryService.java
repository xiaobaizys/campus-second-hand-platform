package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    
    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建后的分类
     */
    Category createCategory(Category category);
    
    /**
     * 更新分类信息
     * @param id 分类ID
     * @param category 更新的分类信息
     * @return 更新后的分类
     */
    Optional<Category> updateCategory(Long id, Category category);
    
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类信息
     */
    Optional<Category> getCategoryById(Long id);
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 获取所有活跃分类
     * @return 活跃分类列表
     */
    List<Category> getActiveCategories();
    
    /**
     * 获取有商品关联的分类
     * @return 有商品关联的分类列表
     */
    List<Category> getCategoriesWithProducts();
    
    /**
     * 根据名称查找分类
     * @param name 分类名称
     * @return 分类信息
     */
    Optional<Category> getCategoryByName(String name);
    
    /**
     * 搜索分类
     * @param keyword 关键词
     * @return 搜索结果
     */
    List<Category> searchCategories(String keyword);
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Long id);
    
    /**
     * 激活/停用分类
     * @param id 分类ID
     * @param isActive 是否激活
     * @return 是否更新成功
     */
    boolean toggleCategoryStatus(Long id, boolean isActive);
    
    /**
     * 统计分类下的商品数量
     * @param categoryId 分类ID
     * @return 商品数量
     */
    long countProductsByCategoryId(Long categoryId);
    
    /**
     * 检查分类名称是否已存在
     * @param name 分类名称
     * @return 是否存在
     */
    boolean existsByName(String name);
}