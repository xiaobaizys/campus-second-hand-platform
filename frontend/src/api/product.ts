import request from '@/utils/request'

export const productApi = {
  // 获取商品列表
  getProducts: (params?: {
    page?: number
    pageSize?: number
    category?: string
    search?: string
    sortBy?: string
    sortDir?: string
  }) => {
    // 处理参数名称映射
    const mappedParams = {
      ...params,
      // 将前端的category参数映射为后端的categoryId参数
      categoryId: params?.category,
      // 将前端的search参数映射为后端的keyword参数
      keyword: params?.search,
      // 移除原始参数
      category: undefined,
      search: undefined
    }
    
    return request.get('/api/products', { params: mappedParams })
  },
  
  // 获取商品详情
  getProductById: (id: number) => {
    return request.get(`/api/products/${id}`)
  },
  
  // 搜索商品
  searchProducts: (keyword: string, params?: any) => {
    return request.get('/api/products/search', { 
      params: { keyword, ...params } 
    })
  },
  
  // 高级搜索商品
  advancedSearchProducts: (params?: {
    page?: number
    pageSize?: number
    keyword?: string
    categoryId?: number
    minPrice?: number
    maxPrice?: number
    location?: string
    isNegotiable?: boolean
    isNew?: boolean
    deliveryMethod?: string
    sortBy?: string
    sortDir?: string
  }) => {
    return request.get('/api/products/advanced-search', { params })
  },
  
  // 按分类获取商品
  getProductsByCategory: (categoryId: number, params?: any) => {
    return request.get(`/api/products/category/${categoryId}`, { params })
  },
  
  // 获取用户发布的商品
  getUserProducts: (userId?: number, params?: any) => {
    const url = userId ? `/api/products/seller/${userId}` : '/api/products/my'
    return request.get(url, { params }).then(response => {
      // 确保返回的是数据数组
      return response.data || response
    })
  },
  
  // 创建商品
  createProduct: (productData: any) => {
    return request.post('/api/products', productData)
  },
  
  // 更新商品
  updateProduct: (id: number, productData: any) => {
    return request.put(`/api/products/${id}`, productData)
  },
  
  // 删除商品
  deleteProduct: (id: number) => {
    return request.delete(`/api/products/${id}`)
  },
  
  // 上传商品图片
  uploadImage: (file: File) => {
    const formData = new FormData()
    formData.append('image', file)
    
    return request.post('/api/products/upload-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  
  // 获取用户收藏的商品
  getFavoriteProducts: () => {
    return request.get('/api/products/favorites')
  },
  
  // 添加收藏
  addToFavorites: (productId: number) => {
    return request.post(`/api/products/${productId}/favorite`)
  },
  
  // 取消收藏
  removeFromFavorites: (productId: number) => {
    return request.delete(`/api/products/${productId}/favorite`)
  },
  
  // 获取商品数量（管理员）
  getProductCount: () => {
    return request.get('/api/products/admin/count')
  }
}