<template>
  <div class="search-suggestions" v-if="showSuggestions">
    <div class="suggestions-header" v-if="hotSearches.length > 0">
      <h4>热门搜索</h4>
      <div class="hot-searches">
        <el-tag
          v-for="item in hotSearches"
          :key="item"
          class="hot-search-tag"
          @click="selectSuggestion(item)"
        >
          {{ item }}
        </el-tag>
      </div>
    </div>
    
    <div class="suggestions-header" v-if="searchHistory.length > 0">
      <div class="history-header">
        <h4>搜索历史</h4>
        <el-button text type="primary" @click="clearHistory">清空</el-button>
      </div>
      <div class="search-history">
        <div
          v-for="item in searchHistory"
          :key="item"
          class="history-item"
          @click="selectSuggestion(item)"
        >
          <el-icon><Clock /></el-icon>
          <span>{{ item }}</span>
        </div>
      </div>
    </div>
    
    <div class="suggestions-header" v-if="suggestions.length > 0">
      <h4>搜索建议</h4>
      <div class="search-suggestions-list">
        <div
          v-for="(item, index) in suggestions"
          :key="index"
          class="suggestion-item"
          @click="selectSuggestion(item)"
        >
          <el-icon><Search /></el-icon>
          <span v-html="highlightMatch(item, query)"></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Search, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

interface Props {
  query: string
  showSuggestions: boolean
  categories?: Array<{ id: number; name: string }>
}

interface Emits {
  (e: 'select', suggestion: string): void
}

const props = withDefaults(defineProps<Props>(), {
  categories: () => []
})

const emit = defineEmits<Emits>()

// 搜索历史
const searchHistory = ref<string[]>([])
const maxHistoryItems = 10

// 热门搜索（可以从后端获取，这里先使用静态数据）
const hotSearches = ref<string[]>([
  '手机', '笔记本电脑', '耳机', '教材', '自行车', '篮球', '吉他', '相机'
])

// 搜索建议
const suggestions = ref<string[]>([])

// 从本地存储加载搜索历史
const loadSearchHistory = () => {
  try {
    const history = localStorage.getItem('searchHistory')
    if (history) {
      searchHistory.value = JSON.parse(history)
    }
  } catch (error) {
    console.error('加载搜索历史失败:', error)
  }
}

// 保存搜索历史到本地存储
const saveSearchHistory = () => {
  try {
    localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
  } catch (error) {
    console.error('保存搜索历史失败:', error)
  }
}

// 添加搜索关键词到历史记录
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
  
  saveSearchHistory()
}

// 清空搜索历史
const clearHistory = () => {
  searchHistory.value = []
  saveSearchHistory()
  ElMessage.success('搜索历史已清空')
}

// 选择建议
const selectSuggestion = (suggestion: string) => {
  addToSearchHistory(suggestion)
  emit('select', suggestion)
}

// 高亮匹配的文本
const highlightMatch = (text: string, query: string) => {
  if (!query.trim()) return text
  
  const regex = new RegExp(`(${query})`, 'gi')
  return text.replace(regex, '<span class="highlight">$1</span>')
}

// 生成搜索建议
const generateSuggestions = () => {
  if (!props.query.trim()) {
    suggestions.value = []
    return
  }
  
  const query = props.query.toLowerCase()
  const newSuggestions: string[] = []
  
  // 从分类名称中查找匹配项
  if (props.categories && props.categories.length > 0) {
    props.categories.forEach(category => {
      if (category.name.toLowerCase().includes(query)) {
        newSuggestions.push(category.name)
      }
    })
  }
  
  // 从热门搜索中查找匹配项
  hotSearches.value.forEach(item => {
    if (item.toLowerCase().includes(query) && !newSuggestions.includes(item)) {
      newSuggestions.push(item)
    }
  })
  
  // 从搜索历史中查找匹配项
  searchHistory.value.forEach(item => {
    if (item.toLowerCase().includes(query) && !newSuggestions.includes(item)) {
      newSuggestions.push(item)
    }
  })
  
  // 限制建议数量
  suggestions.value = newSuggestions.slice(0, 8)
}

// 监听查询变化，生成搜索建议
watch(() => props.query, generateSuggestions)

// 初始化
loadSearchHistory()

// 暴露方法给父组件
defineExpose({
  addToSearchHistory
})
</script>

<style scoped>
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

.search-suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  cursor: pointer;
  color: #606266;
}

.suggestion-item:hover {
  color: #409eff;
}

:deep(.highlight) {
  color: #409eff;
  font-weight: bold;
}
</style>