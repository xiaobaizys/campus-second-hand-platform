import request from '@/utils/request'

// 平台参数类型定义
export interface PlatformParam {
  id?: number
  paramKey: string
  paramValue: string
  description?: string
}

// 平台参数API
export const platformParamApi = {
  // 获取所有平台参数
  getAllParams: () => request.get('/api/platform-params'),

  // 根据ID获取平台参数
  getParamById: (id: number) => request.get(`/api/platform-params/${id}`),

  // 根据Key获取平台参数
  getParamByKey: (key: string) => request.get(`/api/platform-params/key/${key}`),

  // 创建平台参数
  createParam: (param: PlatformParam) => request.post('/api/platform-params', param),

  // 更新平台参数
  updateParam: (id: number, param: PlatformParam) => request.put(`/api/platform-params/${id}`, param),

  // 删除平台参数
  deleteParam: (id: number) => request.delete(`/api/platform-params/${id}`),

  // 根据Key删除平台参数
  deleteParamByKey: (key: string) => request.delete(`/api/platform-params/key/${key}`),
}
