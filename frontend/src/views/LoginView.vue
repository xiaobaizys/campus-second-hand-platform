<template>
  <div class="login-container">
    <div class="login-content">
      <!-- 左侧宣传区域 -->
      <div class="login-banner">
        <div class="banner-content">
          <div class="platform-logo">
            <el-icon size="60" color="#fff"><Shop /></el-icon>
          </div>
          <h1 class="platform-title">校园二手交易平台</h1>
          <p class="platform-subtitle">让闲置物品流转起来，创造更多价值</p>
          <div class="platform-features">
            <div class="feature-item">
              <el-icon size="24" color="#fff"><CircleCheck /></el-icon>
              <span>安全可靠</span>
            </div>
            <div class="feature-item">
              <el-icon size="24" color="#fff"><Location /></el-icon>
              <span>校内交易</span>
            </div>
            <div class="feature-item">
              <el-icon size="24" color="#fff"><Money /></el-icon>
              <span>价格实惠</span>
            </div>
            <div class="feature-item">
              <el-icon size="24" color="#fff"><ChatLineSquare /></el-icon>
              <span>即时沟通</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="login-form">
        <div class="login-header">
          <h2>欢迎回来</h2>
          <p>登录您的账号，继续体验校园二手交易</p>
        </div>

        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="0" size="large">
          <el-form-item prop="username">
            <div class="username-input-container">
              <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
              <el-select v-model="selectedUser" placeholder="快速选择" @change="handleUserSelect" class="user-select">
                <el-option label="普通用户" value="test" />
                <el-option label="管理员" value="admin" />
              </el-select>
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
          </el-form-item>

          <el-form-item>
            <div class="remember-me">
              <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="loading" class="login-button" @click="handleLogin">
              <el-icon><Right /></el-icon>
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <p>还没有账号？<router-link to="/register">立即注册</router-link></p>
          <div class="login-tips">
            <span>测试账号：test / password</span>
            <span>管理员账号：admin / password</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/store/user'
import { Shop, CircleCheck, Location, Money, ChatLineSquare, Right } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const rememberMe = ref(false)
const selectedUser = ref('')

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
})

// 用户预设信息
const userPresets = {
  test: {
    username: 'test',
    password: 'password'
  },
  admin: {
    username: 'admin',
    password: 'password'
  }
}

// 表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
}

// 处理用户选择
const handleUserSelect = (username: string) => {
  if (userPresets[username]) {
    loginForm.username = userPresets[username].username
    loginForm.password = userPresets[username].password
  }
}

// 从localStorage获取保存的登录信息
const loadSavedCredentials = () => {
  const savedUsername = localStorage.getItem('savedUsername')
  const savedPassword = localStorage.getItem('savedPassword')
  const savedRemember = localStorage.getItem('rememberMe') === 'true'

  if (savedUsername && savedPassword && savedRemember) {
    loginForm.username = savedUsername
    loginForm.password = savedPassword
    rememberMe.value = savedRemember
  }
}

// 保存登录信息到localStorage
const saveCredentials = () => {
  if (rememberMe.value) {
    localStorage.setItem('savedUsername', loginForm.username)
    localStorage.setItem('savedPassword', loginForm.password)
    localStorage.setItem('rememberMe', 'true')
  } else {
    // 如果不记住密码，清除保存的信息
    localStorage.removeItem('savedUsername')
    localStorage.removeItem('savedPassword')
    localStorage.setItem('rememberMe', 'false')
  }
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const result = await userStore.login(loginForm.username, loginForm.password)
        if (result.success) {
          // 保存或清除登录信息
          saveCredentials()

          ElMessage.success('登录成功')

          // 如果有重定向地址，跳转到重定向地址
          const redirect = route.query.redirect as string
          router.push(redirect || '/')
        } else {
          ElMessage.error(result.error || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败，请稍后再试')
      } finally {
        loading.value = false
      }
    }
  })
}

// 页面加载时检查是否有保存的登录信息
onMounted(() => {
  loadSavedCredentials()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.login-content {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background-color: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 左侧宣传区域 */
.login-banner {
  flex: 1;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.banner-content {
  max-width: 400px;
}

.platform-logo {
  margin-bottom: 30px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.platform-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 15px;
  line-height: 1.2;
}

.platform-subtitle {
  font-size: 18px;
  margin-bottom: 40px;
  opacity: 0.9;
  line-height: 1.5;
}

.platform-features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 40px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  backdrop-filter: blur(10px);
  transition: transform 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.15);
}

.feature-item span {
  font-size: 14px;
  font-weight: 500;
}

/* 右侧登录表单 */
.login-form {
  width: 450px;
  padding: 60px 40px;
  background-color: #fff;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
  font-weight: 700;
}

.login-header p {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(106, 17, 203, 0.3);
  background: linear-gradient(135deg, #5a0fb8 0%, #1e69d1 100%);
}

.login-button:active {
  transform: translateY(0);
}

.remember-me {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  color: #606266;
}

.login-footer a {
  color: #409eff;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.3s ease;
}

.login-footer a:hover {
  color: #66b1ff;
}

.username-input-container {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.username-input-container .el-input {
  flex: 1;
}

.user-select {
  width: 120px;
}

.login-tips {
  margin-top: 20px;
  font-size: 13px;
  color: #909399;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.login-tips span {
  display: block;
}

/* 表单样式优化 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(106, 17, 203, 0.2);
  border-color: #6a11cb;
}

:deep(.el-select__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-select__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-select__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(106, 17, 203, 0.2);
  border-color: #6a11cb;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #6a11cb;
  border-color: #6a11cb;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #6a11cb;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .login-content {
    flex-direction: column;
    max-width: 500px;
  }
  
  .login-banner {
    width: 100%;
    padding: 40px 30px;
  }
  
  .platform-title {
    font-size: 28px;
  }
  
  .platform-subtitle {
    font-size: 16px;
  }
  
  .platform-features {
    grid-template-columns: repeat(4, 1fr);
    gap: 15px;
  }
  
  .feature-item {
    padding: 12px;
  }
  
  .feature-item span {
    font-size: 13px;
  }
  
  .login-form {
    width: 100%;
    padding: 40px 30px;
  }
  
  .login-header h2 {
    font-size: 24px;
  }
}

@media (max-width: 768px) {
  .login-container {
    padding: 15px;
  }
  
  .login-content {
    border-radius: 15px;
  }
  
  .login-banner {
    padding: 30px 20px;
  }
  
  .platform-title {
    font-size: 24px;
  }
  
  .platform-subtitle {
    font-size: 14px;
    margin-bottom: 30px;
  }
  
  .platform-features {
    grid-template-columns: 1fr 1fr;
    gap: 15px;
    margin-top: 30px;
  }
  
  .login-form {
    padding: 30px 20px;
  }
  
  .login-header h2 {
    font-size: 22px;
  }
  
  .login-header p {
    font-size: 14px;
  }
  
  .login-button {
    height: 45px;
    font-size: 15px;
  }
  
  .username-input-container {
    flex-direction: column;
    gap: 8px;
  }
  
  .user-select {
    width: 100%;
  }
  
  .remember-me {
    font-size: 14px;
  }
  
  .login-footer {
    margin-top: 25px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }
  
  .login-content {
    border-radius: 10px;
  }
  
  .login-banner {
    padding: 25px 15px;
  }
  
  .platform-logo {
    margin-bottom: 20px;
  }
  
  .platform-title {
    font-size: 20px;
  }
  
  .platform-subtitle {
    font-size: 13px;
    margin-bottom: 20px;
  }
  
  .platform-features {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
    margin-top: 20px;
  }
  
  .feature-item {
    padding: 10px;
  }
  
  .feature-item span {
    font-size: 12px;
  }
  
  .login-form {
    padding: 25px 15px;
  }
  
  .login-header h2 {
    font-size: 20px;
  }
  
  .login-header p {
    font-size: 13px;
  }
  
  .login-button {
    height: 40px;
    font-size: 14px;
  }
  
  .remember-me {
    font-size: 13px;
  }
  
  .login-footer {
    margin-top: 20px;
    font-size: 13px;
  }
  
  .login-tips {
    font-size: 12px;
  }
}
</style>
