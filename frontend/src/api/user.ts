import request from '@/utils/request'

export const userApi = {
  // 获取用户信息
  getUserProfile: () => {
    return request.get('/api/users/profile')
  },
  
  // 更新用户信息
  updateUserProfile: (userData: any) => {
    return request.put('/api/users/profile', userData)
  },
  
  // 修改密码
  changePassword: (oldPassword: string, newPassword: string) => {
    return request.put('/api/users/password', { oldPassword, newPassword })
  },
  
  // 上传头像
  uploadAvatar: (formData: FormData) => {
    return request.post('/api/users/avatar', formData)
  },
  
  // 获取用户列表（管理员）
  getUserList: (params?: any) => {
    return request.get('/api/users/admin/list', { params })
  },
  
  // 获取用户详情（管理员）
  getUserById: (id: number) => {
    return request.get(`/api/users/admin/${id}`)
  },
  
  // 禁用/启用用户（管理员）
  updateUserStatus: (id: number, status: boolean) => {
    return request.patch(`/api/users/admin/${id}/status`, null, { 
      params: { status } 
    })
  },
  
  // 注销账号
  deleteAccount: () => {
    return request.delete('/api/users/account')
  },
  
  // 获取用户数量（管理员）
  getUserCount: () => {
    return request.get('/api/users/admin/count')
  },
  
  // 搜索用户（管理员）
  searchUsers: (params?: any) => {
    return request.get('/api/users/admin/search', { params })
  },
  
  // 修改用户角色（管理员）
  updateUserRole: (id: number, role: string) => {
    return request.patch(`/api/users/admin/${id}/role`, { role })
  }
}