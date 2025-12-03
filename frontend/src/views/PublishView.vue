<template>
  <div class="publish-container">
    <AppHeader />
    
    <div class="publish-section">
      <div class="container">
        <div class="publish-form">
          <div class="form-header">
            <h2>{{ isEditMode ? '编辑商品' : '发布商品' }}</h2>
            <el-button type="default" size="small" @click="goBack" class="back-btn">
              返回
            </el-button>
          </div>
          
          <el-form
            ref="publishFormRef"
            :model="publishForm"
            :rules="publishRules"
            label-width="100px"
            size="large"
          >
            <el-form-item label="商品标题" prop="title">
              <el-input
                v-model="publishForm.title"
                placeholder="请输入商品标题"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="商品分类" prop="categoryId">
              <el-select
                v-model="publishForm.categoryId"
                placeholder="请选择商品分类"
                style="width: 100%"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="商品价格" prop="price">
              <el-input-number
                v-model="publishForm.price"
                :min="0.01"
                :precision="2"
                :step="0.01"
                style="width: 100%"
              />
              <div class="form-tip">请输入合理的价格，单位：元</div>
            </el-form-item>
            
            <el-form-item label="商品描述" prop="description">
              <el-input
                v-model="publishForm.description"
                type="textarea"
                :rows="6"
                placeholder="请详细描述您的商品，包括品牌、型号、成色、购买时间、使用情况等"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="商品图片" prop="images">
              <el-upload
                v-model:file-list="fileList"
                action="#"
                list-type="picture-card"
                :auto-upload="false"
                :limit="9"
                :on-exceed="handleExceed"
                :on-preview="handlePictureCardPreview"
                :on-change="handleChange"
                :before-upload="beforeUpload"
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
              <div class="form-tip">最多上传9张图片，第一张将作为封面图</div>
            </el-form-item>
            
            <el-form-item label="交易方式" prop="tradeMethod">
              <el-checkbox-group v-model="publishForm.tradeMethod">
                <el-checkbox label="online">线上交易</el-checkbox>
                <el-checkbox label="offline">线下交易</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            
            <el-form-item v-if="publishForm.tradeMethod.includes('offline')" label="交易地点" prop="tradeLocation">
              <el-input
                v-model="publishForm.tradeLocation"
                placeholder="请输入线下交易地点，如：学校东门"
              />
            </el-form-item>
            
            <el-form-item label="联系方式" prop="contactMethod">
              <el-radio-group v-model="publishForm.contactMethod">
                <el-radio label="phone">手机号</el-radio>
                <el-radio label="wechat">微信</el-radio>
                <el-radio label="qq">QQ</el-radio>
                <el-radio label="platform">平台私信</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="联系信息" prop="contactInfo">
              <el-input
                v-model="publishForm.contactInfo"
                placeholder="请输入您的联系方式"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="submitting"
                @click="submitForm"
              >
                {{ isEditMode ? '更新商品' : '发布商品' }}
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
    
    <!-- 图片预览对话框 -->
    <el-dialog v-model="dialogVisible" title="图片预览">
      <img w-full :src="dialogImageUrl" alt="Preview Image" />
    </el-dialog>
    
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadProps, type UploadUserFile } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { useProductStore } from '@/store/product'
import { useCategoryStore } from '@/store/category'
import { productApi } from '@/api/product'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const productStore = useProductStore()
const categoryStore = useCategoryStore()

// 编辑模式相关
const editId = ref<number | null>(null)
const isEditMode = computed(() => !!editId.value)

// 表单引用
const publishFormRef = ref<FormInstance>()

// 提交状态
const submitting = ref(false)

// 图片预览
const dialogVisible = ref(false)
const dialogImageUrl = ref('')

// 文件列表
const fileList = ref<UploadUserFile[]>([])

// 分类数据
const categories = computed(() => categoryStore.categories)

// 表单数据
const publishForm = reactive({
  title: '',
  categoryId: undefined,
  price: 0,
  description: '',
  images: [] as string[],
  tradeMethod: ['offline'],
  tradeLocation: '',
  contactMethod: 'platform',
  contactInfo: ''
})

// 表单验证规则
const publishRules: FormRules = {
  title: [
    { required: true, message: '请输入商品标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度在 5 到 50 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  images: [
    { required: true, message: '请至少上传一张商品图片', trigger: 'change' }
  ],
  tradeMethod: [
    { required: true, message: '请选择交易方式', trigger: 'change' }
  ],
  tradeLocation: [
    { required: true, message: '请输入交易地点', trigger: 'blur' }
  ],
  contactMethod: [
    { required: true, message: '请选择联系方式', trigger: 'change' }
  ],
  contactInfo: [
    { required: true, message: '请输入联系信息', trigger: 'blur' }
  ]
}

// 处理图片超出限制
const handleExceed: UploadProps['onExceed'] = () => {
  ElMessage.warning('最多只能上传9张图片')
}

// 处理图片预览
const handlePictureCardPreview: UploadProps['onPreview'] = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url as string
  dialogVisible.value = true
}

// 处理图片上传前的钩子
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 处理文件变化
const handleChange: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
  fileList.value = uploadFiles
  // 更新表单中的images字段，创建临时URL
  publishForm.images = uploadFiles.map(file => {
    if (file.raw) {
      return URL.createObjectURL(file.raw)
    }
    return file.url as string
  })
}

// 提交表单
const submitForm = async () => {
  if (!publishFormRef.value) return
  
  // 检查是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  await publishFormRef.value.validate(async (valid) => {
    if (valid) {
      // 检查是否上传了图片
      if (publishForm.images.length === 0) {
        ElMessage.warning('请至少上传一张商品图片')
        return
      }
      
      // 检查线下交易是否填写了交易地点
      if (publishForm.tradeMethod.includes('offline') && !publishForm.tradeLocation) {
        ElMessage.warning('请填写线下交易地点')
        return
      }
      
      submitting.value = true
      try {
        // 先上传图片
        const imageUrls: string[] = []
        
        for (const file of fileList.value) {
          if (file.raw) {
            const res = await productApi.uploadImage(file.raw)
            if (res && res.url) {
              imageUrls.push(res.url)
            }
          } else if (file.url) {
            // 如果是已有URL，直接使用
            imageUrls.push(file.url)
          }
        }
        
        if (imageUrls.length === 0) {
          ElMessage.error('图片上传失败，请重试')
          return
        }
        
        // 构建符合后端API要求的数据结构
        const productData = {
          title: publishForm.title,
          description: publishForm.description,
          price: publishForm.price,
          originalPrice: publishForm.price, // 默认原价等于售价
          imageUrls: imageUrls,
          categoryId: publishForm.categoryId,
          isNegotiable: publishForm.tradeMethod.includes('online'), // 如果支持线上交易则可议价
          isNew: false, // 默认不是全新
          deliveryMethod: publishForm.tradeMethod.join(','), // 将数组转换为逗号分隔的字符串
          location: publishForm.tradeLocation || '',
          contactInfo: publishForm.contactMethod === 'platform' ? '平台私信' : publishForm.contactInfo
        }
        
        console.log('提交商品数据:', productData)
        
        let res
        if (isEditMode.value && editId.value) {
          // 编辑模式：调用更新API
          res = await productApi.updateProduct(editId.value, productData)
          ElMessage.success('商品更新成功！')
        } else {
          // 发布模式：调用创建API
          res = await productApi.createProduct(productData)
          ElMessage.success('商品发布成功！')
        }
        
        if (res) {
          // 跳转到商品详情页
          const productId = isEditMode.value ? editId.value : res.id
          if (productId) {
            router.push(`/product/${productId}`)
          } else {
            ElMessage.error('操作失败，请重试')
          }
        } else {
          ElMessage.error(`${isEditMode.value ? '更新' : '发布'}商品失败，请重试`)
        }
      } catch (error: any) {
        console.error('发布商品失败:', error)
        const errorMsg = error.response?.data?.message || error.message || '商品发布失败，请稍后重试'
        ElMessage.error(errorMsg)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (!publishFormRef.value) return
  
  publishFormRef.value.resetFields()
  fileList.value = []
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 初始化
onMounted(async () => {
  // 如果用户未登录，跳转到登录页
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 获取分类数据
  try {
    await categoryStore.fetchCategories()
  } catch (error) {
    ElMessage.error('获取分类数据失败')
  }
  
  // 检查是否为编辑模式
  const editIdParam = route.query.editId
  if (editIdParam) {
    editId.value = Number(editIdParam)
    if (!isNaN(editId.value)) {
      // 加载商品详情
      await loadProductForEdit(editId.value)
    } else {
      ElMessage.error('无效的商品ID')
      router.push('/publish')
    }
  }
})

// 加载商品详情用于编辑
const loadProductForEdit = async (productId: number) => {
  try {
    const product = await productApi.getProductById(productId)
    if (product && product.data) {
      const productData = product.data
      
      // 填充表单数据
      publishForm.title = productData.title || ''
      publishForm.categoryId = productData.categoryId || undefined
      publishForm.price = productData.price || 0
      publishForm.description = productData.description || ''
      publishForm.tradeMethod = productData.deliveryMethod?.split(',') || ['offline']
      publishForm.tradeLocation = productData.location || ''
      publishForm.contactMethod = productData.contactInfo === '平台私信' ? 'platform' : 'phone'
      publishForm.contactInfo = productData.contactInfo === '平台私信' ? '' : productData.contactInfo || ''
      
      // 处理图片
      if (productData.images && productData.images.length > 0) {
        publishForm.images = productData.images
        // 转换为UploadUserFile格式
        fileList.value = productData.images.map((imageUrl: string) => ({
          url: imageUrl,
          name: `image-${Math.random().toString(36).substring(2, 11)}`,
          status: 'success' as const
        }))
      }
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败，请稍后重试')
    router.push('/publish')
  }
}
</script>

<style scoped>
.publish-container {
  min-height: 100vh;
}

.publish-section {
  padding: 40px 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 140px);
}

.publish-form {
  max-width: 800px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.publish-form h2 {
  margin: 0;
  color: #303133;
}

.back-btn {
  font-size: 12px;
  padding: 6px 12px;
  background-color: #f5f7fa;
  border-color: #dcdfe6;
  color: #606266;
  transition: all 0.3s;
}

.back-btn:hover {
  background-color: #ecf5ff;
  border-color: #c6e2ff;
  color: #409eff;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
  line-height: 120px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 120px;
  height: 120px;
}

@media (max-width: 768px) {
  .publish-section {
    padding: 20px 0;
  }
  
  .publish-form {
    padding: 20px;
  }
  
  .publish-form h2 {
    font-size: 22px;
    margin-bottom: 20px;
  }
  
  :deep(.el-form-item__label) {
    width: 80px !important;
    font-size: 14px;
  }
  
  :deep(.el-input__inner) {
    font-size: 14px;
  }
  
  :deep(.el-textarea__inner) {
    font-size: 14px;
  }
  
  :deep(.el-button) {
    font-size: 14px;
  }
  
  :deep(.el-upload--picture-card) {
    width: 100px;
    height: 100px;
    line-height: 100px;
  }
  
  :deep(.el-upload-list--picture-card .el-upload-list__item) {
    width: 100px;
    height: 100px;
  }
  
  .form-tip {
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .publish-section {
    padding: 15px 0;
  }
  
  .publish-form {
    padding: 15px;
  }
  
  .publish-form h2 {
    font-size: 20px;
    margin-bottom: 15px;
  }
  
  :deep(.el-form-item__label) {
    width: 70px !important;
    font-size: 13px;
  }
  
  :deep(.el-input__inner) {
    font-size: 13px;
  }
  
  :deep(.el-textarea__inner) {
    font-size: 13px;
  }
  
  :deep(.el-button) {
    font-size: 13px;
  }
  
  :deep(.el-upload--picture-card) {
    width: 80px;
    height: 80px;
    line-height: 80px;
  }
  
  :deep(.el-upload-list--picture-card .el-upload-list__item) {
    width: 80px;
    height: 80px;
  }
  
  .form-tip {
    font-size: 10px;
  }
}
</style>