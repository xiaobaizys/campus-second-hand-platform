import request from '@/utils/request'

export const orderApi = {
  // 创建订单
  createOrder: (orderData: any) => {
    return request.post('/api/orders', orderData)
  },
  
  // 获取订单详情
  getOrderById: (id: number) => {
    return request.get(`/api/orders/${id}`)
  },
  
  // 根据订单号获取订单
  getOrderByNumber: (orderNumber: string) => {
    return request.get(`/api/orders/number/${orderNumber}`)
  },
  
  // 更新订单信息
  updateOrder: (id: number, orderData: any) => {
    return request.put(`/api/orders/${id}`, orderData)
  },
  
  // 更新订单状态
  updateOrderStatus: (id: number, status: string) => {
    return request.patch(`/api/orders/${id}/status`, null, { 
      params: { status } 
    })
  },
  
  // 取消订单
  cancelOrder: (id: number) => {
    return request.patch(`/api/orders/${id}/cancel`)
  },
  
  // 完成订单
  completeOrder: (id: number) => {
    return request.patch(`/api/orders/${id}/complete`)
  },
  
  // 确认订单（卖家操作）
  confirmOrder: (id: number) => {
    return request.patch(`/api/orders/${id}/confirm`)
  },
  
  // 发货（卖家操作）
  shipOrder: (id: number) => {
    return request.patch(`/api/orders/${id}/ship`)
  },
  
  // 支付订单（买家操作）
  payOrder: (id: number, paymentMethod: string) => {
    return request.patch(`/api/orders/${id}/pay`, null, { 
      params: { paymentMethod } 
    })
  },
  
  // 申请退款（买家操作）
  requestRefund: (id: number, reason: string) => {
    return request.patch(`/api/orders/${id}/refund`, null, { 
      params: { reason } 
    })
  },
  
  // 获取当前用户的订单列表（作为买家）
  getBuyerOrders: (params?: any) => {
    return request.get('/api/orders/buyer', { params })
  },
  
  // 获取当前用户的订单列表（作为卖家）
  getSellerOrders: (params?: any) => {
    return request.get('/api/orders/seller', { params })
  },
  
  // 获取当前用户指定状态的订单列表（作为买家）
  getBuyerOrdersByStatus: (status: string, params?: any) => {
    return request.get(`/api/orders/buyer/status/${status}`, { params })
  },
  
  // 获取当前用户指定状态的订单列表（作为卖家）
  getSellerOrdersByStatus: (status: string, params?: any) => {
    return request.get(`/api/orders/seller/status/${status}`, { params })
  },
  
  // 获取订单统计信息（管理员）
  getOrderStatistics: () => {
    return request.get('/api/orders/admin/statistics')
  },
  
  // 获取当前用户的订单统计信息
  getUserOrderStatistics: () => {
    return request.get('/api/orders/statistics')
  },
  
  // 获取订单数量（管理员）
  getOrderCount: () => {
    return request.get('/api/orders/admin/count')
  },
  
  // 获取所有订单（管理员）
  getAllOrders: (params?: any) => {
    return request.get('/api/orders/admin/all', { params })
  }
}