import { useUserStore } from '@/store/user'

export const initUserState = async () => {
  console.log('开始初始化用户状态...')
  const userStore = useUserStore()
  
  // 如果有token但没有用户信息，则获取用户信息
  if (userStore.token) {
    console.log('发现token，尝试获取用户信息...')
    try {
      await userStore.fetchUserProfile()
      console.log('用户信息获取成功')
    } catch (error) {
      console.error('初始化用户状态失败:', error)
      // 如果获取用户信息失败，可能是token无效，清除登录状态
      userStore.logout()
    }
  } else {
    console.log('未发现token，跳过用户信息获取')
  }
}