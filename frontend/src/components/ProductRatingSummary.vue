<template>
  <div class="product-rating-summary">
    <div class="rating-overview">
      <div class="average-rating">
        <div class="rating-score">{{ averageRating.toFixed(1) }}</div>
        <el-rate v-model="averageRating" disabled show-score text-color="#ff9900" />
        <div class="total-reviews">{{ totalReviews }}条评价</div>
      </div>
      
      <div class="rating-breakdown">
        <div v-for="star in 5" :key="star" class="rating-row">
          <span class="star-label">{{ 6 - star }}星</span>
          <el-progress 
            :percentage="getRatingPercentage(6 - star)" 
            :show-text="false" 
            :stroke-width="8"
            :color="getProgressColor(6 - star)"
          />
          <span class="star-count">{{ getRatingCount(6 - star) }}</span>
        </div>
      </div>
    </div>
    
    <div class="rating-tags">
      <div class="tag-title">评价关键词</div>
      <div class="tag-cloud">
        <el-tag 
          v-for="tag in ratingTags" 
          :key="tag.name"
          :type="tag.type"
          effect="plain"
          class="rating-tag"
        >
          {{ tag.name }} ({{ tag.count }})
        </el-tag>
      </div>
    </div>
    
    <div class="quality-scores">
      <div class="tag-title">商品评分</div>
      <div class="score-list">
        <div v-for="score in qualityScores" :key="score.name" class="score-item">
          <span class="score-name">{{ score.name }}</span>
          <el-rate 
            v-model="score.value" 
            disabled 
            :max="5" 
            :show-score="false"
            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          />
          <span class="score-value">{{ score.value.toFixed(1) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

// 定义评价类型
interface Review {
  id: string | number
  userId: string | number
  userName: string
  userAvatar: string
  rating: number
  content: string
  images: string[]
  likes: number
  createdAt: string | Date
  sellerReply: string | null
  sellerReplyDate: string | Date | null
}

const props = defineProps<{
  productId: string
  reviews: Review[]
}>()

// 计算平均评分
const averageRating = computed(() => {
  if (props.reviews.length === 0) return 0
  const sum = props.reviews.reduce((total, review) => total + review.rating, 0)
  return sum / props.reviews.length
})

// 总评价数
const totalReviews = computed(() => props.reviews.length)

// 获取各星级评价数量
const getRatingCount = (rating: number) => {
  return props.reviews.filter(review => review.rating === rating).length
}

// 获取各星级评价百分比
const getRatingPercentage = (rating: number) => {
  if (props.reviews.length === 0) return 0
  return (getRatingCount(rating) / props.reviews.length) * 100
}

// 获取进度条颜色
const getProgressColor = (rating: number) => {
  const colors: Record<number, string> = {
    5: '#67C23A', // 绿色
    4: '#E6A23C', // 橙色
    3: '#F7BA2A', // 黄色
    2: '#E6A23C', // 橙色
    1: '#F56C6C'  // 红色
  }
  return colors[rating] || '#909399'
}

// 评价关键词标签
const ratingTags = ref([
  { name: '质量好', count: 23, type: 'success' },
  { name: '发货快', count: 18, type: 'primary' },
  { name: '描述相符', count: 15, type: 'success' },
  { name: '包装好', count: 12, type: 'info' },
  { name: '性价比高', count: 10, type: 'warning' },
  { name: '客服态度好', count: 8, type: 'primary' },
  { name: '物流快', count: 7, type: 'info' },
  { name: '有瑕疵', count: 3, type: 'danger' }
])

// 商品质量评分
const qualityScores = ref([
  { name: '商品质量', value: 4.5 },
  { name: '外观设计', value: 4.2 },
  { name: '性价比', value: 4.3 },
  { name: '物流速度', value: 4.6 },
  { name: '服务态度', value: 4.4 }
])
</script>

<style scoped>
.product-rating-summary {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.rating-overview {
  display: flex;
  margin-bottom: 25px;
}

.average-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-right: 30px;
  border-right: 1px solid #eee;
  min-width: 150px;
}

.rating-score {
  font-size: 36px;
  font-weight: 600;
  color: #ff9900;
  margin-bottom: 8px;
}

.total-reviews {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.rating-breakdown {
  flex: 1;
  padding-left: 30px;
}

.rating-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.star-label {
  width: 40px;
  font-size: 14px;
  color: #666;
}

.rating-row .el-progress {
  flex: 1;
  margin: 0 15px;
}

.star-count {
  width: 30px;
  font-size: 14px;
  color: #666;
  text-align: right;
}

.rating-tags, .quality-scores {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.tag-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.rating-tag {
  cursor: pointer;
  transition: all 0.3s ease;
}

.rating-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.score-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.score-item {
  display: flex;
  align-items: center;
}

.score-name {
  width: 100px;
  font-size: 14px;
  color: #666;
}

.score-item .el-rate {
  flex: 1;
  margin: 0 15px;
}

.score-value {
  width: 30px;
  font-size: 14px;
  font-weight: 600;
  color: #ff9900;
  text-align: right;
}

@media (max-width: 768px) {
  .rating-overview {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .average-rating {
    padding-right: 0;
    border-right: none;
    border-bottom: 1px solid #eee;
    padding-bottom: 20px;
    flex-direction: row;
    gap: 15px;
    min-width: auto;
  }
  
  .rating-breakdown {
    padding-left: 0;
    width: 100%;
  }
  
  .rating-score {
    font-size: 28px;
  }
  
  .score-item {
    flex-wrap: wrap;
  }
  
  .score-name {
    width: 80px;
  }
  
  .score-item .el-rate {
    margin: 5px 15px;
  }
}
</style>