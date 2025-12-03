package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.dto.CategoryCreateDTO;
import com.campus.secondhandplatform.dto.CategoryDTO;
import com.campus.secondhandplatform.dto.CategoryUpdateDTO;
import com.campus.secondhandplatform.entity.Category;
import com.campus.secondhandplatform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * 将Category实体转换为CategoryDTO
     */
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIconUrl(category.getIconUrl());
        dto.setSortOrder(category.getSortOrder());
        dto.setIsActive(category.getIsActive());
        dto.setProductCount(categoryService.countProductsByCategoryId(category.getId()));
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
    
    /**
     * 将CategoryCreateDTO转换为Category实体
     */
    private Category convertToEntity(CategoryCreateDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setIconUrl(dto.getIconUrl());
        category.setSortOrder(dto.getSortOrder());
        category.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return category;
    }
    
    /**
     * 获取所有分类
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }
    
    /**
     * 获取所有活跃分类
     */
    @GetMapping("/active")
    public ResponseEntity<List<CategoryDTO>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }
    
    /**
     * 获取有商品关联的分类
     */
    @GetMapping("/with-products")
    public ResponseEntity<List<CategoryDTO>> getCategoriesWithProducts() {
        List<Category> categories = categoryService.getCategoriesWithProducts();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }
    
    /**
     * 根据ID获取分类详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(c -> ResponseEntity.ok(convertToDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据名称获取分类
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String name) {
        Optional<Category> category = categoryService.getCategoryByName(name);
        return category.map(c -> ResponseEntity.ok(convertToDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 搜索分类
     */
    @GetMapping("/search")
    public ResponseEntity<List<CategoryDTO>> searchCategories(@RequestParam String keyword) {
        List<Category> categories = categoryService.searchCategories(keyword);
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }
    
    /**
     * 创建新分类
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryCreateDTO categoryCreateDTO) {
        Category category = convertToEntity(categoryCreateDTO);
        Category savedCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(convertToDTO(savedCategory));
    }
    
    /**
     * 更新分类信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id, 
            @RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        
        // 将DTO转换为实体
        Category category = new Category();
        category.setName(categoryUpdateDTO.getName());
        category.setDescription(categoryUpdateDTO.getDescription());
        category.setIconUrl(categoryUpdateDTO.getIconUrl());
        category.setSortOrder(categoryUpdateDTO.getSortOrder());
        category.setIsActive(categoryUpdateDTO.getIsActive());
        
        Optional<Category> updatedCategory = categoryService.updateCategory(id, category);
        return updatedCategory.map(c -> ResponseEntity.ok(convertToDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        boolean deleted = categoryService.deleteCategory(id);
        Map<String, String> response = new HashMap<>();
        
        if (deleted) {
            response.put("message", "分类删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "分类不存在或删除失败");
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 激活分类
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> activateCategory(@PathVariable Long id) {
        boolean updated = categoryService.toggleCategoryStatus(id, true);
        Map<String, String> response = new HashMap<>();
        
        if (updated) {
            response.put("message", "分类激活成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "分类不存在或激活失败");
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 停用分类
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deactivateCategory(@PathVariable Long id) {
        boolean updated = categoryService.toggleCategoryStatus(id, false);
        Map<String, String> response = new HashMap<>();
        
        if (updated) {
            response.put("message", "分类停用成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "分类不存在或停用失败");
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取分类下的商品数量
     */
    @GetMapping("/{id}/product-count")
    public ResponseEntity<Map<String, Long>> getProductCountByCategoryId(@PathVariable Long id) {
        long count = categoryService.countProductsByCategoryId(id);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 检查分类名称是否存在
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Boolean>> checkCategoryNameExists(@RequestParam String name) {
        boolean exists = categoryService.existsByName(name);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}