import request from '@/utils/request'

export interface Comment {
  id: number
  user: {
    id: number
    username: string
    avatar: string
  }
  product: {
    id: number
    title: string
  }
  content: string
  rating: number
  createdAt: Date
}

export const commentApi = {
  // 获取商品评论
  getComments: (productId: number, params?: any) => {
    return request.get(`/api/products/${productId}/comments`, { params })
  },
  
  // 添加评论
  createComment: (data: {
    productId: number
    content: string
    rating?: number
  }) => {
    return request.post('/api/comments', data)
  },
  
  // 更新评论
  updateComment: (id: number, data: {
    content: string
    rating?: number
  }) => {
    return request.put(`/api/comments/${id}`, data)
  },
  
  // 删除评论
  deleteComment: (id: number) => {
    return request.delete(`/api/comments/${id}`)
  },
  
  // 获取用户评论
  getUserComments: (params?: any) => {
    return request.get('/api/user/comments', { params })
  }
}