<template>
  <div class="product-detail-container">
    <AppHeader />

    <div v-if="true" v-loading="loading" class="product-detail-section">
      <div class="container">
        <div v-if="product" class="product-detail">
          <!-- 返回按钮 -->
          <div class="back-button-container">
            <el-button type="default" size="small" @click="goBack" class="back-btn"> 返回 </el-button>
          </div>
          <div class="product-images">
            <div class="main-image">
              <el-carousel v-if="product.images && product.images.length > 0" height="400px" indicator-position="outside" @change="handleImageChange" @click="handleCarouselClick">
                <el-carousel-item v-for="(image, index) in product.images" :key="index">
                  <img :src="image || '/placeholder.svg'" :alt="`${product.title || '商品图片'} ${index + 1}`" />
                </el-carousel-item>
              </el-carousel>
              <!-- 如果没有图片，显示一个占位符 -->
              <img v-else src="/placeholder.svg" :alt="product.title || '商品图片'" />
            </div>
            <div class="image-thumbnails">
              <div v-for="(image, index) in product.images || []" :key="index" class="thumbnail" :class="{ active: currentImage === image }" @click="currentImage = image; carouselRef?.setActiveItem(index)">
                <img :src="image || '/placeholder.svg'" :alt="`${product.title || '商品图片'} ${index + 1}`" />
              </div>
              <!-- 如果没有图片，显示一个占位符 -->
              <div v-if="!product.images || product.images.length === 0" class="thumbnail active">
                <img src="/placeholder.svg" :alt="product.title || '商品图片'" />
              </div>
            </div>
          </div>

          <div class="product-info">
            <!-- 编辑和删除按钮 -->
            <div class="product-actions-header" v-if="canEditOrDelete">
              <el-button type="primary" size="small" @click="editProduct" class="edit-btn"> 编辑 </el-button>
              <el-button type="danger" size="small" @click="deleteProduct" class="delete-btn"> 删除 </el-button>
            </div>
            <h1 class="product-title">{{ product.title || '未知商品' }}</h1>
            <div class="product-price">¥{{ product.price ? product.price.toFixed(2) : '0.00' }}</div>

            <div class="product-meta">
              <div class="meta-item">
                <span class="label">分类：</span>
                <span class="value">{{ product.category ? product.category.name : '未分类' }}</span>
              </div>
              <div class="meta-item">
                <span class="label">发布时间：</span>
                <span class="value">{{ formatDate(product?.createdAt) }}</span>
              </div>
              <div class="meta-item">
                <span class="label">状态：</span>
                <el-tag :type="getStatusType(product.status)">
                  {{ getStatusText(product.status) }}
                </el-tag>
              </div>
              <div class="meta-item">
                <span class="label">浏览量：</span>
                <span class="value">{{ product.viewCount || 0 }}</span>
              </div>
              <div class="meta-item">
                <span class="label">点赞数：</span>
                <span class="value">{{ product.likeCount || 0 }}</span>
              </div>
              <div class="meta-item">
                <span class="label">原价：</span>
                <span class="value"
                  ><del>¥{{ product.originalPrice ? product.originalPrice.toFixed(2) : '0.00' }}</del></span
                >
              </div>
              <div class="meta-item">
                <span class="label">可议价：</span>
                <span class="value">{{ product.isNegotiable ? '是' : '否' }}</span>
              </div>
              <div class="meta-item">
                <span class="label">全新：</span>
                <span class="value">{{ product.isNew ? '是' : '否' }}</span>
              </div>
            </div>

            <div class="product-description">
              <h3>商品描述</h3>
              <p>{{ product.description || '暂无描述' }}</p>
            </div>

            <div class="seller-info">
              <h3>卖家信息</h3>
              <div class="seller-card">
                <el-avatar :size="50" :src="product.sellerAvatar || ''" />
                <div class="seller-details">
                  <div class="seller-name">{{ product.sellerName || '未知卖家' }}</div>
                  <div class="seller-rating">
                    <el-rate :model-value="sellerRating" disabled show-score text-color="#ff9900" score-template="{value}" />
                  </div>
                </div>
                <el-button type="primary" @click="contactSeller">联系卖家</el-button>
              </div>
            </div>

            <div class="product-actions">
              <el-button v-if="!isProductOwner && isProductAvailable" type="primary" size="large" @click="buyProduct"> 立即购买 </el-button>
              <el-button v-if="!isProductOwner && isProductAvailable" :type="isInCart ? 'default' : 'success'" size="large" :loading="loadingCart" @click="addToCart" :disabled="isInCart">
                {{ isInCart ? '已加入购物车' : '加入购物车' }}
              </el-button>
              <el-button v-if="isProductAvailable" size="large" :loading="loadingFavorite" :type="isFavorited ? 'default' : 'primary'" @click="addToFavorites">
                {{ isFavorited ? '已收藏' : '添加收藏' }}
              </el-button>
            </div>
          </div>
        </div>

        <div v-else-if="!loading" class="product-not-found">
          <el-result icon="warning" title="商品不存在" sub-title="您访问的商品可能已下架或不存在">
            <template #extra>
              <el-button type="primary" @click="goBack">返回上一页</el-button>
              <el-button @click="goToProducts">浏览更多商品</el-button>
            </template>
          </el-result>
        </div>
      </div>
    </div>

    <!-- 评价区域 -->
    <div v-if="product" class="reviews-section">
      <div class="container">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="商品评价" name="reviews">
            <ProductRatingSummary :product-id="product.id" :reviews="reviews" />
            <ProductReviews :product-id="product.id" :has-purchased="hasPurchased" :has-reviewed="hasReviewed" @review-submitted="handleReviewSubmitted" />
          </el-tab-pane>

          <el-tab-pane label="商品评论" name="comments">
            <div class="comment-form">
              <el-input v-model="newComment" type="textarea" :rows="3" placeholder="请输入您的评论..." maxlength="500" show-word-limit />
              <el-button type="primary" :loading="submittingComment" @click="submitComment"> 发表评论 </el-button>
            </div>

            <div v-if="true" v-loading="loadingComments" class="comments-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <el-avatar :size="40" :src="comment.user?.avatar || ''" />
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-username">{{ comment.user?.username || '未知用户' }}</span>
                    <span class="comment-time">{{ formatDate(comment?.createdAt) }}</span>
                  </div>
                  <div class="comment-text">{{ comment.content }}</div>
                </div>
              </div>

              <div v-if="comments.length === 0" class="empty-comments">
                <el-empty description="暂无评论" />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import ProductReviews from '@/components/ProductReviews.vue'
import ProductRatingSummary from '@/components/ProductRatingSummary.vue'
import { useProductStore } from '@/store/product'
import { useOrderStore } from '@/store/order'
import { useUserStore } from '@/store/user'
import { favoriteApi } from '@/api/favorite'
import { commentApi, type Comment } from '@/api/comment'
import { orderApi } from '@/api/order'
import type { Product } from '@/types/product'

const router = useRouter()
const route = useRoute()

// Store
const productStore = useProductStore()
const orderStore = useOrderStore()
const userStore = useUserStore()
import { productApi } from '@/api/product'

// 响应式数据
const loading = ref(false)
const product = ref<Product | null>(null)
const currentImage = ref('')
const carouselRef = ref<any>(null)

// 计算属性：获取卖家评分
const sellerRating = computed(() => {
  // 如果 product 或 product.seller 不存在，返回默认值 4.5
  if (!product.value || !product.value.seller) {
    return 4.5
  }

  // 如果 product.seller 有 rating 字段，使用该字段值
  if (product.value.seller.rating !== undefined) {
    return product.value.seller.rating
  }

  // 否则返回默认值 4.5
  return 4.5
})

// 计算属性：判断当前用户是否可以编辑或删除商品
const canEditOrDelete = computed(() => {
  if (!userStore.isAuthenticated || !product.value) {
    return false
  }

  // 管理员可以编辑或删除任何商品
  if (userStore.user?.role === 'ADMIN') {
    return true
  }

  // 卖家可以编辑或删除自己的商品
  return userStore.user?.id === product.value.sellerId
})

// 计算属性：判断当前用户是否是商品卖家
const isProductOwner = computed(() => {
  if (!userStore.isAuthenticated || !product.value) {
    return false
  }

  return userStore.user?.id === product.value.sellerId
})

// 计算属性：判断商品是否可购买
const isProductAvailable = computed(() => {
  if (!product.value) {
    return false
  }

  // 检查商品状态（兼容大写和小写状态值）
  const status = product.value.status?.toLowerCase() || ''
  // 兼容 'active', 'available' 两种状态值（已通过toLowerCase()转换，无需额外处理大写）
  return status === 'active' || status === 'available'
})

// 评论相关
const loadingComments = ref(false)
const comments = ref<Comment[]>([])
const newComment = ref('')
const submittingComment = ref(false)

// 收藏相关
const isFavorited = ref(false)
const loadingFavorite = ref(false)

// 购物车相关
const loadingCart = ref(false)
const isInCart = ref(false)
import { cartApi } from '@/api/cartApi'

// 评价相关
const activeTab = ref('reviews')
const hasPurchased = ref(false)
const hasReviewed = ref(false)
const reviews = ref([
  {
    id: '1',
    userId: 'user1',
    userName: '买家A',
    userAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    rating: 5,
    content: '商品质量很好，与描述完全一致，卖家发货速度很快，包装也很仔细，非常满意的一次购物体验！',
    images: [],
    likes: 12,
    createdAt: '2023-06-15T10:30:00Z',
    sellerReply: '感谢您的评价，很高兴您对商品满意，期待您的再次光临！',
    sellerReplyDate: '2023-06-15T14:20:00Z',
  },
  {
    id: '2',
    userId: 'user2',
    userName: '买家B',
    userAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    rating: 4,
    content: '商品整体不错，就是颜色和图片稍有差异，其他都挺好的。',
    images: [],
    likes: 5,
    createdAt: '2023-06-10T15:45:00Z',
    sellerReply: null,
    sellerReplyDate: null,
  },
])

// 获取商品详情
const fetchProductDetail = async () => {
  const productId = Number(route.params.id)
  if (isNaN(productId)) {
    ElMessage.error('无效的商品ID')
    router.push('/products')
    return
  }

  loading.value = true
  try {
    // 使用productStore获取商品详情
    await productStore.fetchProductById(productId)

    if (productStore.currentProduct) {
      product.value = productStore.currentProduct
      // 确保images数组存在
      if (!product.value.images) {
        product.value.images = []
      }
      // 初始化当前图片，如果没有图片则使用占位符
      currentImage.value = product.value.images[0] || '/placeholder.svg'

      // 获取评论
      fetchComments()

      // 检查收藏状态
      if (userStore.isAuthenticated) {
        checkFavoriteStatus()
      }
    } else {
      ElMessage.error('商品不存在')
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

// 获取评论
const fetchComments = async () => {
  if (!product.value) return

  loadingComments.value = true
  try {
    const response = await commentApi.getComments(product.value.id)
    comments.value = response.content || []
  } catch (error) {
    console.error('获取评论失败:', error)
    // 如果API调用失败，使用模拟数据
    const mockComments: any[] = [
      {
        id: 1,
        user: {
          id: 2,
          username: '李四',
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        },
        content: '电脑成色很好，和描述一致，卖家很爽快，推荐！',
        createdAt: new Date('2023-05-16'),
        rating: 5,
      },
      {
        id: 2,
        user: {
          id: 3,
          username: '王五',
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        },
        content: '已经买下了，电脑性能不错，卖家描述很详细，交易愉快。',
        createdAt: new Date('2023-05-17'),
        rating: 4,
      },
    ]

    comments.value = mockComments
  } finally {
    loadingComments.value = false
  }
}

// 根据商品状态获取标签类型
const getStatusType = (status: string | undefined) => {
  // 将状态转换为大写，确保统一处理
  const normalizedStatus = status?.toUpperCase() || ''
  switch (normalizedStatus) {
    case 'AVAILABLE':
    case 'ACTIVE':
      return 'success'
    case 'SOLD':
      return 'danger'
    case 'RESERVED':
      return 'warning'
    case 'REMOVED':
      return 'info'
    default:
      return 'info'
  }
}

// 根据商品状态获取文本
const getStatusText = (status: string | undefined) => {
  // 将状态转换为大写，确保统一处理
  const normalizedStatus = status?.toUpperCase() || ''
  switch (normalizedStatus) {
    case 'AVAILABLE':
    case 'ACTIVE':
      return '在售'
    case 'SOLD':
      return '已售出'
    case 'RESERVED':
      return '已预订'
    case 'REMOVED':
      return '已下架'
    default:
      return '未知状态'
  }
}

// 处理轮播图图片变化
const handleImageChange = (index: number) => {
  if (product.value && product.value.images) {
    currentImage.value = product.value.images[index] || '/placeholder.svg'
  }
}

// 处理轮播图点击事件
const handleCarouselClick = () => {
  if (product.value && product.value.images && product.value.images.length > 1) {
    const currentIndex = product.value.images.indexOf(currentImage.value)
    const nextIndex = (currentIndex + 1) % product.value.images.length
    carouselRef.value?.setActiveItem(nextIndex)
  }
}

// 格式化日期
const formatDate = (date: Date | string | number | undefined | null) => {
  // 如果date为undefined或null，返回当前日期
  if (!date) {
    return new Date().toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })
  }

  let dateObj: Date

  // 根据date的类型进行处理
  if (typeof date === 'string') {
    dateObj = new Date(date)
  } else if (typeof date === 'number') {
    // 处理数字类型的时间戳
    dateObj = new Date(date)
  } else {
    // 如果date已经是Date对象，直接使用
    dateObj = date
  }

  // 检查转换是否成功
  if (isNaN(dateObj.getTime())) {
    // 如果转换失败，返回当前日期
    return new Date().toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })
  }

  return dateObj.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

// 返回商品列表页
const goBack = () => {
  router.push('/products')
}

// 跳转到商品列表页
const goToProducts = () => {
  router.push('/products')
}

// 跳转到我的收藏页
const goToFavorites = () => {
  router.push('/favorites')
}

// 跳转到我的订单页
const goToOrders = () => {
  router.push('/orders')
}

// 跳转到购物车页
const goToCart = () => {
  router.push('/cart')
}

// 联系卖家
const contactSeller = () => {
  if (!userStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!product.value) return

  // 确保卖家信息存在
  const sellerContact = product.value.contactInfo || '暂无联系方式'

  ElMessageBox.alert(`卖家联系方式：${sellerContact}`, '联系卖家', {
    confirmButtonText: '知道了',
    type: 'info',
  })
}

// 购买商品
const buyProduct = async () => {
  if (!userStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!product.value) return

  // 检查商品状态
  if (!isProductAvailable.value) {
    const status = product.value.status?.toLowerCase() || ''
    const statusText = status === 'sold' ? '已卖出' : status === 'removed' ? '已下架' : '不可购买'
    ElMessage.error(`该商品${statusText}，无法购买`)
    return
  }

  try {
    await ElMessageBox.confirm('确定要购买此商品吗？', '购买确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    // 使用orderApi创建订单
    const orderData = {
      productId: product.value.id,
      quantity: 1,
    }

    await orderApi.createOrder(orderData)

    ElMessage.success('购买成功！请联系卖家完成交易')

    // 更新商品状态
    if (product.value) {
      product.value.status = 'SOLD'
    }
  } catch (error) {
    // 用户取消操作或API错误
    console.error('购买失败:', error)
    if (error !== 'cancel') {
      ElMessage.error('购买失败，请稍后重试')
    }
  }
}

// 收藏商品
const addToFavorites = async () => {
  if (!userStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!product.value) return

  loadingFavorite.value = true
  try {
    if (isFavorited.value) {
      // 取消收藏
      await favoriteApi.removeFavorite(product.value.id)
      ElMessage.success('已取消收藏')
      isFavorited.value = false
    } else {
      // 添加收藏
      await favoriteApi.addFavorite(product.value.id)
      ElMessage.success('已添加到收藏')
      isFavorited.value = true
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    // 即使API返回错误，也尝试检查实际的收藏状态，因为操作可能已经成功
    try {
      await checkFavoriteStatus()
      // 如果状态已经改变，说明操作成功
      ElMessage.success(isFavorited.value ? '已添加到收藏' : '已取消收藏')
    } catch (checkError) {
      console.error('检查收藏状态失败:', checkError)
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    loadingFavorite.value = false
  }
}

// 添加到购物车
const addToCart = async () => {
  if (!userStore.isAuthenticated || !userStore.user) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (!product.value) return

  // 检查商品状态（使用统一的isProductAvailable计算属性）
  if (!isProductAvailable.value) {
    const status = product.value.status?.toLowerCase() || ''
    const statusText = status === 'sold' ? '已卖出' : status === 'removed' ? '已下架' : '不可购买'
    ElMessage.error(`该商品${statusText}，无法添加到购物车`)
    return
  }

  loadingCart.value = true
  try {
    // 调用cartApi添加到购物车
    await cartApi.addToCart(userStore.user.id, product.value.id, 1)

    ElMessage.success('已添加到购物车')

    // 更新购物车状态
    isInCart.value = true
  } catch (error) {
    console.error('添加到购物车失败:', error)
    ElMessage.error('添加到购物车失败，请稍后重试')
  } finally {
    loadingCart.value = false
  }
}

// 检查收藏状态
const checkFavoriteStatus = async () => {
  if (!product.value || !userStore.isAuthenticated) return

  try {
    const response = await favoriteApi.checkFavorite(product.value.id)
    // 处理不同格式的响应
    if (typeof response === 'boolean') {
      // 如果直接返回布尔值
      isFavorited.value = response
    } else if (response.success !== undefined) {
      // 如果返回的是包含success字段的对象
      isFavorited.value = response.isFavorited || false
    } else {
      // 其他情况，默认为未收藏
      isFavorited.value = false
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    // 检查失败时，不显示错误信息，避免影响用户体验
  }
}

// 检查购物车状态
const checkCartStatus = async () => {
  if (!product.value || !userStore.isAuthenticated || !userStore.user) return
  try {
    const isInCartResult = await cartApi.checkCartStatus(userStore.user.id, product.value.id)
    isInCart.value = isInCartResult
  } catch (error) {
    console.error('检查购物车状态失败:', error)
  }
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }

  if (!userStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!product.value) return

  submittingComment.value = true
  try {
    await commentApi.createComment({
      productId: product.value.id,
      content: newComment.value,
    })

    // 重新获取评论列表
    await fetchComments()

    newComment.value = ''
    ElMessage.success('评价发表成功')
  } catch (error) {
    // 如果API调用失败，模拟添加评论
    const newCommentObj: Comment = {
      id: comments.value.length + 1,
      user: {
        id: 999,
        username: '当前用户',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      },
      content: newComment.value,
      createdAt: new Date(),
    }

    comments.value.unshift(newCommentObj)
    newComment.value = ''
    ElMessage.success('评价发表成功')
  } finally {
    submittingComment.value = false
  }
}

// 处理标签页切换
const handleTabClick = (tab: any) => {
  if (tab.name === 'reviews') {
    // 可以在这里加载评价数据
  } else if (tab.name === 'comments') {
    // 可以在这里加载评论数据
  }
}

// 编辑商品
const editProduct = () => {
  if (product.value) {
    // 跳转到发布商品页面，并传递商品ID以便编辑
    router.push({ name: 'publish', query: { editId: product.value.id } })
  }
}

// 删除商品
const deleteProduct = async () => {
  if (!product.value) return

  try {
    await ElMessageBox.confirm('确定要删除此商品吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await productApi.deleteProduct(product.value.id)
    ElMessage.success('商品删除成功')

    // 删除成功后跳转到商品列表页
    router.push('/products')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除商品失败，请稍后重试')
    }
  }
}

// 处理评价提交
const handleReviewSubmitted = (review: any) => {
  hasReviewed.value = true
  ElMessage.success('评价提交成功')
}

// 初始化
onMounted(async () => {
  await fetchProductDetail()
  checkCartStatus()
})
</script>

<style scoped>
.product-detail-container {
  min-height: 100vh;
}

.product-detail-section {
  padding: 40px 0;
  background-color: #f5f7fa;
}

.product-detail {
  display: flex;
  gap: 40px;
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
}

.back-button-container {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 10;
}

.back-btn {
  font-size: 12px;
  padding: 6px 12px;
  background-color: #f5f7fa;
  border-color: #dcdfe6;
  color: #606266;
}

.back-btn:hover {
  background-color: #ecf5ff;
  border-color: #c6e2ff;
  color: #409eff;
}

.product-images {
  flex: 1;
}

.main-image {
  height: 400px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 15px;
  background-color: #f5f7fa;
  
  /* 轮播图样式 */
  :deep(.el-carousel) {
    height: 100%;
  }
  
  :deep(.el-carousel__container) {
    height: 100%;
  }
  
  :deep(.el-carousel__item) {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  :deep(.el-carousel__indicator) {
    margin-top: 10px;
  }
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  max-height: 400px;
}

.image-thumbnails {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.thumbnail {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.3s;
  background-color: #f5f7fa;
}

.thumbnail.active {
  border-color: #409eff;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.product-info {
  flex: 1;
}

.product-actions-header {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  justify-content: flex-end;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #303133;
}

.product-price {
  font-size: 32px;
  font-weight: 700;
  color: #f56c6c;
  margin-bottom: 20px;
}

.product-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 25px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-item .label {
  color: #909399;
  margin-right: 5px;
}

.meta-item .value {
  color: #606266;
}

.product-description {
  margin-bottom: 25px;
}

.product-description h3 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #303133;
}

.product-description p {
  line-height: 1.6;
  color: #606266;
}

.seller-info {
  margin-bottom: 30px;
}

.seller-info h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #303133;
}

.seller-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 5px;
}

.product-actions {
  display: flex;
  gap: 15px;
}

.product-not-found {
  padding: 50px 0;
  text-align: center;
}

.comments-section {
  padding: 40px 0;
  background-color: #fff;
}

.reviews-section {
  padding: 40px 0;
  background-color: #fff;
}

.comments-section h2 {
  font-size: 24px;
  margin-bottom: 30px;
  color: #303133;
}

.comment-form {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.comment-form .el-input {
  flex: 1;
}

.comments-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.comment-username {
  font-weight: 500;
  color: #303133;
}

.comment-time {
  color: #909399;
  font-size: 14px;
}

.comment-text {
  line-height: 1.6;
  color: #606266;
}

.empty-comments {
  padding: 30px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .product-detail-section {
    padding: 20px 0;
  }

  .product-detail {
    flex-direction: column;
    gap: 20px;
  }

  .product-images {
    width: 100%;
  }

  .main-image {
    height: 250px;
    margin-bottom: 10px;
  }

  .image-thumbnails {
    gap: 8px;
  }

  .thumbnail-item {
    width: 60px;
    height: 60px;
  }

  .product-info {
    width: 100%;
    padding: 0 15px;
  }

  .product-title {
    font-size: 20px;
    margin-bottom: 15px;
  }

  .product-price {
    font-size: 24px;
    margin-bottom: 15px;
  }

  .product-meta {
    flex-direction: column;
    gap: 10px;
    margin-bottom: 15px;
  }

  .meta-item {
    font-size: 14px;
  }

  .seller-card {
    flex-direction: column;
    text-align: center;
    padding: 15px 10px;
    margin-bottom: 15px;
  }

  .product-actions {
    flex-direction: column;
    gap: 10px;
  }

  .product-actions .el-button {
    width: 100%;
    height: 45px;
    font-size: 16px;
  }

  .product-description {
    margin-top: 30px;
    padding: 0 15px;
  }

  .product-description h3 {
    font-size: 18px;
    margin-bottom: 15px;
  }

  .product-description p {
    font-size: 14px;
    line-height: 1.6;
  }

  .reviews-section {
    padding: 20px 0;
    margin-top: 30px;
  }

  .reviews-section h3 {
    font-size: 18px;
    margin-bottom: 15px;
  }

  .comment-form {
    flex-direction: column;
    gap: 10px;
    padding: 0 15px;
  }

  .comment-form .el-input {
    width: 100%;
  }

  .comment-form .el-button {
    width: 100%;
  }

  .comments-list {
    padding: 0 15px;
  }

  .comment-item {
    padding: 15px 0;
    gap: 10px;
  }

  .comment-content {
    flex: 1;
  }

  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .comment-time {
    font-size: 12px;
  }

  .comment-text {
    font-size: 14px;
    line-height: 1.5;
  }
}

@media (max-width: 480px) {
  .product-detail-section {
    padding: 15px 0;
  }

  .main-image {
    height: 200px;
  }

  .thumbnail-item {
    width: 50px;
    height: 50px;
  }

  .product-title {
    font-size: 18px;
  }

  .product-price {
    font-size: 22px;
  }

  .product-actions .el-button {
    height: 40px;
    font-size: 14px;
  }

  .product-description h3,
  .reviews-section h3 {
    font-size: 16px;
  }

  .comment-item {
    padding: 12px 0;
  }

  .comment-text {
    font-size: 13px;
  }
}
</style>
