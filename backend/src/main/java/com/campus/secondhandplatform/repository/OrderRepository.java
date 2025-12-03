package com.campus.secondhandplatform.repository;

import com.campus.secondhandplatform.entity.Order;
import com.campus.secondhandplatform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 根据买家ID查找订单
     */
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);
    
    /**
     * 根据卖家ID查找订单
     */
    Page<Order> findBySellerId(Long sellerId, Pageable pageable);
    
    /**
     * 根据买家ID和订单状态查找订单
     */
    Page<Order> findByBuyerIdAndStatus(Long buyerId, Order.OrderStatus status, Pageable pageable);
    
    /**
     * 根据卖家ID和订单状态查找订单
     */
    Page<Order> findBySellerIdAndStatus(Long sellerId, Order.OrderStatus status, Pageable pageable);
    
    /**
     * 根据商品ID查找订单
     */
    List<Order> findByProductId(Long productId);
    
    /**
     * 根据商品ID和订单状态查找订单
     */
    List<Order> findByProductIdAndStatus(Long productId, Order.OrderStatus status);
    
    /**
     * 根据订单状态查找订单
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    /**
     * 根据订单状态统计订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") Order.OrderStatus status);
    
    /**
     * 查找指定时间范围内的订单
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * 查找指定时间范围内买家ID的订单
     */
    @Query("SELECT o FROM Order o WHERE o.buyer.id = :buyerId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByBuyerIdAndCreatedAtBetween(@Param("buyerId") Long buyerId,
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    /**
     * 查找指定时间范围内卖家ID的订单
     */
    @Query("SELECT o FROM Order o WHERE o.seller.id = :sellerId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findBySellerIdAndCreatedAtBetween(@Param("sellerId") Long sellerId,
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    /**
     * 统计买家订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.buyer.id = :buyerId")
    long countByBuyerId(@Param("buyerId") Long buyerId);
    
    /**
     * 统计卖家订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.seller.id = :sellerId")
    long countBySellerId(@Param("sellerId") Long sellerId);
    
    /**
     * 统计买家指定状态的订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.buyer.id = :buyerId AND o.status = :status")
    long countByBuyerIdAndStatus(@Param("buyerId") Long buyerId, @Param("status") Order.OrderStatus status);
    
    /**
     * 统计卖家指定状态的订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.seller.id = :sellerId AND o.status = :status")
    long countBySellerIdAndStatus(@Param("sellerId") Long sellerId, @Param("status") Order.OrderStatus status);
    
    /**
     * 计算买家订单总金额
     */
    @Query("SELECT SUM(o.finalPrice) FROM Order o WHERE o.buyer.id = :buyerId AND o.status = 'COMPLETED'")
    BigDecimal sumFinalPriceByBuyerIdAndCompleted(@Param("buyerId") Long buyerId);
    
    /**
     * 计算卖家订单总金额
     */
    @Query("SELECT SUM(o.finalPrice) FROM Order o WHERE o.seller.id = :sellerId AND o.status = 'COMPLETED'")
    BigDecimal sumFinalPriceBySellerIdAndCompleted(@Param("sellerId") Long sellerId);
    
    /**
     * 计算所有订单总金额
     */
    @Query("SELECT SUM(o.finalPrice) FROM Order o")
    BigDecimal sumFinalPrice();
    
    /**
     * 计算指定状态订单总金额
     */
    @Query("SELECT SUM(o.finalPrice) FROM Order o WHERE o.status = :status")
    BigDecimal sumFinalPriceByStatus(@Param("status") Order.OrderStatus status);
    
    /**
     * 查找买家最近订单
     */
    @Query("SELECT o FROM Order o WHERE o.buyer.id = :buyerId ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersByBuyerId(@Param("buyerId") Long buyerId, Pageable pageable);
    
    /**
     * 查找卖家最近订单
     */
    @Query("SELECT o FROM Order o WHERE o.seller.id = :sellerId ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);
    
    /**
     * 根据买家ID查找订单（按创建时间倒序）
     */
    Page<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId, Pageable pageable);
    
    /**
     * 根据卖家ID查找订单（按创建时间倒序）
     */
    Page<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId, Pageable pageable);
    
    /**
     * 检查用户是否参与过订单（买家或卖家）
     */
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o WHERE o.buyer.id = :userId OR o.seller.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}