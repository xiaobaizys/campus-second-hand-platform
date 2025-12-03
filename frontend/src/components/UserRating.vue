<template>
  <div class="user-rating">
    <div class="rating-header">
      <span class="rating-label">信用评级</span>
      <el-tooltip content="基于交易历史、评价和平台活跃度综合计算" placement="top">
        <el-icon class="info-icon"><InfoFilled /></el-icon>
      </el-tooltip>
    </div>
    
    <div class="rating-content">
      <div class="rating-score">
        <span class="score-value">{{ ratingScore }}</span>
        <span class="score-max">/100</span>
      </div>
      
      <div class="rating-level">
        <el-tag :type="getLevelType(ratingLevel)" size="large">
          {{ getLevelText(ratingLevel) }}
        </el-tag>
      </div>
      
      <div class="rating-stars">
        <el-rate
          v-model="ratingStars"
          disabled
          show-score
          text-color="#ff9900"
          score-template="{value}"
        />
      </div>
    </div>
    
    <div class="rating-details">
      <div class="detail-item">
        <span class="detail-label">交易次数</span>
        <span class="detail-value">{{ transactionCount }}</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">好评率</span>
        <span class="detail-value">{{ positiveRate }}%</span>
      </div>
      <div class="detail-item">
        <span class="detail-label">响应时间</span>
        <span class="detail-value">{{ responseTime }}小时</span>
      </div>
    </div>
    
    <div class="rating-progress">
      <div class="progress-item">
        <span class="progress-label">信用进度</span>
        <el-progress :percentage="ratingScore" :color="getProgressColor(ratingScore)" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { InfoFilled } from '@element-plus/icons-vue'

// 定义组件属性
interface Props {
  ratingScore?: number // 信用分数 0-100
  transactionCount?: number // 交易次数
  positiveRate?: number // 好评率 0-100
  responseTime?: number // 平均响应时间(小时)
}

const props = withDefaults(defineProps<Props>(), {
  ratingScore: 85,
  transactionCount: 23,
  positiveRate: 96,
  responseTime: 2
})

// 计算星级评分 (5星制)
const ratingStars = computed(() => {
  return Math.ceil(props.ratingScore / 20)
})

// 计算信用等级
const ratingLevel = computed(() => {
  if (props.ratingScore >= 90) return 'excellent'
  if (props.ratingScore >= 80) return 'good'
  if (props.ratingScore >= 70) return 'average'
  if (props.ratingScore >= 60) return 'below_average'
  return 'poor'
})

// 获取等级类型
const getLevelType = (level: string) => {
  switch (level) {
    case 'excellent': return 'success'
    case 'good': return 'primary'
    case 'average': return 'warning'
    case 'below_average': return 'info'
    case 'poor': return 'danger'
    default: return 'info'
  }
}

// 获取等级文本
const getLevelText = (level: string) => {
  switch (level) {
    case 'excellent': return '优秀'
    case 'good': return '良好'
    case 'average': return '一般'
    case 'below_average': return '较差'
    case 'poor': return '差'
    default: return '未知'
  }
}

// 获取进度条颜色
const getProgressColor = (score: number) => {
  if (score >= 90) return '#67c23a'
  if (score >= 80) return '#409eff'
  if (score >= 70) return '#e6a23c'
  if (score >= 60) return '#909399'
  return '#f56c6c'
}
</script>

<style scoped>
.user-rating {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.rating-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.rating-label {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.info-icon {
  margin-left: 6px;
  color: #909399;
  cursor: pointer;
}

.rating-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 16px;
}

.rating-score {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.score-value {
  font-size: 36px;
  font-weight: 700;
  color: #409eff;
}

.score-max {
  font-size: 16px;
  color: #909399;
  margin-left: 2px;
}

.rating-level {
  margin-bottom: 12px;
}

.rating-stars {
  width: 100%;
  display: flex;
  justify-content: center;
}

.rating-details {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  color: #909399;
}

.detail-value {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.rating-progress {
  margin-top: 8px;
}

.progress-item {
  margin-bottom: 8px;
}

.progress-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 6px;
  display: block;
}
</style>