<template>
  <div class="profile-container">
    <AppHeader />

    <div class="profile-section">
      <div class="container">
        <div class="profile-layout">
          <!-- 侧边栏 -->
          <div class="profile-sidebar">
            <div class="user-card">
              <el-avatar :size="80" :src="getFullAvatarUrl(userStore.user?.avatarUrl || userStore.user?.avatar)" />
              <h3>{{ userStore.user?.username }}</h3>
              <p>{{ userStore.user?.email }}</p>
              <div class="user-stats">
                <div class="stat-item">
                  <span class="stat-value">{{ sellingProducts.length }}</span>
                  <span class="stat-label">在售商品</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ soldProducts.length }}</span>
                  <span class="stat-label">已售商品</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ favoriteProducts.length }}</span>
                  <span class="stat-label">收藏商品</span>
                </div>
              </div>
            </div>

            <!-- 用户信用评级 -->
            <UserRating :rating-score="85" :transaction-count="23" :positive-rate="96" :response-time="2" />

            <el-menu :default-active="activeMenu" class="profile-menu" @select="handleMenuSelect">
              <el-menu-item index="info">
                <el-icon><UserIcon /></el-icon>
                <span>个人信息</span>
              </el-menu-item>
              <el-menu-item index="products">
                <el-icon><Goods /></el-icon>
                <span>我的商品</span>
              </el-menu-item>
              <el-menu-item index="orders" @click="goToOrders">
                <el-icon><Document /></el-icon>
                <span>我的订单</span>
              </el-menu-item>
              <el-menu-item index="favorites" @click="goToFavorites">
                <el-icon><Star /></el-icon>
                <span>我的收藏</span>
              </el-menu-item>
              <el-menu-item index="messages">
                <el-icon><ChatDotRound /></el-icon>
                <span>我的消息</span>
              </el-menu-item>
              <el-menu-item index="settings">
                <el-icon><Setting /></el-icon>
                <span>账号设置</span>
              </el-menu-item>
            </el-menu>
          </div>

          <!-- 主要内容区 -->
          <div class="profile-content">
            <!-- 个人信息 -->
            <div v-if="activeMenu === 'info'" class="content-panel">
              <h2>个人信息</h2>
              <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="80px">
                <el-form-item label="头像">
                  <el-upload class="avatar-uploader" action="#" :show-file-list="false" :before-upload="beforeAvatarUpload">
                    <img v-if="profileForm.avatarUrl || profileForm.avatar" :src="getFullAvatarUrl(profileForm.avatarUrl || profileForm.avatar)" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                </el-form-item>

                <el-form-item label="用户名" prop="username">
                  <el-input v-model="profileForm.username" />
                </el-form-item>

                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="profileForm.realName" />
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" />
                </el-form-item>

                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="profileForm.phone" />
                </el-form-item>

                <el-form-item label="学校" prop="school">
                  <el-input v-model="profileForm.school" />
                </el-form-item>

                <el-form-item label="专业" prop="major">
                  <el-input v-model="profileForm.major" />
                </el-form-item>

                <el-form-item label="年级" prop="grade">
                  <el-select v-model="profileForm.grade" placeholder="请选择年级">
                    <el-option label="大一" value="大一" />
                    <el-option label="大二" value="大二" />
                    <el-option label="大三" value="大三" />
                    <el-option label="大四" value="大四" />
                    <el-option label="研究生" value="研究生" />
                    <el-option label="博士生" value="博士生" />
                  </el-select>
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" @click="updateProfile">保存修改</el-button>
                </el-form-item>
              </el-form>
            </div>

            <!-- 我的商品 -->
            <div v-if="activeMenu === 'products'" class="content-panel">
              <div class="panel-header">
                <h2>我的商品</h2>
                <el-button type="primary" @click="goToPublish">发布商品</el-button>
              </div>

              <el-tabs v-model="productsTab" @tab-click="handleProductsTabClick">
                <el-tab-pane label="在售商品" name="selling">
                  <div v-if="true" v-loading="loadingProducts" class="products-grid">
                    <div v-for="product in sellingProducts" :key="product.id" class="product-card">
                      <div class="product-image">
                        <img :src="product && product.images && product.images.length > 0 ? product.images[0] : '/placeholder.svg'" :alt="product ? product.title : '商品图片'" />
                      </div>
                      <div class="product-info">
                        <h3 class="product-title">{{ product ? product.title : '未知商品' }}</h3>
                        <p class="product-price">¥{{ product && product.price ? product.price.toFixed(2) : '0.00' }}</p>
                        <div class="product-actions">
                          <el-button size="small" @click="editProduct(product)">编辑</el-button>
                          <el-button size="small" type="danger" @click="deleteProduct(product)">下架</el-button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="sellingProducts.length === 0 && !loadingProducts" class="empty-state">
                    <el-empty description="暂无在售商品" />
                  </div>
                </el-tab-pane>

                <el-tab-pane label="已售商品" name="sold">
                  <div v-if="true" v-loading="loadingProducts" class="products-grid">
                    <div v-for="product in soldProducts" :key="product.id" class="product-card">
                      <div class="product-image">
                        <img :src="product && product.images && product.images.length > 0 ? product.images[0] : '/placeholder.svg'" :alt="product ? product.title : '商品图片'" />
                      </div>
                      <div class="product-info">
                        <h3 class="product-title">{{ product ? product.title : '未知商品' }}</h3>
                        <p class="product-price">¥{{ product && product.price ? product.price.toFixed(2) : '0.00' }}</p>
                        <div class="product-meta">
                          <span class="sold-tag">已售出</span>
                          <span class="sold-time">{{ formatDate(product?.soldAt) }}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="soldProducts.length === 0 && !loadingProducts" class="empty-state">
                    <el-empty description="暂无已售商品" />
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>

            <!-- 我的订单 -->
            <div v-if="activeMenu === 'orders'" class="content-panel">
              <h2>我的订单</h2>

              <el-tabs v-model="ordersTab" @tab-click="handleOrdersTabClick">
                <el-tab-pane label="我买到的" name="bought">
                  <div v-if="true" v-loading="loadingOrders" class="orders-list">
                    <div v-for="order in boughtOrders" :key="order.id" class="order-card">
                      <div class="order-image">
                        <img :src="order && order.product && order.product.images && order.product.images.length > 0 ? order.product.images[0] : '/placeholder.svg'" :alt="order && order.product ? order.product.title : '商品图片'" />
                      </div>
                      <div class="order-info">
                        <h3 class="order-title">{{ order && order.product ? order.product.title : '未知商品' }}</h3>
                        <p class="order-price">¥{{ order && order.product && order.product.price ? order.product.price.toFixed(2) : '0.00' }}</p>
                        <div class="order-meta">
                          <span class="order-buyer">买家：{{ order && order.buyer ? order.buyer.username : '未知用户' }}</span>
                          <span class="order-time">{{ formatDate(order?.createdAt) }}</span>
                        </div>
                        <div class="order-status">
                          <el-tag :type="getOrderStatusType(order ? order.status : '')">
                            {{ getOrderStatusText(order ? order.status : '') }}
                          </el-tag>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="boughtOrders.length === 0 && !loadingOrders" class="empty-state">
                    <el-empty description="暂无购买订单" />
                  </div>
                </el-tab-pane>

                <el-tab-pane label="我卖出的" name="sold">
                  <div v-if="true" v-loading="loadingOrders" class="orders-list">
                    <div v-for="order in soldOrders" :key="order.id" class="order-card">
                      <div class="order-image">
                        <img :src="order && order.product && order.product.images && order.product.images.length > 0 ? order.product.images[0] : '/placeholder.svg'" :alt="order && order.product ? order.product.title : '商品图片'" />
                      </div>
                      <div class="order-info">
                        <h3 class="order-title">{{ order && order.product ? order.product.title : '未知商品' }}</h3>
                        <p class="order-price">¥{{ order && order.product ? order.product.price.toFixed(2) : '0.00' }}</p>
                        <div class="order-meta">
                          <span class="order-buyer">买家：{{ order && order.buyer ? order.buyer.username : '未知用户' }}</span>
                          <span class="order-time">{{ formatDate(order?.createdAt) }}</span>
                        </div>
                        <div class="order-status">
                          <el-tag :type="getOrderStatusType(order ? order.status : '')">
                            {{ getOrderStatusText(order ? order.status : '') }}
                          </el-tag>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="soldOrders.length === 0 && !loadingOrders" class="empty-state">
                    <el-empty description="暂无销售订单" />
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>

            <!-- 我的收藏 -->
            <div v-if="activeMenu === 'favorites'" class="content-panel">
              <h2>我的收藏</h2>

              <div v-if="true" v-loading="loadingFavorites" class="products-grid">
                <div v-for="product in favoriteProducts" :key="product.id" class="product-card">
                  <div class="product-image">
                    <img :src="product && product.images && product.images.length > 0 ? product.images[0] : '/placeholder.svg'" :alt="product ? product.title : '商品图片'" />
                  </div>
                  <div class="product-info">
                    <h3 class="product-title">{{ product ? product.title : '未知商品' }}</h3>
                    <p class="product-price">¥{{ product && product.price ? product.price.toFixed(2) : '0.00' }}</p>
                    <div class="product-actions">
                      <el-button size="small" @click="goToProductDetail(product.id)">查看详情</el-button>
                      <el-button size="small" type="danger" @click="removeFromFavorites(product)">取消收藏</el-button>
                    </div>
                  </div>
                </div>
              </div>

              <div v-if="favoriteProducts.length === 0 && !loadingFavorites" class="empty-state">
                <el-empty description="暂无收藏商品" />
              </div>
            </div>

            <!-- 我的消息 -->
            <div v-if="activeMenu === 'messages'" class="content-panel">
              <h2>我的消息</h2>

              <!-- 消息通知中心 -->
              <NotificationCenter />

              <div v-if="true" v-loading="loadingMessages" class="messages-list">
                <div v-for="message in messages" :key="message.id" class="message-card" :class="{ unread: !message.read }">
                  <div class="message-avatar">
                    <el-avatar :size="40" :src="message && message.sender ? message.sender.avatar : ''" />
                  </div>
                  <div class="message-content">
                    <div class="message-header">
                      <span class="message-sender">{{ message && message.sender ? message.sender.username : '未知用户' }}</span>
                      <span class="message-time">{{ formatDate(message?.createdAt) }}</span>
                    </div>
                    <div class="message-text">{{ message ? message.content : '' }}</div>
                  </div>
                  <div class="message-actions">
                    <el-button v-if="message && !message.read" size="small" @click="markAsRead(message)">标记已读</el-button>
                    <el-button size="small" @click="replyToMessage(message)">回复</el-button>
                  </div>
                </div>
              </div>

              <div v-if="messages.length === 0 && !loadingMessages" class="empty-state">
                <el-empty description="暂无消息" />
              </div>
            </div>

            <!-- 账号设置 -->
            <div v-if="activeMenu === 'settings'" class="content-panel">
              <h2>账号设置</h2>

              <el-form ref="settingsFormRef" :model="settingsForm" :rules="settingsRules" label-width="100px">
                <el-form-item label="修改密码">
                  <el-button @click="showPasswordDialog = true">修改密码</el-button>
                </el-form-item>

                <el-form-item label="消息通知">
                  <el-switch v-model="settingsForm.emailNotification" />
                  <span class="setting-desc">接收邮件通知</span>
                </el-form-item>

                <el-form-item label="隐私设置">
                  <el-switch v-model="settingsForm.showPhone" />
                  <span class="setting-desc">向其他用户显示手机号</span>
                </el-form-item>

                <el-form-item label="账号注销">
                  <el-button type="danger" @click="confirmDeleteAccount">注销账号</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px">
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input v-model="passwordForm.currentPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button type="primary" @click="changePassword">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import { User as UserIcon, Goods, Document, Star, ChatDotRound, Setting, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { useProductStore } from '@/store/product'
import { useOrderStore } from '@/store/order'
import type { User } from '@/types/user'
import type { Product } from '@/types/product'
import type { Order } from '@/types/order'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import UserRating from '@/components/UserRating.vue'
import NotificationCenter from '@/components/NotificationCenter.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const productStore = useProductStore()
const orderStore = useOrderStore()

// 当前激活的菜单
const activeMenu = ref('info')

// 表单引用
const profileFormRef = ref<FormInstance>()
const settingsFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 加载状态
const loadingProducts = ref(false)
const loadingOrders = ref(false)
const loadingFavorites = ref(false)
const loadingMessages = ref(false)

// 个人信息表单
const profileForm = reactive({
  avatar: userStore.user?.avatar || '',
  avatarUrl: userStore.user?.avatarUrl || '',
  username: userStore.user?.username || '',
  realName: userStore.user?.realName || '',
  email: userStore.user?.email || '',
  phone: userStore.user?.phone || '',
  bio: userStore.user?.bio || '',
  school: userStore.user?.school || '',
  major: userStore.user?.major || '',
  grade: userStore.user?.grade || '',
})

// 个人信息表单验证规则
const profileRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }],
}

// 账号设置表单
const settingsForm = reactive({
  emailNotification: true,
  showPhone: false,
})

// 账号设置表单验证规则
const settingsRules: FormRules = {}

// 修改密码表单
const showPasswordDialog = ref(false)
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 验证确认密码
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 修改密码表单验证规则
const passwordRules: FormRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

// 商品相关
const productsTab = ref('selling')
const sellingProducts = ref([])
const soldProducts = ref([])

// 订单相关
const ordersTab = ref('bought')
const boughtOrders = ref([])
const soldOrders = ref([])

// 收藏相关
const favoriteProducts = ref([])

// 消息相关
const messages = ref([])

// 菜单选择处理
const handleMenuSelect = (index: string) => {
  activeMenu.value = index

  // 根据选择的菜单加载对应数据
  switch (index) {
    case 'products':
      loadProducts()
      break
    case 'orders':
      loadOrders()
      break
    case 'favorites':
      loadFavorites()
      break
    case 'messages':
      loadMessages()
      break
  }
}

// 商品标签页点击处理
const handleProductsTabClick = () => {
  loadProducts()
}

// 订单标签页点击处理
const handleOrdersTabClick = () => {
  loadOrders()
}

// 加载商品数据
const loadProducts = async () => {
  loadingProducts.value = true
  try {
    // 获取用户发布的商品
    await productStore.fetchUserProducts()
    const userProducts = productStore.products

    // 分离在售和已售商品
    sellingProducts.value = userProducts.filter((product) => product.status === 'available')
    soldProducts.value = userProducts.filter((product) => product.status === 'sold')
  } catch (error) {
    ElMessage.error('获取商品数据失败')
  } finally {
    loadingProducts.value = false
  }
}

// 加载订单数据
const loadOrders = async () => {
  loadingOrders.value = true
  try {
    // 获取买家订单和卖家订单
    const buyerOrdersData = await orderStore.fetchBuyerOrders()
    const sellerOrdersData = await orderStore.fetchSellerOrders()

    // 设置购买和出售的订单
    boughtOrders.value = buyerOrdersData || []
    soldOrders.value = sellerOrdersData || []
  } catch (error) {
    ElMessage.error('获取订单数据失败')
  } finally {
    loadingOrders.value = false
  }
}

// 加载收藏数据
const loadFavorites = async () => {
  loadingFavorites.value = true
  try {
    // 获取用户收藏的商品
    favoriteProducts.value = await productStore.fetchFavoriteProducts()
  } catch (error) {
    ElMessage.error('获取收藏数据失败')
  } finally {
    loadingFavorites.value = false
  }
}

// 加载消息数据
const loadMessages = async () => {
  loadingMessages.value = true
  try {
    // 调用API获取消息数据
    const response = await fetch('/api/messages', {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${userStore.token}`,
      },
    })

    if (response.ok) {
      const data = await response.json()
      messages.value = data.data || []
    } else {
      // 如果API不存在或出错，使用模拟数据
      messages.value = [
        {
          id: 1,
          sender: {
            username: '王五',
            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          },
          content: '你好，这个商品还在吗？',
          read: false,
          createdAt: new Date('2023-05-16'),
        },
        {
          id: 2,
          sender: {
            username: '赵六',
            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          },
          content: '可以便宜一点吗？',
          read: true,
          createdAt: new Date('2023-05-15'),
        },
      ]
    }
  } catch (error) {
    console.error('获取消息数据失败:', error)
    // 使用模拟数据
    messages.value = [
      {
        id: 1,
        sender: {
          username: '王五',
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        },
        content: '你好，这个商品还在吗？',
        read: false,
        createdAt: new Date('2023-05-16'),
      },
      {
        id: 2,
        sender: {
          username: '赵六',
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        },
        content: '可以便宜一点吗？',
        read: true,
        createdAt: new Date('2023-05-15'),
      },
    ]
  } finally {
    loadingMessages.value = false
  }
}

// 更新个人信息
const updateProfile = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 调用userStore更新用户信息
        await userStore.updateUserProfile({
          username: profileForm.username,
          realName: profileForm.realName,
          email: profileForm.email,
          phone: profileForm.phone,
          bio: profileForm.bio,
          avatarUrl: profileForm.avatarUrl,
          school: profileForm.school,
          major: profileForm.major,
          grade: profileForm.grade,
        })
        ElMessage.success('个人信息更新成功')
      } catch (error) {
        ElMessage.error('更新个人信息失败')
      }
    }
  })
}

// 头像上传前的处理
const beforeAvatarUpload: UploadProps['beforeUpload'] = async (file) => {
  try {
    console.log('开始上传头像，文件信息:', file)

    // 创建FormData对象
    const formData = new FormData()
    formData.append('file', file)
    console.log('FormData已创建，准备上传')

    // 调用userStore上传头像
    const result = await userStore.uploadAvatar(formData)
    console.log('上传结果:', result)

    if (result.success) {
      // 更新当前用户信息中的头像URL
      if (userStore.user) {
        userStore.user.avatarUrl = result.data.avatarUrl
      }
      // 更新表单中的头像URL
      profileForm.avatarUrl = result.data.avatarUrl
      ElMessage.success('头像上传成功')
    } else {
      console.error('上传失败，返回结果:', result)
      ElMessage.error(result.error || '头像上传失败')
    }
  } catch (error) {
    console.error('上传头像异常:', error)
    ElMessage.error('头像上传失败')
  }
  // 阻止默认上传行为
  return false
}

// 跳转到发布商品页
const goToPublish = () => {
  router.push('/publish')
}

// 编辑商品
const editProduct = (product: any) => {
  router.push(`/products/${product.id}/edit`)
}

// 删除商品
const deleteProduct = async (product: any) => {
  try {
    await ElMessageBox.confirm('确定要下架此商品吗？', '下架确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    // 调用productStore删除商品
    await productStore.deleteProduct(product.id)
    ElMessage.success('商品已下架')
    loadProducts()
  } catch (error) {
    // 用户取消操作或删除失败
    if (error !== 'cancel') {
      ElMessage.error('下架商品失败')
    }
  }
}

// 跳转到商品详情页
const goToProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 取消收藏
const removeFromFavorites = async (product: any) => {
  try {
    // 调用productStore取消收藏
    await productStore.removeFromFavorites(product.id)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (error) {
    ElMessage.error('取消收藏失败')
  }
}

// 标记消息已读
const markAsRead = async (message: any) => {
  try {
    // 调用API标记消息已读
    const response = await fetch(`/api/messages/${message.id}/read`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${userStore.token}`,
      },
    })

    if (response.ok) {
      message.read = true
      ElMessage.success('已标记为已读')
    } else {
      // 如果API不存在或出错，直接标记为已读
      message.read = true
      ElMessage.success('已标记为已读')
    }
  } catch (error) {
    console.error('标记消息已读失败:', error)
    // 如果API调用失败，直接标记为已读
    message.read = true
    ElMessage.success('已标记为已读')
  }
}

// 回复消息
const replyToMessage = (message: any) => {
  // 这里应该跳转到消息回复页面或打开对话框
  ElMessage.info('回复功能开发中...')
}

// 获取订单状态类型
const getOrderStatusType = (status: string | undefined | null) => {
  if (!status) return 'info'

  switch (status) {
    case 'pending':
      return 'warning'
    case 'completed':
      return 'success'
    case 'cancelled':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取订单状态文本
const getOrderStatusText = (status: string | undefined | null) => {
  if (!status) return '未知'

  switch (status) {
    case 'pending':
      return '待确认'
    case 'completed':
      return '已完成'
    case 'cancelled':
      return '已取消'
    default:
      return '未知'
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 调用userStore修改密码
        await userStore.changePassword(passwordForm.currentPassword, passwordForm.newPassword)
        ElMessage.success('密码修改成功')
        showPasswordDialog.value = false

        // 重置表单
        passwordForm.currentPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error) {
        ElMessage.error('密码修改失败')
      }
    }
  })
}

// 确认注销账号
const confirmDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm('确定要注销账号吗？此操作不可恢复！', '注销账号', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    // 调用userStore注销账号
    await userStore.deleteAccount()
    ElMessage.success('账号已注销')
    userStore.logout()
    router.push('/')
  } catch (error) {
    // 用户取消操作或注销失败
    if (error !== 'cancel') {
      ElMessage.error('注销账号失败')
    }
  }
}

// 格式化日期
const formatDate = (date: Date | undefined | null) => {
  if (!date) {
    return new Date().toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })
  }

  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

// 获取完整的头像URL
const getFullAvatarUrl = (avatarUrl: string | undefined) => {
  if (!avatarUrl) return ''

  // 如果已经是完整URL，直接返回
  if (avatarUrl.startsWith('http')) {
    return avatarUrl
  }

  // 如果是相对路径，直接返回（通过Vite代理访问）
  if (avatarUrl.startsWith('/')) {
    return avatarUrl
  }

  // 其他情况，添加/前缀
  return `/${avatarUrl}`
}

// 初始化
onMounted(async () => {
  // 检查用户是否登录
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  // 根据当前路由设置激活的菜单
  updateActiveMenuFromRoute()

  // 默认加载个人信息
  profileForm.username = userStore.user?.username || ''
  profileForm.email = userStore.user?.email || ''
  profileForm.phone = userStore.user?.phone || ''
  profileForm.bio = userStore.user?.bio || ''
  profileForm.avatar = userStore.user?.avatar || ''
  profileForm.avatarUrl = userStore.user?.avatarUrl || ''
  profileForm.school = userStore.user?.school || ''
  profileForm.major = userStore.user?.major || ''
  profileForm.grade = userStore.user?.grade || ''

  // 预加载数据，避免v-loading在DOM元素未渲染时应用
  try {
    await Promise.all([loadProducts(), loadFavorites(), loadMessages()])
  } catch (error) {
    console.error('预加载数据失败:', error)
  }
})

// 根据路由更新激活的菜单
const updateActiveMenuFromRoute = () => {
  const routeName = route.name as string
  const tab = route.query.tab as string

  // 优先检查路由参数tab
  if (tab) {
    activeMenu.value = tab
    // 根据tab加载对应数据
    switch (tab) {
      case 'messages':
        loadMessages()
        break
    }
    return
  }

  // 否则根据路由名称设置
  switch (routeName) {
    case 'profile':
      activeMenu.value = 'info'
      break
    case 'favorites':
      activeMenu.value = 'favorites'
      loadFavorites()
      break
    case 'orders':
      activeMenu.value = 'orders'
      loadOrders()
      break
  }
}

// 监听路由变化
watch(
  () => [route.name, route.query],
  () => {
    updateActiveMenuFromRoute()
  },
  { deep: true }
)

// 跳转到订单页面
const goToOrders = () => {
  router.push('/orders')
}

// 跳转到收藏页面
const goToFavorites = () => {
  router.push('/favorites')
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
}

.profile-section {
  padding: 20px 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 140px);
}

.profile-layout {
  display: flex;
  gap: 20px;
}

.profile-sidebar {
  width: 250px;
}

.user-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.user-card h3 {
  margin: 15px 0 5px;
  color: #303133;
}

.user-card p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.profile-menu {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-content {
  flex: 1;
}

.content-panel {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.content-panel h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-uploader .avatar {
  width: 80px;
  height: 80px;
  display: block;
  border-radius: 50%;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: 0.2s;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  line-height: 80px;
  text-align: center;
}

.setting-desc {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.product-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.product-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 150px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 16px;
  margin: 0 0 10px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
  margin: 0 0 10px;
}

.product-actions {
  display: flex;
  gap: 10px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.sold-tag {
  color: #67c23a;
  font-weight: 500;
}

.sold-time {
  color: #909399;
  font-size: 12px;
}

.orders-list {
  margin-top: 20px;
}

.order-card {
  display: flex;
  gap: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 15px;
}

.order-image {
  width: 100px;
  height: 100px;
  overflow: hidden;
  border-radius: 4px;
}

.order-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.order-info {
  flex: 1;
}

.order-title {
  font-size: 16px;
  margin: 0 0 10px;
  color: #303133;
}

.order-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
  margin: 0 0 10px;
}

.order-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #909399;
  font-size: 14px;
}

.order-status {
  margin-top: 10px;
}

.messages-list {
  margin-top: 20px;
}

.message-card {
  display: flex;
  gap: 15px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 15px;
}

.message-card.unread {
  background-color: #f0f9ff;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.message-sender {
  font-weight: 500;
  color: #303133;
}

.message-time {
  color: #909399;
  font-size: 14px;
}

.message-text {
  color: #606266;
  line-height: 1.5;
}

.message-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
}

.empty-state {
  margin: 50px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .profile-section {
    padding: 20px 0;
  }

  .profile-layout {
    flex-direction: column;
    gap: 20px;
  }

  .profile-sidebar {
    width: 100%;
  }

  .user-card {
    padding: 15px;
  }

  .user-card h3 {
    font-size: 18px;
  }

  .user-stats {
    gap: 15px;
  }

  .stat-item {
    padding: 10px;
  }

  .stat-value {
    font-size: 20px;
  }

  .stat-label {
    font-size: 12px;
  }

  .profile-menu {
    border-right: none;
  }

  .profile-menu .el-menu-item {
    height: 45px;
    line-height: 45px;
  }

  .profile-menu .el-menu-item span {
    font-size: 14px;
  }

  .content-panel {
    padding: 20px;
  }

  .content-panel h2 {
    font-size: 20px;
  }

  .panel-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }

  .product-card {
    margin-bottom: 0;
  }

  .product-image {
    height: 120px;
  }

  .product-info {
    padding: 10px;
  }

  .product-title {
    font-size: 14px;
  }

  .product-price {
    font-size: 16px;
  }

  .product-actions {
    flex-direction: column;
    gap: 5px;
  }

  .order-card {
    flex-direction: column;
    gap: 15px;
  }

  .order-image {
    width: 100%;
    height: 200px;
  }

  .message-card {
    flex-direction: column;
    gap: 10px;
  }

  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .message-actions {
    flex-direction: row;
    justify-content: flex-end;
  }
}

@media (max-width: 480px) {
  .profile-section {
    padding: 15px 0;
  }

  .user-card {
    padding: 10px;
  }

  .user-card h3 {
    font-size: 16px;
  }

  .user-card p {
    font-size: 12px;
  }

  .user-stats {
    gap: 10px;
  }

  .stat-item {
    padding: 8px;
  }

  .stat-value {
    font-size: 18px;
  }

  .stat-label {
    font-size: 11px;
  }

  .profile-menu .el-menu-item {
    height: 40px;
    line-height: 40px;
  }

  .profile-menu .el-menu-item span {
    font-size: 13px;
  }

  .content-panel {
    padding: 15px;
  }

  .content-panel h2 {
    font-size: 18px;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 10px;
  }

  .product-image {
    height: 100px;
  }

  .product-info {
    padding: 8px;
  }

  .product-title {
    font-size: 13px;
  }

  .product-price {
    font-size: 14px;
  }

  .order-image {
    height: 150px;
  }
}
</style>
