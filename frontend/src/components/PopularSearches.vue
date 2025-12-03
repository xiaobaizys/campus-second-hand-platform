<template>
  <div class="popular-searches">
    <div class="popular-searches-header">
      <h3>热门搜索</h3>
      <el-button 
        type="text" 
        size="small" 
        @click="refreshPopularSearches"
        :loading="loading"
      >
        刷新
      </el-button>
    </div>
    
    <div class="popular-searches-content" v-if="!loading && popularSearches.length > 0">
      <el-tag
        v-for="(item, index) in popularSearches"
        :key="index"
        class="popular-search-tag"
        :type="getTagType(index)"
        effect="light"
        @click="handleTagClick(item)"
      >
        {{ item.keyword }}
      </el-tag>
    </div>
    
    <div class="popular-searches-empty" v-else-if="!loading">
      <el-empty description="暂无热门搜索" :image-size="60" />
    </div>
    
    <div class="popular-searches-loading" v-else>
      <el-skeleton :rows="3" animated />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 定义组件属性
interface Props {
  maxItems?: number // 最大显示数量
}

// 定义组件事件
interface Emits {
  (e: 'select', keyword: string): void
}

const props = withDefaults(defineProps<Props>(), {
  maxItems: 10
})

const emit = defineEmits<Emits>()

// 响应式数据
const loading = ref(false)
const popularSearches = ref<Array<{keyword: string, count: number}>>([])

// 获取标签类型
const getTagType = (index: number) => {
  const types = ['danger', 'warning', 'primary', 'success', 'info']
  return types[index % types.length]
}

// 处理标签点击
const handleTagClick = (item: {keyword: string, count: number}) => {
  emit('select', item.keyword)
}

// 刷新热门搜索
const refreshPopularSearches = async () => {
  loading.value = true
  try {
    // 模拟API调用，实际项目中应该调用后端API
    // const response = await api.getPopularSearches()
    
    // 模拟数据
    setTimeout(() => {
      popularSearches.value = [
        { keyword: '二手手机', count: 156 },
        { keyword: '笔记本电脑', count: 142 },
        { keyword: '教材教辅', count: 128 },
        { keyword: '运动鞋', count: 115 },
        { keyword: '耳机音响', count: 98 },
        { keyword: '自行车', count: 87 },
        { keyword: '考研资料', count: 76 },
        { keyword: '游戏机', count: 65 },
        { keyword: '护肤品', count: 54 },
        { keyword: '宿舍用品', count: 43 },
        { keyword: '篮球', count: 32 },
        { keyword: '键盘鼠标', count: 28 }
      ].slice(0, props.maxItems)
      
      loading.value = false
    }, 500)
  } catch (error) {
    console.error('获取热门搜索失败:', error)
    ElMessage.error('获取热门搜索失败')
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  refreshPopularSearches()
})
</script>

<style scoped>
.popular-searches {
  margin-top: 20px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.popular-searches-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.popular-searches-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.popular-searches-content {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.popular-search-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.popular-search-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.popular-searches-empty {
  padding: 20px 0;
  text-align: center;
}

.popular-searches-loading {
  padding: 10px 0;
}
</style>