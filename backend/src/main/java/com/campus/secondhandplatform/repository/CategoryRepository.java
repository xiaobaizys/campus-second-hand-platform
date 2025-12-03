package com.campus.secondhandplatform.repository;

import com.campus.secondhandplatform.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 根据名称查找分类
     */
    Optional<Category> findByName(String name);
    
    /**
     * 检查分类名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 查找所有活跃的分类
     */
    List<Category> findByIsActiveTrueOrderBySortOrderAsc();
    
    /**
     * 查找所有不活跃的分类
     */
    List<Category> findByIsActiveFalse();
    
    /**
     * 查找有商品关联的分类
     */
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p WHERE p.status = 'AVAILABLE'")
    List<Category> findCategoriesWithProducts();
    
    /**
     * 统计分类下的商品数量
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.status = 'AVAILABLE'")
    long countProductsByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据名称模糊搜索分类
     */
    List<Category> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根据排序顺序查找活跃分类
     */
    List<Category> findByIsActiveTrueOrderBySortOrderAscNameAsc();
    
    /**
     * 根据排序顺序查找所有分类
     */
    List<Category> findAllByOrderBySortOrderAscNameAsc();
}