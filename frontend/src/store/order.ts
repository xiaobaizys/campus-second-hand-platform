import { defineStore } from 'pinia'
import { ref } from 'vue'
import { orderApi } from '@/api/order'
import type { Order, OrderCreateDTO, OrderUpdateDTO, OrderStatistics, OrderStatus } from '@/types'

export const useOrderStore = defineStore('order', () => {
  // 状态
  const orders = ref<Order[]>([])
  const currentOrder = ref<Order | null>(null)
  const buyerOrders = ref<Order[]>([])
  const sellerOrders = ref<Order[]>([])
  const orderStatistics = ref<OrderStatistics | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  
  // 获取订单详情
  const fetchOrderById = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.getOrderById(id)
      currentOrder.value = response.data || response
      return response
    } catch (err: any) {
      error.value = err.message || '获取订单详情失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 根据订单号获取订单
  const fetchOrderByNumber = async (orderNumber: string) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.getOrderByNumber(orderNumber)
      currentOrder.value = response.data || response
      return response
    } catch (err: any) {
      error.value = err.message || '获取订单详情失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 创建订单
  const createOrder = async (orderData: OrderCreateDTO) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.createOrder(orderData)
      // 如果成功，将新订单添加到买家订单列表中
      if (response.data || response) {
        const newOrder = response.data || response
        buyerOrders.value.unshift(newOrder)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '创建订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 更新订单信息
  const updateOrder = async (id: number, orderData: OrderUpdateDTO) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.updateOrder(id, orderData)
      // 如果成功，更新列表中的订单
      if (response.data || response) {
        const updatedOrder = response.data || response
        updateOrderInLists(updatedOrder)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '更新订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 更新订单状态
  const updateOrderStatus = async (id: number, status: OrderStatus) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.updateOrderStatus(id, status)
      // 如果成功，更新列表中的订单
      if (response.data || response) {
        const updatedOrder = response.data || response
        updateOrderInLists(updatedOrder)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '更新订单状态失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 取消订单
  const cancelOrder = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.cancelOrder(id)
      // 如果成功，更新列表中的订单
      if (response.data || response) {
        const updatedOrder = response.data || response
        updateOrderInLists(updatedOrder)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '取消订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 完成订单
  const completeOrder = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.completeOrder(id)
      // 如果成功，更新列表中的订单
      if (response.data || response) {
        const updatedOrder = response.data || response
        updateOrderInLists(updatedOrder)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '完成订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 确认订单（卖家操作）
  const confirmOrder = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.confirmOrder(id)
      // 如果成功，更新列表中的订单
      if (response.data || response) {
        const updatedOrder = response.data || response
        updateOrderInLists(updatedOrder)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '确认订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 发货（卖家操作）
  const shipOrder = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.shipOrder(id)
      // 如果成功，更新列表中的订单
      if (response.data || response) {
        const updatedOrder = response.data || response
        updateOrderInLists(updatedOrder)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '发货失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 支付订单（买家操作）
  const payOrder = async (id: number, paymentMethod: string) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.payOrder(id, paymentMethod)
      // 如果成功，更新列表中的订单
      if (response.data || response) {
        const updatedOrder = response.data || response
        updateOrderInLists(updatedOrder)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '支付订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 申请退款（买家操作）
  const requestRefund = async (id: number, reason: string) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.requestRefund(id, reason)
      // 如果成功，更新列表中的订单
      if (response.data || response) {
        const updatedOrder = response.data || response
        updateOrderInLists(updatedOrder)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '申请退款失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取当前用户的订单列表（作为买家）
  const fetchBuyerOrders = async (params?: any) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.getBuyerOrders(params)
      // 处理不同格式的响应
      if (response && response.content) {
        // 如果返回的是Spring Data JPA的Page对象
        buyerOrders.value = response.content
      } else if (response && response.data) {
        // 如果返回的是包含data字段的对象
        buyerOrders.value = response.data
      } else {
        // 如果返回的是直接的订单列表
        buyerOrders.value = response
      }
      return response
    } catch (err: any) {
      error.value = err.message || '获取买家订单列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取当前用户的订单列表（作为卖家）
  const fetchSellerOrders = async (params?: any) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.getSellerOrders(params)
      // 处理不同格式的响应
      if (response && response.content) {
        // 如果返回的是Spring Data JPA的Page对象
        sellerOrders.value = response.content
      } else if (response && response.data) {
        // 如果返回的是包含data字段的对象
        sellerOrders.value = response.data
      } else {
        // 如果返回的是直接的订单列表
        sellerOrders.value = response
      }
      return response
    } catch (err: any) {
      error.value = err.message || '获取卖家订单列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取当前用户指定状态的订单列表（作为买家）
  const fetchBuyerOrdersByStatus = async (status: OrderStatus, params?: any) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.getBuyerOrdersByStatus(status, params)
      buyerOrders.value = response.data || response
      return response
    } catch (err: any) {
      error.value = err.message || '获取指定状态订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取当前用户指定状态的订单列表（作为卖家）
  const fetchSellerOrdersByStatus = async (status: OrderStatus, params?: any) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.getSellerOrdersByStatus(status, params)
      sellerOrders.value = response.data || response
      return response
    } catch (err: any) {
      error.value = err.message || '获取指定状态订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取订单统计信息（管理员）
  const fetchOrderStatistics = async () => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.getOrderStatistics()
      orderStatistics.value = response.data || response
      return response
    } catch (err: any) {
      error.value = err.message || '获取订单统计信息失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取当前用户的订单统计信息
  const fetchUserOrderStatistics = async () => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.getUserOrderStatistics()
      orderStatistics.value = response.data || response
      return response
    } catch (err: any) {
      error.value = err.message || '获取用户订单统计信息失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 清空当前订单
  const clearCurrentOrder = () => {
    currentOrder.value = null
  }
  
  // 辅助函数：更新订单列表中的订单
  const updateOrderInLists = (updatedOrder: Order) => {
    // 更新通用订单列表
    const index = orders.value.findIndex(o => o.id === updatedOrder.id)
    if (index !== -1) {
      orders.value[index] = updatedOrder
    }
    
    // 更新买家订单列表
    const buyerIndex = buyerOrders.value.findIndex(o => o.id === updatedOrder.id)
    if (buyerIndex !== -1) {
      buyerOrders.value[buyerIndex] = updatedOrder
    }
    
    // 更新卖家订单列表
    const sellerIndex = sellerOrders.value.findIndex(o => o.id === updatedOrder.id)
    if (sellerIndex !== -1) {
      sellerOrders.value[sellerIndex] = updatedOrder
    }
    
    // 如果是当前订单，也更新它
    if (currentOrder.value && currentOrder.value.id === updatedOrder.id) {
      currentOrder.value = updatedOrder
    }
  }
  
  // 获取订单数量（管理员）
  const getOrderCount = async () => {
    loading.value = true
    error.value = null
    
    try {
      const response = await orderApi.getOrderCount()
      // 处理不同格式的响应
      let count = 0
      if (typeof response === 'number') {
        count = response
      } else if (response && typeof response === 'object') {
        count = response.count || response.data?.count || 0
      }
      return count
    } catch (err: any) {
      error.value = err.message || '获取订单数量失败'
      // 不再抛出错误，而是返回0，避免影响其他功能
      return 0
    } finally {
      loading.value = false
    }
  }
  
  // 获取所有订单（管理员）
  const fetchAllOrders = async (params?: any) => {
    loading.value = true
    error.value = null
    
    try {
      // 后端的page参数从0开始，前端从1开始，需要调整
      const adjustedParams = {
        ...params,
        page: params?.page ? params.page - 1 : 0
      }
      
      const response = await orderApi.getAllOrders(adjustedParams)
      // 处理不同格式的响应
      let orderList = []
      let total = 0
      
      if (response && response.content) {
        // 如果返回的是Spring Data JPA的Page对象
        orderList = response.content
        total = response.totalElements
      } else if (response && response.data) {
        // 如果返回的是包含data和total字段的对象
        orderList = response.data
        total = response.total || 0
      } else {
        // 如果返回的是直接的订单列表
        orderList = response
        total = response.length || 0
      }
      
      // 转换字段名称，确保前端显示正确
      const formattedOrders = orderList.map((order: any) => ({
        ...order,
        productTitle: order.productName, // 后端返回productName，前端使用productTitle
        createTime: order.createdAt,      // 后端返回createdAt，前端使用createTime
        updateTime: order.updatedAt,      // 后端返回updatedAt，前端使用updateTime
        totalPrice: order.finalPrice || order.price // 总价使用finalPrice或price
      }))
      
      orders.value = formattedOrders
      return { orders: formattedOrders, total }
    } catch (err: any) {
      error.value = err.message || '获取所有订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  return {
    // 状态
    orders,
    currentOrder,
    buyerOrders,
    sellerOrders,
    orderStatistics,
    loading,
    error,
    
    // 方法
    fetchOrderById,
    fetchOrderByNumber,
    createOrder,
    updateOrder,
    updateOrderStatus,
    cancelOrder,
    completeOrder,
    confirmOrder,
    shipOrder,
    payOrder,
    requestRefund,
    fetchBuyerOrders,
    fetchSellerOrders,
    fetchBuyerOrdersByStatus,
    fetchSellerOrdersByStatus,
    fetchOrderStatistics,
    fetchUserOrderStatistics,
    clearCurrentOrder,
    getOrderCount,
    fetchAllOrders
  }
})