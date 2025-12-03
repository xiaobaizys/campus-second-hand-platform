import request from '@/utils/request'

export interface Discussion {
  id: number
  title: string
  content: string
  user: {
    id: number
    username: string
    avatar: string
    isCampusVerified: boolean
  }
  campus: string
  type: string
  tags: string[]
  images: string[]
  videos: string[]
  likeCount: number
  commentCount: number
  favoriteCount: number
  viewCount: number
  liked: boolean
  favorited: boolean
  createdAt: string
  comments?: Comment[]
  relatedProduct?: {
    id: number
    title: string
    price: number
    images: string[]
  }
}

export interface Comment {
  id: number
  user: {
    id: number
    username: string
    avatar: string
  }
  content: string
  createdAt: string
}

export const communityApi = {
  // 获取讨论列表
  getDiscussions: (params?: any) => {
    // 转换参数名，将sort转换为sortBy
    const transformedParams = { ...params }
    if (transformedParams.sort) {
      transformedParams.sortBy = transformedParams.sort
      delete transformedParams.sort
    }
    // 删除campus参数，因为后端不支持
    delete transformedParams.campus
    // 处理tags参数，后端只支持单个tag，所以只传递第一个tag
    if (transformedParams.tags && Array.isArray(transformedParams.tags) && transformedParams.tags.length > 0) {
      transformedParams.tag = transformedParams.tags[0]
    }
    delete transformedParams.tags
    // 正常返回，让响应拦截器处理响应数据
    return request.get('/api/discussions', { params: transformedParams })
  },

  // 获取讨论详情
  getDiscussionDetail: (id: number) => {
    return request.get(`/api/discussions/${id}`)
  },

  // 创建讨论
  createDiscussion: (data: { title: string; content: string; type: string; campus: string; tags: string[]; images?: string[]; videos?: string[]; relatedProductId?: number | null }) => {
    return request.post('/api/discussions', data)
  },

  // 更新讨论
  updateDiscussion: (
    id: number,
    data: {
      title: string
      content: string
      tags: string[]
    }
  ) => {
    return request.put(`/api/discussions/${id}`, data)
  },

  // 删除讨论
  deleteDiscussion: (id: number) => {
    return request.delete(`/api/discussions/${id}`)
  },

  // 点赞讨论
  likeDiscussion: (id: number) => {
    return request.post(`/api/discussions/${id}/like`)
  },

  // 取消点赞讨论（使用与点赞相同的POST请求，后端会处理切换）
  unlikeDiscussion: (id: number) => {
    return request.post(`/api/discussions/${id}/like`)
  },

  // 获取讨论评论
  getComments: (discussionId: number, params?: any) => {
    const page = params?.page || 0;
    const size = params?.size || 10;
    return request.get(`/api/discussions/${discussionId}/comments`, { 
      params: { 
        page, 
        size, 
        sortBy: 'createdAt', 
        sortDir: 'desc' 
      } 
    })
  },

  // 添加评论
  createComment: (
    discussionId: number,
    data: {
      content: string
    }
  ) => {
    return request.post('/api/comments', {
      discussionId,
      content: data.content
    })
  },

  // 更新评论
  updateComment: (
    commentId: number,
    data: {
      content: string
    }
  ) => {
    return request.put(`/api/comments/${commentId}`, data)
  },

  // 删除评论
  deleteComment: (id: number) => {
    return request.delete(`/api/comments/${id}`)
  },

  // 上传图片
  uploadImage: (formData: FormData) => {
    return request.post('/api/products/upload', formData)
  },

  // 获取热门标签
  getHotTags: () => {
    return request.get('/api/discussions/hot-tags')
  },
}
