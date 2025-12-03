<template>
  <div class="orders-container">
    <AppHeader />

    <div class="orders-section">
      <div class="container">
        <div class="page-header">
          <h1>我的订单</h1>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>我的订单</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="orders-tabs">
          <el-tab-pane label="我买到的" name="bought">
            <div v-loading="loading" class="orders-list">
              <div v-if="boughtOrders.length > 0" class="order-cards">
                <div v-for="order in boughtOrders" :key="order.id" class="order-card">
                  <div class="order-header">
                    <div class="order-number">订单号：{{ order.orderNumber }}</div>
                    <div class="order-time">{{ formatDate(order.createdAt) }}</div>
                  </div>
                  
                  <div class="order-content">
                    <div class="product-info">
                      <div class="product-image">
                        <img :src="order.productImage || '/placeholder.svg'" :alt="order.productName" />
                      </div>
                      <div class="product-details">
                        <h3 class="product-title">{{ order.productName }}</h3>
                        <div class="product-meta">
                          <span class="seller">卖家：{{ order.sellerName }}</span>
                          <span class="quantity">数量：1</span>
                        </div>
                      </div>
                    </div>
                    
                    <div class="order-info">
                      <div class="price-info">
                        <div class="price">¥{{ order.finalPrice || order.price }}</div>
                        <div v-if="order.finalPrice && order.finalPrice !== order.price" class="original-price">
                          原价：¥{{ order.price }}
                        </div>
                      </div>
                      
                      <div class="order-status">
                        <el-tag :type="getOrderStatusType(order.status)">
                          {{ getOrderStatusText(order.status) }}
                        </el-tag>
                      </div>
                      
                      <div class="order-actions">
                        <el-button size="small" @click="viewOrderDetail(order)">查看详情</el-button>
                        
                        <template v-if="order.status === 'PENDING'">
                          <el-button size="small" type="primary" @click="payOrder(order)">立即付款</el-button>
                          <el-button size="small" type="danger" @click="cancelOrder(order)">取消订单</el-button>
                        </template>
                        
                        <template v-if="order.status === 'PAID'">
                          <el-button size="small" type="success" @click="confirmReceive(order)">确认收货</el-button>
                        </template>
                        
                        <template v-if="order.status === 'COMPLETED'">
                          <el-button size="small" type="warning" @click="reviewProduct(order)">评价商品</el-button>
                        </template>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else-if="!loading" class="empty-state">
                <el-empty description="暂无购买订单" />
                <el-button type="primary" @click="goToProducts">去逛逛</el-button>
              </div>
              
              <div v-if="boughtOrders.length > 0" class="pagination">
                <el-pagination
                  v-model:current-page="boughtPage"
                  v-model:page-size="pageSize"
                  :page-sizes="[10, 20, 50]"
                  :total="boughtTotal"
                  layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleSizeChange"
                  @current-change="handleBoughtPageChange"
                />
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="我卖出的" name="sold">
            <div v-loading="loading" class="orders-list">
              <div v-if="soldOrders.length > 0" class="order-cards">
                <div v-for="order in soldOrders" :key="order.id" class="order-card">
                  <div class="order-header">
                    <div class="order-number">订单号：{{ order.orderNumber }}</div>
                    <div class="order-time">{{ formatDate(order.createdAt) }}</div>
                  </div>
                  
                  <div class="order-content">
                    <div class="product-info">
                      <div class="product-image">
                        <img :src="order.productImage || '/placeholder.svg'" :alt="order.productName" />
                      </div>
                      <div class="product-details">
                        <h3 class="product-title">{{ order.productName }}</h3>
                        <div class="product-meta">
                          <span class="buyer">买家：{{ order.buyerName }}</span>
                          <span class="quantity">数量：1</span>
                        </div>
                      </div>
                    </div>
                    
                    <div class="order-info">
                      <div class="price-info">
                        <div class="price">¥{{ order.finalPrice || order.price }}</div>
                        <div v-if="order.finalPrice && order.finalPrice !== order.price" class="original-price">
                          原价：¥{{ order.price }}
                        </div>
                      </div>
                      
                      <div class="order-status">
                        <el-tag :type="getOrderStatusType(order.status)">
                          {{ getOrderStatusText(order.status) }}
                        </el-tag>
                      </div>
                      
                      <div class="order-actions">
                        <el-button size="small" @click="viewOrderDetail(order)">查看详情</el-button>
                        
                        <template v-if="order.status === 'PENDING'">
                          <el-button size="small" type="danger" @click="cancelOrder(order)">取消订单</el-button>
                        </template>
                        
                        <template v-if="order.status === 'PAID'">
                          <el-button size="small" type="primary" @click="shipOrder(order)">发货</el-button>
                        </template>
                        
                        <template v-if="order.status === 'SHIPPED'">
                          <el-button size="small" type="success" disabled>等待买家确认收货</el-button>
                        </template>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else-if="!loading" class="empty-state">
                <el-empty description="暂无销售订单" />
                <el-button type="primary" @click="goToPublish">发布商品</el-button>
              </div>
              
              <div v-if="soldOrders.length > 0" class="pagination">
                <el-pagination
                  v-model:current-page="soldPage"
                  v-model:page-size="pageSize"
                  :page-sizes="[10, 20, 50]"
                  :total="soldTotal"
                  layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleSizeChange"
                  @current-change="handleSoldPageChange"
                />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="orderDetailVisible" title="订单详情" width="600px">
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <h3>订单信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ currentOrder.orderNumber }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getOrderStatusType(currentOrder.status)">
                {{ getOrderStatusText(currentOrder.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ formatDate(currentOrder.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ formatDate(currentOrder.updatedAt) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="detail-section">
          <h3>商品信息</h3>
          <div class="product-detail">
            <div class="product-image">
              <img :src="currentOrder.productImage || '/placeholder.jpg'" :alt="currentOrder.productName" />
            </div>
            <div class="product-info">
              <h4>{{ currentOrder.productName }}</h4>
              <div class="price">¥{{ currentOrder.finalPrice || currentOrder.price }}</div>
              <div v-if="currentOrder.finalPrice && currentOrder.finalPrice !== currentOrder.price" class="original-price">
                原价：¥{{ currentOrder.price }}
              </div>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>交易信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="买家">{{ currentOrder.buyerName }}</el-descriptions-item>
            <el-descriptions-item label="卖家">{{ currentOrder.sellerName }}</el-descriptions-item>
            <el-descriptions-item label="联系方式">{{ currentOrder.contactInfo || '未提供' }}</el-descriptions-item>
            <el-descriptions-item label="配送方式">{{ getDeliveryMethodText(currentOrder.deliveryMethod) }}</el-descriptions-item>
            <el-descriptions-item label="配送地址" :span="2">{{ currentOrder.deliveryAddress || '未提供' }}</el-descriptions-item>
            <el-descriptions-item label="支付方式">{{ getPaymentMethodText(currentOrder.paymentMethod) }}</el-descriptions-item>
            <el-descriptions-item label="支付状态">{{ getPaymentStatusText(currentOrder.paymentStatus) }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ currentOrder.remarks || '无' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="orderDetailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import { useUserStore } from '@/store/user'
import { useOrderStore } from '@/store/order'

const router = useRouter()
const userStore = useUserStore()

// 引入orderStore
const orderStore = useOrderStore()

// 响应式数据
const activeTab = ref('bought')
const boughtPage = ref(1)
const soldPage = ref(1)
const pageSize = ref(10)
const boughtTotal = ref(0)
const soldTotal = ref(0)

// 订单详情对话框
const orderDetailVisible = ref(false)
const currentOrder = ref(null)

// 计算属性：使用orderStore中的订单数据
const boughtOrders = computed(() => orderStore.buyerOrders)
const soldOrders = computed(() => orderStore.sellerOrders)
const loading = computed(() => orderStore.loading)

// 获取订单列表
const fetchOrders = async () => {
  if (!userStore.isAuthenticated) {
    ElMessage.error('请先登录')
    return
  }

  try {
    // 获取当前标签页的数据
    if (activeTab.value === 'bought') {
      // 获取购买订单
      const response = await orderStore.fetchBuyerOrders({ page: boughtPage.value - 1, size: pageSize.value })
      if (response && response.totalElements) {
        boughtTotal.value = response.totalElements
      }
    } else {
      // 获取销售订单
      const response = await orderStore.fetchSellerOrders({ page: soldPage.value - 1, size: pageSize.value })
      if (response && response.totalElements) {
        soldTotal.value = response.totalElements
      }
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  }
}

// 处理标签页切换
const handleTabClick = () => {
  fetchOrders()
}

// 处理分页大小变化
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  fetchOrders()
}

// 处理购买订单页码变化
const handleBoughtPageChange = (newPage: number) => {
  boughtPage.value = newPage
  fetchOrders()
}

// 处理销售订单页码变化
const handleSoldPageChange = (newPage: number) => {
  soldPage.value = newPage
  fetchOrders()
}

// 查看订单详情
const viewOrderDetail = async (order: any) => {
  try {
    await orderStore.fetchOrderById(order.id)
    currentOrder.value = orderStore.currentOrder
    orderDetailVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 付款
const payOrder = async (order: any) => {
  try {
    await ElMessageBox.confirm('确认付款该订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 更新订单状态为已付款
    await orderStore.payOrder(order.id, 'BALANCE')
    
    ElMessage.success('付款成功')
    fetchOrders() // 刷新订单数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('付款失败:', error)
      ElMessage.error('付款失败')
    }
  }
}

// 取消订单
const cancelOrder = async (order: any) => {
  try {
    await ElMessageBox.confirm('确认取消该订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 更新订单状态为已取消
    await orderStore.cancelOrder(order.id)
    
    ElMessage.success('订单已取消')
    fetchOrders() // 刷新订单数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 确认收货
const confirmReceive = async (order: any) => {
  try {
    await ElMessageBox.confirm('确认收货？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 更新订单状态为已完成
    await orderStore.completeOrder(order.id)
    
    ElMessage.success('已确认收货')
    fetchOrders() // 刷新订单数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error('确认收货失败')
    }
  }
}

// 发货
const shipOrder = async (order: any) => {
  try {
    await ElMessageBox.confirm('确认发货？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 更新订单状态为已发货
    await orderStore.shipOrder(order.id)
    
    ElMessage.success('已发货')
    fetchOrders() // 刷新订单数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发货失败:', error)
      ElMessage.error('发货失败')
    }
  }
}

// 评价商品
const reviewProduct = (order: any) => {
  ElMessage.info('评价功能开发中')
}

// 跳转到商品列表页
const goToProducts = () => {
  router.push('/products')
}

// 跳转到发布商品页
const goToPublish = () => {
  router.push('/publish')
}

// 获取订单状态类型
const getOrderStatusType = (status: string) => {
  switch (status) {
    case 'PENDING': return 'warning'
    case 'PAID': return 'primary'
    case 'SHIPPED': return 'info'
    case 'COMPLETED': return 'success'
    case 'CANCELLED': return 'danger'
    default: return 'info'
  }
}

// 获取订单状态文本
const getOrderStatusText = (status: string) => {
  switch (status) {
    case 'PENDING': return '待付款'
    case 'PAID': return '已付款'
    case 'SHIPPED': return '已发货'
    case 'COMPLETED': return '已完成'
    case 'CANCELLED': return '已取消'
    default: return '未知状态'
  }
}

// 获取配送方式文本
const getDeliveryMethodText = (method: string) => {
  switch (method) {
    case 'PICKUP': return '自提'
    case 'DELIVERY': return '快递'
    case 'ON_CAMPUS': return '校内配送'
    default: return '未知'
  }
}

// 获取支付方式文本
const getPaymentMethodText = (method: string) => {
  switch (method) {
    case 'ALIPAY': return '支付宝'
    case 'WECHAT': return '微信支付'
    case 'BALANCE': return '余额支付'
    case 'CASH': return '现金'
    default: return '未知'
  }
}

// 获取支付状态文本
const getPaymentStatusText = (status: string) => {
  switch (status) {
    case 'UNPAID': return '未支付'
    case 'PAID': return '已支付'
    case 'REFUNDED': return '已退款'
    default: return '未知'
  }
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 页面加载时获取订单列表
onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.orders-section {
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.orders-tabs {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.orders-list {
  margin-top: 20px;
}

.order-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  background-color: #f9f9f9;
  padding: 10px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ebeef5;
}

.order-number {
  font-weight: 500;
  color: #303133;
}

.order-time {
  color: #909399;
  font-size: 14px;
}

.order-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-info {
  display: flex;
  gap: 15px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
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
}

.product-meta {
  display: flex;
  gap: 15px;
  color: #909399;
  font-size: 14px;
}

.order-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-info {
  text-align: center;
}

.price {
  font-size: 18px;
  font-weight: 500;
  color: #f56c6c;
}

.original-price {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.order-detail {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h3 {
  margin-bottom: 10px;
  font-size: 16px;
  color: #303133;
}

.product-detail {
  display: flex;
  gap: 15px;
}

.product-detail .product-image {
  width: 100px;
  height: 100px;
}

.product-detail .product-info h4 {
  font-size: 16px;
  margin-bottom: 10px;
}

.product-detail .product-info .price {
  font-size: 18px;
  font-weight: 500;
  color: #f56c6c;
}

@media (max-width: 768px) {
  .order-content {
    flex-direction: column;
  }
  
  .order-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .order-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>