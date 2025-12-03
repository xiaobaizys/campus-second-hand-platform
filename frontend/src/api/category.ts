import request from '@/utils/request'

export const categoryApi = {
  // 获取所有分类
  getAllCategories: () => {
    return request.get('/api/categories')
  },
  
  // 获取分类详情
  getCategoryById: (id: number) => {
    return request.get(`/api/categories/${id}`)
  },
  
  // 创建分类（管理员）
  createCategory: (categoryData: any) => {
    return request.post('/api/categories', categoryData)
  },
  
  // 更新分类（管理员）
  updateCategory: (id: number, categoryData: any) => {
    return request.put(`/api/categories/${id}`, categoryData)
  },
  
  // 删除分类（管理员）
  deleteCategory: (id: number) => {
    return request.delete(`/api/categories/${id}`)
  }
}