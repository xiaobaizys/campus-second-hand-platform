<template>
  <div class="register-container">
    <div class="register-content">
      <!-- 左侧宣传区域 -->
      <div class="register-banner">
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
      
      <!-- 右侧注册表单 -->
      <div class="register-form">
        <div class="register-header">
          <h2>创建新账号</h2>
          <p>加入校园二手交易平台，开启闲置物品流转之旅</p>
        </div>

        <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="0" size="large">
          <el-form-item prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" />
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="Lock" show-password />
          </el-form-item>

          <el-form-item prop="email">
            <el-input v-model="registerForm.email" placeholder="请输入邮箱" prefix-icon="Message" />
          </el-form-item>

          <el-form-item prop="realName">
            <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" prefix-icon="UserFilled" />
          </el-form-item>

          <el-form-item prop="phone">
            <el-input v-model="registerForm.phone" placeholder="请输入手机号（选填）" prefix-icon="Phone" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="loading" class="register-button" @click="handleRegister">
              <el-icon><Right /></el-icon>
              注册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <p>已有账号？<router-link to="/login">立即登录</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/store/user'
import { Shop, CircleCheck, Location, Money, ChatLineSquare, Right } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref<FormInstance>()
const loading = ref(false)

// 表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  realName: '',
  phone: '',
})

// 验证确认密码
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' },
  ],
  phone: [{ pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }],
}

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const result = await userStore.register({
          username: registerForm.username,
          password: registerForm.password,
          email: registerForm.email,
          realName: registerForm.realName,
          phone: registerForm.phone,
        })

        if (result.success) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(result.error || '注册失败')
        }
      } catch (error) {
        ElMessage.error('注册失败，请稍后再试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.register-content {
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
.register-banner {
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

/* 右侧注册表单 */
.register-form {
  width: 450px;
  padding: 60px 40px;
  background-color: #fff;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
  font-weight: 700;
}

.register-header p {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.register-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  border: none;
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(106, 17, 203, 0.3);
  background: linear-gradient(135deg, #5a0fb8 0%, #1e69d1 100%);
}

.register-button:active {
  transform: translateY(0);
}

.register-footer {
  text-align: center;
  margin-top: 30px;
  color: #606266;
}

.register-footer a {
  color: #409eff;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.3s ease;
}

.register-footer a:hover {
  color: #66b1ff;
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

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #6a11cb;
  border-color: #6a11cb;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #6a11cb;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .register-content {
    flex-direction: column;
    max-width: 500px;
  }
  
  .register-banner {
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
  
  .register-form {
    width: 100%;
    padding: 40px 30px;
  }
  
  .register-header h2 {
    font-size: 24px;
  }
}

@media (max-width: 768px) {
  .register-container {
    padding: 15px;
  }
  
  .register-content {
    border-radius: 15px;
  }
  
  .register-banner {
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
  
  .register-form {
    padding: 30px 20px;
  }
  
  .register-header h2 {
    font-size: 22px;
  }
  
  .register-header p {
    font-size: 14px;
  }
  
  .register-button {
    height: 45px;
    font-size: 15px;
  }
  
  .register-footer {
    margin-top: 25px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .register-container {
    padding: 10px;
  }
  
  .register-content {
    border-radius: 10px;
  }
  
  .register-banner {
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
  
  .register-form {
    padding: 25px 15px;
  }
  
  .register-header h2 {
    font-size: 20px;
  }
  
  .register-header p {
    font-size: 13px;
  }
  
  .register-button {
    height: 40px;
    font-size: 14px;
  }
  
  .register-footer {
    margin-top: 20px;
    font-size: 13px;
  }
}
</style>
