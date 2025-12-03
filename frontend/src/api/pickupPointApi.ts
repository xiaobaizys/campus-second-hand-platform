import request from '@/utils/request'

// 自提点类型定义
export interface PickupPoint {
  id?: number
  name: string
  address: string
  contact: string
  phone: string
  isActive?: boolean
}

// 自提点API
export const pickupPointApi = {
  // 获取所有自提点
  getAllPickupPoints: () => request.get('/api/pickup-points'),

  // 获取活跃自提点
  getActivePickupPoints: () => request.get('/api/pickup-points/active'),

  // 根据ID获取自提点
  getPickupPointById: (id: number) => request.get(`/api/pickup-points/${id}`),

  // 创建自提点
  createPickupPoint: (pickupPoint: PickupPoint) => request.post('/api/pickup-points', pickupPoint),

  // 更新自提点
  updatePickupPoint: (id: number, pickupPoint: PickupPoint) => request.put(`/api/pickup-points/${id}`, pickupPoint),

  // 删除自提点
  deletePickupPoint: (id: number) => request.delete(`/api/pickup-points/${id}`),

  // 更新自提点状态
  updatePickupPointStatus: (id: number, isActive: boolean) => request.put(`/api/pickup-points/${id}/status`, {}, { params: { isActive } }),
}
