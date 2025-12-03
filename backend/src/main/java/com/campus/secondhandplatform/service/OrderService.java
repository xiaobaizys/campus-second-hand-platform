package com.campus.secondhandplatform.service;

import com.campus.secondhandplatform.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    
    /**
     * 创建新订单
     */
    Order createOrder(Order order, Long productId);
    
    /**
     * 根据ID获取订单
     */
    Order getOrderById(Long id);
    
    /**
     * 根据订单号获取订单
     */
    Order getOrderByOrderNumber(String orderNumber);
    
    /**
     * 更新订单信息
     */
    Order updateOrder(Long id, Order order);
    
    /**
     * 更新订单状态
     */
    Optional<Order> updateOrderStatus(Long id, Order.OrderStatus status);
    
    /**
     * 取消订单
     */
    Order cancelOrder(Long id, Long currentUserId);
    
    /**
     * 完成订单
     */
    Order completeOrder(Long id, Long currentUserId);
    
    /**
     * 删除订单
     */
    boolean deleteOrder(Long id);
    
    /**
     * 获取所有订单（管理员）
     */
    Page<Order> getAllOrders(Pageable pageable);
    
    /**
     * 根据订单状态获取订单（管理员）
     */
    Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable);
    
    /**
     * 获取买家订单列表
     */
    Page<Order> getOrdersByBuyerId(Long buyerId, Pageable pageable);
    
    /**
     * 获取卖家订单列表
     */
    Page<Order> getOrdersBySellerId(Long sellerId, Pageable pageable);
    
    /**
     * 获取买家指定状态的订单列表
     */
    Page<Order> getOrdersByBuyerIdAndStatus(Long buyerId, Order.OrderStatus status, Pageable pageable);
    
    /**
     * 获取卖家指定状态的订单列表
     */
    Page<Order> getOrdersBySellerIdAndStatus(Long sellerId, Order.OrderStatus status, Pageable pageable);
    
    /**
     * 获取商品相关订单
     */
    List<Order> getOrdersByProductId(Long productId);
    
    /**
     * 获取指定时间范围内的订单
     */
    List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取买家指定时间范围内的订单
     */
    List<Order> getOrdersByBuyerIdAndDateRange(Long buyerId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取卖家指定时间范围内的订单
     */
    List<Order> getOrdersBySellerIdAndDateRange(Long sellerId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 统计买家订单数量
     */
    long countOrdersByBuyerId(Long buyerId);
    
    /**
     * 统计卖家订单数量
     */
    long countOrdersBySellerId(Long sellerId);
    
    /**
     * 统计买家指定状态的订单数量
     */
    long countOrdersByBuyerIdAndStatus(Long buyerId, Order.OrderStatus status);
    
    /**
     * 统计卖家指定状态的订单数量
     */
    long countOrdersBySellerIdAndStatus(Long sellerId, Order.OrderStatus status);
    
    /**
     * 计算买家已完成订单总金额
     */
    BigDecimal calculateBuyerTotalAmount(Long buyerId);
    
    /**
     * 计算卖家已完成订单总金额
     */
    BigDecimal calculateSellerTotalAmount(Long sellerId);
    
    /**
     * 获取买家最近订单
     */
    List<Order> getRecentOrdersByBuyerId(Long buyerId, Pageable pageable);
    
    /**
     * 获取卖家最近订单
     */
    List<Order> getRecentOrdersBySellerId(Long sellerId, Pageable pageable);
    
    /**
     * 检查用户是否有权限查看订单
     */
    boolean hasPermissionToViewOrder(Long orderId, Long userId);
    
    /**
     * 检查用户是否有权限修改订单
     */
    boolean hasPermissionToModifyOrder(Long orderId, Long userId);
    
    /**
     * 生成唯一订单号
     */
    String generateOrderNumber();
    
    /**
     * 确认订单（卖家确认）
     */
    Order confirmOrder(Long id, Long sellerId);
    
    /**
     * 发货（卖家操作）
     */
    Order shipOrder(Long id, Long sellerId);
    
    /**
     * 支付订单（买家操作）
     */
    Order payOrder(Long id, Long buyerId, String paymentMethod);
    
    /**
     * 申请退款（买家操作）
     */
    Order requestRefund(Long id, Long buyerId, String reason);
    
    /**
     * 检查用户是否是订单参与者
     */
    boolean isOrderParticipant(Long orderId, String username);
    
    /**
     * 检查用户是否是订单参与者（通过订单号）
     */
    boolean isOrderParticipantByNumber(String orderNumber, String username);
    
    /**
     * 检查用户是否是订单买家
     */
    boolean isOrderBuyer(Long orderId, String username);
    
    /**
     * 检查用户是否是订单卖家
     */
    boolean isOrderSeller(Long orderId, String username);
    
    /**
     * 获取订单数量统计
     */
    Map<String, Long> getOrderCountStats();
    
    /**
     * 获取订单金额统计
     */
    Map<String, BigDecimal> getOrderAmountStats();
    
    /**
     * 获取用户订单统计
     */
    Map<String, Object> getUserOrderStats(Long userId);
}