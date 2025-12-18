<template>
  <div class="home-container">
    <AppHeader />
    
    <!-- 轮播图模块 -->
    <div class="hero-section">
      <el-carousel :interval="3000" type="card" height="400px" autoplay>
        <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
          <div class="carousel-content">
            <h1>{{ item.title }}</h1>
            <p>{{ item.description }}</p>
            <div class="hero-actions">
              <el-button type="primary" size="large" @click="item.primaryAction.action">
                {{ item.primaryAction.text }}
              </el-button>
              <el-button size="large" @click="item.secondaryAction.action">
                {{ item.secondaryAction.text }}
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    

    
    <!-- 商品列表模块 -->
    <div class="products-section">
      <div class="container">
        <div class="section-header">
          <h2>热门商品</h2>
          <el-button type="primary" @click="goToProducts" class="view-more-btn">
            查看更多
          </el-button>
        </div>
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        <div v-else-if="products.length > 0" class="products-grid">
          <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="goToProductDetail(product.id)"
          >
            <div class="product-image">
              <img 
                :src="product.images && product.images.length > 0 ? product.images[0] : '/placeholder.svg'" 
                :alt="product.title"
              />
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ product.title }}</h3>
              <p class="product-price">¥{{ product.price ? product.price.toFixed(2) : '0.00' }}</p>
              <div class="product-meta">
                <span class="product-category">{{ product.categoryName || '未分类' }}</span>
                <span class="product-time">{{ formatTime(product.createdAt) }}</span>
              </div>
              <div class="product-seller">
                <el-avatar :size="24" :src="product.sellerAvatar || ''" />
                <span>{{ product.sellerName || '未知卖家' }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-empty description="暂无商品">
            <el-button type="primary" @click="goToProducts">去发布</el-button>
          </el-empty>
        </div>
      </div>
    </div>
    
    <div class="features-section">
      <div class="container">
        <h2>平台特色</h2>
        <div class="features-grid">
          <div class="feature-card">
            <el-icon size="40"><CircleCheck /></el-icon>
            <h3>安全可靠</h3>
            <p>实名认证，交易安全有保障</p>
          </div>
          <div class="feature-card">
            <el-icon size="40"><Location /></el-icon>
            <h3>校内交易</h3>
            <p>面对面交易，方便快捷</p>
          </div>
          <div class="feature-card">
            <el-icon size="40"><Money /></el-icon>
            <h3>价格实惠</h3>
            <p>学生之间交易，价格更实惠</p>
          </div>
          <div class="feature-card">
            <el-icon size="40"><ChatLineSquare /></el-icon>
            <h3>即时沟通</h3>
            <p>在线沟通，随时了解商品详情</p>
          </div>
        </div>
      </div>
    </div>
    
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { CircleCheck, Location, Money, ChatLineSquare } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { useProductStore } from '@/store/product'

const router = useRouter()
const productStore = useProductStore()

// 轮播图数据
const carouselItems = ref([
  {
    title: '校园二手交易平台',
    description: '让闲置物品流转起来，创造更多价值',
    primaryAction: {
      text: '浏览商品',
      action: () => router.push('/products')
    },
    secondaryAction: {
      text: '发布商品',
      action: () => router.push('/publish')
    }
  },
  {
    title: '低价好货，应有尽有',
    description: '从教材到电子产品，从服装到生活用品，满足你的各种需求',
    primaryAction: {
      text: '立即抢购',
      action: () => router.push('/products')
    },
    secondaryAction: {
      text: '了解更多',
      action: () => router.push('/about')
    }
  },
  {
    title: '安全交易，放心购买',
    description: '实名认证，线下交易，让你的购物更安全',
    primaryAction: {
      text: '去认证',
      action: () => router.push('/profile')
    },
    secondaryAction: {
      text: '浏览规则',
      action: () => router.push('/about')
    }
  }
])

// 商品数据
const products = ref([])
const loading = ref(false)

// 跳转到商品列表页
const goToProducts = () => {
  router.push('/products')
}

// 跳转到商品详情页
const goToProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 跳转到发布商品页
const goToPublish = () => {
  // 这里需要判断用户是否登录
  router.push('/publish')
}



// 加载商品数据
const loadProducts = async () => {
  loading.value = true
  try {
    // 获取热门商品或最新商品
    const params = {
      page: 0,
      pageSize: 6,
      sortBy: 'createdAt',
      sortDir: 'desc'
    }
    await productStore.fetchProducts(params)
    // 直接使用productStore中的products状态，因为fetchProducts已经处理了图片字段名转换
    products.value = productStore.products
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (date: Date | string | number) => {
  if (!date) return '未知时间'
  
  const now = new Date()
  let targetDate: Date;
  
  // 根据date的类型进行处理
  if (typeof date === 'string') {
    targetDate = new Date(date);
  } else if (typeof date === 'number') {
    // 处理数字类型的时间戳
    targetDate = new Date(date);
  } else {
    // 如果date已经是Date对象，直接使用
    targetDate = date;
  }
  
  // 检查转换是否成功
  if (isNaN(targetDate.getTime())) {
    return '未知时间';
  }
  
  const diff = now.getTime() - targetDate.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60))
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60))
      return minutes <= 1 ? '刚刚' : `${minutes}分钟前`
    }
    return `${hours}小时前`
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return targetDate.toLocaleDateString()
  }
}

// 生命周期钩子
onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
  padding: 40px 0;
  text-align: center;
}

.hero-section .el-carousel {
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 8px;
  overflow: hidden;
}

.hero-section .el-carousel__item {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}

.carousel-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.carousel-content h1 {
  font-size: 48px;
  margin-bottom: 20px;
  font-weight: 700;
  color: white;
}

.carousel-content p {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
  color: white;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.hero-actions .el-button {
  padding: 12px 30px;
  font-size: 18px;
}

/* 卡片类型轮播图样式调整 */
.hero-section .el-carousel__item--card {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.hero-section .el-carousel__item--card.is-active {
  opacity: 1;
  transform: scale(1.05);
  transition: all 0.3s;
}

/* 轮播指示器样式 */
.hero-section .el-carousel__indicator {
  background-color: rgba(255, 255, 255, 0.5);
}

.hero-section .el-carousel__indicator.is-active button {
  background-color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-section {
    padding: 20px 0;
  }
  
  .carousel-content h1 {
    font-size: 32px;
  }
  
  .carousel-content p {
    font-size: 16px;
    margin-bottom: 30px;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }
  
  .hero-actions .el-button {
    width: 200px;
    margin-bottom: 10px;
  }
}

.features-section {
  padding: 80px 0;
}

.features-section h2 {
  text-align: center;
  font-size: 36px;
  margin-bottom: 50px;
  color: #303133;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-card {
  background-color: #fff;
  border-radius: 10px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.feature-card .el-icon {
  color: #409eff;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #303133;
}

.feature-card p {
  color: #606266;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 60px 0;
  }
  
  .hero-content h1 {
    font-size: 32px;
  }
  
  .hero-content p {
    font-size: 16px;
    margin-bottom: 30px;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }
  
  .features-section {
    padding: 50px 0;
  }
  
  .features-section h2 {
    font-size: 28px;
    margin-bottom: 30px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .feature-card {
    padding: 25px;
  }
  
  .feature-card .el-icon {
    margin-bottom: 15px;
  }
  
  .feature-card h3 {
    font-size: 18px;
    margin-bottom: 10px;
  }
  
  .feature-card p {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 40px 0;
  }
  
  .hero-content h1 {
    font-size: 28px;
  }
  
  .hero-content p {
    font-size: 14px;
    margin-bottom: 25px;
  }
  
  .hero-actions {
    gap: 12px;
  }
  
  .features-section, .products-section {
    padding: 40px 0;
  }
  
  .features-section h2, .products-section h2 {
    font-size: 24px;
    margin-bottom: 25px;
  }
  
  .features-grid, .products-grid {
    gap: 15px;
  }
  
  .feature-card, .product-card {
    padding: 20px;
  }
  
  .feature-card .el-icon {
    margin-bottom: 12px;
  }
  
  .feature-card h3, .product-title {
    font-size: 16px;
    margin-bottom: 8px;
  }
  
  .feature-card p {
    font-size: 13px;
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .view-more-btn {
    width: 100%;
  }
}

/* 商品列表样式 */
.products-section {
  padding: 80px 0;
  background-color: #f5f7fa;
}

.products-section .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.section-header h2 {
  font-size: 36px;
  color: #303133;
  margin: 0;
}

.view-more-btn {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.view-more-btn:hover {
  background-color: #66b1ff;
}

.loading-container {
  margin: 20px 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
}

.product-card {
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background-color: #f0f0f0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s;
  background-color: #f5f7fa;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 20px;
}

.product-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.product-price {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
  margin-bottom: 15px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
  color: #909399;
}

.product-category {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.product-time {
  font-size: 12px;
}

.product-seller {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

@media (max-width: 768px) {
  .products-section {
    padding: 50px 0;
  }
  
  .section-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .section-header h2 {
    font-size: 28px;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 20px;
  }
  
  .product-title {
    font-size: 16px;
  }
  
  .product-price {
    font-size: 20px;
  }
  
  .product-info {
    padding: 15px;
  }
}
</style>