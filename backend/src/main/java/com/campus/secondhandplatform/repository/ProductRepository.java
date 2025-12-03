package com.campus.secondhandplatform.repository;

import com.campus.secondhandplatform.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  // 根据卖家ID查找商品
  List<Product> findBySellerId(Long sellerId);

  // 根据分类ID查找商品
  List<Product> findByCategoryId(Long categoryId);

  // 根据状态查找商品
  List<Product> findByStatus(Product.ProductStatus status);

  // 根据卖家ID和状态查找商品
  List<Product> findBySellerIdAndStatus(Long sellerId, Product.ProductStatus status);

  // 根据分类ID和状态查找商品
  List<Product> findByCategoryIdAndStatus(Long categoryId, Product.ProductStatus status);

  // 分页查询所有商品
  Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);

  // 根据标题或描述搜索商品
  @Query("SELECT p FROM Product p WHERE p.status = :status AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%)")
  Page<Product> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") Product.ProductStatus status,
      Pageable pageable);

  // 根据分类和标题或描述搜索商品
  @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.status = :status AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%)")
  Page<Product> findByCategoryIdAndKeywordAndStatus(@Param("categoryId") Long categoryId,
      @Param("keyword") String keyword, @Param("status") Product.ProductStatus status, Pageable pageable);

  // 高级搜索商品
  @Query("SELECT p FROM Product p WHERE " +
         "(:keyword IS NULL OR p.title LIKE %:keyword% OR p.description LIKE %:keyword%) AND " +
         "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
         "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
         "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
         "(:location IS NULL OR p.location LIKE %:location%) AND " +
         "(:isNegotiable IS NULL OR p.isNegotiable = :isNegotiable) AND " +
         "(:isNew IS NULL OR p.isNew = :isNew) AND " +
         "(:deliveryMethod IS NULL OR p.deliveryMethod = :deliveryMethod) AND " +
         "p.status = :status")
  Page<Product> advancedSearchProducts(
      @Param("keyword") String keyword,
      @Param("categoryId") Long categoryId,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      @Param("location") String location,
      @Param("isNegotiable") Boolean isNegotiable,
      @Param("isNew") Boolean isNew,
      @Param("deliveryMethod") String deliveryMethod,
      @Param("status") Product.ProductStatus status,
      Pageable pageable);

  // 查找热门商品（按浏览量排序）
  @Query("SELECT p FROM Product p WHERE p.status = :status ORDER BY p.viewCount DESC")
  List<Product> findTopProductsByViewCount(@Param("status") Product.ProductStatus status, Pageable pageable);

  // 查找最新商品
  @Query("SELECT p FROM Product p WHERE p.status = :status ORDER BY p.createdAt DESC")
  List<Product> findTopProductsByCreatedAt(@Param("status") Product.ProductStatus status, Pageable pageable);

  // 统计商品总数
  long countByStatus(Product.ProductStatus status);

  // 统计某个卖家的商品总数
  long countBySellerIdAndStatus(Long sellerId, Product.ProductStatus status);

  // 统计某个分类的商品总数
  long countByCategoryIdAndStatus(Long categoryId, Product.ProductStatus status);
}