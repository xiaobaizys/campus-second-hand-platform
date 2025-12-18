import request from '@/utils/request'

export const assistantApi = {
  // AI聊天接口
  chat: (message: string) => {
    return request.post('/api/assistant/chat', { message })
  }
}
