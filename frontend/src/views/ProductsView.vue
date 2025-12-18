<template>
  <div class="products-container">
    <AppHeader />

    <div class="products-section">
      <div class="container">
        <!-- 热门搜索 -->
        <PopularSearches @select="handlePopularSearchSelect" />

        <div class="products-header">
          <h1>商品列表</h1>
          <div class="products-actions">
            <div class="search-input-container">
              <el-input v-model="searchQuery" placeholder="搜索商品" prefix-icon="Search" clearable @keyup.enter="handleSearch" @input="handleSearchInput" @focus="showSearchSuggestions = true" @blur="hideSearchSuggestions">
                <template #append>
                  <el-button icon="Search" @click="handleSearch" />
                </template>
              </el-input>
              <SearchSuggestions :query="searchQuery" :show-suggestions="showSearchSuggestions" :categories="categories" @select="handleSuggestionSelect" ref="searchSuggestionsRef" />
            </div>
            <el-select v-model="selectedCategory" placeholder="选择分类" clearable @change="handleCategoryChange">
              <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
            </el-select>
            <el-select v-model="sortBy" placeholder="排序方式" @change="handleSortChange">
              <el-option label="最新发布" value="newest" />
              <el-option label="价格从低到高" value="price_asc" />
              <el-option label="价格从高到低" value="price_desc" />
              <el-option label="按名称排序" value="name" />
            </el-select>
            <el-button type="primary" @click="handleAdvancedSearch">高级搜索</el-button>
            <el-dropdown @command="handleSavedSearchAction">
              <el-button type="info">
                保存的搜索<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="save">保存当前搜索</el-dropdown-item>
                  <el-dropdown-item command="manage">管理保存的搜索</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button v-if="selectedProducts.length > 0" type="success" @click="handleBatchBuy"> 批量购买 ({{ selectedProducts.length }}) </el-button>
          </div>
        </div>

        <div v-if="true" v-loading="loading" class="products-grid">
          <div v-for="product in products" :key="product.id" class="product-card">
            <!-- 商品选择复选框 -->
            <div class="product-checkbox">
              <el-checkbox v-model="selectedProducts" :label="product.id" @change="handleProductSelect(product)" @click.stop />
            </div>
            <div class="product-content" @click="goToProductDetail(product.id)">
              <div class="product-image">
                <img :src="product && product.images && product.images.length > 0 ? product.images[0] : '/placeholder.svg'" :alt="product ? product.title : '商品图片'" />
              </div>
              <div class="product-info">
                <h3 class="product-title">
                  <HighlightText :text="product ? product.title : '未知商品'" :query="searchQuery" />
                </h3>
                <p class="product-price">¥{{ product && product.price ? product.price.toFixed(2) : '0.00' }}</p>
                <div class="product-meta">
                  <span class="product-category">{{ product && product.category ? product.category.name : '未分类' }}</span>
                  <span class="product-time">{{ product ? formatTime(product.createdAt) : '未知时间' }}</span>
                </div>
                <div class="product-seller">
                  <el-avatar :size="24" :src="product && product.seller ? product.seller.avatar : ''" />
                  <span>{{ product && product.seller ? product.seller.username : '未知卖家' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="products.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无商品">
            <el-button type="primary" @click="resetFilters">重置筛选条件</el-button>
          </el-empty>
        </div>

        <div v-if="products.length > 0" class="pagination">
          <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[12, 24, 36, 48]" layout="sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
      </div>
    </div>

    <!-- 高级搜索对话框 -->
    <AdvancedSearchDialog v-model:visible="showAdvancedSearch" :categories="categories" @search="handleAdvancedSearchParams" />

    <!-- 保存搜索对话框 -->
    <el-dialog v-model="showSaveSearchDialog" title="保存搜索条件" width="400px">
      <el-form :model="saveSearchForm" label-width="80px">
        <el-form-item label="搜索名称">
          <el-input v-model="saveSearchForm.name" placeholder="请输入搜索名称" maxlength="30" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSaveSearchDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmSaveSearch">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 管理保存的搜索对话框 -->
    <el-dialog v-model="showManageSavedSearchesDialog" title="管理保存的搜索" width="800px">
      <SavedSearches ref="savedSearchesRef" @apply="applySavedSearch" />
    </el-dialog>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute, onBeforeRouteUpdate } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import AdvancedSearchDialog from '@/components/AdvancedSearchDialog.vue'
import SearchSuggestions from '@/components/SearchSuggestions.vue'
import HighlightText from '@/components/HighlightText.vue'
import SavedSearches from '@/components/SavedSearches.vue'
import PopularSearches from '@/components/PopularSearches.vue'
import UserRating from '@/components/UserRating.vue'
import NotificationCenter from '@/components/NotificationCenter.vue'
import { useProductStore } from '@/store/product'
import { useCategoryStore } from '@/store/category'
import type { Product, Category } from '@/types'

const router = useRouter()
const route = useRoute()
const productStore = useProductStore()
const categoryStore = useCategoryStore()

// 响应式数据
const searchQuery = ref('')
const selectedCategory = ref<number | undefined>(undefined)
const sortBy = ref('newest')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const showAdvancedSearch = ref(false)
const showSearchSuggestions = ref(false)
const searchSuggestionsRef = ref()
const showSaveSearchDialog = ref(false)
const showManageSavedSearchesDialog = ref(false)
const savedSearchesRef = ref()
const saveSearchForm = ref({
  name: '',
})
// 选中的商品数组
const selectedProducts = ref<number[]>([])
// 选中的商品详情数组
const selectedProductsDetails = ref<any[]>([])

// 计算属性
const sortDir = computed(() => {
  return sortBy.value === 'price_desc' ? 'desc' : 'asc'
})

// 使用store中的状态
const { products, loading } = productStore
const { categories } = categoryStore

// 获取商品列表
const fetchProducts = async () => {
  try {
    // 处理排序参数
    let sortParam = 'createdAt'
    if (sortBy.value === 'newest') {
      sortParam = 'createdAt'
    } else if (sortBy.value === 'price_asc') {
      sortParam = 'price'
    } else if (sortBy.value === 'price_desc') {
      sortParam = 'price'
    } else if (sortBy.value === 'name') {
      sortParam = 'title'
    }

    // 处理排序方向
    const sortDir = sortBy.value === 'price_desc' ? 'desc' : 'asc'

    const params = {
      page: currentPage.value - 1, // 后端分页从0开始
      pageSize: pageSize.value,
      category: selectedCategory.value,
      search: searchQuery.value,
      sortBy: sortParam,
      sortDir: sortDir,
      // 移除status参数，获取所有状态的商品，或让后端处理默认状态
    }

    await productStore.fetchProducts(params)
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    await categoryStore.fetchCategories()
  } catch (error) {
    ElMessage.error('获取分类列表失败')
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

// 检查商品是否可购买
const isProductAvailable = (product: any) => {
  if (!product) return false

  // 因为我们已经在获取商品列表时过滤掉了非活跃状态的商品
  // 所以这里直接返回true
  return true
}

// 跳转到商品详情页
const goToProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 处理商品选择
const handleProductSelect = (product: any) => {
  if (selectedProducts.value.includes(product.id)) {
    // 添加到选中商品详情数组
    selectedProductsDetails.value.push(product)
  } else {
    // 从选中商品详情数组中移除
    const index = selectedProductsDetails.value.findIndex((p) => p.id === product.id)
    if (index > -1) {
      selectedProductsDetails.value.splice(index, 1)
    }
  }
}

// 处理批量购买
const handleBatchBuy = () => {
  if (selectedProducts.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }

  // 跳转到订单创建页面，并传递选中的商品ID
  router.push({
    name: 'order-create',
    query: {
      productIds: selectedProducts.value.join(','),
    },
  })
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  showSearchSuggestions.value = false

  // 添加到搜索历史
  if (searchQuery.value.trim() && searchSuggestionsRef.value) {
    searchSuggestionsRef.value.addToSearchHistory(searchQuery.value)
  }

  fetchProducts()
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

// 选择搜索建议
const handleSuggestionSelect = (suggestion: string) => {
  searchQuery.value = suggestion
  showSearchSuggestions.value = false
  handleSearch()
}

// 分类变化处理
const handleCategoryChange = () => {
  currentPage.value = 1
  fetchProducts()
}

// 排序变化处理
const handleSortChange = () => {
  fetchProducts()
}

// 页面大小变化处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchProducts()
}

// 当前页变化处理
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchProducts()
}

// 重置筛选条件
const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = undefined
  sortBy.value = 'newest'
  currentPage.value = 1
  fetchProducts()
}

// 高级搜索
const handleAdvancedSearch = () => {
  showAdvancedSearch.value = true
}

// 处理高级搜索参数
const handleAdvancedSearchParams = (params: any) => {
  // 应用高级搜索参数
  currentPage.value = 1

  // 更新基本搜索参数
  if (params.keyword) searchQuery.value = params.keyword
  if (params.categoryId) selectedCategory.value = params.categoryId

  // 应用排序
  if (params.sortBy && params.sortDir) {
    if (params.sortBy === 'price' && params.sortDir === 'asc') {
      sortBy.value = 'price_asc'
    } else if (params.sortBy === 'price' && params.sortDir === 'desc') {
      sortBy.value = 'price_desc'
    } else {
      sortBy.value = params.sortBy
    }
  }

  // 使用高级搜索参数获取商品
  fetchProductsWithAdvancedParams(params)
}

// 使用高级搜索参数获取商品数据
const fetchProductsWithAdvancedParams = async (params: any) => {
  try {
    // 构建搜索参数
    const searchParams = {
      page: currentPage.value - 1, // 后端分页从0开始
      pageSize: pageSize.value,
      sortBy: sortBy.value,
      sortDir: sortDir.value,
      status: 'active', // 只获取活跃状态的商品
      ...params,
    }

    // 调用高级搜索API
    const response = await productStore.advancedSearchProducts(searchParams)

    // 处理响应数据
    let productsData = []
    if (response.data) {
      // 如果后端返回的是分页数据结构
      if (response.data.content) {
        productsData = response.data.content
        total.value = response.data.totalElements
      } else {
        // 如果直接返回商品数组
        productsData = response.data
        total.value = response.data.length
      }
    } else if (response.content) {
      // 如果直接返回分页对象
      productsData = response.content
      total.value = response.totalElements
    } else {
      // 如果直接返回商品数组
      productsData = response
      total.value = response.length
    }

    // 处理商品图片字段，确保imageUrls被正确解析为数组
    products.value = productsData.map((product: any) => {
      if (product.imageUrls && !product.images) {
        // 检查imageUrls是否为字符串，如果是则尝试解析为JSON数组
        let imageUrls = product.imageUrls
        if (typeof imageUrls === 'string') {
          try {
            // 尝试解析JSON字符串
            imageUrls = JSON.parse(imageUrls)
          } catch (e) {
            // 如果解析失败，使用空数组
            imageUrls = []
          }
        }
        // 确保imageUrls是数组
        if (!Array.isArray(imageUrls)) {
          imageUrls = []
        }
        product.images = imageUrls
      }
      return product
    })

    // 关闭高级搜索对话框
    showAdvancedSearch.value = false
  } catch (error) {
    console.error('高级搜索商品失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理保存搜索操作
const handleSavedSearchAction = (command: string) => {
  if (command === 'save') {
    // 检查是否有搜索条件
    if (!searchQuery.value && !selectedCategory.value) {
      ElMessage.warning('请先设置搜索条件')
      return
    }

    // 打开保存搜索对话框
    saveSearchForm.value.name = `搜索 ${new Date().toLocaleDateString()}`
    showSaveSearchDialog.value = true
  } else if (command === 'manage') {
    // 打开管理保存的搜索对话框
    showManageSavedSearchesDialog.value = true
  }
}

// 确认保存搜索
const confirmSaveSearch = () => {
  if (!saveSearchForm.value.name.trim()) {
    ElMessage.warning('请输入搜索名称')
    return
  }

  // 构建搜索参数
  const searchParams = {
    keyword: searchQuery.value,
    categoryId: selectedCategory.value,
    categoryName: selectedCategory.value ? getCategoryName(selectedCategory.value) : undefined,
    sortBy: sortBy.value,
    sortDir: sortDir.value,
  }

  // 保存搜索
  if (savedSearchesRef.value) {
    savedSearchesRef.value.saveCurrentSearch(searchParams, saveSearchForm.value.name)
  }

  // 关闭对话框
  showSaveSearchDialog.value = false
}

// 获取分类名称
const getCategoryName = (categoryId: number) => {
  const category = categories.value.find((c) => c.id === categoryId)
  return category ? category.name : ''
}

// 应用保存的搜索
const applySavedSearch = (savedSearch: any) => {
  // 应用搜索条件
  if (savedSearch.keyword) {
    searchQuery.value = savedSearch.keyword
  }

  if (savedSearch.categoryId) {
    selectedCategory.value = savedSearch.categoryId
  } else {
    selectedCategory.value = undefined
  }

  if (savedSearch.sortBy && savedSearch.sortDir) {
    if (savedSearch.sortBy === 'price' && savedSearch.sortDir === 'asc') {
      sortBy.value = 'price_asc'
    } else if (savedSearch.sortBy === 'price' && savedSearch.sortDir === 'desc') {
      sortBy.value = 'price_desc'
    } else {
      sortBy.value = savedSearch.sortBy
    }
  }

  // 重置页码
  currentPage.value = 1

  // 关闭对话框
  showManageSavedSearchesDialog.value = false

  // 执行搜索
  if (savedSearch.minPrice || savedSearch.maxPrice || savedSearch.location || savedSearch.isNegotiable !== undefined || savedSearch.isNew !== undefined || savedSearch.deliveryMethod) {
    // 如果有高级搜索参数，使用高级搜索
    const advancedParams = {
      keyword: savedSearch.keyword,
      categoryId: savedSearch.categoryId,
      minPrice: savedSearch.minPrice,
      maxPrice: savedSearch.maxPrice,
      location: savedSearch.location,
      isNegotiable: savedSearch.isNegotiable,
      isNew: savedSearch.isNew,
      deliveryMethod: savedSearch.deliveryMethod,
    }
    fetchProductsWithAdvancedParams(advancedParams)
  } else {
    // 否则使用基本搜索
    fetchProducts()
  }

  ElMessage.success('已应用保存的搜索条件')
}

// 处理热门搜索选择
const handlePopularSearchSelect = (keyword: string) => {
  searchQuery.value = keyword
  currentPage.value = 1
  fetchProducts()
}

// 初始化
const initPage = async () => {
  // 从路由参数中获取分类ID和搜索关键词
  const categoryId = route.query.category
  const searchKeyword = route.query.search

  if (categoryId) {
    selectedCategory.value = Number(categoryId)
  } else {
    selectedCategory.value = undefined
  }

  if (searchKeyword) {
    searchQuery.value = String(searchKeyword)
  } else {
    searchQuery.value = ''
  }

  // 重置页码
  currentPage.value = 1

  // 获取分类列表
  await fetchCategories()

  // 获取商品列表
  await fetchProducts()
}

// 组件挂载时初始化
onMounted(async () => {
  await initPage()
})

// 路由更新时重新初始化（确保每次进入页面都获取最新数据）
onBeforeRouteUpdate(async () => {
  await initPage()
})
</script>

<style scoped>
.products-container {
  min-height: 100vh;
}

.products-section {
  padding: 40px 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 140px);
}

.products-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 20px;
}

.products-header h1 {
  font-size: 28px;
  color: #303133;
}

.products-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.search-input-container {
  position: relative;
  width: 200px;
}

.products-actions .el-input {
  width: 100%;
}

.products-actions .el-select {
  width: 150px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.product-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  align-items: flex-start;
  padding: 10px;
  cursor: default;
}

.product-checkbox {
  margin-right: 10px;
  margin-top: 10px;
  cursor: pointer;
  z-index: 10;
}

.product-content {
  flex: 1;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.product-image {
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 200px;
  overflow: hidden;
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
  padding: 15px;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #303133;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
  margin-bottom: 10px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
  color: #909399;
}

.product-seller {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.empty-state {
  margin: 50px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .products-section {
    padding: 20px 0;
  }

  .products-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .products-header h1 {
    font-size: 24px;
  }

  .products-actions {
    width: 100%;
    flex-direction: column;
    gap: 10px;
  }

  .search-input-container {
    width: 100%;
  }

  .products-actions .el-select {
    width: 100%;
  }

  .products-actions .el-button {
    width: 100%;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 15px;
  }

  .product-card {
    margin-bottom: 0;
    border-radius: 8px;
    overflow: hidden;
    padding: 8px;
  }

  .product-image {
    height: 180px;
  }

  .product-title {
    font-size: 14px;
    -webkit-line-clamp: 2;
  }

  .product-price {
    font-size: 16px;
  }

  .product-meta,
  .product-seller {
    font-size: 12px;
  }

  .product-info {
    padding: 10px;
  }

  .product-info {
    padding: 10px;
  }

  .product-title {
    font-size: 14px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .product-price {
    font-size: 16px;
    margin-bottom: 5px;
  }

  .product-meta {
    font-size: 11px;
    gap: 10px;
  }

  .product-seller {
    font-size: 12px;
    gap: 5px;
  }

  .pagination {
    margin-top: 20px;
  }

  .pagination .el-pagination {
    font-size: 12px;
  }

  .pagination .el-pagination__sizes {
    display: none;
  }
}

@media (max-width: 480px) {
  .products-section {
    padding: 15px 0;
  }

  .products-header h1 {
    font-size: 20px;
  }

  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }

  .product-image {
    height: 140px;
  }

  .product-info {
    padding: 8px;
  }

  .product-title {
    font-size: 13px;
  }

  .product-price {
    font-size: 15px;
  }
}
</style>
