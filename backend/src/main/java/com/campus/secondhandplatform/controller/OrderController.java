package com.campus.secondhandplatform.controller;

import com.campus.secondhandplatform.dto.OrderCreateDTO;
import com.campus.secondhandplatform.dto.OrderDTO;
import com.campus.secondhandplatform.dto.OrderUpdateDTO;
import com.campus.secondhandplatform.entity.Order;
import com.campus.secondhandplatform.entity.User;
import com.campus.secondhandplatform.repository.UserRepository;
import com.campus.secondhandplatform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    private final UserRepository userRepository;
    
    @Autowired
    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("用户未认证");
        }

        String name = authentication.getName();

        // 尝试先将name作为用户ID处理
        try {
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            // 如果不是数字，说明是用户名，需要根据用户名查找用户ID
            User user = userRepository.findByUsername(name)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + name));
            return user.getId();
        }
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setProductId(order.getProduct().getId());
        dto.setProductName(order.getProduct().getTitle());
        dto.setProductImage(order.getProduct().getImageUrl());
        dto.setBuyerId(order.getBuyer().getId());
        dto.setBuyerName(order.getBuyer().getUsername());
        dto.setSellerId(order.getSeller().getId());
        dto.setSellerName(order.getSeller().getUsername());
        dto.setPrice(order.getPrice());
        dto.setFinalPrice(order.getFinalPrice());
        dto.setStatus(order.getStatus());
        dto.setContactInfo(order.getContactInfo());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryMethod(order.getDeliveryMethod());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setRemarks(order.getRemarks());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }
    
    private Order convertToEntity(OrderCreateDTO dto) {
        Order order = new Order();
        order.setPrice(dto.getPrice());
        order.setContactInfo(dto.getContactInfo());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setDeliveryMethod(dto.getDeliveryMethod());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setRemarks(dto.getRemarks());
        
        // 设置买家信息
        Long currentUserId = getCurrentUserId();
        User buyer = userRepository.findById(currentUserId).orElse(null);
        order.setBuyer(buyer);
        
        return order;
    }
    
    /**
     * 创建新订单
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        // 将DTO转换为实体（包含买家信息）
        Order order = convertToEntity(orderCreateDTO);
        
        // 保存订单
        Order savedOrder = orderService.createOrder(order, orderCreateDTO.getProductId());
        
        return ResponseEntity.ok(convertToDTO(savedOrder));
    }
    
    /**
     * 根据ID获取订单详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        
        // 检查权限
        if (!orderService.hasPermissionToViewOrder(id, currentUserId)) {
            return ResponseEntity.status(403).build();
        }
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(order));
    }
    
    /**
     * 根据订单号获取订单详情
     */
    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> getOrderByOrderNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        
        // 检查订单是否存在
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 检查权限
        Long currentUserId = getCurrentUserId();
        if (!orderService.hasPermissionToViewOrder(order.getId(), currentUserId)) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(convertToDTO(order));
    }
    
    /**
     * 更新订单信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or @orderService.isOrderParticipant(#id, authentication.name)")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable Long id, 
            @RequestBody OrderUpdateDTO orderUpdateDTO) {
        
        // 将DTO转换为实体
        Order orderDetails = new Order();
        orderDetails.setFinalPrice(orderUpdateDTO.getFinalPrice());
        orderDetails.setContactInfo(orderUpdateDTO.getContactInfo());
        orderDetails.setDeliveryAddress(orderUpdateDTO.getDeliveryAddress());
        orderDetails.setDeliveryMethod(orderUpdateDTO.getDeliveryMethod());
        orderDetails.setPaymentMethod(orderUpdateDTO.getPaymentMethod());
        orderDetails.setPaymentStatus(orderUpdateDTO.getPaymentStatus());
        orderDetails.setRemarks(orderUpdateDTO.getRemarks());
        
        Order updatedOrder = orderService.updateOrder(id, orderDetails);
        if (updatedOrder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(updatedOrder));
    }
    
    /**
     * 更新订单状态
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id, 
            @RequestParam Order.OrderStatus status) {
        
        Long currentUserId = getCurrentUserId();
        
        // 检查权限
        if (!orderService.hasPermissionToModifyOrder(id, currentUserId)) {
            return ResponseEntity.status(403).build();
        }
        
        Optional<Order> updatedOrder = orderService.updateOrderStatus(id, status);
        return updatedOrder.map(o -> ResponseEntity.ok(convertToDTO(o)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 取消订单
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        
        Order order = orderService.cancelOrder(id, currentUserId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(order));
    }
    
    /**
     * 完成订单
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> completeOrder(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        
        Order order = orderService.completeOrder(id, currentUserId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(order));
    }
    
    /**
     * 确认订单（卖家操作）
     */
    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> confirmOrder(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        
        Order order = orderService.confirmOrder(id, currentUserId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(order));
    }
    
    /**
     * 发货（卖家操作）
     */
    @PatchMapping("/{id}/ship")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> shipOrder(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        
        Order order = orderService.shipOrder(id, currentUserId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(order));
    }
    
    /**
     * 支付订单（买家操作）
     */
    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> payOrder(
            @PathVariable Long id,
            @RequestParam String paymentMethod) {
        Long currentUserId = getCurrentUserId();
        
        Order order = orderService.payOrder(id, currentUserId, paymentMethod);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(order));
    }
    
    /**
     * 申请退款（买家操作）
     */
    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> requestRefund(
            @PathVariable Long id,
            @RequestParam String reason) {
        Long currentUserId = getCurrentUserId();
        
        Order order = orderService.requestRefund(id, currentUserId, reason);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(order));
    }
    
    /**
     * 获取当前用户的订单列表（作为买家）
     */
    @GetMapping("/buyer")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getOrdersByBuyer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Long currentUserId = getCurrentUserId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Order> orders = orderService.getOrdersByBuyerId(currentUserId, pageable);
        Page<OrderDTO> orderDTOs = orders.map(this::convertToDTO);
        
        return ResponseEntity.ok(orderDTOs);
    }
    
    /**
     * 获取当前用户的订单列表（作为卖家）
     */
    @GetMapping("/seller")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getOrdersBySeller(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Long currentUserId = getCurrentUserId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Order> orders = orderService.getOrdersBySellerId(currentUserId, pageable);
        Page<OrderDTO> orderDTOs = orders.map(this::convertToDTO);
        
        return ResponseEntity.ok(orderDTOs);
    }
    
    /**
     * 获取当前用户指定状态的订单列表（作为买家）
     */
    @GetMapping("/buyer/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getOrdersByBuyerAndStatus(
            @PathVariable Order.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Long currentUserId = getCurrentUserId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Order> orders = orderService.getOrdersByBuyerIdAndStatus(currentUserId, status, pageable);
        Page<OrderDTO> orderDTOs = orders.map(this::convertToDTO);
        
        return ResponseEntity.ok(orderDTOs);
    }
    
    /**
     * 获取当前用户指定状态的订单列表（作为卖家）
     */
    @GetMapping("/seller/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getOrdersBySellerAndStatus(
            @PathVariable Order.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Long currentUserId = getCurrentUserId();
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Order> orders = orderService.getOrdersBySellerIdAndStatus(currentUserId, status, pageable);
        Page<OrderDTO> orderDTOs = orders.map(this::convertToDTO);
        
        return ResponseEntity.ok(orderDTOs);
    }
    
    /**
     * 获取所有订单（管理员）
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Order> orders = orderService.getAllOrders(pageable);
        Page<OrderDTO> orderDTOs = orders.map(this::convertToDTO);
        
        return ResponseEntity.ok(orderDTOs);
    }
    
    /**
     * 根据状态获取订单（管理员）
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Order> orders = orderService.getOrdersByStatus(status, pageable);
        Page<OrderDTO> orderDTOs = orders.map(this::convertToDTO);
        
        return ResponseEntity.ok(orderDTOs);
    }
    
    /**
     * 获取指定时间范围内的订单（管理员）
     */
    @GetMapping("/admin/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderDTOs);
    }
    
    /**
     * 获取订单统计信息（管理员）
     */
    @GetMapping("/admin/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计各状态订单数量
        Map<String, Long> statusCounts = new HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = orderService.getOrdersByStatus(status, Pageable.unpaged()).getTotalElements();
            statusCounts.put(status.toString(), count);
        }
        statistics.put("statusCounts", statusCounts);
        
        // 总订单数
        long totalOrders = orderService.getAllOrders(Pageable.unpaged()).getTotalElements();
        statistics.put("totalOrders", totalOrders);
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 获取当前用户的订单统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserOrderStatistics() {
        Long currentUserId = getCurrentUserId();
        Map<String, Object> statistics = new HashMap<>();
        
        // 作为买家的统计
        Map<String, Long> buyerStatusCounts = new HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = orderService.countOrdersByBuyerIdAndStatus(currentUserId, status);
            buyerStatusCounts.put(status.toString(), count);
        }
        statistics.put("buyerStatusCounts", buyerStatusCounts);
        
        // 作为卖家的统计
        Map<String, Long> sellerStatusCounts = new HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = orderService.countOrdersBySellerIdAndStatus(currentUserId, status);
            sellerStatusCounts.put(status.toString(), count);
        }
        statistics.put("sellerStatusCounts", sellerStatusCounts);
        
        // 总金额
        BigDecimal buyerTotalAmount = orderService.calculateBuyerTotalAmount(currentUserId);
        BigDecimal sellerTotalAmount = orderService.calculateSellerTotalAmount(currentUserId);
        statistics.put("buyerTotalAmount", buyerTotalAmount);
        statistics.put("sellerTotalAmount", sellerTotalAmount);
        
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> getOrderCountStats() {
        return orderService.getOrderCountStats();
    }

    @GetMapping("/stats/amount")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, BigDecimal> getOrderAmountStats() {
        return orderService.getOrderAmountStats();
    }

    @GetMapping("/stats/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Map<String, Object> getUserOrderStats(@PathVariable Long userId) {
        return orderService.getUserOrderStats(userId);
    }

    /**
     * 管理员获取订单数量
     */
    @GetMapping("/admin/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getOrderCount() {
        long count = orderService.getAllOrders(Pageable.unpaged()).getTotalElements();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}