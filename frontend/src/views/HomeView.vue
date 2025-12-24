<template>
  <div class="home-container">
    <AppHeader />

    <!-- 轮播图模块 -->
    <div class="hero-section">
      <el-carousel :interval="3000" type="card" height="400px" autoplay>
        <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
          <div class="carousel-item" :style="{ backgroundColor: item.backgroundColor }">
            <div class="carousel-content">
              <h1>{{ item.title }}</h1>
              <p>{{ item.description }}</p>
              <div class="hero-actions">
                <el-button type="primary" size="large" @click="goToRoute(item.primaryAction.action)">
                  {{ item.primaryAction.text }}
                </el-button>
                <el-button size="large" @click="goToRoute(item.secondaryAction.action)">
                  {{ item.secondaryAction.text }}
                </el-button>
              </div>
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
          <el-button type="primary" @click="goToProducts" class="view-more-btn"> 查看更多 </el-button>
        </div>
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        <div v-else-if="products.length > 0" class="products-grid">
          <div v-for="product in products" :key="product.id" class="product-card" @click="goToProductDetail(product.id)">
            <div class="product-image">
              <img :src="product.images && product.images.length > 0 ? product.images[0] : '/placeholder.svg'" :alt="product.title" />
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

    <!-- 商品分类模块 -->
    <div class="categories-section">
      <div class="container">
        <div class="section-header">
          <h2>商品分类</h2>
        </div>
        <div v-if="categoryLoading" class="loading-container">
          <el-skeleton :rows="2" animated />
        </div>
        <div v-else-if="categories.length > 0" class="categories-grid">
          <div v-for="category in categories" :key="category.id" class="category-card" @click="goToCategoryProducts(category.id)">
            <div class="category-icon">
              <el-icon size="40">
                <component :is="getCategoryIcon(category.name)" />
              </el-icon>
            </div>
            <h3 class="category-name">{{ category.name }}</h3>
            <p class="category-count">{{ category.productCount || 0 }} 件商品</p>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-empty description="暂无分类" />
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
import { CircleCheck, Location, Money, ChatLineSquare, Iphone, Reading, House, Basketball, ShoppingBag, Camera, Food, More } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { useProductStore } from '@/store/product'
import { useCategoryStore } from '@/store/category'

const router = useRouter()
const productStore = useProductStore()
const categoryStore = useCategoryStore()

// 轮播图数据
const carouselItems = ref([
  {
    title: '校园二手交易平台',
    description: '让闲置物品流转起来，创造更多价值',
    backgroundColor: '#6a11cb',
    primaryAction: {
      text: '浏览商品',
      action: '/products',
    },
    secondaryAction: {
      text: '发布商品',
      action: '/publish',
    },
  },
  {
    title: '低价好货，应有尽有',
    description: '从教材到电子产品，从服装到生活用品，满足你的各种需求',
    backgroundColor: '#2575fc',
    primaryAction: {
      text: '立即抢购',
      action: '/products',
    },
    secondaryAction: {
      text: '了解更多',
      action: '/about',
    },
  },
  {
    title: '安全交易，放心购买',
    description: '实名认证，线下交易，让你的购物更安全',
    backgroundColor: '#409eff',
    primaryAction: {
      text: '去认证',
      action: '/profile',
    },
    secondaryAction: {
      text: '浏览规则',
      action: '/about',
    },
  },
])

// 商品数据
const products = ref([])
const loading = ref(false)

// 分类数据
const categories = ref([])
const categoryLoading = ref(false)

// 分类图标映射
const categoryIcons: { [key: string]: any } = {
  数码产品: Iphone,
  图书教材: Reading,
  生活用品: House,
  运动健身: Basketball,
  服装鞋包: ShoppingBag,
  美妆护肤: Camera,
  食品零食: Food,
  其他: More,
}

// 获取分类图标
const getCategoryIcon = (categoryName: string) => {
  return categoryIcons[categoryName] || More
}

// 跳转到指定路由
const goToRoute = (route: string) => {
  router.push(route)
}

// 跳转到商品列表页
const goToProducts = () => {
  router.push('/products')
}

// 跳转到分类商品页
const goToCategoryProducts = (categoryId: number) => {
  router.push(`/products?categoryId=${categoryId}`)
}

// 跳转到商品详情页
const goToProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 加载商品分类数据
const loadCategories = async () => {
  categoryLoading.value = true
  try {
    await categoryStore.fetchCategories()
    categories.value = categoryStore.categories
  } catch (error) {
    console.error('加载分类失败:', error)
  } finally {
    categoryLoading.value = false
  }
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
      sortDir: 'desc',
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
  let targetDate: Date

  // 根据date的类型进行处理
  if (typeof date === 'string') {
    targetDate = new Date(date)
  } else if (typeof date === 'number') {
    // 处理数字类型的时间戳
    targetDate = new Date(date)
  } else {
    // 如果date已经是Date对象，直接使用
    targetDate = date
  }

  // 检查转换是否成功
  if (isNaN(targetDate.getTime())) {
    return '未知时间'
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
  loadCategories()
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
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}

.carousel-item {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.carousel-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 60px 40px;
  position: relative;
  z-index: 1;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  text-align: center;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.3) 50%, transparent 100%);
  backdrop-filter: blur(5px);
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.carousel-item:hover .carousel-content {
  transform: scale(1.02);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
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

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 30px;
}

.hero-actions .el-button {
  padding: 14px 35px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 50px;
  transition: all 0.35s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.hero-actions .el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.25);
}

.hero-actions .el-button:nth-child(1) {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  border: none;
}

.hero-actions .el-button:nth-child(1):hover {
  background: linear-gradient(135deg, #5a10b0 0%, #2165d9 100%);
  transform: translateY(-3px) scale(1.05);
}

.hero-actions .el-button:nth-child(2) {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid white;
  backdrop-filter: blur(10px);
}

.hero-actions .el-button:nth-child(2):hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-3px) scale(1.05);
}

/* 卡片类型轮播图样式调整 */
.hero-section .el-carousel__item--card {
  color: white;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  transition: all 0.3s ease;
}

.hero-section .el-carousel__item--card.is-active {
  opacity: 1;
  transform: scale(1.05);
  transition: all 0.3s ease;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
}

/* 轮播指示器样式 */
.hero-section .el-carousel__indicator {
  background-color: rgba(255, 255, 255, 0.5);
}

.hero-section .el-carousel__indicator.is-active button {
  background-color: white;
}

/* 分类模块样式 */
.categories-section {
  padding: 60px 0;
  background-color: #fff;
}

.categories-section .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.category-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 16px;
  padding: 35px 25px;
  text-align: center;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.06);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.category-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(106, 17, 203, 0.05), transparent);
  transition: left 0.5s ease;
}

.category-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 45px rgba(0, 0, 0, 0.15);
  border-color: #6a11cb;
  background: linear-gradient(135deg, #ffffff 0%, #eef2ff 100%);
}

.category-card:hover::before {
  left: 100%;
}

.category-icon {
  color: #6a11cb;
  margin-bottom: 25px;
  transition: all 0.35s ease;
  position: relative;
  z-index: 1;
}

.category-card:hover .category-icon {
  transform: scale(1.25) rotate(5deg);
  color: #2575fc;
}

.category-name {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  transition: color 0.35s ease;
  position: relative;
  z-index: 1;
}

.category-card:hover .category-name {
  color: #6a11cb;
}

.category-count {
  font-size: 14px;
  color: #666;
  margin: 0;
  transition: all 0.35s ease;
  position: relative;
  z-index: 1;
}

.category-card:hover .category-count {
  color: #2575fc;
  transform: translateY(2px);
}

/* 添加分类卡片的底部装饰 */
.category-card::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  border-radius: 3px 3px 0 0;
  opacity: 0;
  transition: all 0.35s ease;
}

.category-card:hover::after {
  opacity: 1;
  width: 60px;
  height: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .categories-section {
    padding: 40px 0;
  }

  .categories-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 20px;
  }

  .category-card {
    padding: 25px 20px;
    border-radius: 12px;
  }

  .category-name {
    font-size: 18px;
  }

  .category-icon .el-icon {
    font-size: 32px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }

  .hero-actions .el-button {
    padding: 12px 30px;
    font-size: 16px;
    width: 200px;
  }

  .carousel-content {
    padding: 40px 30px;
    background: linear-gradient(135deg, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.4) 50%, transparent 100%);
  }

  .carousel-content h1 {
    font-size: 32px;
  }

  .carousel-content p {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }

  .category-card {
    padding: 20px 15px;
  }

  .category-name {
    font-size: 16px;
  }

  .category-icon .el-icon {
    font-size: 28px;
  }

  .hero-actions .el-button {
    padding: 10px 25px;
    font-size: 15px;
    width: 180px;
  }

  .carousel-content h1 {
    font-size: 28px;
  }

  .carousel-content p {
    font-size: 14px;
  }

  .hero-section .el-carousel {
    height: 300px;
  }

  .hero-section .el-carousel__item {
    height: 300px;
  }
}

.features-section {
  padding: 80px 0;
  background-color: #f5f7fa;
}

.features-section .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
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
  border-radius: 12px;
  padding: 40px 30px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 4px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  border-radius: 0 0 10px 10px;
  opacity: 0;
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-12px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.feature-card:hover::before {
  opacity: 1;
  width: 80px;
}

.feature-card .el-icon {
  color: #6a11cb;
  margin-bottom: 25px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.feature-card:hover .el-icon {
  color: #2575fc;
  transform: scale(1.1) rotate(5deg);
}

.feature-card h3 {
  font-size: 22px;
  margin-bottom: 20px;
  color: #303133;
  font-weight: 600;
  transition: color 0.3s ease;
  position: relative;
  z-index: 1;
}

.feature-card:hover h3 {
  color: #6a11cb;
}

.feature-card p {
  color: #606266;
  line-height: 1.7;
  font-size: 15px;
  transition: color 0.3s ease;
  position: relative;
  z-index: 1;
}

.feature-card:hover p {
  color: #404040;
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
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.product-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  height: 220px;
  overflow: hidden;
  background-color: #f8f9fa;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent 60%, rgba(0, 0, 0, 0.1) 100%);
  z-index: 1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-image::before {
  opacity: 1;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
  background-color: #f8f9fa;
  padding: 20px;
}

.product-card:hover .product-image img {
  transform: scale(1.08);
}

.product-info {
  padding: 20px;
  position: relative;
}

.product-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.product-card:hover .product-title {
  color: #409eff;
}

.product-price {
  font-size: 26px;
  font-weight: 700;
  color: #f56c6c;
  margin-bottom: 15px;
  display: flex;
  align-items: baseline;
}

.product-price::before {
  content: '¥';
  font-size: 18px;
  margin-right: 2px;
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
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.product-card:hover .product-category {
  background-color: #d9ecff;
  color: #337ecc;
}

.product-time {
  font-size: 12px;
  opacity: 0.8;
}

.product-seller {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #606266;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.product-seller .el-avatar {
  transition: transform 0.3s ease;
}

.product-card:hover .product-seller .el-avatar {
  transform: scale(1.1);
}

.product-seller span {
  font-weight: 500;
  transition: color 0.3s ease;
}

.product-card:hover .product-seller span {
  color: #409eff;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

@media (max-width: 768px) {
  .hero-section {
    padding: 60px 0;
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

@media (max-width: 480px) {
  .hero-section {
    padding: 40px 0;
  }

  .carousel-content h1 {
    font-size: 28px;
  }

  .carousel-content p {
    font-size: 14px;
    margin-bottom: 25px;
  }

  .hero-actions {
    gap: 12px;
  }

  .features-section,
  .products-section {
    padding: 40px 0;
  }

  .features-section h2,
  .products-section h2 {
    font-size: 24px;
    margin-bottom: 25px;
  }

  .features-grid,
  .products-grid {
    gap: 15px;
  }

  .feature-card,
  .product-card {
    padding: 20px;
  }

  .feature-card .el-icon {
    margin-bottom: 12px;
  }

  .feature-card h3,
  .product-title {
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
</style>
