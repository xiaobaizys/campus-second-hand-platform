import request from '@/utils/request'

export const assistantApi = {
  // AI聊天接口
  chat: (message: string, sessionId?: string) => {
    return request.post('/api/assistant/chat', { message, sessionId })
  },
  
  // 获取聊天记录
  getHistory: (sessionId: string) => {
    return request.get('/api/assistant/history', { params: { sessionId } })
  },
  
  // 清空聊天记录
  clearHistory: (sessionId: string) => {
    return request.delete('/api/assistant/history', { params: { sessionId } })
  }
}
