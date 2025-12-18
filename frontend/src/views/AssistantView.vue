<template>
  <div class="assistant-container">
    <AppHeader />
    <main class="assistant-main">
      <div class="container">
        <div class="assistant-title">
          <h1>校园助手</h1>
          <p>欢迎使用校园二手交易平台的AI助手，有什么问题可以随时问我哦！</p>
        </div>

        <div class="chat-container">
          <!-- 聊天记录 -->
          <div class="chat-messages" ref="chatMessages">
            <!-- 系统欢迎消息 -->
            <div class="message system-message">
              <div class="message-content">
                <div class="message-header">
                  <el-avatar src="" class="assistant-avatar">AI</el-avatar>
                  <span class="message-sender">校园助手</span>
                  <span class="message-time">{{ currentTime }}</span>
                </div>
                <div class="message-text">您好！我是校园二手交易平台的AI助手，很高兴为您服务。请问有什么可以帮助您的吗？</div>
              </div>
            </div>

            <!-- 用户和助手的聊天记录 -->
            <div v-for="(message, index) in messages" :key="index" :class="['message', message.role === 'user' ? 'user-message' : 'assistant-message']">
              <div class="message-content">
                <div class="message-header">
                  <el-avatar :src="message.role === 'user' ? userAvatar : ''" :class="message.role === 'user' ? 'user-avatar' : 'assistant-avatar'">
                    {{ message.role === 'user' ? 'U' : 'AI' }}
                  </el-avatar>
                  <span class="message-sender">{{ message.role === 'user' ? '我' : '校园助手' }}</span>
                  <span class="message-time">{{ formatTime(message.timestamp) }}</span>
                </div>
                <div class="message-text">{{ message.content }}</div>
              </div>
            </div>

            <!-- 加载状态 -->
            <div v-if="loading" class="message assistant-message">
              <div class="message-content">
                <div class="message-header">
                  <el-avatar class="assistant-avatar">AI</el-avatar>
                  <span class="message-sender">校园助手</span>
                  <span class="message-time">{{ currentTime }}</span>
                </div>
                <div class="message-text loading-text">
                  <el-skeleton :rows="3" animated />
                </div>
              </div>
            </div>
          </div>

          <!-- 消息输入区域 -->
          <div class="message-input-area">
            <el-input v-model="inputMessage" type="textarea" :rows="3" placeholder="请输入您的问题..." resize="none" @keyup.enter.exact="sendMessage" @keyup.enter.shift="inputMessage += '\n'" />
            <div class="input-actions">
              <el-button type="primary" @click="sendMessage" :loading="loading"> 发送 </el-button>
            </div>
          </div>
        </div>
      </div>
    </main>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useUserStore } from '@/store/user'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { assistantApi } from '@/api/assistant'

// 用户信息
const userStore = useUserStore()
const userAvatar = computed(() => userStore.user?.avatar || '')

// 聊天相关
const messages = ref<Array<{ role: 'user' | 'assistant'; content: string; timestamp: number }>>([])
const inputMessage = ref('')
const loading = ref(false)
const chatMessages = ref<HTMLElement | null>(null)

// 当前时间
const currentTime = computed(() => {
  const now = new Date()
  return now.toLocaleTimeString()
})

// 格式化时间
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString()
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  const messageContent = inputMessage.value.trim()
  inputMessage.value = ''

  // 添加用户消息到聊天记录
  messages.value.push({
    role: 'user',
    content: messageContent,
    timestamp: Date.now(),
  })

  // 滚动到底部
  await scrollToBottom()

  // 显示加载状态
  loading.value = true

  try {
    // 调用后端API获取AI回复
    const response = await assistantApi.chat(messageContent)

    // 添加AI回复到聊天记录
    messages.value.push({
      role: 'assistant',
      content: response.content || '抱歉，我暂时无法回答这个问题，请稍后再试。',
      timestamp: Date.now(),
    })
  } catch (error) {
    console.error('Failed to get AI response:', error)
    messages.value.push({
      role: 'assistant',
      content: '抱歉，服务器出现问题，请稍后再试。',
      timestamp: Date.now(),
    })
  } finally {
    loading.value = false
    // 滚动到底部
    await scrollToBottom()
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  }
}

// 组件挂载时滚动到底部
onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.assistant-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.assistant-main {
  flex: 1;
  padding: 40px 0;
  position: relative;
  overflow: hidden;
}

/* 添加装饰性元素 */
.assistant-main::before,
.assistant-main::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  z-index: 0;
}

.assistant-main::before {
  width: 300px;
  height: 300px;
  top: -50px;
  left: -50px;
}

.assistant-main::after {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
}

.container {
  position: relative;
  z-index: 1;
}

.assistant-title {
  text-align: center;
  margin-bottom: 40px;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.assistant-title h1 {
  font-size: 36px;
  margin-bottom: 15px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.assistant-title p {
  font-size: 18px;
  margin: 0;
  opacity: 0.9;
  font-weight: 400;
}

.chat-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  height: 650px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.chat-messages {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cGF0aCBkPSJNMCAwaDYwdjYwSDB6IiBmaWxsPSIjZmZmZmZmMjAiLz48cGF0aCBkPSJNNTMgNThIMTd2LTJIMTZWMThoMXYxSDE3VjVaIiBmaWxsPSIjZmZmZmZmMzAiLz48L2c+PC9zdmc+')
    repeat;
  background-color: #fafafa;
}

/* 美化滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  transition: all 0.3s ease;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #5a6fd8 0%, #6a4290 100%);
  transform: scale(1.1);
}

.message {
  display: flex;
  gap: 12px;
  animation: messageSlideIn 0.3s ease-out;
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  justify-content: flex-end;
}

.assistant-message,
.system-message {
  justify-content: flex-start;
}

.message-content {
  max-width: 75%;
  padding: 16px 20px;
  border-radius: 20px;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.message-content:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.user-message .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-bottom-right-radius: 6px;
  border-bottom-left-radius: 20px;
}

.assistant-message .message-content {
  background: #ffffff;
  color: #333;
  border-bottom-left-radius: 6px;
  border-bottom-right-radius: 20px;
  border: 1px solid #e0e0e0;
}

.system-message .message-content {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
  border-bottom-left-radius: 6px;
  border-bottom-right-radius: 20px;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 12px;
  opacity: 0.8;
}

.user-message .message-header {
  justify-content: flex-end;
}

.assistant-message .message-header,
.system-message .message-header {
  justify-content: flex-start;
}

.message-sender {
  font-weight: 600;
  font-size: 13px;
}

.user-message .message-sender {
  color: rgba(255, 255, 255, 0.9);
}

.assistant-message .message-sender {
  color: #667eea;
}

.system-message .message-sender {
  color: rgba(255, 255, 255, 0.9);
}

.message-time {
  font-size: 11px;
  opacity: 0.7;
}

.user-message .message-time {
  color: rgba(255, 255, 255, 0.8);
}

.assistant-message .message-time {
  color: #999;
}

.system-message .message-time {
  color: rgba(255, 255, 255, 0.8);
}

.message-text {
  font-size: 15px;
  line-height: 1.6;
  word-wrap: break-word;
  font-weight: 400;
}

/* 美化头像 */
.user-avatar,
.assistant-avatar {
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: #fff !important;
}

.assistant-avatar {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%) !important;
  color: #fff !important;
}

.user-avatar:hover,
.assistant-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 美化输入区域 */
.message-input-area {
  padding: 24px 30px;
  background: #ffffff;
  border-top: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 美化文本域 */
:deep(.el-textarea) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

:deep(.el-textarea:hover) {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

:deep(.el-textarea__inner) {
  border: none;
  resize: none;
  border-radius: 16px;
  padding: 16px 20px;
  font-size: 15px;
  line-height: 1.5;
  background: #fafafa;
  transition: all 0.3s ease;
}

:deep(.el-textarea__inner:focus) {
  background: #fff;
  box-shadow: inset 0 0 0 2px #667eea;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 美化按钮 */
:deep(.el-button) {
  border-radius: 50px;
  padding: 12px 32px;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

:deep(.el-button-primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

:deep(.el-button-primary:hover) {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4290 100%);
}

/* 美化加载状态 */
.loading-text {
  min-height: 60px;
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-skeleton) {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loadingAnimation 1.5s infinite;
}

@keyframes loadingAnimation {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .assistant-main {
    padding: 20px 0;
  }

  .assistant-title h1 {
    font-size: 28px;
  }

  .assistant-title p {
    font-size: 16px;
  }

  .chat-container {
    height: calc(100vh - 200px);
    margin: 0 16px;
    border-radius: 12px;
  }

  .chat-messages {
    padding: 20px;
    gap: 16px;
  }

  .message-content {
    max-width: 85%;
    padding: 14px 18px;
  }

  .message-text {
    font-size: 14px;
  }

  .message-input-area {
    padding: 20px;
  }

  :deep(.el-button) {
    padding: 10px 24px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .assistant-title {
    margin-bottom: 20px;
  }

  .assistant-title h1 {
    font-size: 24px;
  }

  .chat-container {
    height: calc(100vh - 180px);
    margin: 0 12px;
  }

  .chat-messages {
    padding: 16px;
  }

  .message-content {
    max-width: 90%;
    padding: 12px 16px;
  }

  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .user-message .message-header {
    align-items: flex-end;
  }
}
</style>
