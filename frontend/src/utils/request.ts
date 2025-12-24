import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 30000, // 增加超时时间到30秒，适应AI请求
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 直接从localStorage获取token，避免在拦截器中使用useUserStore()
    const token = localStorage.getItem('token')
    if (token && token.trim()) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 如果是FormData，让浏览器自动设置Content-Type
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 如果响应数据有data字段，返回data，否则返回整个响应
    return response.data && typeof response.data === 'object' && 'data' in response.data 
      ? response.data.data 
      : response.data
  },
  (error) => {
    // 处理401未授权错误
    if (error.response && error.response.status === 401) {
      // 直接清除localStorage中的token，避免在拦截器中使用useUserStore()
      localStorage.removeItem('token')
      ElMessage.error('登录已过期，请重新登录')
      window.location.href = '/login'
      return Promise.reject(error)
    }
    
    // 对于获取统计数据的API，不显示403错误信息
    const url = error.config?.url || ''
    const isStatsApi = url.includes('/api/users/admin/count') || 
                      url.includes('/api/products/admin/count') || 
                      url.includes('/api/orders/admin/count')
    
    // 显示错误信息，但忽略统计数据的403错误
    if (error.response && error.response.status === 403 && isStatsApi) {
      // 不显示错误信息，只在控制台打印
      console.warn('获取统计数据失败:', error.message)
      return Promise.reject(error)
    }
    
    // 显示其他错误信息
    let message = '请求失败'
    if (error.response) {
      // 服务器返回了错误状态码
      if (error.response.data) {
        // 尝试从不同可能的字段获取错误信息
        message = error.response.data.message || 
                  error.response.data.error || 
                  error.response.data.msg || 
                  `请求失败 (${error.response.status})`
      } else {
        message = `请求失败 (${error.response.status})`
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      message = '网络连接失败，请检查网络设置'
    } else {
      // 其他错误
      message = error.message || '未知错误'
    }
    
    ElMessage.error(message)
    
    return Promise.reject(error)
  }
)

export default request