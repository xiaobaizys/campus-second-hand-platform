import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, userApi } from '@/api'
import type { User } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // 方法
  const login = async (username: string, password: string) => {
    try {
      const response = await authApi.login(username, password)

      // 检查响应是否包含token
      if (!response || !response.token) {
        return { success: false, error: '登录失败：响应无效' }
      }

      const { token: jwt, ...userData } = response

      // 处理角色映射，确保角色格式正确
      let role = userData.role || 'USER'
      if (role.startsWith('ROLE_')) {
        role = role.substring(5)
      }

      // 确保admin用户能被正确识别为管理员
      if (userData.username === 'admin' || role === 'ADMIN') {
        role = 'ADMIN'
      }

      // 保存token和用户信息
      token.value = jwt
      user.value = {
        ...userData,
        role: role === 'ADMIN' ? 'ADMIN' : 'USER',
      } as User
      localStorage.setItem('token', jwt)

      // 登录成功后，获取完整的用户信息
      await fetchUserProfile()

      return { success: true }
    } catch (error: any) {
      console.error('登录失败:', error)

      // 尝试从错误中获取更具体的错误信息
      let errorMessage = '用户名或密码错误'
      if (error.response && error.response.data) {
        errorMessage = error.response.data.message || error.response.data.error || error.response.data.msg || errorMessage
      } else if (error.message) {
        errorMessage = error.message
      }

      return { success: false, error: errorMessage }
    }
  }

  const register = async (userData: any) => {
    try {
      await authApi.register(userData)
      return { success: true }
    } catch (error: any) {
      console.error('注册失败:', error)

      // 尝试从错误中获取更具体的错误信息
      let errorMessage = '注册失败，请稍后再试'
      if (error.response && error.response.data) {
        errorMessage = error.response.data.message || error.response.data.error || error.response.data.msg || errorMessage
      } else if (error.message) {
        errorMessage = error.message
      }

      return { success: false, error: errorMessage }
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
  }

  const fetchUserProfile = async () => {
    try {
      console.log('开始获取用户信息，当前token:', token.value ? '存在' : '不存在')
      const userData = await userApi.getUserProfile()
      console.log('获取到的用户数据:', userData)

      // 检查响应是否有效
      if (!userData || typeof userData !== 'object') {
        throw new Error('获取用户信息失败：响应无效')
      }

      // 处理后端返回的User对象，映射到前端User类型
      let role = 'USER'

      // 尝试从role字段获取角色信息（兼容旧版本）
      if (userData.role) {
        role = userData.role

        // 处理后端返回的带有ROLE_前缀的角色，转换为前端期望的不带前缀的角色
        if (role.startsWith('ROLE_')) {
          role = role.substring(5)
        }
      }
      // 尝试从roles集合中提取角色信息
      else if (userData.roles && Array.isArray(userData.roles) && userData.roles.length > 0) {
        const firstRole = userData.roles[0]
        role = firstRole.name || firstRole

        // 处理后端返回的带有ROLE_前缀的角色，转换为前端期望的不带前缀的角色
        if (role.startsWith('ROLE_')) {
          role = role.substring(5)
        }
      }

      // 确保admin用户能被正确识别为管理员
      if (userData.username === 'admin' || role === 'ADMIN') {
        role = 'ADMIN'
      }

      const mappedUser = {
        id: userData.id,
        username: userData.username,
        email: userData.email,
        phone: userData.phoneNumber || userData.phone,
        realName: userData.realName,
        avatar: userData.avatarUrl || userData.avatar,
        avatarUrl: userData.avatarUrl || userData.avatar,
        role: role === 'ADMIN' ? 'ADMIN' : 'USER',
      }

      // 设置用户信息
      user.value = mappedUser as User
      console.log('设置用户信息后，user.value:', user.value)

      // 确保头像URL正确设置
      if (user.value && user.value.avatarUrl) {
        // 如果头像URL不是完整URL，保持原样，前端显示时会处理
        console.log('用户头像URL:', user.value.avatarUrl)
      }
    } catch (error: any) {
      console.error('获取用户信息失败:', error)

      // 如果是401错误，说明token无效，清除登录状态
      if (error.response && error.response.status === 401) {
        logout()
      }
    }
  }

  // 更新用户信息
  const updateUserProfile = async (userData: any) => {
    try {
      const response = await userApi.updateUserProfile(userData)

      // 检查响应是否有效
      if (!response) {
        throw new Error('更新用户信息失败：响应无效')
      }

      if (response.data || response) {
        user.value = response.data || response

        // 确保头像URL正确设置
        if (user.value && user.value.avatarUrl) {
          console.log('更新后的用户头像URL:', user.value.avatarUrl)
        }
      }
      return { success: true }
    } catch (error: any) {
      console.error('更新用户信息失败:', error)

      // 尝试从错误中获取更具体的错误信息
      let errorMessage = '更新用户信息失败'
      if (error.response && error.response.data) {
        errorMessage = error.response.data.message || error.response.data.error || error.response.data.msg || errorMessage
      } else if (error.message) {
        errorMessage = error.message
      }

      return { success: false, error: errorMessage }
    }
  }

  // 修改密码
  const changePassword = async (oldPassword: string, newPassword: string) => {
    try {
      await userApi.changePassword(oldPassword, newPassword)
      return { success: true }
    } catch (error: any) {
      console.error('修改密码失败:', error)

      // 尝试从错误中获取更具体的错误信息
      let errorMessage = '修改密码失败'
      if (error.response && error.response.data) {
        errorMessage = error.response.data.message || error.response.data.error || error.response.data.msg || errorMessage
      } else if (error.message) {
        errorMessage = error.message
      }

      return { success: false, error: errorMessage }
    }
  }

  // 上传头像
  const uploadAvatar = async (formData: FormData) => {
    try {
      console.log('userStore.uploadAvatar 开始上传，FormData:', formData)
      const response = await userApi.uploadAvatar(formData)
      console.log('API响应:', response)

      // 检查响应是否有效
      if (!response) {
        throw new Error('上传头像失败：响应无效')
      }

      // 后端返回的是Map<String, String>，直接从response中获取avatarUrl
      const avatarUrl = response.avatarUrl
      console.log('获取到的头像URL:', avatarUrl)

      if (!avatarUrl) {
        throw new Error('上传头像失败：未返回头像URL')
      }

      if (user.value) {
        user.value.avatarUrl = avatarUrl
        console.log('更新用户头像URL为:', avatarUrl)
      }
      return { success: true, data: { avatarUrl } }
    } catch (error: any) {
      console.error('上传头像失败:', error)

      // 尝试从错误中获取更具体的错误信息
      let errorMessage = '上传头像失败'
      if (error.response && error.response.data) {
        errorMessage = error.response.data.message || error.response.data.error || error.response.data.msg || errorMessage
      } else if (error.message) {
        errorMessage = error.message
      }

      return { success: false, error: errorMessage }
    }
  }

  // 注销账号
  const deleteAccount = async () => {
    try {
      await userApi.deleteAccount()
      logout()
      return { success: true }
    } catch (error: any) {
      console.error('注销账号失败:', error)

      // 尝试从错误中获取更具体的错误信息
      let errorMessage = '注销账号失败'
      if (error.response && error.response.data) {
        errorMessage = error.response.data.message || error.response.data.error || error.response.data.msg || errorMessage
      } else if (error.message) {
        errorMessage = error.message
      }

      return { success: false, error: errorMessage }
    }
  }

  // 获取用户数量（管理员）
  const getUserCount = async () => {
    try {
      const response = await userApi.getUserCount()
      // 处理后端返回的响应，兼容多种格式
      let count = 0

      // 检查响应是否为数字（如果后端直接返回数字）
      if (typeof response === 'number') {
        count = response
      }
      // 检查响应是否为对象，并且包含count字段
      else if (response && typeof response === 'object') {
        // 直接取count字段，因为后端返回的是 { count: 数字 } 格式
        count = response.count || 0
      }

      return count
    } catch (error: any) {
      console.error('获取用户数量失败:', error)
      // 不再抛出错误，而是返回0，避免影响其他功能
      return 0
    }
  }

  // 获取用户列表（管理员）
  const fetchUsers = async (params: any = {}) => {
    try {
      let response
      // 如果有搜索条件，使用searchUsers接口，否则使用getUserList接口
      if (params.search || params.role) {
        response = await userApi.searchUsers(params)
      } else {
        response = await userApi.getUserList(params)
      }

      console.log('获取用户列表的API响应:', response)

      // 处理后端返回的响应，兼容多种格式
      let users = []
      let total = 0

      // 检查响应是否为数组（如果后端直接返回用户数组）
      if (Array.isArray(response)) {
        users = response
        total = response.length
      }
      // 检查响应是否为对象，并且包含data和total字段
      else if (response && typeof response === 'object') {
        users = response.data || response.users || []
        total = response.total || users.length
      }

      console.log('处理后的用户列表:', users)
      console.log('处理后的用户总数:', total)

      return {
        data: users,
        total: total,
      }
    } catch (error: any) {
      console.error('获取用户列表失败:', error)
      throw error
    }
  }

  // 禁用/启用用户（管理员）
  const banUser = async (userId: number, status: boolean = false) => {
    try {
      const response = await userApi.updateUserStatus(userId, status)
      console.log('禁用/启用用户的API响应:', response)
      return response
    } catch (error: any) {
      console.error('禁用/启用用户失败:', error)
      throw error
    }
  }

  // 修改用户角色（管理员）
  const updateUserRole = async (userId: number, role: string) => {
    try {
      const response = await userApi.updateUserRole(userId, role)
      console.log('修改用户角色的API响应:', response)
      return response
    } catch (error: any) {
      console.error('修改用户角色失败:', error)
      throw error
    }
  }

  // 初始化逻辑移至main.ts中的initUserState函数

  return {
    user,
    token,
    isAuthenticated,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    fetchUserProfile,
    updateUserProfile,
    changePassword,
    uploadAvatar,
    deleteAccount,
    getUserCount,
    fetchUsers,
    banUser,
    updateUserRole,
  }
})
