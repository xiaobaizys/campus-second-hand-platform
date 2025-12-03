import request from '@/utils/request'

// 角色类型定义
export interface Role {
  id?: number
  name: string
  description?: string
}

// 角色API
export const roleApi = {
  // 获取所有角色
  getAllRoles: () => request.get('/api/roles'),

  // 根据ID获取角色
  getRoleById: (id: number) => request.get(`/api/roles/${id}`),

  // 创建角色
  createRole: (role: Role) => request.post('/api/roles', role),

  // 更新角色
  updateRole: (id: number, role: Role) => request.put(`/api/roles/${id}`, role),

  // 删除角色
  deleteRole: (id: number) => request.delete(`/api/roles/${id}`),
}
