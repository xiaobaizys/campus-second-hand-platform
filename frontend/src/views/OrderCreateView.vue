<template>
  <div class="order-create-container">
    <AppHeader />

    <div class="order-create-section">
      <div class="container">
        <div class="page-header">
          <h1>创建订单</h1>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/cart' }">购物车</el-breadcrumb-item>
            <el-breadcrumb-item>创建订单</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="order-create-content">
          <div class="order-main">
            <!-- 收货地址 -->
            <div class="address-section">
              <div class="section-header">
                <h3>收货地址</h3>
                <el-button type="primary" size="small" @click="showAddAddressDialog = true">新增地址</el-button>
              </div>

              <div class="address-list">
                <div v-for="address in addresses" :key="address.id" class="address-item" :class="{ active: selectedAddress === address.id }" @click="selectedAddress = address.id">
                  <div class="address-info">
                    <div class="address-name-phone">
                      <span class="name">{{ address.name }}</span>
                      <span class="phone">{{ address.phone }}</span>
                    </div>
                    <div class="address-detail">{{ address.address }}</div>
                  </div>
                  <div class="address-actions">
                    <el-radio v-model="selectedAddress" :label="address.id" />
                  </div>
                </div>

                <div v-if="addresses.length === 0" class="no-address">
                  <el-empty description="暂无收货地址">
                    <el-button type="primary" @click="showAddAddressDialog = true">新增地址</el-button>
                  </el-empty>
                </div>
              </div>
            </div>

            <!-- 配送方式 -->
            <div class="delivery-section">
              <div class="section-header">
                <h3>配送方式</h3>
              </div>

              <div class="delivery-options">
                <div class="delivery-option" :class="{ active: deliveryMethod === 'delivery' }" @click="deliveryMethod = 'delivery'">
                  <el-radio v-model="deliveryMethod" label="delivery">配送</el-radio>
                  <div class="option-info">
                    <span class="option-title">配送到指定地址</span>
                    <span class="option-desc">预计1-2天送达</span>
                  </div>
                </div>

                <div class="delivery-option" :class="{ active: deliveryMethod === 'pickup' }" @click="deliveryMethod = 'pickup'">
                  <el-radio v-model="deliveryMethod" label="pickup">自提</el-radio>
                  <div class="option-info">
                    <span class="option-title">到指定地点自提</span>
                    <span class="option-desc">选择自提点和自提时间</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 自提信息 -->
            <div v-if="deliveryMethod === 'pickup'" class="pickup-section">
              <div class="section-header">
                <h3>自提信息</h3>
              </div>

              <div class="pickup-info">
                <div class="pickup-point">
                  <label>选择自提点</label>
                  <el-select v-model="selectedPickupPoint" placeholder="请选择自提点" style="width: 100%; margin-bottom: 20px">
                    <el-option v-for="point in pickupPoints" :key="point.id" :label="point.name" :value="point.id" />
                  </el-select>
                </div>

                <div class="pickup-time">
                  <label>选择自提时间</label>
                  <el-input v-model="pickupTime" type="datetime-local" placeholder="请选择自提时间" style="width: 100%" />
                </div>
              </div>
            </div>

            <!-- 订单商品 -->
            <div class="order-items-section">
              <div class="section-header">
                <h3>订单商品</h3>
              </div>

              <div class="order-items">
                <div v-for="item in orderItems" :key="item.id" class="order-item">
                  <div class="item-image">
                    <img :src="item.images && item.images.length > 0 ? item.images[0] : '/placeholder.svg'" :alt="item.title" />
                  </div>
                  <div class="item-info">
                    <div class="item-title">{{ item.title }}</div>
                    <div class="item-meta">
                      <span class="item-price">¥{{ item.price ? item.price.toFixed(2) : '0.00' }}</span>
                      <span class="item-quantity">x{{ item.quantity }}</span>
                    </div>
                  </div>
                  <div class="item-subtotal">¥{{ calculateSubtotal(item).toFixed(2) }}</div>
                </div>
              </div>
            </div>

            <!-- 订单备注 -->
            <div class="remark-section">
              <div class="section-header">
                <h3>订单备注</h3>
              </div>

              <div class="remark-content">
                <el-input v-model="orderRemark" type="textarea" placeholder="请输入备注信息（如自提时间、特殊要求等）" rows="3" style="width: 100%" />
              </div>
            </div>
          </div>

          <div class="order-sidebar">
            <div class="sidebar-header">
              <h3>订单金额</h3>
            </div>

            <div class="sidebar-content">
              <div class="order-summary">
                <div class="summary-item">
                  <span>商品总价：</span>
                  <span class="value">¥{{ calculateTotal().toFixed(2) }}</span>
                </div>

                <div class="summary-item">
                  <span>运费：</span>
                  <span class="value">{{ deliveryMethod === 'delivery' ? '¥10.00' : '¥0.00' }}</span>
                </div>

                <div class="summary-item discount">
                  <span>优惠：</span>
                  <span class="value">-¥0.00</span>
                </div>

                <div class="summary-total">
                  <span>实付款：</span>
                  <span class="value">¥{{ calculateFinalTotal().toFixed(2) }}</span>
                </div>
              </div>

              <div class="submit-section">
                <div class="total-price">
                  <span>合计：</span>
                  <span class="value">¥{{ calculateFinalTotal().toFixed(2) }}</span>
                </div>
                <el-button type="primary" size="large" @click="submitOrder" :loading="submitting"> 提交订单 </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增地址对话框 -->
    <el-dialog v-model="showAddAddressDialog" title="新增收货地址" width="500px">
      <el-form :model="newAddress" :rules="addressRules" ref="addressFormRef">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="newAddress.name" placeholder="请输入收货人姓名" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="newAddress.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="收货地址" prop="address">
          <el-input v-model="newAddress.address" type="textarea" placeholder="请输入详细地址" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddAddressDialog = false">取消</el-button>
          <el-button type="primary" @click="addAddress">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppHeader from '@/components/AppHeader.vue'
import { useProductStore } from '@/store/product'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const productStore = useProductStore()
const userStore = useUserStore()

// 响应式数据
const selectedAddress = ref('')
const deliveryMethod = ref('delivery')
const selectedPickupPoint = ref('')
const pickupTime = ref('')
const orderRemark = ref('')
const showAddAddressDialog = ref(false)
const submitting = ref(false)

// 表单引用
const addressFormRef = ref()

// 订单商品
const orderItems = ref([])

// 初始化为空数组，将在onMounted中根据用户信息生成默认地址
const addresses = ref([])

const pickupPoints = ref([
  { id: 1, name: '广州应用科技学院北门' },
  { id: 2, name: '广州应用科技学院南门' },
])

// 新增地址表单
const newAddress = ref({
  name: '',
  phone: '',
  address: '',
})

// 地址表单规则
const addressRules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
}

// 计算小计
const calculateSubtotal = (item: any) => {
  return (item.price || 0) * (item.quantity || 0)
}

// 计算商品总价
const calculateTotal = () => {
  return orderItems.value.reduce((total, item) => total + calculateSubtotal(item), 0)
}

// 计算最终总价
const calculateFinalTotal = () => {
  const total = calculateTotal()
  const shippingFee = deliveryMethod.value === 'delivery' ? 10 : 0
  return total + shippingFee
}

// 新增地址
const addAddress = async () => {
  if (!addressFormRef.value) return

  await addressFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      // 模拟添加地址
      const newId = addresses.value.length + 1
      const address = {
        id: newId,
        ...newAddress.value,
      }
      addresses.value.push(address)
      selectedAddress.value = newId
      showAddAddressDialog.value = false
      ElMessage.success('地址添加成功')

      // 重置表单
      newAddress.value = {
        name: '',
        phone: '',
        address: '',
      }
    }
  })
}

// 提交订单
const submitOrder = async () => {
  if (!selectedAddress) {
    ElMessage.warning('请选择收货地址')
    return
  }

  if (deliveryMethod.value === 'pickup' && !selectedPickupPoint) {
    ElMessage.warning('请选择自提点')
    return
  }

  submitting.value = true
  try {
    // 模拟提交订单
    await new Promise((resolve) => setTimeout(resolve, 1500))
    ElMessage.success('订单创建成功')
    router.push('/orders')
  } catch (error) {
    ElMessage.error('订单创建失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 页面加载时处理路由参数
onMounted(async () => {
  // 从路由参数中获取购物车选中的商品
  const itemsParam = route.query.items
  // 从路由参数中获取商品列表选中的商品ID
  const productIdsParam = route.query.productIds

  // 处理商品列表传递过来的商品ID
  if (productIdsParam) {
    try {
      const productIds = (productIdsParam as string).split(',').map(Number)
      console.log('从商品列表选中的商品ID:', productIds)

      // 使用模拟数据构建订单商品列表（因为productStore.fetchProductById会覆盖currentProduct）
      // 实际项目中应该调用批量获取商品详情的API
      orderItems.value = productIds.map((id, index) => {
        // 这里使用模拟数据，实际项目中应该从后端获取
        return {
          id: index + 1,
          productId: id,
          title: `商品 ${id}`,
          price: 100 + id * 10,
          quantity: 1,
          images: [`/placeholder.svg`],
        }
      })

      console.log('构建的订单商品列表:', orderItems.value)
    } catch (error) {
      console.error('处理商品ID失败:', error)
      ElMessage.error('处理商品失败，请重试')
    }
  } else if (itemsParam) {
    // 处理购物车传递过来的商品
    try {
      const items = JSON.parse(itemsParam as string)
      console.log('从购物车选中的商品:', items)
      // 这里可以根据items获取商品详情，现在使用模拟数据
    } catch (error) {
      console.error('解析商品数据失败:', error)
    }
  }

  // 根据当前用户信息生成默认地址
  if (userStore.isAuthenticated && userStore.user) {
    const currentUser = userStore.user
    // 生成默认地址
    const defaultAddress = {
      id: 1,
      name: currentUser.username || '未知用户',
      phone: currentUser.phone || '13800138000',
      address: '广州应用科技学院',
    }
    // 设置默认地址
    addresses.value = [defaultAddress]
    selectedAddress.value = defaultAddress.id
  }
})
</script>

<style scoped>
.order-create-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.order-create-section {
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

.order-create-content {
  display: flex;
  gap: 20px;
}

.order-main {
  flex: 1;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 16px;
  color: #303133;
  margin: 0;
}

/* 地址部分 */
.address-list {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.address-item {
  flex: 1;
  min-width: 300px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #409eff;
}

.address-item.active {
  border-color: #409eff;
  background-color: rgba(64, 158, 255, 0.05);
}

.address-info {
  margin-bottom: 10px;
}

.address-name-phone {
  display: flex;
  gap: 20px;
  margin-bottom: 8px;
}

.address-name-phone .name {
  font-weight: 500;
  color: #303133;
}

.address-name-phone .phone {
  color: #606266;
}

.address-detail {
  color: #606266;
  line-height: 1.5;
}

.address-actions {
  text-align: right;
}

.no-address {
  width: 100%;
  padding: 40px 0;
}

/* 配送方式 */
.delivery-options {
  display: flex;
  gap: 20px;
}

.delivery-option {
  flex: 1;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 15px;
}

.delivery-option:hover {
  border-color: #409eff;
}

.delivery-option.active {
  border-color: #409eff;
  background-color: rgba(64, 158, 255, 0.05);
}

.option-info {
  flex: 1;
}

.option-title {
  display: block;
  font-weight: 500;
  color: #303133;
  margin-bottom: 5px;
}

.option-desc {
  font-size: 14px;
  color: #909399;
}

/* 自提信息 */
.pickup-info {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.pickup-info label {
  display: block;
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

/* 订单商品 */
.order-items {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  overflow: hidden;
  border-radius: 4px;
  margin-right: 20px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-title {
  font-size: 16px;
  color: #303133;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-meta {
  display: flex;
  gap: 20px;
  align-items: center;
}

.item-price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: 500;
}

.item-quantity {
  font-size: 14px;
  color: #909399;
}

.item-subtotal {
  font-size: 16px;
  color: #f56c6c;
  font-weight: 500;
}

/* 订单备注 */
.remark-content {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

/* 侧边栏 */
.order-sidebar {
  width: 350px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  height: fit-content;
}

.sidebar-header h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
}

.order-summary {
  margin-bottom: 20px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 14px;
  color: #606266;
}

.summary-item.discount {
  color: #f56c6c;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.summary-total .value {
  color: #f56c6c;
  font-size: 18px;
}

.submit-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.total-price {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.total-price .value {
  color: #f56c6c;
}

.submit-section .el-button {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .order-create-content {
    flex-direction: column;
  }

  .order-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .address-list {
    flex-direction: column;
  }

  .address-item {
    min-width: auto;
  }

  .delivery-options {
    flex-direction: column;
  }

  .order-item {
    flex-wrap: wrap;
  }

  .item-image {
    margin-bottom: 15px;
  }

  .item-info {
    flex-basis: calc(100% - 100px);
  }

  .item-subtotal {
    flex-basis: 100%;
    margin-top: 15px;
    text-align: right;
  }
}
</style>
