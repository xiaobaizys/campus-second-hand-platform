import request from '@/utils/request'

export interface CartItem {
  id: number
  productId: number
  title: string
  price: number
  quantity: number
  stock: number
  status: string
  images: string[]
  sellerAvatar: string
  sellerName: string
}

export const cartApi = {
  // 获取购物车列表
  getCartItems: (userId: number) => {
    return request.get<CartItem[]>('/api/cart', { params: { userId } })
  },

  // 添加商品到购物车
  addToCart: (userId: number, productId: number, quantity: number) => {
    return request.post<CartItem>('/api/cart', null, { params: { userId, productId, quantity } })
  },

  // 更新购物车商品数量
  updateCartItemQuantity: (cartItemId: number, quantity: number) => {
    return request.put<CartItem>(`/api/cart/${cartItemId}`, null, { params: { quantity } })
  },

  // 移除购物车商品
  removeCartItem: (cartItemId: number) => {
    return request.delete(`/api/cart/${cartItemId}`)
  },

  // 清空购物车
  clearCart: (userId: number) => {
    return request.delete('/api/cart', { params: { userId } })
  },
  
  // 检查商品是否已经在购物车中
  checkCartStatus: (userId: number, productId: number) => {
    return request.get<boolean>('/api/cart/check', { params: { userId, productId } })
  },
}
