import request from '@/utils/request'

export const favoriteApi = {
  // 添加收藏
  addFavorite: (productId: number) => {
    return request.post(`/api/products/${productId}/favorite`)
  },
  
  // 取消收藏
  removeFavorite: (productId: number) => {
    return request.delete(`/api/products/${productId}/favorite`)
  },
  
  // 批量取消收藏
  batchRemoveFavorites: (productIds: number[]) => {
    return request.post('/api/favorites/batch-delete', { productIds })
  },
  
  // 检查是否已收藏
  checkFavorite: (productId: number) => {
    return request.get(`/api/products/${productId}/favorite/check`)
  },
  
  // 获取用户的收藏列表
  getUserFavorites: (params?: any) => {
    return request.get('/api/products/favorites', { params })
  }
}