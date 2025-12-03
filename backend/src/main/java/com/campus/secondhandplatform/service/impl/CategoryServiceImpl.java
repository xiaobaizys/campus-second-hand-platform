package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.Category;
import com.campus.secondhandplatform.repository.CategoryRepository;
import com.campus.secondhandplatform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Category createCategory(Category category) {
        // 检查分类名称是否已存在
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("分类名称已存在: " + category.getName());
        }
        
        // 设置默认排序顺序
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        
        // 设置默认状态为活跃
        if (category.getIsActive() == null) {
            category.setIsActive(true);
        }
        
        return categoryRepository.save(category);
    }
    
    @Override
    public Optional<Category> updateCategory(Long id, Category category) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // 检查名称是否与其他分类重复
                    Optional<Category> categoryByName = categoryRepository.findByName(category.getName());
                    if (categoryByName.isPresent() && !categoryByName.get().getId().equals(id)) {
                        throw new IllegalArgumentException("分类名称已存在: " + category.getName());
                    }
                    
                    // 更新分类信息
                    existingCategory.setName(category.getName());
                    existingCategory.setDescription(category.getDescription());
                    existingCategory.setIconUrl(category.getIconUrl());
                    existingCategory.setSortOrder(category.getSortOrder());
                    existingCategory.setIsActive(category.getIsActive());
                    
                    return categoryRepository.save(existingCategory);
                });
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAscNameAsc();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderBySortOrderAscNameAsc();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesWithProducts() {
        return categoryRepository.findCategoriesWithProducts();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> searchCategories(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    @Override
    public boolean deleteCategory(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    // 检查分类下是否有商品
                    long productCount = countProductsByCategoryId(id);
                    if (productCount > 0) {
                        throw new IllegalStateException("该分类下还有商品，不能删除");
                    }
                    
                    categoryRepository.delete(category);
                    return true;
                })
                .orElse(false);
    }
    
    @Override
    public boolean toggleCategoryStatus(Long id, boolean isActive) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setIsActive(isActive);
                    categoryRepository.save(category);
                    return true;
                })
                .orElse(false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countProductsByCategoryId(Long categoryId) {
        return categoryRepository.countProductsByCategoryId(categoryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}