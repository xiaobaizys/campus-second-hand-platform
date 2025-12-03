<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <div class="logo" @click="goToHome">
          <h1>校园二手交易平台</h1>
        </div>

        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/products" class="nav-item">商品列表</router-link>
          <router-link to="/community" class="nav-item">社区讨论</router-link>
          <router-link to="/about" class="nav-item">关于我们</router-link>
          <!-- 管理员专属导航 -->
          <router-link v-if="isAdmin" to="/admin" class="nav-item admin-nav">管理后台</router-link>
          <!-- 数据统计和系统设置已移至管理后台 -->
        </nav>

        <div class="header-actions">
          <div class="search-input-container">
            <el-input v-model="searchQuery" placeholder="搜索商品" prefix-icon="Search" class="search-input" @keyup.enter="handleSearch" @input="handleSearchInput" @focus="showSearchSuggestions = true" @blur="hideSearchSuggestions">
              <template #append>
                <el-button icon="Search" @click="handleSearch" />
              </template>
            </el-input>
            <div class="search-suggestions" v-if="showSearchSuggestions">
              <div class="suggestions-header" v-if="hotSearches.length > 0">
                <h4>热门搜索</h4>
                <div class="hot-searches">
                  <el-tag v-for="item in hotSearches" :key="item" class="hot-search-tag" @click="handleHotSearchClick(item)">
                    {{ item }}
                  </el-tag>
                </div>
              </div>

              <div class="suggestions-header" v-if="searchHistory.length > 0">
                <div class="history-header">
                  <h4>搜索历史</h4>
                  <el-button text type="primary" @click="clearSearchHistory">清空</el-button>
                </div>
                <div class="search-history">
                  <div v-for="item in searchHistory" :key="item" class="history-item" @click="handleHistoryClick(item)">
                    <el-icon><Clock /></el-icon>
                    <span>{{ item }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 主题切换按钮 -->
          <el-button :icon="isDarkMode ? Sunny : Moon" @click="toggleTheme" circle size="small" class="theme-toggle-btn" title="切换主题" />

          <div v-if="!isLoggedIn" class="auth-buttons">
            <el-button @click="goToLogin">登录</el-button>
            <el-button type="primary" @click="goToRegister">注册</el-button>
          </div>

          <div v-else class="user-menu">
            <!-- 通知图标 -->
            <NotificationIcon />

            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userAvatar" />
                <span class="username">{{ username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="cart">购物车</el-dropdown-item>
                  <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="publish">发布商品</el-dropdown-item>
                  <el-dropdown-item v-if="isAdmin" divided command="admin">管理后台</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, provide } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ArrowDown, Sunny, Moon, Search, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import NotificationIcon from '@/components/NotificationIcon.vue'

const router = useRouter()
const userStore = useUserStore()

// 搜索查询
const searchQuery = ref('')
// 搜索建议显示状态
const showSearchSuggestions = ref(false)
// 搜索历史
const searchHistory = ref<string[]>([])
const maxHistoryItems = 10

// 热门搜索（可以从后端获取，这里先使用静态数据）
const hotSearches = ref<string[]>(['手机', '笔记本电脑', '耳机', '教材', '自行车', '篮球', '吉他', '相机'])

// 主题状态管理
const isDarkMode = ref(false)

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const username = computed(() => userStore.user?.username || '')
const userAvatar = computed(() => userStore.user?.avatar || '')
const isAdmin = computed(() => userStore.isAdmin)

// 主题切换方法
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  document.documentElement.classList.toggle('dark-mode', isDarkMode.value)

  // 保存主题设置到localStorage
  localStorage.setItem('theme', isDarkMode.value ? 'dark' : 'light')
}

// 初始化主题
const initTheme = () => {
  // 从localStorage获取主题设置，如果没有则使用默认值
  const savedTheme = localStorage.getItem('theme')
  isDarkMode.value = savedTheme === 'dark'
  document.documentElement.classList.toggle('dark-mode', isDarkMode.value)
}

// 初始化搜索历史
const initSearchHistory = () => {
  try {
    const history = localStorage.getItem('searchHistory')
    if (history) {
      searchHistory.value = JSON.parse(history)
    }
  } catch (error) {
    console.error('加载搜索历史失败:', error)
  }
}

// 初始化主题和搜索历史
initTheme()
initSearchHistory()

// 提供主题状态给子组件
provide('isDarkMode', isDarkMode)
provide('toggleTheme', toggleTheme)

// 跳转到首页
const goToHome = () => {
  router.push('/')
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}

// 搜索处理
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    // 添加到搜索历史
    addToSearchHistory(searchQuery.value)
    router.push(`/products?search=${encodeURIComponent(searchQuery.value)}`)
  } else {
    router.push('/products')
  }
  showSearchSuggestions.value = false
}

// 搜索输入处理
const handleSearchInput = () => {
  if (searchQuery.value.trim()) {
    showSearchSuggestions.value = true
  } else {
    showSearchSuggestions.value = false
  }
}

// 隐藏搜索建议
const hideSearchSuggestions = () => {
  // 延迟隐藏，以便点击建议项
  setTimeout(() => {
    showSearchSuggestions.value = false
  }, 200)
}

// 添加到搜索历史
const addToSearchHistory = (keyword: string) => {
  if (!keyword.trim()) return

  // 移除已存在的相同关键词
  const index = searchHistory.value.indexOf(keyword)
  if (index !== -1) {
    searchHistory.value.splice(index, 1)
  }

  // 添加到开头
  searchHistory.value.unshift(keyword)

  // 限制历史记录数量
  if (searchHistory.value.length > maxHistoryItems) {
    searchHistory.value = searchHistory.value.slice(0, maxHistoryItems)
  }

  // 保存到本地存储
  saveSearchHistory()
}

// 保存搜索历史到本地存储
const saveSearchHistory = () => {
  try {
    localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
  } catch (error) {
    console.error('保存搜索历史失败:', error)
  }
}

// 清空搜索历史
const clearSearchHistory = () => {
  searchHistory.value = []
  saveSearchHistory()
  ElMessage.success('搜索历史已清空')
}

// 处理热门搜索点击
const handleHotSearchClick = (keyword: string) => {
  searchQuery.value = keyword
  handleSearch()
}

// 处理历史记录点击
const handleHistoryClick = (keyword: string) => {
  searchQuery.value = keyword
  handleSearch()
}

// 下拉菜单命令处理
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'cart':
      router.push('/cart')
      break
    case 'favorites':
      router.push('/favorites')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'publish':
      router.push('/publish')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      router.push('/')
      break
  }
}
</script>

<style scoped>
.app-header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo {
  cursor: pointer;
}

.logo h1 {
  font-size: 20px;
  color: #409eff;
  margin: 0;
  white-space: nowrap;
}

.nav-menu {
  display: flex;
  gap: 30px;
}

.nav-item {
  color: #303133;
  text-decoration: none;
  font-weight: 500;
  padding: 5px 0;
  border-bottom: 2px solid transparent;
  transition: color 0.3s, border-color 0.3s;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #409eff;
  border-bottom-color: #409eff;
}

/* 管理员导航样式 */
.admin-nav {
  color: #67c23a;
  font-weight: 600;
}

.admin-nav:hover,
.admin-nav.router-link-active {
  color: #85ce61;
  border-bottom-color: #67c23a;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.search-input-container {
  position: relative;
  width: 200px;
}

.search-input {
  width: 100%;
}

/* 搜索建议样式 */
.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  max-height: 400px;
  overflow-y: auto;
}

.suggestions-header {
  padding: 10px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.suggestions-header:last-child {
  border-bottom: none;
}

.suggestions-header h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #909399;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hot-searches {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hot-search-tag {
  cursor: pointer;
}

.search-history {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 0;
  cursor: pointer;
  color: #606266;
}

.history-item:hover {
  color: #409eff;
}

.auth-buttons {
  display: flex;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #303133;
}

@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 0;
  }

  .logo {
    order: 1;
    width: 100%;
    text-align: center;
    margin-bottom: 10px;
  }

  .logo h1 {
    font-size: 18px;
  }

  .nav-menu {
    order: 3;
    width: 100%;
    justify-content: center;
    margin-top: 10px;
    gap: 15px;
  }

  .nav-item {
    font-size: 14px;
  }

  .header-actions {
    order: 2;
    width: 100%;
    justify-content: space-between;
    gap: 10px;
  }

  .search-input {
    flex: 1;
    max-width: 150px;
  }

  .auth-buttons {
    gap: 5px;
  }

  .auth-buttons .el-button {
    padding: 8px 12px;
    font-size: 12px;
  }

  .user-info {
    padding: 5px;
  }

  .username {
    display: none;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 8px 0;
  }

  .logo h1 {
    font-size: 16px;
  }

  .nav-menu {
    gap: 10px;
  }

  .nav-item {
    font-size: 12px;
    padding: 3px 0;
  }

  .search-input {
    max-width: 120px;
  }

  .auth-buttons .el-button {
    padding: 6px 10px;
    font-size: 11px;
  }
}
</style>
