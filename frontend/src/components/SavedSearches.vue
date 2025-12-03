<template>
  <div class="saved-searches">
    <div class="saved-searches-header">
      <h3>保存的搜索</h3>
      <el-button 
        v-if="savedSearches.length > 0" 
        text 
        type="danger" 
        @click="clearAllSavedSearches"
      >
        清空全部
      </el-button>
    </div>
    
    <div v-if="savedSearches.length === 0" class="empty-saved-searches">
      <p>暂无保存的搜索条件</p>
      <p class="tip">进行搜索后，可以保存常用的搜索条件</p>
    </div>
    
    <div v-else class="saved-searches-list">
      <div 
        v-for="(search, index) in savedSearches" 
        :key="index"
        class="saved-search-item"
      >
        <div class="search-info" @click="applySavedSearch(search)">
          <h4>{{ search.name }}</h4>
          <div class="search-details">
            <span v-if="search.keyword">关键词: {{ search.keyword }}</span>
            <span v-if="search.categoryName">分类: {{ search.categoryName }}</span>
            <span v-if="search.minPrice && search.maxPrice">
              价格: ¥{{ search.minPrice }} - ¥{{ search.maxPrice }}
            </span>
            <span v-if="search.location">位置: {{ search.location }}</span>
            <span v-if="search.isNegotiable !== undefined">
              {{ search.isNegotiable ? '可议价' : '不可议价' }}
            </span>
            <span v-if="search.isNew !== undefined">
              {{ search.isNew ? '全新' : '二手' }}
            </span>
            <span v-if="search.deliveryMethod">
              配送: {{ getDeliveryMethodText(search.deliveryMethod) }}
            </span>
          </div>
          <div class="search-time">
            保存于 {{ formatTime(search.createdAt) }}
          </div>
        </div>
        <div class="search-actions">
          <el-button 
            text 
            type="primary" 
            @click="applySavedSearch(search)"
          >
            应用
          </el-button>
          <el-button 
            text 
            type="danger" 
            @click="deleteSavedSearch(index)"
          >
            删除
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface SavedSearch {
  name: string
  keyword?: string
  categoryId?: number
  categoryName?: string
  minPrice?: number
  maxPrice?: number
  location?: string
  isNegotiable?: boolean
  isNew?: boolean
  deliveryMethod?: string
  sortBy?: string
  sortDir?: string
  createdAt: Date
}

interface Emits {
  (e: 'apply', search: SavedSearch): void
}

const emit = defineEmits<Emits>()

// 保存的搜索条件
const savedSearches = ref<SavedSearch[]>([])

// 配送方式映射
const deliveryMethodMap: Record<string, string> = {
  'ONLINE': '线上交易',
  'OFFLINE': '线下交易',
  'DELIVERY': '快递配送',
  'PICKUP': '自提'
}

// 从本地存储加载保存的搜索
const loadSavedSearches = () => {
  try {
    const saved = localStorage.getItem('savedSearches')
    if (saved) {
      savedSearches.value = JSON.parse(saved)
    }
  } catch (error) {
    console.error('加载保存的搜索失败:', error)
  }
}

// 保存搜索条件到本地存储
const saveSavedSearches = () => {
  try {
    localStorage.setItem('savedSearches', JSON.stringify(savedSearches.value))
  } catch (error) {
    console.error('保存搜索条件失败:', error)
  }
}

// 保存当前搜索条件
const saveCurrentSearch = (searchParams: any, name?: string) => {
  const searchName = name || `搜索 ${new Date().toLocaleDateString()}`
  
  // 检查是否已存在相同名称的搜索
  const existingIndex = savedSearches.value.findIndex(s => s.name === searchName)
  
  const newSearch: SavedSearch = {
    name: searchName,
    keyword: searchParams.keyword,
    categoryId: searchParams.categoryId,
    categoryName: searchParams.categoryName,
    minPrice: searchParams.minPrice,
    maxPrice: searchParams.maxPrice,
    location: searchParams.location,
    isNegotiable: searchParams.isNegotiable,
    isNew: searchParams.isNew,
    deliveryMethod: searchParams.deliveryMethod,
    sortBy: searchParams.sortBy,
    sortDir: searchParams.sortDir,
    createdAt: new Date()
  }
  
  if (existingIndex !== -1) {
    // 更新现有搜索
    savedSearches.value[existingIndex] = newSearch
    ElMessage.success('搜索条件已更新')
  } else {
    // 添加新搜索
    savedSearches.value.unshift(newSearch)
    ElMessage.success('搜索条件已保存')
  }
  
  saveSavedSearches()
}

// 应用保存的搜索
const applySavedSearch = (search: SavedSearch) => {
  emit('apply', search)
}

// 删除保存的搜索
const deleteSavedSearch = (index: number) => {
  savedSearches.value.splice(index, 1)
  saveSavedSearches()
  ElMessage.success('搜索条件已删除')
}

// 清空所有保存的搜索
const clearAllSavedSearches = () => {
  ElMessageBox.confirm('确定要清空所有保存的搜索条件吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    savedSearches.value = []
    saveSavedSearches()
    ElMessage.success('已清空所有保存的搜索条件')
  }).catch(() => {
    // 用户取消操作
  })
}

// 获取配送方式文本
const getDeliveryMethodText = (method: string) => {
  return deliveryMethodMap[method] || method
}

// 格式化时间
const formatTime = (date: Date | string) => {
  if (!date) return ''
  
  const now = new Date()
  const targetDate = typeof date === 'string' ? new Date(date) : date
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

// 初始化
onMounted(() => {
  loadSavedSearches()
})

// 暴露方法给父组件
defineExpose({
  saveCurrentSearch
})
</script>

<style scoped>
.saved-searches {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.saved-searches-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.saved-searches-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.empty-saved-searches {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}

.empty-saved-searches p {
  margin: 5px 0;
}

.tip {
  font-size: 14px;
}

.saved-searches-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.saved-search-item {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 15px;
  transition: box-shadow 0.3s;
}

.saved-search-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-info {
  cursor: pointer;
}

.search-info h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #303133;
}

.search-details {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 8px;
}

.search-details span {
  font-size: 14px;
  color: #606266;
  background-color: #f5f7fa;
  padding: 3px 8px;
  border-radius: 4px;
}

.search-time {
  font-size: 12px;
  color: #909399;
}

.search-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
</style>