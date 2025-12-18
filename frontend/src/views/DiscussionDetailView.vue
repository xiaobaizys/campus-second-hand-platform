<template>
  <div class="discussion-detail-page">
    <AppHeader />
    <div class="discussion-detail-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="10" animated />
      </div>

      <!-- 帖子详情 -->
      <div v-else-if="discussion" class="discussion-content">
        <!-- 帖子标题和基本信息 -->
        <div class="discussion-header">
          <div class="header-top">
            <div class="header-left">
              <el-button type="default" size="small" @click="goBack" class="back-btn"> 返回 </el-button>
              <h1 class="discussion-title">{{ discussion.title }}</h1>
            </div>
            <!-- 编辑和删除按钮 -->
            <div class="discussion-actions-header" v-if="canEditOrDelete">
              <el-button type="primary" size="small" @click="handleEditClick" class="edit-btn"> <Edit /> 编辑 </el-button>
              <el-button type="danger" size="small" @click="handleDeleteDiscussion" class="delete-btn"> <Delete /> 删除 </el-button>
            </div>
          </div>
          <div class="discussion-meta">
            <div class="user-info" @click="discussion.user?.id && goToUserProfile(discussion.user.id)">
              <el-avatar :src="discussion.user?.avatar || ''" :size="40" class="user-avatar" />
              <div class="user-details">
                <div class="username-row">
                  <span class="username">{{ discussion.user?.username || '未知用户' }}</span>
                  <el-tag v-if="discussion.user?.isCampusVerified" size="small" type="success" class="verified-tag"> 校园认证 </el-tag>
                </div>
                <div class="meta-info">
                  <span class="campus">{{ discussion.campus }}</span>
                  <span class="time">{{ formatDate(discussion.createdAt) }}</span>
                  <span class="view-count"><View /> {{ discussion.viewCount }}</span>
                </div>
              </div>
            </div>
            <!-- 联系方式 -->
            <div class="contact-info" v-if="canViewContactInfo">
              <h3>联系方式</h3>
              <div class="contact-detail">
                <el-icon><Phone /></el-icon>
                <span>{{ userStore.user?.phone || '暂无联系方式' }}</span>
              </div>
              <div class="contact-detail">
                <el-icon><Message /></el-icon>
                <span>{{ userStore.user?.email || '暂无邮箱' }}</span>
              </div>
            </div>
          </div>

          <!-- 帖子标签 -->
          <div class="discussion-tags">
            <el-tag v-for="tag in discussion.tags" :key="tag" size="small" :type="getTagType(tag)" class="tag-item">
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <!-- 帖子正文 -->
        <div class="discussion-body">
          <p class="content">{{ discussion.content }}</p>

          <!-- 帖子图片 -->
          <div v-if="discussion.images && discussion.images.length > 0" class="discussion-images">
            <el-image v-for="(image, index) in discussion.images" :key="index" :src="image" fit="cover" class="discussion-image" preview-teleported :preview-src-list="discussion.images" />
          </div>

          <!-- 帖子视频 -->
          <div v-if="discussion.videos && discussion.videos.length > 0" class="discussion-videos">
            <video v-for="(video, index) in discussion.videos" :key="index" :src="video" controls class="discussion-video"></video>
          </div>
        </div>

        <!-- 互动按钮 -->
        <div class="discussion-actions">
          <el-button :type="discussion.liked ? 'primary' : 'default'" @click="toggleLike" class="action-btn">
            <StarFilled :fill="discussion.liked ? '#ff4d4f' : 'currentColor'" />
            <span>{{ discussion.likeCount }}</span>
            <span class="action-label">点赞</span>
          </el-button>
          <el-button :type="discussion.favorited ? 'primary' : 'default'" @click="toggleFavorite" class="action-btn">
            <Collection :fill="discussion.favorited ? '#ff4d4f' : 'currentColor'" />
            <span>{{ discussion.favoriteCount }}</span>
            <span class="action-label">收藏</span>
          </el-button>
          <el-button type="default" class="action-btn" @click="scrollToComment">
            <ChatDotRound />
            <span>{{ discussion.commentCount }}</span>
            <span class="action-label">评论</span>
          </el-button>
        </div>

        <!-- 关联二手商品 -->
        <div v-if="discussion.relatedProduct" class="related-product">
          <h3>关联商品</h3>
          <el-card class="product-card" @click="goToProductDetail(discussion.relatedProduct.id)">
            <div class="product-info">
              <el-image :src="discussion.relatedProduct.images[0]" fit="cover" class="product-image" />
              <div class="product-details">
                <h4 class="product-title">{{ discussion.relatedProduct.title }}</h4>
                <p class="product-price">¥{{ discussion.relatedProduct.price }}</p>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 评论区 -->
        <div class="comments-section" ref="commentsRef">
          <h3>评论 ({{ discussion.commentCount }})</h3>

          <!-- 评论输入框 -->
          <div class="comment-input-section">
            <el-avatar :src="userStore.user?.avatar || ''" :size="40" class="comment-avatar" />
            <div class="comment-input-wrapper">
              <el-input v-model="newComment.content" placeholder="写下你的评论..." type="textarea" :rows="2" maxlength="500" show-word-limit @keyup.enter.ctrl="submitComment" />
              <div class="comment-submit-section">
                <el-button type="primary" @click="submitComment">发布评论</el-button>
              </div>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="comments-list">
            <el-card v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <div class="comment-user-info" @click="comment.user?.id && goToUserProfile(comment.user.id)">
                  <el-avatar :src="comment.user?.avatar || ''" :size="32" class="comment-user-avatar" />
                  <span class="comment-username">{{ comment.user?.username || '未知用户' }}</span>
                </div>
                <div class="comment-actions">
                  <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                  <!-- 评论操作按钮，只有评论作者或管理员可以操作 -->
                  <template v-if="canEditComment(comment)">
                    <el-button type="text" size="small" @click="handleEditComment(comment)">编辑</el-button>
                    <el-button type="text" size="small" @click="handleDeleteComment(comment)" style="color: #f56c6c">删除</el-button>
                  </template>
                </div>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
            </el-card>

            <!-- 没有评论时的提示 -->
            <div v-if="comments.length === 0" class="no-comments">
              <el-empty description="暂无评论，快来发表第一条评论吧！" />
            </div>
          </div>

          <!-- 评论分页 -->
          <div v-if="totalComments > pageSize" class="comments-pagination">
            <el-pagination v-model:current-page="commentPage" v-model:page-size="pageSize" :total="totalComments" layout="prev, pager, next" @current-change="fetchComments" />
          </div>
        </div>
      </div>

      <!-- 未找到帖子 -->
      <div v-else class="not-found">
        <el-empty description="帖子不存在或已被删除" />
        <el-button type="primary" @click="router.back()">返回社区</el-button>
      </div>
    </div>

    <!-- 编辑讨论弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑讨论" width="800px">
      <el-form
        :model="editDiscussion"
        label-position="top"
        :rules="{
          title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
          content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
        }"
        ref="formRef"
      >
        <!-- 标题输入 -->
        <el-form-item label="标题" prop="title">
          <el-input v-model="editDiscussion.title" placeholder="请输入讨论标题" maxlength="50" show-word-limit />
        </el-form-item>

        <!-- 内容编辑 -->
        <el-form-item label="内容" prop="content">
          <el-input v-model="editDiscussion.content" type="textarea" placeholder="请输入讨论内容" :rows="6" maxlength="2000" show-word-limit />
        </el-form-item>

        <!-- 标签选择 -->
        <el-form-item label="标签">
          <el-select v-model="editDiscussion.tags" placeholder="选择标签" multiple collapse-tags>
            <el-option label="求购" value="求购" />
            <el-option label="转让" value="转让" />
            <el-option label="置换" value="置换" />
            <el-option label="答疑" value="答疑" />
            <el-option label="避坑" value="避坑" />
            <el-option label="经验分享" value="经验分享" />
          </el-select>
        </el-form-item>

        <!-- 图片预览 -->
        <el-form-item label="图片">
          <div v-if="editDiscussion.images && editDiscussion.images.length > 0" class="preview-images">
            <el-image v-for="(image, index) in editDiscussion.images" :key="index" :src="image" fit="cover" class="preview-image" />
          </div>
          <div v-else class="no-images">暂无图片</div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleEditSubmit">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑评论弹窗 -->
    <el-dialog v-model="showCommentEditDialog" title="编辑评论" width="600px">
      <el-form
        :model="editComment"
        label-position="top"
        :rules="{
          content: [{ required: true, message: '请输入评论内容', trigger: 'blur' }],
        }"
        ref="commentFormRef"
      >
        <!-- 评论内容编辑 -->
        <el-form-item label="评论内容" prop="content">
          <el-input v-model="editComment.content" type="textarea" placeholder="请输入评论内容" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCommentEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleCommentEditSubmit">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { StarFilled, Collection, ChatDotRound, View, Edit, Delete, Phone, Message } from '@element-plus/icons-vue'
import { communityApi } from '@/api/community'
import type { Discussion, Comment } from '@/api/community'
import AppHeader from '@/components/AppHeader.vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 状态
const loading = ref(true)
const discussion = ref<Discussion | null>(null)
const comments = ref<Comment[]>([])
const totalComments = ref(0)
const commentPage = ref(1)
const pageSize = ref(10)
const commentsRef = ref<HTMLElement | null>(null)
const showEditDialog = ref(false)
const formRef = ref<FormInstance>()
const showCommentEditDialog = ref(false)
const commentFormRef = ref<FormInstance>()
const currentCommentId = ref<number | null>(null)

// 编辑表单数据
const editDiscussion = ref({
  title: '',
  content: '',
  tags: [] as string[],
  images: [] as string[],
})

// 编辑评论表单数据
const editComment = ref({
  content: '',
})

// 计算属性：判断当前用户是否可以编辑或删除讨论
const canEditOrDelete = computed(() => {
  if (!discussion.value || !userStore.user) return false
  // 只有讨论作者或管理员可以编辑/删除
  return discussion.value.user?.id === userStore.user.id || userStore.user.role === 'ADMIN'
})

// 计算属性：判断当前用户是否可以查看联系方式
const canViewContactInfo = computed(() => {
  if (!discussion.value || !userStore.user) return false
  // 只有讨论作者或管理员可以查看联系方式
  return discussion.value.user?.id === userStore.user.id || userStore.user.role === 'ADMIN'
})

// 新评论
const newComment = ref({
  content: '',
})

// 计算属性：判断当前用户是否可以编辑或删除指定评论
const canEditComment = (comment: Comment) => {
  if (!userStore.user) return false
  // 只有评论作者或管理员可以编辑/删除评论
  return comment.user?.id === userStore.user.id || userStore.user.role === 'ADMIN'
}

// 获取帖子详情
const fetchDiscussionDetail = async () => {
  const id = Number(route.params.id)
  if (!id) return

  loading.value = true
  try {
    const response = await communityApi.getDiscussionDetail(id)
    if (response) {
      // 响应拦截器已经处理了响应数据，直接使用response
      discussion.value = response

      // 确保user对象存在
      if (!discussion.value.user) {
        // 如果user对象不存在，尝试从其他字段获取用户信息
        discussion.value.user = {
          id: 0,
          username: '未知用户',
          avatar: '',
          isCampusVerified: false,
        }
      }
    }
  } catch (error) {
    ElMessage.error('获取帖子详情失败')
  } finally {
    loading.value = false
  }
}

// 处理编辑按钮点击事件
const handleEditClick = () => {
  if (!discussion.value) return

  // 填充表单数据
  editDiscussion.value = {
    title: discussion.value.title,
    content: discussion.value.content,
    tags: [...discussion.value.tags],
    images: [...discussion.value.images],
  }

  showEditDialog.value = true
}

// 处理删除按钮点击事件
const handleDeleteDiscussion = async () => {
  if (!discussion.value) return

  try {
    await ElMessageBox.confirm('确定要删除这条讨论吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await communityApi.deleteDiscussion(discussion.value.id)
    ElMessage.success('删除成功')
    router.push('/community')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，请重试')
    }
  }
}

// 处理编辑表单提交事件
const handleEditSubmit = async () => {
  if (!discussion.value || !formRef.value) return

  try {
    await formRef.value.validate()

    // 只发送后端期望的字段，不发送images字段
    const updateData = {
      title: editDiscussion.value.title,
      content: editDiscussion.value.content,
      tags: editDiscussion.value.tags
    }

    await communityApi.updateDiscussion(discussion.value.id, updateData)

    // 更新本地讨论数据
    Object.assign(discussion.value, updateData)

    showEditDialog.value = false
    ElMessage.success('编辑成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('编辑失败，请重试')
    }
  }
}

// 获取评论列表
const fetchComments = async () => {
  const id = Number(route.params.id)
  if (!id) return

  try {
    const response = await communityApi.getComments(id, {
      page: commentPage.value - 1, // 后端使用0-based分页
      size: pageSize.value,
    })
    if (response) {
      comments.value = response.content || []
      totalComments.value = response.totalElements || 0
    }
  } catch (error) {
    ElMessage.error('获取评论失败')
  }
}

// 切换点赞状态
const toggleLike = async () => {
  if (!discussion.value) return

  try {
    await communityApi.likeDiscussion(discussion.value.id)
    discussion.value.liked = !discussion.value.liked
    discussion.value.likeCount += discussion.value.liked ? 1 : -1
    ElMessage.success(discussion.value.liked ? '点赞成功' : '取消点赞成功')
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

// 切换收藏状态
const toggleFavorite = async () => {
  if (!discussion.value) return

  try {
    // 这里需要实现收藏API调用
    discussion.value.favorited = !discussion.value.favorited
    discussion.value.favoriteCount += discussion.value.favorited ? 1 : -1
    ElMessage.success(discussion.value.favorited ? '收藏成功' : '取消收藏成功')
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

// 提交评论
const submitComment = async () => {
  if (!discussion.value || !newComment.value.content.trim()) return

  try {
    const response = await communityApi.createComment(discussion.value.id, {
      content: newComment.value.content.trim(),
    })
    if (response) {
      comments.value.unshift(response)
      discussion.value.commentCount++
      newComment.value.content = ''
      ElMessage.success('评论发布成功')
    }
  } catch (error) {
    ElMessage.error('评论发布失败，请重试')
  }
}

// 处理编辑评论按钮点击事件
const handleEditComment = (comment: Comment) => {
  currentCommentId.value = comment.id
  editComment.value.content = comment.content
  showCommentEditDialog.value = true
}

// 处理编辑评论表单提交事件
const handleCommentEditSubmit = async () => {
  if (!currentCommentId.value || !commentFormRef.value) return

  try {
    await commentFormRef.value.validate()

    await communityApi.updateComment(currentCommentId.value, {
      content: editComment.value.content.trim(),
    })

    // 更新本地评论数据
    const commentIndex = comments.value.findIndex((c) => c.id === currentCommentId.value)
    if (commentIndex !== -1) {
      comments.value[commentIndex].content = editComment.value.content.trim()
      comments.value[commentIndex].updatedAt = new Date().toISOString()
    }

    showCommentEditDialog.value = false
    ElMessage.success('评论编辑成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('评论编辑失败，请重试')
    }
  }
}

// 处理删除评论按钮点击事件
const handleDeleteComment = async (comment: Comment) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await communityApi.deleteComment(comment.id)

    // 更新本地评论数据
    comments.value = comments.value.filter((c) => c.id !== comment.id)
    if (discussion.value) {
      discussion.value.commentCount--
    }
    totalComments.value--

    ElMessage.success('评论删除成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('评论删除失败，请重试')
    }
  }
}

// 跳转到用户主页
const goToUserProfile = (userId: number) => {
  router.push(`/user/${userId}`)
}

// 跳转到商品详情
const goToProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 滚动到评论区
const scrollToComment = () => {
  commentsRef.value?.scrollIntoView({ behavior: 'smooth' })
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
  fetchDiscussionDetail()
  fetchComments()
})

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 监听路由变化
watch(
  () => route.params.id,
  () => {
    fetchDiscussionDetail()
    fetchComments()
  }
)
</script>

<style scoped>
.discussion-detail-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.discussion-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.loading-state {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.discussion-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.discussion-header {
  margin-bottom: 20px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
  margin-right: 20px;
}

.discussion-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 0;
  line-height: 1.4;
  flex: 1;
}

.back-btn {
  font-size: 12px;
  padding: 6px 12px;
  background-color: #f5f7fa;
  border-color: #dcdfe6;
  color: #606266;
  transition: all 0.3s;
  white-space: nowrap;
}

.back-btn:hover {
  background-color: #ecf5ff;
  border-color: #c6e2ff;
  color: #409eff;
}

.discussion-actions-header {
  display: flex;
  gap: 10px;
}

.edit-btn,
.delete-btn {
  font-size: 12px;
  padding: 4px 12px;
}

.preview-images {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.preview-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}

.no-images {
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}

.discussion-meta {
  margin-bottom: 15px;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.contact-info {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e9ecef;
}

.contact-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.contact-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.contact-detail .el-icon {
  color: #409eff;
  font-size: 16px;
}

.user-info {
  display: flex;
  gap: 10px;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.user-info:hover {
  opacity: 0.8;
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

.view-count {
  display: flex;
  align-items: center;
  gap: 5px;
}

.discussion-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.tag-item {
  margin-bottom: 0;
}

.discussion-body {
  margin-bottom: 20px;
}

.discussion-body .content {
  font-size: 16px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 20px;
}

.discussion-images {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.discussion-image {
  width: 200px;
  height: 200px;
  border-radius: 4px;
  object-fit: cover;
  cursor: pointer;
  transition: all 0.3s;
}

.discussion-image:hover {
  transform: scale(1.05);
}

.discussion-videos {
  margin-bottom: 20px;
}

.discussion-video {
  width: 100%;
  max-width: 500px;
  height: auto;
  border-radius: 4px;
  margin-bottom: 10px;
}

.discussion-actions {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s;
  font-size: 12px;
}

.action-label {
  font-size: 11px;
  color: #909399;
  margin-left: 2px;
}

.action-btn:hover .action-label {
  color: #409eff;
}

.related-product {
  margin-bottom: 30px;
}

.related-product h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
}

.product-card {
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.product-info {
  display: flex;
  gap: 15px;
  align-items: center;
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  object-fit: cover;
}

.product-details {
  flex: 1;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
  margin: 0;
}

.comments-section {
  margin-top: 30px;
}

.comments-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
}

.comment-input-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: flex-start;
}

.comment-avatar {
  margin-top: 5px;
}

.comment-input-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.comment-input-section .el-input {
  flex: 1;
}

.comment-submit-section {
  display: flex;
  justify-content: flex-end;
}

.comments-list {
  margin-bottom: 20px;
}

.comment-item {
  margin-bottom: 15px;
  transition: all 0.3s;
}

.comment-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-user-info {
  display: flex;
  gap: 8px;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.comment-user-info:hover {
  opacity: 0.8;
}

.comment-user-avatar {
  border: 1px solid #f0f0f0;
}

.comment-username {
  font-weight: 500;
  color: #303133;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-actions .el-button {
  font-size: 11px;
  padding: 0;
  height: auto;
  line-height: normal;
  margin: 0;
}

.comment-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
}

.comments-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.not-found {
  text-align: center;
  padding: 60px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .discussion-detail-container {
    padding: 10px;
  }

  .discussion-title {
    font-size: 20px;
  }

  .discussion-actions {
    flex-wrap: wrap;
  }

  .discussion-images {
    justify-content: center;
  }

  .discussion-image {
    width: 150px;
    height: 150px;
  }

  .product-info {
    flex-direction: column;
    text-align: center;
  }

  .product-image {
    margin-bottom: 10px;
  }

  .comment-input-section {
    flex-direction: column;
    align-items: stretch;
  }

  .comment-avatar {
    align-self: flex-start;
    margin-bottom: 10px;
  }
}
</style>
