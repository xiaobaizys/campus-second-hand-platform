<template>
  <div class="product-reviews">
    <div class="reviews-header">
      <h3>商品评价</h3>
      <div class="reviews-summary">
        <div class="average-rating">
          <span class="rating-score">{{ averageRating.toFixed(1) }}</span>
          <el-rate v-model="averageRating" disabled show-score text-color="#ff9900" />
          <span class="total-reviews">({{ reviews.length }}条评价)</span>
        </div>
        <div class="rating-distribution">
          <div v-for="star in 5" :key="star" class="rating-bar">
            <span>{{ 6 - star }}星</span>
            <el-progress :percentage="getRatingPercentage(6 - star)" :show-text="false" />
            <span>{{ getRatingCount(6 - star) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="reviews-filters">
      <el-radio-group v-model="filterRating" @change="filterReviews">
        <el-radio-button label="all">全部评价</el-radio-button>
        <el-radio-button label="5">好评</el-radio-button>
        <el-radio-button label="3">中评</el-radio-button>
        <el-radio-button label="1">差评</el-radio-button>
        <el-radio-button label="hasImage">有图评价</el-radio-button>
      </el-radio-group>
      <el-select v-model="sortBy" placeholder="排序方式" @change="sortReviews">
        <el-option label="默认排序" value="default" />
        <el-option label="最新评价" value="newest" />
        <el-option label="评分最高" value="highest" />
        <el-option label="评分最低" value="lowest" />
      </el-select>
    </div>
    
    <div class="reviews-list">
      <div v-if="filteredReviews.length === 0" class="empty-reviews">
        <el-empty description="暂无评价" />
      </div>
      <div v-else>
        <div v-for="review in filteredReviews" :key="review.id" class="review-item">
          <div class="review-header">
            <el-avatar :size="40" :src="review.userAvatar" />
            <div class="reviewer-info">
              <div class="reviewer-name">{{ review.userName }}</div>
              <div class="review-date">{{ formatDate(review.createdAt) }}</div>
            </div>
            <div class="review-rating">
              <el-rate v-model="review.rating" disabled />
            </div>
          </div>
          <div class="review-content">
            <p>{{ review.content }}</p>
            <div v-if="review.images && review.images.length > 0" class="review-images">
              <el-image 
                v-for="(image, index) in review.images" 
                :key="index"
                :src="image" 
                :preview-src-list="review.images"
                fit="cover"
                class="review-image"
              />
            </div>
          </div>
          <div v-if="review.sellerReply" class="seller-reply">
            <div class="reply-header">
              <span class="reply-tag">卖家回复</span>
              <span class="reply-date">{{ formatDate(review.sellerReplyDate) }}</span>
            </div>
            <p>{{ review.sellerReply }}</p>
          </div>
          <div class="review-actions">
            <el-button size="small" text @click="likeReview(review.id)">
              点赞
              {{ review.likes || 0 }}
            </el-button>
            <el-button size="small" text @click="reportReview(review.id)">举报</el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="hasPurchased && !hasReviewed" class="write-review">
      <el-button type="primary" @click="showReviewDialog = true">写评价</el-button>
    </div>
    
    <!-- 写评价对话框 -->
    <el-dialog v-model="showReviewDialog" title="写评价" width="500px">
      <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="80px">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="reviewForm.rating" show-text />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input 
            v-model="reviewForm.content" 
            type="textarea" 
            :rows="4" 
            placeholder="请分享您的使用体验"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="上传图片">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handleImageChange"
            :limit="3"
          >
            <el-icon><plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showReviewDialog = false">取消</el-button>
          <el-button type="primary" @click="submitReview">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { commentApi } from '@/api/comment'
import type { Comment } from '@/api/comment'

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

const props = defineProps({
  productId: {
    type: String,
    required: true
  },
  hasPurchased: {
    type: Boolean,
    default: false
  },
  hasReviewed: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['review-submitted'])

// 评价列表
const reviews = ref<Review[]>([])
const loading = ref(false)

// 筛选和排序
const filterRating = ref('all')
const sortBy = ref('default')
const filteredReviews = ref<Review[]>([])

// 获取评论列表
const fetchReviews = async () => {
  loading.value = true
  try {
    const response = await commentApi.getComments(Number(props.productId))
    reviews.value = response.data || []
    filterReviews()
  } catch (error) {
    ElMessage.error('获取评论失败')
  } finally {
    loading.value = false
  }
}

// 监听商品ID变化，重新获取评论
watch(() => props.productId, () => {
  fetchReviews()
})

// 写评价
const showReviewDialog = ref(false)
const reviewFormRef = ref<InstanceType<typeof import('element-plus').ElForm> | null>(null)
const reviewForm = ref({
  rating: 5,
  content: '',
  images: [] as any[]
})

const reviewRules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 10, message: '评价内容至少10个字符', trigger: 'blur' }
  ]
}

// 计算平均评分
const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((total, review) => total + review.rating, 0)
  return sum / reviews.value.length
})

// 获取各星级评价数量
const getRatingCount = (rating: number) => {
  return reviews.value.filter(review => review.rating === rating).length
}

// 获取各星级评价百分比
const getRatingPercentage = (rating: number) => {
  if (reviews.value.length === 0) return 0
  return (getRatingCount(rating) / reviews.value.length) * 100
}

// 筛选评价
const filterReviews = () => {
  let result = [...reviews.value]
  
  if (filterRating.value !== 'all') {
    if (filterRating.value === 'hasImage') {
      result = result.filter(review => review.images && review.images.length > 0)
    } else {
      const rating = parseInt(filterRating.value)
      if (rating === 5) {
        result = result.filter(review => review.rating >= 4)
      } else if (rating === 3) {
        result = result.filter(review => review.rating === 3)
      } else if (rating === 1) {
        result = result.filter(review => review.rating <= 2)
      }
    }
  }
  
  filteredReviews.value = result
  sortReviews()
}

// 排序评价
const sortReviews = () => {
  let result = [...filteredReviews.value]
  
  switch (sortBy.value) {
    case 'newest':
      result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      break
    case 'highest':
      result.sort((a, b) => b.rating - a.rating)
      break
    case 'lowest':
      result.sort((a, b) => a.rating - b.rating)
      break
    default:
      // 默认排序，按时间倒序
      result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  }
  
  filteredReviews.value = result
}

// 格式化日期
const formatDate = (dateString) => {
  // 如果dateString为undefined或null，返回当前时间
  if (!dateString) {
    return '刚刚'
  }
  
  let date;
  
  // 根据dateString的类型进行处理
  if (typeof dateString === 'string') {
    date = new Date(dateString)
  } else if (typeof dateString === 'number') {
    // 处理数字类型的时间戳
    date = new Date(dateString)
  } else {
    // 处理Date对象
    date = dateString
  }
  
  // 检查转换是否成功
  if (isNaN(date.getTime())) {
    return '刚刚'
  }
  
  const now = new Date()
  const diffTime = Math.abs(now - date)
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) {
    const diffHours = Math.floor(diffTime / (1000 * 60 * 60))
    if (diffHours === 0) {
      const diffMinutes = Math.floor(diffTime / (1000 * 60))
      return diffMinutes <= 0 ? '刚刚' : `${diffMinutes}分钟前`
    }
    return `${diffHours}小时前`
  } else if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 处理图片上传
const handleImageChange = (file, fileList) => {
  reviewForm.value.images = fileList
}

// 点赞评价
const likeReview = (reviewId) => {
  const review = reviews.value.find(r => r.id === reviewId)
  if (review) {
    review.likes = (review.likes || 0) + 1
    ElMessage.success('点赞成功')
  }
}

// 举报评价
const reportReview = (reviewId) => {
  ElMessage.info('举报已提交，我们会尽快处理')
}

// 提交评价
const submitReview = async () => {
  reviewFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 调用API提交评价
        const response = await commentApi.createComment({
          productId: Number(props.productId),
          content: reviewForm.value.content,
          rating: reviewForm.value.rating
        })
        
        // 添加到评论列表
        reviews.value.unshift({
          id: response.data.id,
          userId: response.data.user.id,
          userName: response.data.user.username,
          userAvatar: response.data.user.avatar,
          rating: response.data.rating,
          content: response.data.content,
          images: [],
          likes: 0,
          createdAt: response.data.createdAt,
          sellerReply: null,
          sellerReplyDate: null
        })
        
        filterReviews()
        
        showReviewDialog.value = false
        reviewForm.value = {
          rating: 5,
          content: '',
          images: []
        }
        
        ElMessage.success('评价提交成功')
        emit('review-submitted', response.data)
      } catch (error) {
        ElMessage.error('提交评价失败，请稍后重试')
      }
    }
  })
}

// 初始化
onMounted(() => {
  fetchReviews()
})
</script>

<style scoped>
.product-reviews {
  margin-top: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.reviews-header {
  margin-bottom: 20px;
}

.reviews-header h3 {
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: 600;
}

.reviews-summary {
  display: flex;
  align-items: center;
  gap: 30px;
}

.average-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-right: 20px;
  border-right: 1px solid #eee;
}

.rating-score {
  font-size: 24px;
  font-weight: 600;
  color: #ff9900;
}

.total-reviews {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.rating-distribution {
  flex: 1;
}

.rating-bar {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.rating-bar span:first-child {
  width: 40px;
  font-size: 14px;
  color: #666;
}

.rating-bar .el-progress {
  flex: 1;
  margin: 0 10px;
}

.rating-bar span:last-child {
  width: 30px;
  font-size: 14px;
  color: #666;
  text-align: right;
}

.reviews-filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.reviews-list {
  margin-top: 20px;
}

.review-item {
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.reviewer-info {
  flex: 1;
  margin-left: 12px;
}

.reviewer-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content {
  margin-bottom: 15px;
}

.review-content p {
  line-height: 1.6;
  margin-bottom: 10px;
}

.review-images {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
}

.seller-reply {
  background-color: #f9f9f9;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.reply-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.reply-tag {
  font-size: 12px;
  color: #409eff;
  margin-right: 8px;
}

.reply-date {
  font-size: 12px;
  color: #999;
}

.review-actions {
  display: flex;
  gap: 15px;
}

.write-review {
  margin-top: 20px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

@media (max-width: 768px) {
  .reviews-summary {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .average-rating {
    padding-right: 0;
    border-right: none;
    border-bottom: 1px solid #eee;
    padding-bottom: 15px;
    flex-direction: row;
    gap: 15px;
  }
  
  .reviews-filters {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .review-images {
    flex-wrap: wrap;
  }
  
  .review-image {
    width: 60px;
    height: 60px;
  }
}
</style>