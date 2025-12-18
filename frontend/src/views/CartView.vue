<template>
  <div class="cart-container">
    <AppHeader />

    <div class="cart-section">
      <div class="container">
        <div class="page-header">
          <h1>购物车</h1>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>购物车</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="cart-content">
          <div v-if="cartItems.length > 0" class="cart-main">
            <div class="cart-header">
              <div class="header-checkbox">
                <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
              </div>
              <div class="header-product">商品信息</div>
              <div class="header-price">单价</div>
              <div class="header-quantity">数量</div>
              <div class="header-subtotal">小计</div>
              <div class="header-actions">操作</div>
            </div>

            <div class="cart-items">
              <div v-for="item in cartItems" :key="item.id" class="cart-item" :class="{ 'item-sold-out': item.status === 'sold', 'item-offline': item.status === 'offline' }">
                <div class="item-checkbox">
                  <el-checkbox v-model="selectedItems" :label="item.id" @change="handleItemSelect"></el-checkbox>
                </div>

                <div class="item-product">
                  <div class="product-image">
                    <img :src="item.images && item.images.length > 0 ? item.images[0] : '/placeholder.svg'" :alt="item.title" />
                  </div>
                  <div class="product-info">
                    <h3 class="product-title">{{ item.title }}</h3>
                    <div class="product-status" v-if="item.status === 'sold'">已卖出</div>
                    <div class="product-status" v-else-if="item.status === 'offline'">已下架</div>
                    <div class="product-seller">
                      <el-avatar :size="20" :src="item.sellerAvatar || ''" />
                      <span>{{ item.sellerName || '未知卖家' }}</span>
                    </div>
                  </div>
                </div>

                <div class="item-price">¥{{ item.price ? item.price.toFixed(2) : '0.00' }}</div>

                <div class="item-quantity">
                  <el-input-number v-model="item.quantity" :min="1" :max="item.stock || 99" :disabled="item.status !== 'active'" @change="handleQuantityChange(item)" />
                </div>

                <div class="item-subtotal">¥{{ calculateSubtotal(item).toFixed(2) }}</div>

                <div class="item-actions">
                  <el-button type="danger" size="small" @click="removeItem(item.id)">移除</el-button>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="empty-cart">
            <el-empty description="购物车是空的">
              <el-button type="primary" @click="goToProducts">去购物</el-button>
            </el-empty>
          </div>

          <div v-if="cartItems.length > 0" class="cart-sidebar">
            <div class="sidebar-header">
              <h3>结算信息</h3>
            </div>

            <div class="sidebar-content">
              <div class="total-items">
                <span>已选商品：</span>
                <span class="value">{{ selectedItems.length }} 件</span>
              </div>

              <div class="total-price">
                <span>合计：</span>
                <span class="value">¥{{ calculateTotal().toFixed(2) }}</span>
              </div>

              <div class="delivery-options">
                <h4>配送方式</h4>
                <el-radio-group v-model="deliveryMethod">
                  <el-radio label="delivery">配送</el-radio>
                  <el-radio label="pickup">自提</el-radio>
                </el-radio-group>
              </div>

              <div class="pickup-info" v-if="deliveryMethod === 'pickup'">
                <h4>自提信息</h4>
                <el-select v-model="selectedPickupPoint" placeholder="选择自提点">
                  <el-option v-for="point in pickupPoints" :key="point.id" :label="point.name" :value="point.id" />
                </el-select>
                <el-input v-model="pickupTime" type="datetime-local" placeholder="选择自提时间" style="margin-top: 10px" />
              </div>

              <div class="delivery-info" v-if="deliveryMethod === 'delivery'">
                <h4>收货地址</h4>
                <el-select v-model="selectedAddress" placeholder="选择收货地址">
                  <el-option v-for="address in addresses" :key="address.id" :label="address.address" :value="address.id" />
                </el-select>
                <el-button type="text" @click="showAddAddressDialog = true">新增地址</el-button>
              </div>

              <div class="order-remark">
                <h4>备注</h4>
                <el-input v-model="orderRemark" type="textarea" placeholder="请输入备注信息" rows="3" />
              </div>

              <div class="checkout-button">
                <el-button type="primary" size="large" @click="handleCheckout" :disabled="selectedItems.length === 0"> 结算 ({{ selectedItems.length }}) </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import AppHeader from '@/components/AppHeader.vue'
import { cartApi, CartItem } from '@/api/cartApi'

const router = useRouter()

// 响应式数据
const cartItems = ref<CartItem[]>([])
const selectAll = ref(false)
const selectedItems = ref<number[]>([])
const deliveryMethod = ref('delivery')
const selectedAddress = ref('')
const selectedPickupPoint = ref('')
const pickupTime = ref('')
const orderRemark = ref('')
const showAddAddressDialog = ref(false)
const loading = ref(false)

// 模拟数据
const addresses = ref([{ id: 1, address: '广州应用科技学院' }])

const pickupPoints = ref([
  { id: 1, name: '清华大学南门' },
  { id: 2, name: '北京大学东门' },
])

// 从用户store获取当前用户ID
const userStore = useUserStore()
const currentUserId = computed(() => userStore.user?.id || 0)

// 获取购物车数据
const fetchCartItems = async () => {
  loading.value = true
  try {
    const response = await cartApi.getCartItems(currentUserId.value)
    cartItems.value = response
    // 初始化选中状态
    selectedItems.value = []
    selectAll.value = false
  } catch (error) {
    console.error('Failed to fetch cart items:', error)
    ElMessage.error('获取购物车数据失败')
  } finally {
    loading.value = false
  }
}

// 计算小计
const calculateSubtotal = (item: CartItem) => {
  return (item.price || 0) * (item.quantity || 0)
}

// 计算总计
const calculateTotal = () => {
  return cartItems.value.filter((item) => selectedItems.value.includes(item.id)).reduce((total, item) => total + calculateSubtotal(item), 0)
}

// 全选处理
const handleSelectAll = () => {
  if (selectAll.value) {
    // 全选时选择所有商品，不考虑状态
    selectedItems.value = cartItems.value.map((item) => item.id)
  } else {
    selectedItems.value = []
  }
}

// 单个商品选择处理
const handleItemSelect = () => {
  // 更新全选状态
  selectAll.value = selectedItems.value.length === cartItems.value.length
}

// 数量变化处理
const handleQuantityChange = async (item: CartItem) => {
  // 库存校验
  if (item.quantity > (item.stock || 99)) {
    item.quantity = item.stock || 99
    ElMessage.warning(`该商品库存不足，最多只能购买 ${item.stock || 99} 件`)
    return
  }

  try {
    await cartApi.updateCartItemQuantity(item.id, item.quantity)
    ElMessage.success('商品数量已更新')
  } catch (error) {
    console.error('Failed to update cart item quantity:', error)
    ElMessage.error('更新商品数量失败')
    // 恢复原来的数量
    fetchCartItems()
  }
}

// 移除商品
const removeItem = async (id: number) => {
  try {
    await cartApi.removeCartItem(id)
    // 从本地数据中移除
    const index = cartItems.value.findIndex((item) => item.id === id)
    if (index !== -1) {
      cartItems.value.splice(index, 1)
      selectedItems.value = selectedItems.value.filter((itemId) => itemId !== id)
    }
    ElMessage.success('商品已移除')
  } catch (error) {
    console.error('Failed to remove cart item:', error)
    ElMessage.error('移除商品失败')
  }
}

// 结算处理
const handleCheckout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }

  // 直接跳转到订单创建页，不检查商品状态
  router.push({
    path: '/order/create',
    query: {
      items: JSON.stringify(selectedItems.value),
      deliveryMethod: deliveryMethod.value,
      addressId: selectedAddress.value,
      pickupPointId: selectedPickupPoint.value,
      pickupTime: pickupTime.value,
      remark: orderRemark.value,
    },
  })
}

// 跳转到商品列表页
const goToProducts = () => {
  router.push('/products')
}

// 组件挂载时获取购物车数据
onMounted(() => {
  fetchCartItems()
})
</script>

<style scoped>
.cart-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.cart-section {
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

.cart-content {
  display: flex;
  gap: 20px;
}

.cart-main {
  flex: 1;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.cart-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  font-weight: 500;
  color: #606266;
}

.header-checkbox {
  width: 60px;
}

.header-product {
  flex: 1;
}

.header-price {
  width: 100px;
  text-align: center;
}

.header-quantity {
  width: 120px;
  text-align: center;
}

.header-subtotal {
  width: 100px;
  text-align: center;
}

.header-actions {
  width: 80px;
  text-align: center;
}

.cart-items {
  max-height: 600px;
  overflow-y: auto;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.3s;
}

.cart-item:hover {
  background-color: #fafafa;
}

.item-checkbox {
  width: 60px;
}

.item-product {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 80px;
  height: 80px;
  overflow: hidden;
  border-radius: 4px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: #f5f7fa;
}

.product-info {
  flex: 1;
}

.product-title {
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-status {
  font-size: 12px;
  color: #f56c6c;
  margin-bottom: 8px;
}

.product-seller {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #909399;
}

.item-price {
  width: 100px;
  text-align: center;
  font-size: 16px;
  color: #f56c6c;
  font-weight: 500;
}

.item-quantity {
  width: 120px;
  text-align: center;
}

.item-subtotal {
  width: 100px;
  text-align: center;
  font-size: 16px;
  color: #f56c6c;
  font-weight: 500;
}

.item-actions {
  width: 80px;
  text-align: center;
}

.item-sold-out,
.item-offline {
  opacity: 0.7;
}

.cart-sidebar {
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

.sidebar-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.total-items,
.total-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-price .value {
  font-size: 24px;
  color: #f56c6c;
  font-weight: 700;
}

.delivery-options h4,
.pickup-info h4,
.delivery-info h4,
.order-remark h4 {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.delivery-options .el-radio-group {
  display: flex;
  gap: 20px;
}

.checkout-button {
  margin-top: 20px;
}

.checkout-button .el-button {
  width: 100%;
}

.empty-cart {
  text-align: center;
  padding: 60px 0;
}

@media (max-width: 1024px) {
  .cart-content {
    flex-direction: column;
  }

  .cart-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .cart-header {
    padding: 10px;
    font-size: 14px;
  }

  .cart-item {
    padding: 15px 10px;
    flex-wrap: wrap;
  }

  .item-product {
    order: 2;
    flex-basis: 100%;
    margin: 10px 0;
  }

  .item-price,
  .item-quantity,
  .item-subtotal,
  .item-actions {
    order: 3;
    flex-basis: 50%;
    text-align: left;
    margin-bottom: 10px;
  }

  .header-product,
  .header-price,
  .header-quantity,
  .header-subtotal,
  .header-actions {
    display: none;
  }
}
</style>
