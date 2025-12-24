package com.campus.secondhandplatform.service.impl;

import com.campus.secondhandplatform.entity.Order;
import com.campus.secondhandplatform.entity.Product;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.repository.OrderRepository;
import com.campus.secondhandplatform.repository.ProductRepository;
import com.campus.secondhandplatform.repository.UserRepository;
import com.campus.secondhandplatform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, 
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public Order createOrder(Order order, Long productId) {
        // 获取商品信息
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            return null;
        }
        Product product = productOpt.get();
        
        // 获取卖家信息（商品的所有者）
        User seller = product.getSeller();
        
        // 设置商品和卖家信息
        order.setProduct(product);
        order.setSeller(seller);
        
        // 买家信息已在Controller层设置
        
        // 生成唯一订单号
        String orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);
        
        // 如果没有设置价格，使用商品价格
        if (order.getPrice() == null) {
            order.setPrice(product.getPrice());
        }
        
        // 如果没有设置最终价格，使用商品价格
        if (order.getFinalPrice() == null) {
            order.setFinalPrice(order.getPrice());
        }
        
        // 设置初始状态
        if (order.getStatus() == null) {
            order.setStatus(Order.OrderStatus.PENDING);
        }
        
        // 设置默认支付状态
        if (order.getPaymentStatus() == null) {
            order.setPaymentStatus("UNPAID");
        }
        
        return orderRepository.save(order);
    }
    
    @Override
    public Order getOrderById(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        return orderOpt.orElse(null);
    }
    
    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        Optional<Order> orderOpt = orderRepository.findByOrderNumber(orderNumber);
        return orderOpt.orElse(null);
    }
    
    @Override
    public Order updateOrder(Long id, Order order) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return null;
        }
        
        Order existingOrder = orderOpt.get();
        // 更新订单信息
        existingOrder.setPrice(order.getPrice());
        existingOrder.setFinalPrice(order.getFinalPrice());
        existingOrder.setContactInfo(order.getContactInfo());
        existingOrder.setDeliveryAddress(order.getDeliveryAddress());
        existingOrder.setDeliveryMethod(order.getDeliveryMethod());
        existingOrder.setPaymentMethod(order.getPaymentMethod());
        existingOrder.setPaymentStatus(order.getPaymentStatus());
        existingOrder.setRemarks(order.getRemarks());
        
        return orderRepository.save(existingOrder);
    }
    
    @Override
    public Optional<Order> updateOrderStatus(Long id, Order.OrderStatus status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                });
    }
    
    @Override
    public Order cancelOrder(Long id, Long currentUserId) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return null;
        }
        
        Order order = orderOpt.get();
        // 检查权限：只有买家可以取消订单
        if (!order.getBuyer().getId().equals(currentUserId)) {
            return null;
        }
        
        // 检查订单状态：只有待确认、已确认或已支付状态的订单可以取消
        if (order.getStatus() != Order.OrderStatus.PENDING && 
            order.getStatus() != Order.OrderStatus.CONFIRMED && 
            order.getStatus() != Order.OrderStatus.PAID) {
            return null;
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
    
    @Override
    public Order completeOrder(Long id, Long currentUserId) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return null;
        }
        
        Order order = orderOpt.get();
        // 检查权限：只有买家可以完成订单
        if (!order.getBuyer().getId().equals(currentUserId)) {
            return null;
        }
        
        // 检查订单状态：只有已发货状态的订单可以完成
        if (order.getStatus() != Order.OrderStatus.SHIPPED) {
            return null;
        }
        
        order.setStatus(Order.OrderStatus.COMPLETED);
        return orderRepository.save(order);
    }
    
    @Override
    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    @Override
    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }
    
    @Override
    public Page<Order> getOrdersByBuyerId(Long buyerId, Pageable pageable) {
        return orderRepository.findByBuyerId(buyerId, pageable);
    }
    
    @Override
    public Page<Order> getOrdersBySellerId(Long sellerId, Pageable pageable) {
        return orderRepository.findBySellerId(sellerId, pageable);
    }
    
    @Override
    public Page<Order> getOrdersByBuyerIdAndStatus(Long buyerId, Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByBuyerIdAndStatus(buyerId, status, pageable);
    }
    
    @Override
    public Page<Order> getOrdersBySellerIdAndStatus(Long sellerId, Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findBySellerIdAndStatus(sellerId, status, pageable);
    }
    
    @Override
    public List<Order> getOrdersByProductId(Long productId) {
        return orderRepository.findByProductId(productId);
    }
    
    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    @Override
    public List<Order> getOrdersByBuyerIdAndDateRange(Long buyerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByBuyerIdAndCreatedAtBetween(buyerId, startDate, endDate);
    }
    
    @Override
    public List<Order> getOrdersBySellerIdAndDateRange(Long sellerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findBySellerIdAndCreatedAtBetween(sellerId, startDate, endDate);
    }
    
    @Override
    public long countOrdersByBuyerId(Long buyerId) {
        return orderRepository.countByBuyerId(buyerId);
    }
    
    @Override
    public long countOrdersBySellerId(Long sellerId) {
        return orderRepository.countBySellerId(sellerId);
    }
    
    @Override
    public long countOrdersByBuyerIdAndStatus(Long buyerId, Order.OrderStatus status) {
        return orderRepository.countByBuyerIdAndStatus(buyerId, status);
    }
    
    @Override
    public long countOrdersBySellerIdAndStatus(Long sellerId, Order.OrderStatus status) {
        return orderRepository.countBySellerIdAndStatus(sellerId, status);
    }
    
    @Override
    public BigDecimal calculateBuyerTotalAmount(Long buyerId) {
        BigDecimal amount = orderRepository.sumFinalPriceByBuyerIdAndCompleted(buyerId);
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal calculateSellerTotalAmount(Long sellerId) {
        BigDecimal amount = orderRepository.sumFinalPriceBySellerIdAndCompleted(sellerId);
        return amount != null ? amount : BigDecimal.ZERO;
    }
    
    @Override
    public List<Order> getRecentOrdersByBuyerId(Long buyerId, Pageable pageable) {
        return orderRepository.findRecentOrdersByBuyerId(buyerId, pageable);
    }
    
    @Override
    public List<Order> getRecentOrdersBySellerId(Long sellerId, Pageable pageable) {
        return orderRepository.findRecentOrdersBySellerId(sellerId, pageable);
    }
    
    @Override
    public boolean hasPermissionToViewOrder(Long orderId, Long userId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        Order order = orderOpt.get();
        // 买家和卖家都有权限查看订单
        return order.getBuyer().getId().equals(userId) || order.getSeller().getId().equals(userId);
    }
    
    @Override
    public boolean hasPermissionToModifyOrder(Long orderId, Long userId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        Order order = orderOpt.get();
        // 买家和卖家都有权限修改订单，但具体操作权限在各自的方法中检查
        return order.getBuyer().getId().equals(userId) || order.getSeller().getId().equals(userId);
    }
    
    @Override
    public String generateOrderNumber() {
        // 格式：ORD + 年月日时分秒 + 4位随机数
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        Random random = new Random();
        int randomNum = 1000 + random.nextInt(9000); // 生成1000-9999之间的随机数
        return "ORD" + timestamp + randomNum;
    }
    
    @Override
    public Order confirmOrder(Long id, Long sellerId) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return null;
        }
        
        Order order = orderOpt.get();
        // 检查权限：只有卖家可以确认订单
        if (!order.getSeller().getId().equals(sellerId)) {
            return null;
        }
        
        // 检查订单状态：只有待确认状态的订单可以确认
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            return null;
        }
        
        order.setStatus(Order.OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }
    
    @Override
    public Order shipOrder(Long id, Long sellerId) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return null;
        }
        
        Order order = orderOpt.get();
        // 检查权限：只有卖家可以发货
        if (!order.getSeller().getId().equals(sellerId)) {
            return null;
        }
        
        // 检查订单状态：只有已确认状态的订单可以发货
        if (order.getStatus() != Order.OrderStatus.CONFIRMED) {
            return null;
        }
        
        order.setStatus(Order.OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }
    
    @Override
    public Order payOrder(Long id, Long buyerId, String paymentMethod) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return null;
        }
        
        Order order = orderOpt.get();
        // 检查权限：只有买家可以支付订单
        if (!order.getBuyer().getId().equals(buyerId)) {
            return null;
        }
        
        // 检查订单状态：允许支付待处理或已确认状态的订单
        if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.CONFIRMED) {
            return null;
        }
        
        order.setStatus(Order.OrderStatus.PAID);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("PAID");
        return orderRepository.save(order);
    }
    
    @Override
    public Order requestRefund(Long id, Long buyerId, String reason) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return null;
        }
        
        Order order = orderOpt.get();
        // 检查权限：只有买家可以申请退款
        if (!order.getBuyer().getId().equals(buyerId)) {
            return null;
        }
        
        // 检查订单状态：只有已支付、已发货状态的订单可以申请退款
        if (order.getStatus() != Order.OrderStatus.PAID && 
            order.getStatus() != Order.OrderStatus.SHIPPED) {
            return null;
        }
        
        order.setStatus(Order.OrderStatus.REFUNDED);
        order.setRemarks("退款申请：" + reason);
        return orderRepository.save(order);
    }
    
    @Override
    public boolean isOrderParticipant(Long orderId, String username) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        Order order = orderOpt.get();
        return order.getBuyer().getUsername().equals(username) || 
               order.getSeller().getUsername().equals(username);
    }
    
    @Override
    public boolean isOrderParticipantByNumber(String orderNumber, String username) {
        Optional<Order> orderOpt = orderRepository.findByOrderNumber(orderNumber);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        Order order = orderOpt.get();
        return order.getBuyer().getUsername().equals(username) || 
               order.getSeller().getUsername().equals(username);
    }
    
    @Override
    public boolean isOrderBuyer(Long orderId, String username) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        Order order = orderOpt.get();
        return order.getBuyer().getUsername().equals(username);
    }
    
    @Override
    public boolean isOrderSeller(Long orderId, String username) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        Order order = orderOpt.get();
        return order.getSeller().getUsername().equals(username);
    }
    
    @Override
    public Map<String, Long> getOrderCountStats() {
        Map<String, Long> stats = new HashMap<>();
        
        // 总订单数
        stats.put("total", orderRepository.count());
        
        // 各状态订单数
        stats.put("pending", orderRepository.countByStatus(Order.OrderStatus.PENDING));
        stats.put("confirmed", orderRepository.countByStatus(Order.OrderStatus.CONFIRMED));
        stats.put("paid", orderRepository.countByStatus(Order.OrderStatus.PAID));
        stats.put("shipped", orderRepository.countByStatus(Order.OrderStatus.SHIPPED));
        stats.put("completed", orderRepository.countByStatus(Order.OrderStatus.COMPLETED));
        stats.put("cancelled", orderRepository.countByStatus(Order.OrderStatus.CANCELLED));
        stats.put("refunded", orderRepository.countByStatus(Order.OrderStatus.REFUNDED));
        
        return stats;
    }
    
    @Override
    public Map<String, BigDecimal> getOrderAmountStats() {
        Map<String, BigDecimal> stats = new HashMap<>();
        
        // 总订单金额
        BigDecimal totalAmount = orderRepository.sumFinalPrice();
        stats.put("total", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        // 各状态订单金额
        BigDecimal pendingAmount = orderRepository.sumFinalPriceByStatus(Order.OrderStatus.PENDING);
        stats.put("pending", pendingAmount != null ? pendingAmount : BigDecimal.ZERO);
        
        BigDecimal confirmedAmount = orderRepository.sumFinalPriceByStatus(Order.OrderStatus.CONFIRMED);
        stats.put("confirmed", confirmedAmount != null ? confirmedAmount : BigDecimal.ZERO);
        
        BigDecimal paidAmount = orderRepository.sumFinalPriceByStatus(Order.OrderStatus.PAID);
        stats.put("paid", paidAmount != null ? paidAmount : BigDecimal.ZERO);
        
        BigDecimal shippedAmount = orderRepository.sumFinalPriceByStatus(Order.OrderStatus.SHIPPED);
        stats.put("shipped", shippedAmount != null ? shippedAmount : BigDecimal.ZERO);
        
        BigDecimal completedAmount = orderRepository.sumFinalPriceByStatus(Order.OrderStatus.COMPLETED);
        stats.put("completed", completedAmount != null ? completedAmount : BigDecimal.ZERO);
        
        BigDecimal cancelledAmount = orderRepository.sumFinalPriceByStatus(Order.OrderStatus.CANCELLED);
        stats.put("cancelled", cancelledAmount != null ? cancelledAmount : BigDecimal.ZERO);
        
        BigDecimal refundedAmount = orderRepository.sumFinalPriceByStatus(Order.OrderStatus.REFUNDED);
        stats.put("refunded", refundedAmount != null ? refundedAmount : BigDecimal.ZERO);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getUserOrderStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 作为买家的统计
        Map<String, Object> buyerStats = new HashMap<>();
        buyerStats.put("totalOrders", countOrdersByBuyerId(userId));
        buyerStats.put("pendingOrders", countOrdersByBuyerIdAndStatus(userId, Order.OrderStatus.PENDING));
        buyerStats.put("confirmedOrders", countOrdersByBuyerIdAndStatus(userId, Order.OrderStatus.CONFIRMED));
        buyerStats.put("paidOrders", countOrdersByBuyerIdAndStatus(userId, Order.OrderStatus.PAID));
        buyerStats.put("shippedOrders", countOrdersByBuyerIdAndStatus(userId, Order.OrderStatus.SHIPPED));
        buyerStats.put("completedOrders", countOrdersByBuyerIdAndStatus(userId, Order.OrderStatus.COMPLETED));
        buyerStats.put("cancelledOrders", countOrdersByBuyerIdAndStatus(userId, Order.OrderStatus.CANCELLED));
        buyerStats.put("refundedOrders", countOrdersByBuyerIdAndStatus(userId, Order.OrderStatus.REFUNDED));
        buyerStats.put("totalAmount", calculateBuyerTotalAmount(userId));
        stats.put("asBuyer", buyerStats);
        
        // 作为卖家的统计
        Map<String, Object> sellerStats = new HashMap<>();
        sellerStats.put("totalOrders", countOrdersBySellerId(userId));
        sellerStats.put("pendingOrders", countOrdersBySellerIdAndStatus(userId, Order.OrderStatus.PENDING));
        sellerStats.put("confirmedOrders", countOrdersBySellerIdAndStatus(userId, Order.OrderStatus.CONFIRMED));
        sellerStats.put("paidOrders", countOrdersBySellerIdAndStatus(userId, Order.OrderStatus.PAID));
        sellerStats.put("shippedOrders", countOrdersBySellerIdAndStatus(userId, Order.OrderStatus.SHIPPED));
        sellerStats.put("completedOrders", countOrdersBySellerIdAndStatus(userId, Order.OrderStatus.COMPLETED));
        sellerStats.put("cancelledOrders", countOrdersBySellerIdAndStatus(userId, Order.OrderStatus.CANCELLED));
        sellerStats.put("refundedOrders", countOrdersBySellerIdAndStatus(userId, Order.OrderStatus.REFUNDED));
        sellerStats.put("totalAmount", calculateSellerTotalAmount(userId));
        stats.put("asSeller", sellerStats);
        
        return stats;
    }
    

}