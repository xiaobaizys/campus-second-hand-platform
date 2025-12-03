<template>
  <el-dialog
    v-model="dialogVisible"
    title="高级搜索"
    width="600px"
    :before-close="handleClose"
  >
    <el-form :model="searchForm" label-width="100px">
      <el-form-item label="商品名称">
        <el-input v-model="searchForm.keyword" placeholder="输入商品名称关键词" />
      </el-form-item>
      
      <el-form-item label="商品分类">
        <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable>
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="价格区间">
        <el-col :span="11">
          <el-input-number v-model="searchForm.minPrice" placeholder="最低价" :min="0" :precision="2" />
        </el-col>
        <el-col :span="2" class="text-center">-</el-col>
        <el-col :span="11">
          <el-input-number v-model="searchForm.maxPrice" placeholder="最高价" :min="0" :precision="2" />
        </el-col>
      </el-form-item>
      
      <el-form-item label="商品状态">
        <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
          <el-option label="全新" value="NEW" />
          <el-option label="九成新" value="LIKE_NEW" />
          <el-option label="八成新" value="GOOD" />
          <el-option label="七成新" value="FAIR" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="是否全新">
        <el-radio-group v-model="searchForm.isNew">
          <el-radio :label="true">全新</el-radio>
          <el-radio :label="false">二手</el-radio>
          <el-radio :label="null">不限</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="是否可议价">
        <el-radio-group v-model="searchForm.isNegotiable">
          <el-radio :label="true">可议价</el-radio>
          <el-radio :label="false">不可议价</el-radio>
          <el-radio :label="null">不限</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="交易方式">
        <el-checkbox-group v-model="searchForm.deliveryMethods">
          <el-checkbox label="ONLINE">线上交易</el-checkbox>
          <el-checkbox label="OFFLINE">线下交易</el-checkbox>
          <el-checkbox label="DELIVERY">快递配送</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      
      <el-form-item label="发布时间">
        <el-select v-model="searchForm.timeRange" placeholder="选择时间范围">
          <el-option label="不限" value="" />
          <el-option label="最近一天" value="1" />
          <el-option label="最近三天" value="3" />
          <el-option label="最近一周" value="7" />
          <el-option label="最近一月" value="30" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="排序方式">
        <el-select v-model="searchForm.sortBy" placeholder="选择排序方式">
          <el-option label="最新发布" value="createdAt" />
          <el-option label="价格从低到高" value="price_asc" />
          <el-option label="价格从高到低" value="price_desc" />
          <el-option label="商品名称" value="title" />
          <el-option label="浏览量" value="viewCount" />
        </el-select>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleReset">重置</el-button>
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import type { Category } from '@/types'

interface SearchForm {
  keyword: string
  categoryId: number | undefined
  minPrice: number | undefined
  maxPrice: number | undefined
  status: string
  isNew: boolean | null
  isNegotiable: boolean | null
  deliveryMethods: string[]
  timeRange: string
  sortBy: string
}

const props = defineProps<{
  visible: boolean
  categories: Category[]
}>()

const emit = defineEmits<{
  (e: 'update:visible', visible: boolean): void
  (e: 'search', params: any): void
}>()

// 搜索表单
const searchForm = reactive<SearchForm>({
  keyword: '',
  categoryId: undefined,
  minPrice: undefined,
  maxPrice: undefined,
  status: '',
  isNew: null,
  isNegotiable: null,
  deliveryMethods: [],
  timeRange: '',
  sortBy: 'createdAt'
})

// 监听visible变化，重置表单
watch(() => props.visible, (newVal) => {
  if (newVal) {
    handleReset()
  }
})

// 计算属性
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// 处理搜索
const handleSearch = () => {
  // 构建搜索参数
  const params: any = {}
  
  if (searchForm.keyword) params.keyword = searchForm.keyword
  if (searchForm.categoryId) params.categoryId = searchForm.categoryId
  if (searchForm.minPrice !== undefined) params.minPrice = searchForm.minPrice
  if (searchForm.maxPrice !== undefined) params.maxPrice = searchForm.maxPrice
  if (searchForm.status) params.status = searchForm.status
  if (searchForm.isNew !== null) params.isNew = searchForm.isNew
  if (searchForm.isNegotiable !== null) params.isNegotiable = searchForm.isNegotiable
  if (searchForm.deliveryMethods.length > 0) params.deliveryMethods = searchForm.deliveryMethods
  if (searchForm.timeRange) params.timeRange = searchForm.timeRange
  
  // 处理排序参数
  if (searchForm.sortBy === 'price_asc') {
    params.sortBy = 'price'
    params.sortDir = 'asc'
  } else if (searchForm.sortBy === 'price_desc') {
    params.sortBy = 'price'
    params.sortDir = 'desc'
  } else {
    params.sortBy = searchForm.sortBy
    params.sortDir = 'desc'
  }
  
  emit('search', params)
  handleClose()
}

// 重置表单
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = undefined
  searchForm.minPrice = undefined
  searchForm.maxPrice = undefined
  searchForm.status = ''
  searchForm.isNew = null
  searchForm.isNegotiable = null
  searchForm.deliveryMethods = []
  searchForm.timeRange = ''
  searchForm.sortBy = 'createdAt'
}

// 关闭对话框
const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style scoped>
.text-center {
  text-align: center;
}
</style>