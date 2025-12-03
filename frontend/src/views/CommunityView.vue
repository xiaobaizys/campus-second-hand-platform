<template>
  <div class="community-page">
    <AppHeader />
    <div class="community-container">
      <!-- 页面标题和快捷发帖按钮 -->
      <div class="community-header">
        <h2>社区讨论</h2>
        <div class="create-buttons">
          <el-dropdown trigger="click" @command="handleCreateCommand">
            <el-button type="primary" class="create-btn">
              <Plus /> 发布讨论
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="普通讨论">普通讨论</el-dropdown-item>
                <el-dropdown-item command="求购帖">求购帖</el-dropdown-item>
                <el-dropdown-item command="转让帖">转让帖</el-dropdown-item>
                <el-dropdown-item command="答疑帖">答疑帖</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 筛选搜索区域 -->
      <div class="community-filters">
        <div class="filter-group">
          <!-- 关键词搜索 -->
          <el-input v-model="searchKeyword" placeholder="搜索讨论（标题/内容/发布人）" clearable class="search-input" @keyup.enter="applyFilters">
            <template #prefix>
              <Search />
            </template>
          </el-input>

          <!-- 校区筛选 -->
          <el-select v-model="selectedCampus" placeholder="选择校区" clearable>
            <el-option v-for="campus in campuses" :key="campus" :label="campus" :value="campus" />
          </el-select>

          <!-- 标签筛选 -->
          <el-select v-model="selectedTags" placeholder="选择标签" multiple collapse-tags>
            <el-option v-for="tag in presetTags" :key="tag" :label="tag" :value="tag" />
          </el-select>

          <!-- 排序选择 -->
          <el-select v-model="sortOption" placeholder="排序方式">
            <el-option label="最新" value="createdAt" />
            <el-option label="最热" value="likeCount" />
            <el-option label="回复最多" value="commentCount" />
          </el-select>

          <!-- 筛选按钮 -->
          <el-button type="primary" @click="applyFilters">筛选</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </div>

      <!-- 帖子列表区域 -->
      <div class="discussions-list">
        <!-- 加载状态 -->
        <div v-if="loading" class="skeleton-list">
          <div v-for="i in 6" :key="i" class="skeleton-card">
            <div class="skeleton-header">
              <div class="skeleton-avatar"></div>
              <div class="skeleton-info">
                <div class="skeleton-line"></div>
                <div class="skeleton-line short"></div>
              </div>
            </div>
            <div class="skeleton-title"></div>
            <div class="skeleton-content"></div>
            <div v-if="Math.random() > 0.5" class="skeleton-images"></div>
            <div class="skeleton-footer">
              <div class="skeleton-tags"></div>
              <div class="skeleton-stats"></div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="filteredDiscussions.length === 0" class="empty-state">
          <el-empty description="暂无相关讨论，快来发布第一条" image="empty">
            <el-button type="primary" @click="showCreateDialog = true"> <Plus /> 发布讨论 </el-button>
          </el-empty>
        </div>

        <!-- 帖子列表 -->
        <div v-else class="discussion-cards">
          <el-card v-for="discussion in filteredDiscussions" :key="discussion.id" class="discussion-card" @click="goToDiscussionDetail(discussion.id)">
            <!-- 帖子标题 -->
            <h3 class="discussion-title">{{ discussion.title }}</h3>

            <!-- 帖子摘要 -->
            <p class="discussion-summary">{{ getSummary(discussion.content) }}</p>

            <!-- 帖子图片 -->
            <div v-if="discussion.images && discussion.images.length > 0" class="discussion-images">
              <el-image v-for="(image, index) in discussion.images.slice(0, 3)" :key="index" :src="image" fit="cover" class="discussion-image" />
              <div v-if="discussion.images.length > 3" class="image-count">{{ discussion.images.length }}</div>
            </div>

            <!-- 发布人信息 -->
            <div class="user-info">
              <el-avatar :src="discussion.user?.avatar || ''" :size="40" class="user-avatar" />
              <div class="user-details">
                <div class="username-row">
                  <span class="username">{{ discussion.user?.username || '未知用户' }}</span>
                  <el-tag v-if="discussion.user?.isCampusVerified" size="small" type="success" class="verified-tag"> 校园认证 </el-tag>
                </div>
                <div class="meta-info">
                  <span class="campus">{{ discussion.campus }}</span>
                  <span class="time">{{ formatDate(discussion.createdAt) }}</span>
                </div>
              </div>
            </div>

            <!-- 标签 -->
            <div class="discussion-tags">
              <el-tag v-for="tag in discussion.tags" :key="tag" size="small" :type="getTagType(tag)" class="tag-item">
                {{ tag }}
              </el-tag>
            </div>

            <!-- 互动统计 -->
            <div class="discussion-stats">
              <div class="stat-item" title="点赞">
                <StarFilled :fill="discussion.liked ? '#ff4d4f' : 'currentColor'" />
                <span>{{ discussion.likeCount }}</span>
                <span class="stat-label">点赞</span>
              </div>
              <div class="stat-item" title="评论">
                <ChatDotRound />
                <span>{{ discussion.commentCount }}</span>
                <span class="stat-label">评论</span>
              </div>
              <div class="stat-item" title="收藏">
                <Collection :fill="discussion.favorited ? '#ff4d4f' : 'currentColor'" />
                <span>{{ discussion.favoriteCount }}</span>
                <span class="stat-label">收藏</span>
              </div>
              <div class="stat-item" title="浏览">
                <View />
                <span>{{ discussion.viewCount }}</span>
                <span class="stat-label">浏览</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="!loading && filteredDiscussions.length > 0" class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="totalDiscussions" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>

      <!-- 底部悬浮快捷发帖按钮 -->
      <div class="floating-create-btn">
        <el-dropdown trigger="click" @command="handleCreateCommand">
          <el-button type="primary" circle size="large">
            <Plus />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="普通讨论">普通讨论</el-dropdown-item>
              <el-dropdown-item command="求购帖">求购帖</el-dropdown-item>
              <el-dropdown-item command="转让帖">转让帖</el-dropdown-item>
              <el-dropdown-item command="答疑帖">答疑帖</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <!-- 发帖弹窗 -->
      <el-dialog v-model="showCreateDialog" :title="`发布${newDiscussion.type}`" width="800px" destroy-on-close>
        <el-form :model="newDiscussion" label-position="top" :rules="formRules" ref="formRef">
          <!-- 帖子类型选择 -->
          <el-form-item label="帖子类型" required>
            <el-radio-group v-model="newDiscussion.type">
              <el-radio-button label="普通讨论" />
              <el-radio-button label="求购帖" />
              <el-radio-button label="转让帖" />
              <el-radio-button label="答疑帖" />
            </el-radio-group>
          </el-form-item>

          <!-- 标题输入 -->
          <el-form-item label="标题" prop="title">
            <el-input v-model="newDiscussion.title" placeholder="请输入讨论标题（最多50字）" maxlength="50" show-word-limit />
          </el-form-item>

          <!-- 正文编辑 -->
          <el-form-item label="内容" prop="content">
            <el-input v-model="newDiscussion.content" type="textarea" placeholder="请输入讨论内容（最多2000字）" :rows="6" maxlength="2000" show-word-limit />
          </el-form-item>

          <!-- 校区选择 -->
          <el-form-item label="校区">
            <el-select v-model="newDiscussion.campus" placeholder="选择校区" :disabled="!userStore.user">
              <el-option v-for="campus in campuses" :key="campus" :label="campus" :value="campus" />
            </el-select>
          </el-form-item>

          <!-- 标签选择 -->
          <el-form-item label="标签">
            <el-select v-model="newDiscussion.tags" placeholder="选择标签" multiple collapse-tags>
              <el-option v-for="tag in presetTags" :key="tag" :label="tag" :value="tag" />
            </el-select>
          </el-form-item>

          <!-- 图片上传 -->
          <el-form-item label="图片（最多9张）">
            <el-upload
              v-model:file-list="imageFileList"
              :http-request="handleCustomUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              list-type="picture-card"
              :on-preview="handlePictureCardPreview"
              :on-remove="handleRemove"
              :limit="9"
              :auto-upload="true"
            >
              <Plus />
              <template #tip>
                <div class="el-upload__tip">支持jpg/png/jpeg格式，单张不超过5MB，最多上传9张</div>
              </template>
            </el-upload>
            <el-dialog v-model="dialogVisible" title="预览">
              <img w-full :src="dialogImageUrl" alt="Preview Image" />
            </el-dialog>
          </el-form-item>

          <!-- 关联二手商品 -->
          <el-form-item label="关联二手商品（可选）">
            <el-select v-model="newDiscussion.relatedProductId" placeholder="从我的二手商品中选择">
              <el-option v-for="product in myProducts" :key="product.id" :label="product.title" :value="product.id" />
            </el-select>
          </el-form-item>

          <!-- 预览区域 -->
          <el-form-item label="预览">
            <div class="preview-section">
              <h4>{{ newDiscussion.title || '标题预览' }}</h4>
              <p>{{ newDiscussion.content || '内容预览' }}</p>
              <div v-if="newDiscussion.images && newDiscussion.images.length > 0" class="preview-images">
                <el-image v-for="(image, index) in newDiscussion.images.slice(0, 3)" :key="index" :src="image" fit="cover" class="preview-image" />
              </div>
            </div>
          </el-form-item>
        </el-form>

        <template #footer>
          <span class="dialog-footer">
            <el-button @click="saveDraft">保存草稿</el-button>
            <el-button @click="showCreateDialog = false">取消</el-button>
            <el-button type="primary" @click="submitDiscussion">发布</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 草稿箱弹窗 -->
      <el-dialog v-model="showDraftDialog" title="我的草稿" width="600px">
        <div v-if="drafts.length === 0" class="empty-drafts">
          <el-empty description="暂无草稿" />
        </div>
        <div v-else class="draft-list">
          <el-card v-for="(draft, index) in drafts" :key="index" class="draft-item">
            <div class="draft-header">
              <h4>{{ draft.title || '未命名草稿' }}</h4>
              <div class="draft-actions">
                <el-button size="small" @click="editDraft(draft)">编辑</el-button>
                <el-button size="small" type="danger" @click="deleteDraft(index)">删除</el-button>
              </div>
            </div>
            <div class="draft-meta">
              <span>{{ draft.type }}</span>
              <span>{{ formatDate(draft.createdAt) }}</span>
            </div>
          </el-card>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import type { UploadFile } from 'element-plus'
import { ElMessage, FormInstance, UploadProps } from 'element-plus'
import { Plus, Search, StarFilled, ChatDotRound, Collection, View, ArrowDown } from '@element-plus/icons-vue'
import { communityApi } from '@/api/community'
import type { Discussion } from '@/api/community'
import AppHeader from '@/components/AppHeader.vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()

// 状态
const showCreateDialog = ref(false)
const showDraftDialog = ref(false)
const loading = ref(false)
const allDiscussions = ref<Discussion[]>([])
const filteredDiscussions = ref<Discussion[]>([])
const totalDiscussions = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const selectedCampus = ref('')
const selectedTags = ref<string[]>([])
const sortOption = ref('createdAt')
const dialogVisible = ref(false)
const dialogImageUrl = ref('')
const imageFileList = ref<UploadFile[]>([])
const uploadUrl = ref('/api/products/upload')

// 预设数据
const campuses = ref(['主校区', '东校区', '西校区', '南校区'])
const presetTags = ref(['求购', '转让', '置换', '答疑', '避坑', '经验分享'])

// 草稿箱
const drafts = ref<any[]>([])

// 新讨论表单数据
const newDiscussion = reactive({
  title: '',
  content: '',
  type: '普通讨论',
  campus: userStore.user?.campus || '主校区',
  tags: [] as string[],
  images: [] as string[],
  videos: [] as string[],
  relatedProductId: null as number | null,
})

// 表单验证规则
const formRules = reactive({
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 50, message: '标题不能超过50个字符', trigger: 'blur' },
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { max: 2000, message: '内容不能超过2000个字符', trigger: 'blur' },
  ],
})

// 模拟我的二手商品列表
const myProducts = ref([
  { id: 1, title: '二手笔记本电脑' },
  { id: 2, title: '大学英语四级真题' },
  { id: 3, title: '自行车' },
])

// 从localStorage加载草稿
const loadDrafts = () => {
  const savedDrafts = localStorage.getItem('discussionDrafts')
  if (savedDrafts) {
    drafts.value = JSON.parse(savedDrafts)
  }
}

// 保存草稿到localStorage
const saveDraftsToStorage = () => {
  localStorage.setItem('discussionDrafts', JSON.stringify(drafts.value))
}

// 获取帖子列表
const fetchDiscussions = async () => {
  loading.value = true
  try {
    const response = await communityApi.getDiscussions({
      page: currentPage.value - 1, // 后端使用0-based分页，前端使用1-based，需要转换
      size: pageSize.value,
      keyword: searchKeyword.value,
      tags: selectedTags.value,
      sort: sortOption.value,
    })

    // 直接使用response对象，因为响应拦截器会返回后端的响应数据
    if (response && Array.isArray(response.content)) {
      allDiscussions.value = response.content
      totalDiscussions.value = response.totalElements || response.content.length
      applyFilters()
    }
  } catch (error: any) {
    // 错误信息由响应拦截器统一处理，这里不再重复打印
  } finally {
    loading.value = false
  }
}

// 应用筛选
const applyFilters = () => {
  let filtered = [...allDiscussions.value]

  // 关键词筛选
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    filtered = filtered.filter((d) => d.title.toLowerCase().includes(keyword) || d.content.toLowerCase().includes(keyword) || d.user.username.toLowerCase().includes(keyword))
  }

  // 校区筛选
  if (selectedCampus.value) {
    filtered = filtered.filter((d) => d.campus === selectedCampus.value)
  }

  // 标签筛选
  if (selectedTags.value.length > 0) {
    filtered = filtered.filter((d) => selectedTags.value.every((tag) => d.tags.includes(tag)))
  }

  // 排序
  switch (sortOption.value) {
    case 'likeCount':
      filtered.sort((a, b) => b.likeCount - a.likeCount)
      break
    case 'commentCount':
      filtered.sort((a, b) => b.commentCount - a.commentCount)
      break
    default:
      filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }

  filteredDiscussions.value = filtered
}

// 重置筛选
const resetFilters = () => {
  searchKeyword.value = ''
  selectedCampus.value = ''
  selectedTags.value = []
  sortOption.value = 'createdAt'
  applyFilters()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchDiscussions()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchDiscussions()
}

// 图片上传处理
const handleRemove = (file: UploadFile) => {
  const index = imageFileList.value.indexOf(file)
  if (index !== -1) {
    imageFileList.value.splice(index, 1)
    // 同时从newDiscussion.images中移除
    const imageIndex = newDiscussion.images.indexOf(file.url as string)
    if (imageIndex !== -1) {
      newDiscussion.images.splice(imageIndex, 1)
    }
  }
}

const handlePictureCardPreview = (file: UploadFile) => {
  dialogImageUrl.value = file.url as string
  dialogVisible.value = true
}

// 图片上传前校验
const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  const isLt5M = rawFile.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB！')
    return false
  }
  return true
}

// 图片上传成功处理
const handleUploadSuccess = (response: any, file: UploadFile) => {
  // 后端返回的是包含url属性的对象
  if (response && response.url) {
    // 添加完整的图片URL（后端返回的是相对路径，需要拼接baseURL）
    // 确保baseURL和相对路径之间有斜杠
    const baseUrl = 'http://localhost:8081'
    const imageUrl = `${baseUrl}${response.url.startsWith('/') ? '' : '/'}${response.url}`
    newDiscussion.images.push(imageUrl)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败：响应格式错误')
  }
}

// 自定义图片上传方法
const handleCustomUpload = async (options: any) => {
  const { file, onSuccess, onError } = options
  try {
    const formData = new FormData()
    formData.append('file', file)

    // 使用communityApi.uploadImage方法上传图片
    const response = await communityApi.uploadImage(formData)

    // 检查响应是否包含error字段
    if (response && response.error) {
      // 如果包含error字段，调用onError
      onError(new Error(response.error))
    } else {
      // 否则调用onSuccess，传递response和file参数
      onSuccess(response, file)
    }
  } catch (error: any) {
    onError(error)
  }
}

// 图片上传失败处理
const handleUploadError = (error: any) => {
  ElMessage.error('图片上传失败，请重试')
}

// 快捷发帖类型处理
const handleCreateCommand = (command: string) => {
  newDiscussion.type = command
  showCreateDialog.value = true
}

// 跳转到帖子详情
const goToDiscussionDetail = (id: number) => {
  router.push(`/discussion/${id}`)
}

// 获取帖子摘要
const getSummary = (content: string) => {
  if (content.length <= 100) {
    return content
  }
  return content.substring(0, 100) + '...'
}

// 获取标签类型
const getTagType = (tag: string) => {
  const tagTypes: Record<string, string> = {
    求购: 'warning',
    转让: 'success',
    置换: 'info',
    答疑: 'primary',
    避坑: 'danger',
    经验分享: 'purple',
  }
  return tagTypes[tag] || 'default'
}

// 提交讨论
const submitDiscussion = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 处理图片上传
    // 注意：图片已经通过el-upload组件自动上传，并且在handleUploadSuccess中添加到了newDiscussion.images
    // 这里不需要再次上传，只需要确保images数组包含所有上传成功的图片URL
    // 清空之前的图片URL，只保留通过el-upload成功上传的图片
    // （因为el-upload组件会在上传成功后调用handleUploadSuccess，将图片URL添加到newDiscussion.images）
    // 这里不需要额外处理，handleUploadSuccess已经处理了图片URL的添加

    // 调用API发布讨论
    await communityApi.createDiscussion(newDiscussion)
    ElMessage.success('讨论发布成功')
    showCreateDialog.value = false
    fetchDiscussions()
    resetForm()
  } catch (error: any) {
    if (error.message.includes('违禁词')) {
      ElMessage.error('发布内容包含违禁词，请修改后重试')
    } else {
      ElMessage.error('发布失败，请检查表单')
    }
  }
}

// 保存草稿
const saveDraft = () => {
  const draft = {
    ...newDiscussion,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  }
  drafts.value.unshift(draft)
  saveDraftsToStorage()
  ElMessage.success('草稿保存成功')
  showCreateDialog.value = false
}

// 编辑草稿
const editDraft = (draft: any) => {
  Object.assign(newDiscussion, draft)
  showDraftDialog.value = false
  showCreateDialog.value = true
}

// 删除草稿
const deleteDraft = (index: number) => {
  drafts.value.splice(index, 1)
  saveDraftsToStorage()
  ElMessage.success('草稿删除成功')
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  imageFileList.value = []
  Object.assign(newDiscussion, {
    title: '',
    content: '',
    type: '普通讨论',
    campus: userStore.user?.campus || '主校区',
    tags: [],
    images: [],
    videos: [],
    relatedProductId: null,
  })
}

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 30) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 初始化
onMounted(() => {
  fetchDiscussions()
  loadDrafts()
})

// 监听用户信息变化，更新默认校区
watch(
  () => userStore.user,
  (newUser) => {
    if (newUser && newUser.campus) {
      newDiscussion.campus = newUser.campus
    }
  },
  { deep: true }
)
</script>

<style scoped>
.community-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.community-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.community-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.community-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.create-btn {
  font-size: 16px;
}

.community-filters {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.filter-group {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 300px;
}

.discussions-list {
  margin-bottom: 30px;
}

/* 骨架屏样式 */
.skeleton-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.skeleton-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.skeleton-header {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.skeleton-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  border-radius: 50%;
}

.skeleton-info {
  flex: 1;
}

.skeleton-line {
  height: 16px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  margin-bottom: 8px;
  border-radius: 4px;
}

.skeleton-line.short {
  width: 60%;
}

.skeleton-title {
  height: 24px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  margin-bottom: 15px;
  border-radius: 4px;
}

.skeleton-content {
  height: 48px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  margin-bottom: 15px;
  border-radius: 4px;
}

.skeleton-images {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.skeleton-images::before,
.skeleton-images::after {
  content: '';
  width: 80px;
  height: 80px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  border-radius: 4px;
}

.skeleton-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.skeleton-tags {
  display: flex;
  gap: 8px;
}

.skeleton-tags::before {
  content: '';
  width: 60px;
  height: 20px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  border-radius: 10px;
}

.skeleton-stats {
  display: flex;
  gap: 20px;
}

.skeleton-stats::before,
.skeleton-stats::after {
  content: '';
  width: 40px;
  height: 16px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  border-radius: 8px;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 帖子卡片样式 */
.discussion-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.discussion-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.discussion-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.discussion-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #303133;
  line-height: 1.4;
}

.discussion-summary {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.discussion-images {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
  position: relative;
}

.discussion-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}

.image-count {
  position: absolute;
  bottom: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 0 0 4px 0;
}

.user-info {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}

.user-avatar {
  border: 2px solid #f0f0f0;
}

.user-details {
  flex: 1;
}

.username-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.username {
  font-weight: 500;
  color: #303133;
}

.verified-tag {
  font-size: 12px;
}

.meta-info {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

.discussion-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.tag-item {
  margin-bottom: 0;
}

.discussion-stats {
  display: flex;
  gap: 20px;
  margin-top: auto;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
  font-size: 14px;
  color: #909399;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  transition: color 0.3s;
  font-size: 12px;
}

.stat-item:hover {
  color: #409eff;
}

.stat-label {
  font-size: 11px;
  color: #909399;
  margin-left: 2px;
}

.stat-item:hover .stat-label {
  color: #409eff;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 底部悬浮按钮 */
.floating-create-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 100;
}

/* 预览区域样式 */
.preview-section {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  margin-top: 10px;
}

.preview-section h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.preview-section p {
  margin: 0 0 10px 0;
  color: #606266;
  line-height: 1.5;
}

.preview-images {
  display: flex;
  gap: 8px;
}

.preview-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}

/* 草稿箱样式 */
.empty-drafts {
  text-align: center;
  padding: 40px 0;
}

.draft-list {
  max-height: 500px;
  overflow-y: auto;
}

.draft-item {
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.draft-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.draft-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.draft-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 400px;
}

.draft-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .community-container {
    padding: 10px;
  }

  .community-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .filter-group {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }

  .discussion-cards {
    grid-template-columns: 1fr;
  }

  .discussion-images {
    justify-content: flex-start;
  }

  .discussion-image {
    width: 60px;
    height: 60px;
  }

  .floating-create-btn {
    bottom: 20px;
    right: 20px;
  }

  .draft-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .draft-header h4 {
    max-width: 100%;
  }
}
</style>
