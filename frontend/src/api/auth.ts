import request from '@/utils/request'

export const authApi = {
  // 登录
  login: (username: string, password: string) => {
    return request.post('/api/auth/login', { username, password })
  },
  
  // 注册
  register: (userData: any) => {
    return request.post('/api/auth/register', userData)
  },
  
  // 获取用户信息
  getProfile: () => {
    return request.get('/api/auth/profile')
  }
}