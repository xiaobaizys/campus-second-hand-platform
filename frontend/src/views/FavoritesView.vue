<template>
  <div class="favorites-container">
      <!-- 如果用户未登录，显示登录提示 -->
      <div v-if="!userStore.isAuthenticated" class="login-prompt">
        <el-empty description="请先登录后查看收藏列表">
          <el-button type="primary" @click="goToLogin">去登录</el-button>
        </el-empty>
      </div>
      
      <!-- 用户已登录，显示收藏列表 -->
      <template v-else>
        <AppHeader />

        <div class="favorites-section">
          <div class="container">
            <div class="page-header">
              <h1>我的收藏</h1>
              <el-breadcrumb separator="/">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>我的收藏</el-breadcrumb-item>
              </el-breadcrumb>
            </div>

            <div class="favorites-toolbar">
              <div class="toolbar-left">
                <el-input
                  v-model="searchQuery"
                  placeholder="搜索收藏的商品"
                  clearable
                  @keyup.enter="handleSearch"
                  style="width: 300px"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                  <template #append>
                    <el-button icon="Search" @click="handleSearch" />
                  </template>
                </el-input>
                
                <el-select v-model="selectedCategory" placeholder="选择分类" clearable @change="handleCategoryChange">
                  <el-option label="全部分类" value="" />
                  <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  />
                </el-select>
              </div>
              
              <div class="toolbar-right">
                <el-button type="primary" @click="batchRemoveFavorites" :disabled="selectedFavorites.length === 0">
                  批量取消收藏 ({{ selectedFavorites.length }})
                </el-button>
              </div>
            </div>

            <div v-loading="loading" class="favorites-content">
              <div v-if="favorites.length > 0" class="favorites-grid">
                <div
                  v-for="product in favorites"
                  :key="product.id"
                  class="product-card"
                  :class="{ 
                    'selected': selectedFavorites.includes(product.id),
                    'product-sold-out': product.status === 'sold',
                    'product-offline': product.status === 'offline'
                  }"
                >
                  <div class="product-checkbox">
                    <el-checkbox
                      v-model="selectedFavorites"
                      :label="product.id"
                      @change="handleSelectionChange"
                    />
                  </div>
                  
                  <!-- 商品状态标签 -->
                  <div v-if="product.status === 'sold'" class="product-status-label sold">已卖出</div>
                  <div v-else-if="product.status === 'offline'" class="product-status-label offline">已下架</div>
                  
                  <div class="product-image" @click="goToProductDetail(product.id)">
                    <img :src="product.images && product.images.length > 0 ? product.images[0] : '/placeholder.svg'" :alt="product.title" />
                  </div>
                  
                  <div class="product-info">
                    <h3 class="product-title" @click="goToProductDetail(product.id)">{{ product.title }}</h3>
                    <div class="product-meta">
                      <span class="product-price">¥{{ product.price ? product.price.toFixed(2) : '0.00' }}</span>
                      <span v-if="product.originalPrice && product.originalPrice > product.price" class="original-price">
                        ¥{{ product.originalPrice ? product.originalPrice.toFixed(2) : '0.00' }}
                      </span>
                    </div>
                    <div class="product-seller">
                      <el-avatar :size="20" :src="product.sellerAvatar || '/default-avatar.png'" />
                      <span>{{ product.sellerName }}</span>
                    </div>
                    <div class="product-time">
                      收藏于 {{ formatDate(product.favoriteCreatedAt) }}
                    </div>
                  </div>
                  
                  <div class="product-actions">
                    <el-button size="small" type="primary" @click="goToProductDetail(product.id)">查看详情</el-button>
                    <el-button size="small" type="danger" @click="removeFavorite(product.id)">取消收藏</el-button>
                  </div>
                </div>
              </div>
              
              <div v-else-if="!loading" class="empty-state">
                <el-empty description="暂无收藏商品" />
                <el-button type="primary" @click="goToProducts">去逛逛</el-button>
              </div>
              
              <div v-if="favorites.length > 0" class="pagination">
                <el-pagination
                  v-model:current-page="currentPage"
                  v-model:page-size="pageSize"
                  :page-sizes="[12, 24, 48]"
                  :total="total"
                  layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                />
              </div>
            </div>
          </div>
        </div>
      </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import { useUserStore } from '@/store/user'
import { favoriteApi, categoryApi } from '@/api'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const favorites = ref([])
const categories = ref([])
const searchQuery = ref('')
const selectedCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const selectedFavorites = ref([])

// 获取收藏列表
const fetchFavorites = async () => {
  console.log('开始获取收藏列表...')
  console.log('当前用户token:', userStore.token ? '存在' : '不存在')
  console.log('当前用户信息:', userStore.user)
  
  if (!userStore.isAuthenticated) {
    console.log('用户未认证，显示错误消息')
    ElMessage.error('请先登录')
    return
  }

  loading.value = true
  try {
    const params: any = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    if (searchQuery.value) {
      params.keyword = searchQuery.value
    }
    
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    
    console.log('请求参数:', params)
    console.log('请求URL:', '/api/favorites/user')
    const response = await favoriteApi.getUserFavorites(params)
    console.log('收藏列表API响应:', response)
    
    // 处理响应数据 - 响应拦截器已经处理了data字段
    let products = []
    if (response.content) {
      console.log('响应是分页对象:', response)
      products = response.content
      total.value = response.totalElements
    } else if (Array.isArray(response)) {
      console.log('响应是数组:', response)
      products = response
      total.value = response.length
    } else {
      console.error('未知的响应格式:', response)
      ElMessage.error('获取收藏列表失败: 响应格式错误')
      return
    }
    
    // 处理商品图片字段名转换：将imageUrls转换为images
    favorites.value = products.map((product: any) => {
      // 检查并处理imageUrls字段
      if (product.imageUrls) {
        // 确保imageUrls是数组
        let imageUrls = product.imageUrls;
        if (!Array.isArray(imageUrls)) {
          imageUrls = [];
        }
        product.images = imageUrls;
      } else {
        // 如果imageUrls字段不存在，设置为空数组
        product.images = [];
      }
      return product;
    })
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    console.error('错误详情:', error.response)
    if (error.response && error.response.status === 401) {
      ElMessage.error('请先登录')
      router.push('/login')
    } else {
      ElMessage.error('获取收藏列表失败')
    }
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await categoryApi.getAllCategories()
    if (response.data) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchFavorites()
}

// 处理分类变化
const handleCategoryChange = () => {
  currentPage.value = 1
  fetchFavorites()
}

// 处理分页大小变化
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchFavorites()
}

// 处理当前页变化
const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
  fetchFavorites()
}

// 处理选择变化
const handleSelectionChange = () => {
  // 选择变化时的处理逻辑
}

// 跳转到商品详情页
const goToProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 跳转到商品列表页
const goToProducts = () => {
  router.push('/products')
}

// 取消收藏
const removeFavorite = async (productId: number) => {
  try {
    await ElMessageBox.confirm('确认取消收藏该商品？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await favoriteApi.removeFavorite(productId)
    ElMessage.success('已取消收藏')
    
    // 重新获取收藏列表
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

// 批量取消收藏
const batchRemoveFavorites = async () => {
  if (selectedFavorites.value.length === 0) {
    ElMessage.warning('请选择要取消收藏的商品')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确认取消收藏选中的 ${selectedFavorites.value.length} 个商品？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 批量删除收藏
    await favoriteApi.batchRemoveFavorites(selectedFavorites.value)
    ElMessage.success(`已取消收藏 ${selectedFavorites.value.length} 个商品`)
    
    // 清空选择
    selectedFavorites.value = []
    
    // 重新获取收藏列表
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量取消收藏失败:', error)
      ElMessage.error('批量取消收藏失败')
    }
  }
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 页面加载时获取收藏列表
onMounted(() => {
  fetchFavorites()
  fetchCategories()
})
</script>

<style scoped>
.login-prompt {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.favorites-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.favorites-section {
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.favorites-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.toolbar-left {
  display: flex;
  gap: 15px;
  align-items: center;
}

.favorites-content {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.product-card {
  position: relative;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.product-card:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transform: translateY(-3px);
}

.product-card.selected {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.product-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 2;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 4px;
  padding: 4px;
}

.product-image {
  height: 200px;
  overflow: hidden;
  cursor: pointer;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s;
  background-color: #f5f7fa;
}

.product-image:hover img {
  transform: scale(1.05);
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  cursor: pointer;
}

.product-title:hover {
  color: #409eff;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.product-price {
  font-size: 18px;
  font-weight: 500;
  color: #f56c6c;
}

.original-price {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
}

.product-seller {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.product-time {
  font-size: 12px;
  color: #909399;
}

.product-actions {
  padding: 0 15px 15px;
  display: flex;
  gap: 10px;
}

.product-status-label {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 8px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
  color: white;
  z-index: 2;
}

.product-status-label.sold {
  background-color: #f56c6c;
}

.product-status-label.offline {
  background-color: #909399;
}

.product-sold-out,
.product-offline {
  opacity: 0.7;
}

.product-sold-out .product-title,
.product-offline .product-title {
  text-decoration: line-through;
}

.product-actions .el-button {
  flex: 1;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .favorites-toolbar {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .toolbar-left {
    flex-direction: column;
    gap: 10px;
  }
  
  .favorites-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 15px;
  }
  
  .product-actions {
    flex-direction: column;
  }
}
</style>