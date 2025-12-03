<template>
  <div class="notification-icon-wrapper" ref="notificationIconWrapper">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
      <el-button 
        class="notification-button" 
        :icon="Bell" 
        circle 
        @click="toggleNotificationPanel"
      />
    </el-badge>
    
    <!-- 通知面板 -->
    <div v-if="showNotificationPanel" class="notification-panel" ref="notificationPanel">
      <div class="notification-header">
        <h3>消息通知</h3>
        <el-button 
          v-if="unreadCount > 0" 
          type="text" 
          size="small" 
          @click="markAllAsRead"
        >
          全部已读
        </el-button>
      </div>
      
      <div class="notification-list" v-if="notifications.length > 0">
        <div 
          v-for="notification in notifications.slice(0, 5)" 
          :key="notification.id" 
          class="notification-item"
          :class="{ unread: !notification.read }"
          @click="handleNotificationClick(notification)"
        >
          <div class="notification-icon">
            <el-icon :size="16" :color="getNotificationColor(notification.type)">
              <component :is="getNotificationIcon(notification.type)" />
            </el-icon>
          </div>
          
          <div class="notification-content">
            <div class="notification-title">{{ notification.title }}</div>
            <div class="notification-message">{{ notification.message }}</div>
            <div class="notification-time">{{ formatTime(notification.createdAt) }}</div>
          </div>
          
          <div class="notification-status">
            <el-tag v-if="!notification.read" type="danger" size="small">未读</el-tag>
          </div>
        </div>
      </div>
      
      <div v-else class="notification-empty">
        <el-empty description="暂无消息" :image-size="60" />
      </div>
      
      <div class="notification-footer">
        <el-button type="text" @click="viewAllNotifications">查看全部</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Bell, 
  ChatDotRound, 
  Goods, 
  Star, 
  Warning,
  CircleCheck
} from '@element-plus/icons-vue'

// 定义通知类型
interface Notification {
  id: string
  type: 'system' | 'message' | 'order' | 'favorite' | 'review'
  title: string
  message: string
  read: boolean
  createdAt: Date
  relatedId?: string // 关联的ID，如商品ID、订单ID等
}

const router = useRouter()

// 响应式数据
const showNotificationPanel = ref(false)
const notifications = ref<Notification[]>([])
const notificationPanel = ref<HTMLElement | null>(null)
const notificationIconWrapper = ref<HTMLElement | null>(null)

// 计算属性
const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

// 获取通知图标
const getNotificationIcon = (type: string) => {
  switch (type) {
    case 'system': return Bell
    case 'message': return ChatDotRound
    case 'order': return Goods
    case 'favorite': return Star
    case 'review': return CircleCheck
    default: return Bell
  }
}

// 获取通知颜色
const getNotificationColor = (type: string) => {
  switch (type) {
    case 'system': return '#909399'
    case 'message': return '#409eff'
    case 'order': return '#67c23a'
    case 'favorite': return '#e6a23c'
    case 'review': return '#f56c6c'
    default: return '#909399'
  }
}

// 格式化时间
const formatTime = (date: Date) => {
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  // 小于1分钟
  if (diff < 60 * 1000) {
    return '刚刚'
  }
  
  // 小于1小时
  if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 1000))}分钟前`
  }
  
  // 小于1天
  if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  }
  
  // 小于7天
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  }
  
  // 超过7天显示具体日期
  return date.toLocaleDateString('zh-CN')
}

// 切换通知面板显示状态
const toggleNotificationPanel = () => {
  showNotificationPanel.value = !showNotificationPanel.value
}

// 关闭通知面板
const closeNotificationPanel = () => {
  showNotificationPanel.value = false
}

// 点击外部关闭面板
const handleClickOutside = (event: MouseEvent) => {
  // 检查点击是否在通知图标包装器内部，如果是则不关闭面板
  if (notificationIconWrapper.value && notificationIconWrapper.value.contains(event.target as Node)) {
    return
  }
  
  // 检查点击是否在通知面板外部，如果是则关闭面板
  if (notificationPanel.value && !notificationPanel.value.contains(event.target as Node)) {
    closeNotificationPanel()
  }
}

// 处理通知点击
const handleNotificationClick = (notification: Notification) => {
  // 标记为已读
  if (!notification.read) {
    notification.read = true
    // 这里应该调用API更新已读状态
  }
  
  // 关闭面板
  closeNotificationPanel()
  
  // 根据通知类型和关联ID进行跳转
  switch (notification.type) {
    case 'message':
      // 跳转到消息页面
      router.push('/profile?tab=messages')
      break
    case 'order':
      // 跳转到订单详情页
      if (notification.relatedId) {
        router.push(`/orders/${notification.relatedId}`)
      }
      break
    case 'favorite':
      // 跳转到商品详情页
      if (notification.relatedId) {
        router.push(`/products/${notification.relatedId}`)
      }
      break
    case 'review':
      // 跳转到评价页面
      if (notification.relatedId) {
        router.push(`/products/${notification.relatedId}`)
      }
      break
  }
}

// 标记全部为已读
const markAllAsRead = () => {
  notifications.value.forEach(notification => {
    notification.read = true
  })
  // 这里应该调用API批量更新已读状态
  ElMessage.success('已将所有消息标记为已读')
}

// 查看所有通知
const viewAllNotifications = () => {
  closeNotificationPanel()
  router.push('/profile?tab=messages')
}

// 初始化通知数据
const initNotifications = () => {
  // 模拟数据，实际项目中应该从API获取
  const now = new Date()
  notifications.value = [
    {
      id: '1',
      type: 'message',
      title: '新消息',
      message: '张三向您咨询了关于"二手手机"的问题',
      read: false,
      createdAt: new Date(now.getTime() - 10 * 60 * 1000), // 10分钟前
      relatedId: '123'
    },
    {
      id: '2',
      type: 'order',
      title: '订单状态更新',
      message: '您的订单"202305160001"已确认，请尽快发货',
      read: false,
      createdAt: new Date(now.getTime() - 30 * 60 * 1000), // 30分钟前
      relatedId: '456'
    },
    {
      id: '3',
      type: 'favorite',
      title: '商品降价提醒',
      message: '您收藏的"MacBook Pro"已降价至¥7500',
      read: true,
      createdAt: new Date(now.getTime() - 2 * 60 * 60 * 1000), // 2小时前
      relatedId: '789'
    },
    {
      id: '4',
      type: 'review',
      title: '收到新评价',
      message: '李四对您的交易给出了5星好评',
      read: true,
      createdAt: new Date(now.getTime() - 1 * 24 * 60 * 60 * 1000), // 1天前
      relatedId: '101112'
    },
    {
      id: '5',
      type: 'system',
      title: '系统通知',
      message: '平台将于5月20日进行系统维护，预计持续2小时',
      read: true,
      createdAt: new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000) // 3天前
    }
  ]
}

// 初始化
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  initNotifications()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.notification-icon-wrapper {
  position: relative;
  display: inline-block;
}

.notification-badge {
  margin-right: 10px;
}

.notification-button {
  border: none;
  background-color: transparent;
  color: #606266;
  font-size: 18px;
}

.notification-button:hover {
  color: #409eff;
  background-color: #f5f7fa;
}

.notification-panel {
  position: absolute;
  top: 40px;
  right: 0;
  width: 360px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1001;
  overflow: hidden;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
}

.notification-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #f0f9ff;
}

.notification-icon {
  margin-right: 12px;
  margin-top: 2px;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.notification-message {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-status {
  margin-left: 8px;
}

.notification-empty {
  padding: 20px 0;
  text-align: center;
}

.notification-footer {
  padding: 10px 16px;
  text-align: center;
  border-top: 1px solid #ebeef5;
}

@media (max-width: 768px) {
  .notification-panel {
    width: 320px;
    right: -100px;
  }
}
</style>