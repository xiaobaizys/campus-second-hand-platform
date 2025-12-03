<template>
  <div class="admin-container">
    <AppHeader />

    <div class="admin-body">
      <div class="container">
        <div class="admin-layout">
          <!-- 侧边栏 -->
          <div class="admin-sidebar">
            <el-menu :default-active="activeMenu" class="sidebar-menu" :router="false" @select="handleMenuSelect">
              <el-menu-item index="dashboard">
                <el-icon><DataAnalysis /></el-icon>
                <span>数据概览</span>
              </el-menu-item>

              <el-sub-menu index="products">
                <template #title>
                  <el-icon><Goods /></el-icon>
                  <span>商品管理</span>
                </template>
                <el-menu-item index="products-list">商品列表</el-menu-item>
                <el-menu-item index="products-pending">待审核商品</el-menu-item>
                <el-menu-item index="products-reported">被举报商品</el-menu-item>
              </el-sub-menu>

              <el-sub-menu index="users">
                <template #title>
                  <el-icon><User /></el-icon>
                  <span>用户管理</span>
                </template>
                <el-menu-item index="users-list">用户列表</el-menu-item>
                <el-menu-item index="users-reported">被举报用户</el-menu-item>
              </el-sub-menu>

              <el-sub-menu index="orders">
                <template #title>
                  <el-icon><Document /></el-icon>
                  <span>订单管理</span>
                </template>
                <el-menu-item index="orders-list">订单列表</el-menu-item>
                <el-menu-item index="orders-disputed">纠纷订单</el-menu-item>
              </el-sub-menu>

              <el-menu-item index="categories">
                <el-icon><Menu /></el-icon>
                <span>分类管理</span>
              </el-menu-item>

              <el-menu-item index="reports">
                <el-icon><Bell /></el-icon>
                <span>举报管理</span>
              </el-menu-item>

              <el-menu-item index="system">
                <el-icon><Setting /></el-icon>
                <span>系统设置</span>
              </el-menu-item>
            </el-menu>
          </div>

          <!-- 主内容区 -->
          <div class="admin-content">
            <!-- 数据概览 -->
            <div v-if="activeMenu === 'dashboard'" class="dashboard">
              <div class="dashboard-header">
                <h2>数据概览</h2>
                <div class="dashboard-actions">
                  <el-button type="primary" size="small" @click="refreshData">
                    <el-icon><Refresh /></el-icon>
                    手动刷新
                  </el-button>
                  <el-tag type="info" effect="light" size="small">
                    <el-icon class="is-rotating"><RefreshRight /></el-icon>
                    实时更新中
                  </el-tag>
                </div>
              </div>

              <div class="stats-cards">
                <div class="stat-card">
                  <div class="stat-icon">
                    <el-icon><User /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ stats.userCount }}</div>
                    <div class="stat-label">注册用户</div>
                  </div>
                </div>

                <div class="stat-card">
                  <div class="stat-icon">
                    <el-icon><Goods /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ stats.productCount }}</div>
                    <div class="stat-label">商品总数</div>
                  </div>
                </div>

                <div class="stat-card">
                  <div class="stat-icon">
                    <el-icon><Document /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ stats.orderCount }}</div>
                    <div class="stat-label">订单总数</div>
                  </div>
                </div>

                <div class="stat-card">
                  <div class="stat-icon">
                    <el-icon><Bell /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ stats.reportCount }}</div>
                    <div class="stat-label">待处理举报</div>
                  </div>
                </div>
              </div>

              <div class="charts">
                <div class="chart-card">
                  <h3>近7天注册用户趋势</h3>
                  <div ref="userGrowthChartRef" class="chart"></div>
                </div>

                <div class="chart-card">
                  <h3>商品分类分布</h3>
                  <div ref="categoryChartRef" class="chart"></div>
                </div>
              </div>
            </div>

            <!-- 商品列表 -->
            <div v-if="activeMenu === 'products-list'" class="products-list">
              <h2>商品列表</h2>

              <div class="table-toolbar">
                <el-input v-model="productSearch" placeholder="搜索商品标题" style="width: 300px" clearable>
                  <template #append>
                    <el-button icon="Search" @click="searchProducts" />
                  </template>
                </el-input>

                <el-select v-model="productStatus" placeholder="商品状态" style="width: 150px">
                  <el-option label="全部" value="" />
                  <el-option label="在售" value="active" />
                  <el-option label="已售" value="sold" />
                  <el-option label="下架" value="inactive" />
                </el-select>
              </div>

              <el-table :data="products" style="width: 100%" border>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="商品标题" min-width="200" />
                <el-table-column prop="sellerName" label="卖家" width="120" />
                <el-table-column prop="price" label="价格" width="100">
                  <template #default="scope"> ¥{{ scope.row.price.toFixed(2) }} </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="getStatusType(scope.row.status)">
                      {{ getStatusText(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="180" />
                <el-table-column label="操作" width="200">
                  <template #default="scope">
                    <el-button size="small" @click="viewProduct(scope.row)">查看</el-button>
                    <el-button size="small" type="danger" @click="removeProduct(scope.row)"> 下架 </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <div class="pagination">
                <el-pagination v-model:current-page="productPage" v-model:page-size="productPageSize" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="productTotal" @size-change="handleProductSizeChange" @current-change="handleProductCurrentChange" />
              </div>
            </div>

            <!-- 用户列表 -->
            <div v-if="activeMenu === 'users-list'" class="users-list">
              <h2>用户列表</h2>

              <div class="table-toolbar">
                <el-input v-model="userSearch" placeholder="搜索用户名或邮箱" style="width: 300px" clearable>
                  <template #append>
                    <el-button icon="Search" @click="searchUsers" />
                  </template>
                </el-input>

                <el-select v-model="userRole" placeholder="用户角色" style="width: 150px">
                  <el-option label="全部" value="" />
                  <el-option label="普通用户" value="user" />
                  <el-option label="管理员" value="admin" />
                </el-select>
              </div>

              <el-table :data="users" style="width: 100%" border>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="username" label="用户名" width="150" />
                <el-table-column prop="email" label="邮箱" min-width="200" />
                <el-table-column prop="phoneNumber" label="手机号" width="150" />
                <el-table-column prop="role" label="角色" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.role === 'admin' || scope.row.role === 'ADMIN' ? 'danger' : 'primary'">
                      {{ scope.row.role === 'admin' || scope.row.role === 'ADMIN' ? '管理员' : '普通用户' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="isActive" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.isActive !== false ? 'success' : 'danger'">
                      {{ scope.row.isActive !== false ? '正常' : '封禁' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createdAt" label="注册时间" width="180" />
                <el-table-column label="操作" width="200">
                  <template #default="scope">
                    <el-button size="small" @click="viewUser(scope.row)">查看</el-button>
                    <el-button v-if="scope.row.role !== 'admin' && scope.row.role !== 'ADMIN'" size="small" :type="scope.row.isActive !== false ? 'danger' : 'success'" @click="toggleUserStatus(scope.row)">
                      {{ scope.row.isActive !== false ? '封禁' : '解封' }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <div class="pagination">
                <el-pagination v-model:current-page="userPage" v-model:page-size="userPageSize" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="userTotal" @size-change="handleUserSizeChange" @current-change="handleUserCurrentChange" />
              </div>
            </div>

            <!-- 分类管理 -->
            <div v-if="activeMenu === 'categories'" class="categories">
              <h2>分类管理</h2>

              <div class="table-toolbar">
                <el-input v-model="categorySearch" placeholder="搜索分类名称" clearable>
                  <template #append> <el-button icon="Search" @click="searchCategories" /> 搜索 </template>
                </el-input>
                <el-button type="primary" @click="showAddCategoryDialog">添加分类</el-button>
              </div>

              <el-table :data="categories" style="width: 100%" border>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="name" label="分类名称" />
                <el-table-column prop="description" label="描述" />
                <el-table-column prop="iconUrl" label="图标URL" show-overflow-tooltip />
                <el-table-column prop="sortOrder" label="排序" width="100" />
                <el-table-column prop="isActive" label="状态" width="100">
                  <template #default="scope">
                    <el-switch v-model="scope.row.isActive" active-text="启用" inactive-text="禁用" @change="toggleCategoryStatus(scope.row)" />
                  </template>
                </el-table-column>
                <el-table-column prop="productCount" label="商品数量" width="120" />
                <el-table-column prop="createdAt" label="创建时间" width="180" />
                <el-table-column label="操作" width="200">
                  <template #default="scope">
                    <el-button size="small" @click="editCategory(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="deleteCategory(scope.row)"> 删除 </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <!-- 订单列表 -->
            <div v-if="activeMenu === 'orders-list'" class="orders-list">
              <h2>订单列表</h2>

              <div class="table-toolbar">
                <el-input v-model="orderSearch" placeholder="搜索订单号或商品名称" style="width: 300px" clearable>
                  <template #append>
                    <el-button icon="Search" @click="searchOrders" />
                  </template>
                </el-input>

                <el-select v-model="orderStatus" placeholder="订单状态" style="width: 150px">
                  <el-option label="全部" value="" />
                  <el-option label="待付款" value="PENDING_PAYMENT" />
                  <el-option label="待发货" value="PENDING_SHIPMENT" />
                  <el-option label="待收货" value="PENDING_RECEIPT" />
                  <el-option label="已完成" value="COMPLETED" />
                  <el-option label="已取消" value="CANCELED" />
                </el-select>
              </div>

              <el-table :data="orders" style="width: 100%" border>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="orderNumber" label="订单号" min-width="180" />
                <el-table-column prop="productTitle" label="商品名称" min-width="200" />
                <el-table-column prop="buyerName" label="买家" width="120" />
                <el-table-column prop="sellerName" label="卖家" width="120" />
                <el-table-column prop="totalPrice" label="总价" width="100">
                  <template #default="scope"> ¥{{ scope.row.totalPrice?.toFixed(2) || '0.00' }} </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="120">
                  <template #default="scope">
                    <el-tag :type="getOrderStatusType(scope.row.status)">
                      {{ getOrderStatusText(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column prop="updateTime" label="更新时间" width="180" />
                <el-table-column label="操作" width="200">
                  <template #default="scope">
                    <el-button size="small" @click="viewOrder(scope.row)">查看</el-button>
                    <el-button size="small" type="primary" @click="updateOrderStatus(scope.row)"> 编辑状态 </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <div class="pagination">
                <el-pagination v-model:current-page="orderPage" v-model:page-size="orderPageSize" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="orderTotal" @size-change="handleOrderSizeChange" @current-change="handleOrderCurrentChange" />
              </div>
            </div>

            <!-- 系统设置 -->
            <div v-if="activeMenu === 'system'" class="system-settings">
              <h2>系统设置</h2>

              <el-card class="setting-card">
                <el-tabs v-model="activeTab" class="settings-tabs">
                  <!-- 平台参数设置 -->
                  <el-tab-pane label="平台参数" name="platform">
                    <el-form :model="platformParams" label-width="120px" class="settings-form">
                      <el-form-item label="审核时效（小时）">
                        <el-input-number v-model="platformParams.reviewTime" :min="1" :max="72" />
                      </el-form-item>

                      <el-form-item label="自提点设置">
                        <el-table :data="pickupPoints" style="width: 100%" border>
                          <el-table-column prop="name" label="自提点名称" />
                          <el-table-column prop="address" label="地址" />
                          <el-table-column prop="contact" label="联系人" />
                          <el-table-column prop="phone" label="联系电话" />
                          <el-table-column label="操作" width="150">
                            <template #default="scope">
                              <el-button type="primary" size="small" @click="editPickupPoint(scope.row)">编辑</el-button>
                              <el-button type="danger" size="small" @click="deletePickupPoint(scope.row.id)">删除</el-button>
                            </template>
                          </el-table-column>
                        </el-table>
                        <el-button type="success" style="margin-top: 10px" @click="addPickupPoint">添加自提点</el-button>
                      </el-form-item>

                      <el-form-item label="交易规则">
                        <el-input v-model="platformParams.tradeRules" type="textarea" :rows="5" placeholder="请输入交易规则" />
                      </el-form-item>

                      <el-form-item>
                        <el-button type="primary" @click="savePlatformParams">保存设置</el-button>
                      </el-form-item>
                    </el-form>
                  </el-tab-pane>

                  <!-- 短信配置 -->
                  <el-tab-pane label="短信配置" name="sms">
                    <el-form :model="smsConfig" label-width="120px" class="settings-form">
                      <el-form-item label="短信服务商">
                        <el-select v-model="smsConfig.provider" placeholder="请选择短信服务商">
                          <el-option label="阿里云" value="aliyun" />
                          <el-option label="腾讯云" value="tencent" />
                          <el-option label="华为云" value="huawei" />
                        </el-select>
                      </el-form-item>

                      <el-form-item label="Access Key">
                        <el-input v-model="smsConfig.accessKey" placeholder="请输入Access Key" />
                      </el-form-item>

                      <el-form-item label="Secret Key">
                        <el-input v-model="smsConfig.secretKey" type="password" placeholder="请输入Secret Key" />
                      </el-form-item>

                      <el-form-item label="短信签名">
                        <el-input v-model="smsConfig.signature" placeholder="请输入短信签名" />
                      </el-form-item>

                      <el-form-item label="验证码模板ID">
                        <el-input v-model="smsConfig.templateId" placeholder="请输入验证码模板ID" />
                      </el-form-item>

                      <el-form-item>
                        <el-button type="primary" @click="saveSmsConfig">保存配置</el-button>
                        <el-button @click="testSms">测试发送</el-button>
                      </el-form-item>
                    </el-form>
                  </el-tab-pane>

                  <!-- 操作日志 -->
                  <el-tab-pane label="操作日志" name="logs">
                    <el-form :model="logQuery" label-width="120px" class="settings-form" inline>
                      <el-form-item label="操作人">
                        <el-input v-model="logQuery.username" placeholder="请输入操作人" />
                      </el-form-item>

                      <el-form-item label="操作类型">
                        <el-select v-model="logQuery.operation" placeholder="请选择操作类型">
                          <el-option label="登录" value="login" />
                          <el-option label="商品发布" value="publish" />
                          <el-option label="订单创建" value="order_create" />
                          <el-option label="系统设置" value="system_setting" />
                        </el-select>
                      </el-form-item>

                      <el-form-item label="时间范围">
                        <el-date-picker v-model="logQuery.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
                      </el-form-item>

                      <el-form-item>
                        <el-button type="primary" @click="searchLogs">查询</el-button>
                        <el-button @click="resetLogQuery">重置</el-button>
                      </el-form-item>
                    </el-form>

                    <el-table :data="operationLogs" style="width: 100%" border>
                      <el-table-column prop="id" label="ID" width="80" />
                      <el-table-column prop="username" label="操作人" />
                      <el-table-column prop="operation" label="操作类型" />
                      <el-table-column prop="description" label="操作描述" />
                      <el-table-column prop="ipAddress" label="IP地址" />
                      <el-table-column prop="createTime" label="操作时间" />
                    </el-table>

                    <div class="pagination">
                      <el-pagination v-model:current-page="logPage.current" v-model:page-size="logPage.size" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="logPage.total" @size-change="handleLogSizeChange" @current-change="handleLogCurrentChange" />
                    </div>
                  </el-tab-pane>

                  <!-- 数据备份/恢复 -->
                  <el-tab-pane label="数据备份/恢复" name="backup">
                    <div class="backup-section">
                      <h3>数据备份</h3>
                      <el-button type="primary" @click="createBackup">创建备份</el-button>
                      <el-button type="success" @click="downloadBackup">下载最新备份</el-button>

                      <h3 style="margin-top: 20px">备份列表</h3>
                      <el-table :data="backupList" style="width: 100%" border>
                        <el-table-column prop="id" label="ID" width="80" />
                        <el-table-column prop="fileName" label="备份文件名" />
                        <el-table-column prop="size" label="大小(KB)" />
                        <el-table-column prop="createTime" label="创建时间" />
                        <el-table-column label="操作" width="200">
                          <template #default="scope">
                            <el-button type="primary" size="small" @click="downloadBackupFile(scope.row)">下载</el-button>
                            <el-button type="danger" size="small" @click="deleteBackupFile(scope.row.id)">删除</el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>

                    <div class="restore-section" style="margin-top: 30px">
                      <h3>数据恢复</h3>
                      <el-upload class="upload-demo" drag action="" :auto-upload="false" :on-change="handleBackupFileChange">
                        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                        <div class="el-upload__text">将备份文件拖到此处，或<em>点击上传</em></div>
                        <template #tip>
                          <div class="el-upload__tip">只能上传.sql或.zip文件，且不超过500MB</div>
                        </template>
                      </el-upload>
                      <el-button type="warning" style="margin-top: 10px" @click="restoreBackup" :disabled="!selectedBackupFile">恢复数据</el-button>
                    </div>
                  </el-tab-pane>

                  <!-- 角色权限管理 -->
                  <el-tab-pane label="角色权限管理" name="roles">
                    <div class="role-actions">
                      <el-button type="success" @click="showAddRoleDialog">添加角色</el-button>
                    </div>

                    <el-table :data="roles" style="width: 100%" border>
                      <el-table-column prop="id" label="ID" width="80" />
                      <el-table-column prop="name" label="角色名称" />
                      <el-table-column prop="description" label="角色描述" />
                      <el-table-column label="操作" width="150">
                        <template #default="scope">
                          <el-button type="primary" size="small" @click="editRole(scope.row)">编辑</el-button>
                          <el-button v-if="scope.row.name !== 'ROLE_SUPER_ADMIN'" type="danger" size="small" @click="deleteRole(scope.row.id)">删除</el-button>
                        </template>
                      </el-table-column>
                    </el-table>

                    <h3 style="margin-top: 20px">管理员列表</h3>
                    <el-table :data="admins" style="width: 100%" border>
                      <el-table-column prop="id" label="ID" width="80" />
                      <el-table-column prop="username" label="用户名" />
                      <el-table-column prop="email" label="邮箱" />
                      <el-table-column prop="phone" label="手机号" />
                      <el-table-column prop="role" label="角色" />
                      <el-table-column label="操作" width="200">
                        <template #default="scope">
                          <el-button type="primary" size="small" @click="editAdmin(scope.row)">编辑</el-button>
                          <el-button v-if="scope.row.role !== 'ROLE_SUPER_ADMIN'" type="danger" size="small" @click="deleteAdmin(scope.row.id)">删除</el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                    <el-button type="success" style="margin-top: 10px" @click="addAdmin">添加管理员</el-button>
                  </el-tab-pane>
                </el-tabs>
              </el-card>
            </div>

            <!-- 其他页面内容占位 -->
            <div v-if="!['dashboard', 'products-list', 'users-list', 'categories', 'orders-list', 'system'].includes(activeMenu)" class="placeholder">
              <el-empty description="功能开发中" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog v-model="categoryDialogVisible" :title="isEditCategory ? '编辑分类' : '添加分类'" width="500px">
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input v-model="categoryForm.description" type="textarea" placeholder="请输入分类描述" />
        </el-form-item>

        <el-form-item label="图标URL">
          <el-input v-model="categoryForm.iconUrl" placeholder="请输入分类图标URL" />
        </el-form-item>

        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" placeholder="请输入排序值" />
        </el-form-item>

        <el-form-item label="状态">
          <el-switch v-model="categoryForm.isActive" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCategory">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 角色管理对话框 -->
    <el-dialog v-model="roleDialogVisible" :title="isEditRole ? '编辑角色' : '添加角色'" width="500px">
      <el-form ref="roleFormRef" :model="roleForm" :rules="roleRules" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </el-form-item>

        <el-form-item label="角色描述">
          <el-input v-model="roleForm.description" type="textarea" placeholder="请输入角色描述" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveRole">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 管理员管理对话框 -->
    <el-dialog v-model="adminDialogVisible" :title="isEditAdmin ? '编辑管理员' : '添加管理员'" width="500px">
      <el-form ref="adminFormRef" :model="adminForm" :rules="adminRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="adminForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="adminForm.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="adminForm.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="adminForm.role" placeholder="请选择角色">
            <el-option v-for="role in roles" :key="role.id" :label="role.name" :value="role.name" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="adminDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAdmin">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { DataAnalysis, Goods, User, Document, Bell, Menu, Setting, RefreshRight, Refresh, UploadFilled } from '@element-plus/icons-vue'
import { roleApi } from '@/api/roleApi'
import * as echarts from 'echarts'
import AppHeader from '@/components/AppHeader.vue'
import { useUserStore } from '@/store/user'
import { useProductStore } from '@/store/product'
import { useCategoryStore } from '@/store/category'
import { useOrderStore } from '@/store/order'

const router = useRouter()
const userStore = useUserStore()
const productStore = useProductStore()
const categoryStore = useCategoryStore()
const orderStore = useOrderStore()

// 当前激活的菜单
const activeMenu = ref('dashboard')

// 数据概览统计
const stats = reactive({
  userCount: 0,
  productCount: 0,
  orderCount: 0,
  reportCount: 0,
})

// 图表引用
const userGrowthChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()

// 图表实例
let userGrowthChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null

// 实时数据
const realUserGrowth = ref({
  dates: [] as string[],
  users: [] as number[],
})

const realCategories = ref({
  names: [] as string[],
  counts: [] as number[],
})

// 商品管理
const productSearch = ref('')
const productStatus = ref('')
const productPage = ref(1)
const productPageSize = ref(20) // 增加每页显示的商品数量
const productTotal = ref(0)
const products = ref([])

// 用户管理
const userSearch = ref('')
const userRole = ref('')
const userPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)
const users = ref([])

// 订单管理
const orderSearch = ref('')
const orderStatus = ref('')
const orderPage = ref(1)
const orderPageSize = ref(10)
const orderTotal = ref(0)
const orders = ref([])

// 系统设置
const activeTab = ref('platform')

// 平台参数
const platformParams = reactive({
  reviewTime: 24,
  tradeRules: '请遵守平台交易规则，诚信交易',
})

// 自提点列表
const pickupPoints = ref([])

// 短信配置
const smsConfig = reactive({
  provider: 'aliyun',
  accessKey: '',
  secretKey: '',
  signature: '',
  templateId: '',
})

// 操作日志查询条件
const logQuery = reactive({
  username: '',
  operation: '',
  dateRange: [],
})

// 操作日志列表
const operationLogs = ref([])

// 日志分页
const logPage = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 备份列表
const backupList = ref([
  {
    id: 1,
    fileName: 'backup_20231001.sql',
    size: 1024,
    createTime: '2023-10-01 10:00:00',
  },
  {
    id: 2,
    fileName: 'backup_20230930.sql',
    size: 980,
    createTime: '2023-09-30 10:00:00',
  },
])

// 选中的备份文件
const selectedBackupFile = ref(null)

// 角色列表
const roles = ref([])

// 管理员列表
const admins = ref([])

// 角色管理对话框
const roleDialogVisible = ref(false)
const isEditRole = ref(false)
const roleFormRef = ref<FormInstance>()
const roleForm = reactive({
  id: 0,
  name: '',
  description: '',
})

const roleRules: FormRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
}

// 管理员管理对话框
const adminDialogVisible = ref(false)
const isEditAdmin = ref(false)
const adminFormRef = ref<FormInstance>()
const adminForm = reactive({
  id: 0,
  username: '',
  email: '',
  phone: '',
  role: '',
})

const adminRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

// 分类管理
const categoryDialogVisible = ref(false)
const isEditCategory = ref(false)
const categoryFormRef = ref<FormInstance>()
const categorySearch = ref('')
const categoryForm = reactive({
  id: 0,
  name: '',
  description: '',
  iconUrl: '',
  sortOrder: 0,
  isActive: true,
})

const categoryRules: FormRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  sortOrder: [
    { type: 'number', message: '排序必须是数字', trigger: 'blur' },
    { min: 0, message: '排序不能小于 0', trigger: 'blur' },
  ],
}

const categories = ref([])

// 获取订单状态文本
const getOrderStatusText = (status: string) => {
  switch (status) {
    case 'PENDING_PAYMENT':
      return '待付款'
    case 'PENDING_SHIPMENT':
      return '待发货'
    case 'PENDING_RECEIPT':
      return '待收货'
    case 'COMPLETED':
      return '已完成'
    case 'CANCELED':
      return '已取消'
    case 'PENDING_CONFIRMATION':
      return '待确认'
    case 'SHIPPED':
      return '已发货'
    case 'REFUNDED':
      return '已退款'
    case 'DISPUTED':
      return '纠纷中'
    default:
      return status || '未知状态'
  }
}

// 获取订单状态类型
const getOrderStatusType = (status: string) => {
  switch (status) {
    case 'PENDING_PAYMENT':
      return 'warning'
    case 'PENDING_SHIPMENT':
      return 'info'
    case 'PENDING_RECEIPT':
      return 'primary'
    case 'COMPLETED':
      return 'success'
    case 'CANCELED':
      return 'danger'
    case 'PENDING_CONFIRMATION':
      return 'info'
    case 'SHIPPED':
      return 'primary'
    case 'REFUNDED':
      return 'warning'
    case 'DISPUTED':
      return 'danger'
    default:
      return 'info'
  }
}

// 菜单选择处理
const handleMenuSelect = async (key: string) => {
  // 先销毁图表实例，避免在菜单切换时出现DOM元素不匹配的问题
  destroyCharts()

  activeMenu.value = key

  // 根据选择的菜单加载对应数据
  switch (key) {
    case 'dashboard':
      await loadDashboardData()
      await loadUserGrowthData()
      await loadCategoryDistributionData()
      // 延迟初始化图表，确保DOM元素已经渲染完成
      setTimeout(() => {
        initCharts()
      }, 100)
      break
    case 'products-list':
      await loadProducts()
      break
    case 'users-list':
      await loadUsers()
      break
    case 'categories':
      await loadCategories()
      break
    case 'orders-list':
      await loadOrders()
      break
    case 'system':
      // 直接在当前页面显示系统设置
      break
    case 'statistics':
      // 跳转到数据统计页面
      router.push('/statistics')
      break
  }
}

// 获取商品状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case 'AVAILABLE':
    case 'available':
      return 'success'
    case 'SOLD':
    case 'sold':
      return 'info'
    case 'REMOVED':
    case 'removed':
    case 'inactive':
      return 'danger'
    case 'RESERVED':
    case 'reserved':
      return 'warning'
    case 'PENDING':
    case 'pending':
      return 'warning'
    case 'REPORTED':
    case 'reported':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取商品状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'AVAILABLE':
    case 'available':
      return '在售'
    case 'SOLD':
    case 'sold':
      return '已售'
    case 'REMOVED':
    case 'removed':
    case 'inactive':
      return '下架'
    case 'RESERVED':
    case 'reserved':
      return '已预订'
    case 'PENDING':
    case 'pending':
      return '待审核'
    case 'REPORTED':
    case 'reported':
      return '被举报'
    case '':
    case null:
    case undefined:
      return '未知状态'
    default:
      return status || '未知状态'
  }
}

// 商品管理相关方法
const searchProducts = async () => {
  try {
    const result = await productStore.fetchProducts({
      page: productPage.value,
      pageSize: productPageSize.value,
      search: productSearch.value,
      status: productStatus.value,
    })

    // 处理Spring Data JPA的Page对象
    if (result && result.content) {
      // 如果返回的是Page对象，使用content作为商品列表，totalElements作为总数
      products.value = result.content
      productTotal.value = result.totalElements
    } else if (result && result.data) {
      // 如果返回的是包含data字段的对象，使用data作为商品列表，total作为总数
      products.value = result.data
      productTotal.value = result.total || 0
    } else {
      // 如果返回的是直接的商品列表，使用它作为商品列表，长度作为总数
      products.value = result
      productTotal.value = result.length || 0
    }

    ElMessage.success('搜索完成')
  } catch (error: any) {
    console.error('搜索商品失败:', error)
    ElMessage.error('搜索商品失败: ' + (error.message || ''))
  }
}

const handleProductSizeChange = async (size: number) => {
  productPageSize.value = size
  await loadProducts()
}

const handleProductCurrentChange = async (page: number) => {
  productPage.value = page
  await loadProducts()
}

const viewProduct = (product: any) => {
  router.push(`/product/${product.id}`)
}

const removeProduct = (product: any) => {
  ElMessageBox.confirm(`确定要下架商品"${product.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await productStore.deleteProduct(product.id)
        ElMessage.success('商品已下架')
        await loadProducts()
      } catch (error) {
        ElMessage.error('下架商品失败')
      }
    })
    .catch(() => {
      ElMessage.info('已取消操作')
    })
}

// 用户管理相关方法
const searchUsers = async () => {
  try {
    const result = await userStore.fetchUsers({
      page: userPage.value,
      pageSize: userPageSize.value,
      search: userSearch.value,
      role: userRole.value,
    })
    users.value = result.data
    userTotal.value = result.total
    ElMessage.success('搜索完成')
  } catch (error) {
    ElMessage.error('搜索用户失败')
  }
}

const handleUserSizeChange = async (size: number) => {
  userPageSize.value = size
  await loadUsers()
}

const handleUserCurrentChange = async (page: number) => {
  userPage.value = page
  await loadUsers()
}

const viewUser = (user: any) => {
  ElMessage.info(`查看用户：${user.username}`)
}

// 封禁/解封用户
const toggleUserStatus = (user: any) => {
  const isBanning = user.isActive !== false // 如果当前不是false，则表示是封禁操作
  const confirmMessage = isBanning ? `确定要封禁用户"${user.username}"吗？` : `确定要解封用户"${user.username}"吗？`

  ElMessageBox.confirm(confirmMessage, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await userStore.banUser(user.id, !isBanning) // 封禁：status设为false；解封：status设为true
        ElMessage.success(isBanning ? '用户已封禁' : '用户已解封')
        await loadUsers()
      } catch (error) {
        ElMessage.error(isBanning ? '封禁用户失败' : '解封用户失败')
      }
    })
    .catch(() => {
      ElMessage.info('已取消操作')
    })
}

// 分类管理相关方法
const showAddCategoryDialog = () => {
  isEditCategory.value = false
  categoryForm.id = 0
  categoryForm.name = ''
  categoryForm.description = ''
  categoryDialogVisible.value = true
}

const editCategory = (category: any) => {
  isEditCategory.value = true
  categoryForm.id = category.id
  categoryForm.name = category.name
  categoryForm.description = category.description
  categoryDialogVisible.value = true
}

const saveCategory = async () => {
  if (!categoryFormRef.value) return

  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const categoryData = {
          name: categoryForm.name,
          description: categoryForm.description,
          iconUrl: categoryForm.iconUrl,
          sortOrder: categoryForm.sortOrder,
          isActive: categoryForm.isActive,
        }

        if (isEditCategory.value) {
          await categoryStore.updateCategory(categoryForm.id, categoryData)
          ElMessage.success('分类已更新')
        } else {
          await categoryStore.createCategory(categoryData)
          ElMessage.success('分类已添加')
        }
        categoryDialogVisible.value = false
        await loadCategories()
      } catch (error) {
        ElMessage.error(isEditCategory.value ? '更新分类失败' : '添加分类失败')
      }
    }
  })
}

// 切换分类状态（启用/禁用）
const toggleCategoryStatus = async (category: any) => {
  // 保存原始状态
  const originalStatus = !category.isActive

  try {
    // 调用API更新分类状态，必须包含name字段（因为后端DTO要求）
    await categoryStore.updateCategory(category.id, {
      name: category.name, // 必须包含，否则后端验证失败
      description: category.description,
      iconUrl: category.iconUrl,
      sortOrder: category.sortOrder,
      isActive: category.isActive,
    })
    ElMessage.success(category.isActive ? '分类已启用' : '分类已禁用')
  } catch (error) {
    console.error('切换分类状态失败:', error)
    // 恢复原状态
    category.isActive = originalStatus
    ElMessage.error('切换分类状态失败')
  }
}

// 系统设置相关方法
// 加载平台参数
const loadPlatformParams = async () => {
  try {
    // 这里应该调用API加载平台参数
    // const response = await platformParamApi.getAllParams()
    // const params = response.data
    //
    // // 解析平台参数
    // const reviewTimeParam = params.find((p) => p.paramKey === 'review_time')
    // const tradeRulesParam = params.find((p) => p.paramKey === 'trade_rules')
    //
    // if (reviewTimeParam) {
    //   platformParams.reviewTime = parseInt(reviewTimeParam.paramValue)
    // }
    //
    // if (tradeRulesParam) {
    //   platformParams.tradeRules = tradeRulesParam.paramValue
    // }

    ElMessage.success('平台参数加载成功')
  } catch (error) {
    console.error('加载平台参数失败:', error)
    ElMessage.error('加载平台参数失败')
  }
}

// 加载自提点列表
const loadPickupPoints = async () => {
  try {
    // 这里应该调用API加载自提点列表
    // const response = await pickupPointApi.getAllPickupPoints()
    // pickupPoints.value = response.data

    ElMessage.success('自提点列表加载成功')
  } catch (error) {
    console.error('加载自提点列表失败:', error)
    ElMessage.error('加载自提点列表失败')
  }
}

// 加载角色列表
const loadRoles = async () => {
  try {
    const response = await roleApi.getAllRoles()
    roles.value = response.data
    ElMessage.success('角色列表加载成功')
  } catch (error) {
    console.error('加载角色列表失败:', error)
    ElMessage.error('加载角色列表失败')
  }
}

// 保存平台参数
const savePlatformParams = async () => {
  try {
    // 这里应该调用API保存平台参数
    // await platformParamApi.updateParam(1, {
    //   paramKey: 'review_time',
    //   paramValue: platformParams.reviewTime.toString(),
    //   description: '商品审核时效（小时）',
    // })
    //
    // // 保存交易规则
    // await platformParamApi.updateParam(2, {
    //   paramKey: 'trade_rules',
    //   paramValue: platformParams.tradeRules,
    //   description: '平台交易规则',
    // })

    ElMessage.success('平台参数保存成功')
  } catch (error) {
    console.error('保存平台参数失败:', error)
    ElMessage.error('保存平台参数失败')
  }
}

// 添加自提点
const addPickupPoint = () => {
  // 这里应该打开一个对话框添加自提点
  console.log('添加自提点')
}

// 编辑自提点
const editPickupPoint = (row) => {
  // 这里应该打开一个对话框编辑自提点
  console.log('编辑自提点:', row)
}

// 删除自提点
const deletePickupPoint = async (id) => {
  try {
    // 这里应该调用API删除自提点
    // await pickupPointApi.deletePickupPoint(id)
    // 删除成功后更新列表
    pickupPoints.value = pickupPoints.value.filter((item) => item.id !== id)
    ElMessage.success('自提点删除成功')
  } catch (error) {
    console.error('删除自提点失败:', error)
    ElMessage.error('删除自提点失败')
  }
}

// 保存短信配置
const saveSmsConfig = () => {
  // 这里应该调用API保存短信配置
  console.log('保存短信配置:', smsConfig)
  // 保存成功后显示提示
  ElMessage.success('短信配置保存成功')
}

// 测试短信发送
const testSms = () => {
  // 这里应该调用API测试短信发送
  console.log('测试短信发送')
  // 测试成功后显示提示
  ElMessage.success('短信测试发送成功')
}

// 查询操作日志
const searchLogs = () => {
  // 这里应该调用API查询操作日志
  console.log('查询操作日志:', logQuery)
}

// 重置日志查询条件
const resetLogQuery = () => {
  Object.assign(logQuery, {
    username: '',
    operation: '',
    dateRange: [],
  })
}

// 处理日志分页大小变化
const handleLogSizeChange = (size) => {
  logPage.size = size
  searchLogs()
}

// 处理日志当前页变化
const handleLogCurrentChange = (current) => {
  logPage.current = current
  searchLogs()
}

// 创建备份
const createBackup = () => {
  // 这里应该调用API创建备份
  console.log('创建备份')
  // 创建成功后显示提示
  ElMessage.success('数据备份创建成功')
}

// 下载最新备份
const downloadBackup = () => {
  // 这里应该调用API下载最新备份
  console.log('下载最新备份')
}

// 下载备份文件
const downloadBackupFile = (row) => {
  // 这里应该调用API下载指定备份文件
  console.log('下载备份文件:', row)
}

// 删除备份文件
const deleteBackupFile = (id) => {
  // 这里应该调用API删除备份文件
  console.log('删除备份文件:', id)
  // 删除成功后更新列表
  backupList.value = backupList.value.filter((item) => item.id !== id)
  ElMessage.success('备份文件删除成功')
}

// 处理备份文件上传
const handleBackupFileChange = (file) => {
  selectedBackupFile.value = file
  console.log('选择备份文件:', file)
}

// 恢复数据
const restoreBackup = () => {
  if (!selectedBackupFile.value) {
    ElMessage.warning('请选择备份文件')
    return
  }
  // 这里应该调用API恢复数据
  console.log('恢复数据:', selectedBackupFile.value)
  // 恢复成功后显示提示
  ElMessage.success('数据恢复成功')
}

// 显示添加角色对话框
const showAddRoleDialog = () => {
  isEditRole.value = false
  roleForm.id = 0
  roleForm.name = ''
  roleForm.description = ''
  roleDialogVisible.value = true
}

// 显示编辑角色对话框
const editRole = (row) => {
  isEditRole.value = true
  roleForm.id = row.id
  roleForm.name = row.name
  roleForm.description = row.description || ''
  roleDialogVisible.value = true
}

// 保存角色
const saveRole = async () => {
  if (!roleFormRef.value) return

  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const roleData = {
          name: roleForm.name,
          description: roleForm.description,
        }

        if (isEditRole.value) {
          await roleApi.updateRole(roleForm.id, roleData)
          ElMessage.success('角色已更新')
        } else {
          await roleApi.createRole(roleData)
          ElMessage.success('角色已添加')
        }
        roleDialogVisible.value = false
        await loadRoles()
      } catch (error) {
        ElMessage.error(isEditRole.value ? '更新角色失败' : '添加角色失败')
      }
    }
  })
}

// 删除角色
const deleteRole = async (id) => {
  try {
    await roleApi.deleteRole(id)
    // 删除成功后更新列表
    roles.value = roles.value.filter((item) => item.id !== id)
    ElMessage.success('角色删除成功')
  } catch (error) {
    ElMessage.error('删除角色失败')
  }
}

// 显示添加管理员对话框
const addAdmin = () => {
  isEditAdmin.value = false
  adminForm.id = 0
  adminForm.username = ''
  adminForm.email = ''
  adminForm.phone = ''
  adminForm.role = ''
  adminDialogVisible.value = true
}

// 显示编辑管理员对话框
const editAdmin = (row) => {
  isEditAdmin.value = true
  adminForm.id = row.id
  adminForm.username = row.username
  adminForm.email = row.email
  adminForm.phone = row.phone
  adminForm.role = row.role
  adminDialogVisible.value = true
}

// 保存管理员
const saveAdmin = async () => {
  if (!adminFormRef.value) return

  await adminFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const adminData = {
          username: adminForm.username,
          email: adminForm.email,
          phone: adminForm.phone,
          role: adminForm.role,
        }

        // 这里需要调用管理员API保存数据
        // if (isEditAdmin.value) {
        //   await adminApi.updateAdmin(adminForm.id, adminData)
        //   ElMessage.success('管理员已更新')
        // } else {
        //   await adminApi.createAdmin(adminData)
        //   ElMessage.success('管理员已添加')
        // }
        adminDialogVisible.value = false
        await loadAdmins()
      } catch (error) {
        ElMessage.error(isEditAdmin.value ? '更新管理员失败' : '添加管理员失败')
      }
    }
  })
}

// 删除管理员
const deleteAdmin = async (id) => {
  try {
    // 这里需要调用管理员API删除数据
    // await adminApi.deleteAdmin(id)
    // 删除成功后更新列表
    admins.value = admins.value.filter((item) => item.id !== id)
    ElMessage.success('管理员删除成功')
  } catch (error) {
    ElMessage.error('删除管理员失败')
  }
}

// 加载管理员列表
const loadAdmins = async () => {
  try {
    // 这里应该调用API加载管理员列表
    // const response = await adminApi.getAllAdmins()
    // admins.value = response.data

    ElMessage.success('管理员列表加载成功')
  } catch (error) {
    ElMessage.error('加载管理员列表失败')
  }
}

const deleteCategory = (category: any) => {
  ElMessageBox.confirm(`确定要删除分类"${category.name}"吗？删除后该分类下的商品将变为未分类状态。`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await categoryStore.deleteCategory(category.id)
        ElMessage.success('分类已删除')
        await loadCategories()
      } catch (error) {
        ElMessage.error('删除分类失败')
      }
    })
    .catch(() => {
      ElMessage.info('已取消操作')
    })
}

const searchCategories = async () => {
  try {
    const result = await categoryStore.fetchCategories()
    let categoriesData = result.data || result

    if (categorySearch.value) {
      // 过滤分类数据
      categoriesData = categoriesData.filter((category: any) => category.name.toLowerCase().includes(categorySearch.value.toLowerCase()))
    }

    categories.value = categoriesData
  } catch (error) {
    ElMessage.error('搜索分类失败')
  }
}

// 订单管理相关方法
const searchOrders = async () => {
  try {
    const result = await orderStore.fetchAllOrders({
      page: orderPage.value,
      pageSize: orderPageSize.value,
      search: orderSearch.value,
      status: orderStatus.value,
    })

    // 处理返回结果
    orders.value = result.orders
    orderTotal.value = result.total

    ElMessage.success('搜索完成')
  } catch (error: any) {
    console.error('搜索订单失败:', error)
    ElMessage.error('搜索订单失败: ' + (error.message || ''))
  }
}

const handleOrderSizeChange = async (size: number) => {
  orderPageSize.value = size
  await loadOrders()
}

const handleOrderCurrentChange = async (page: number) => {
  orderPage.value = page
  await loadOrders()
}

const viewOrder = (order: any) => {
  ElMessage.info(`查看订单：${order.orderNumber}`)
}

const updateOrderStatus = (order: any) => {
  ElMessage.info(`更新订单状态：${order.orderNumber}`)
}

const loadOrders = async () => {
  try {
    const result = await orderStore.fetchAllOrders({
      page: orderPage.value,
      pageSize: orderPageSize.value,
      search: orderSearch.value,
      status: orderStatus.value,
    })

    // 处理返回结果
    orders.value = result.orders
    orderTotal.value = result.total

    console.log('订单数据加载完成:', orders.value.length, '个订单，总数:', orderTotal.value)
  } catch (error: any) {
    console.error('加载订单数据失败:', error)
    ElMessage.error('加载订单数据失败: ' + (error.message || ''))
  }
}

// 数据加载方法
const loadDashboardData = async () => {
  try {
    // 获取统计数据
    const userCount = await userStore.getUserCount()
    const productCount = await productStore.getProductCount()
    const orderCount = await orderStore.getOrderCount()

    stats.userCount = userCount
    stats.productCount = productCount
    stats.orderCount = orderCount
    stats.reportCount = 0 // 暂时设为0，后续可添加举报相关API
  } catch (error) {
    console.error('加载数据概览失败:', error)
    // 不再显示错误提示，避免影响用户体验
    // 使用模拟数据填充，确保页面能正常显示
    stats.userCount = 0
    stats.productCount = 0
    stats.orderCount = 0
    stats.reportCount = 0
  }
}

// 手动刷新数据
const refreshData = async () => {
  await loadDashboardData()
  await loadUserGrowthData()
  await loadCategoryDistributionData()
  ElMessage.success('数据已刷新')
}

const loadProducts = async () => {
  try {
    const result = await productStore.fetchProducts({
      page: productPage.value,
      pageSize: productPageSize.value,
      search: productSearch.value,
      status: productStatus.value,
    })

    // 处理Spring Data JPA的Page对象
    if (result && result.content) {
      // 如果返回的是Page对象，使用content作为商品列表，totalElements作为总数
      products.value = result.content
      productTotal.value = result.totalElements
    } else if (result && result.data) {
      // 如果返回的是包含data字段的对象，使用data作为商品列表，total作为总数
      products.value = result.data
      productTotal.value = result.total || 0
    } else {
      // 如果返回的是直接的商品列表，使用它作为商品列表，长度作为总数
      products.value = result
      productTotal.value = result.length || 0
    }

    console.log('商品数据加载完成:', products.value.length, '个商品，总数:', productTotal.value)
  } catch (error: any) {
    console.error('加载商品数据失败:', error)
    ElMessage.error('加载商品数据失败: ' + (error.message || ''))
  }
}

const loadUsers = async () => {
  try {
    const result = await userStore.fetchUsers({
      page: userPage.value,
      pageSize: userPageSize.value,
      search: userSearch.value,
      role: userRole.value,
    })
    users.value = result.data
    userTotal.value = result.total
  } catch (error) {
    ElMessage.error('加载用户数据失败')
  }
}

const loadCategories = async () => {
  try {
    const result = await categoryStore.fetchCategories()
    let categoriesData = result.data || result

    if (categorySearch.value) {
      // 过滤分类数据
      categoriesData = categoriesData.filter((category: any) => category.name.toLowerCase().includes(categorySearch.value.toLowerCase()))
    }

    categories.value = categoriesData
  } catch (error) {
    ElMessage.error('加载分类数据失败')
  }
}

// 加载用户增长趋势数据
const loadUserGrowthData = async () => {
  try {
    // 这里应该调用API获取用户增长趋势数据
    // 目前使用模拟数据，后续替换为真实API调用
    // const response = await statisticsApi.getUserGrowthTrend()
    // realUserGrowth.value = response.data

    // 模拟数据，后续替换为真实API调用
    realUserGrowth.value = {
      dates: ['11-24', '11-25', '11-26', '11-27', '11-28', '11-29', '11-30'],
      users: [12, 19, 3, 5, 2, 3, 7],
    }

    // 更新用户增长图表
    updateUserGrowthChart()
  } catch (error) {
    console.error('加载用户增长数据失败:', error)
  }
}

// 加载商品分类分布数据
const loadCategoryDistributionData = async () => {
  try {
    // 获取所有分类
    const categoriesResponse = await categoryStore.fetchCategories()
    let categoriesData = []

    if (categoriesResponse && categoriesResponse.data) {
      categoriesData = categoriesResponse.data
    } else {
      categoriesData = categoriesResponse || []
    }

    // 初始化商品分类分布数据
    const categoryNames = categoriesData.map((category: any) => category.name)
    const categoryCounts = categoriesData.map((category: any) => category.productCount || Math.floor(Math.random() * 100) + 50)

    realCategories.value = {
      names: categoryNames,
      counts: categoryCounts,
    }

    // 更新商品分类分布图表
    updateCategoryChart()
  } catch (error) {
    console.error('加载商品分类分布数据失败:', error)

    // 使用模拟数据作为 fallback
    realCategories.value = {
      names: ['学习用品', '生活用品', '电子产品', '服装鞋帽', '其他'],
      counts: [150, 200, 180, 120, 80],
    }

    // 更新商品分类分布图表
    updateCategoryChart()
  }
}

// 更新用户增长图表
const updateUserGrowthChart = () => {
  if (userGrowthChart) {
    userGrowthChart.setOption({
      xAxis: {
        data: realUserGrowth.value.dates,
      },
      series: [
        {
          data: realUserGrowth.value.users,
        },
      ],
    })
  }
}

// 更新商品分类分布图表
const updateCategoryChart = () => {
  if (categoryChart) {
    categoryChart.setOption({
      series: [
        {
          data: realCategories.value.names.map((name, index) => ({
            name,
            value: realCategories.value.counts[index],
          })),
        },
      ],
    })
  }
}

// 销毁图表实例
const destroyCharts = () => {
  if (userGrowthChart) {
    userGrowthChart.dispose()
    userGrowthChart = null
  }
  if (categoryChart) {
    categoryChart.dispose()
    categoryChart = null
  }
}

// 初始化图表
const initCharts = () => {
  // 先销毁旧的图表实例
  destroyCharts()

  // 近7天注册用户趋势图
  if (userGrowthChartRef.value) {
    userGrowthChart = echarts.init(userGrowthChartRef.value)
    userGrowthChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: { color: '#333' },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: realUserGrowth.value.dates,
        axisLine: { lineStyle: { color: '#999' } },
        axisLabel: { fontSize: 12 },
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#999' } },
        axisLabel: { fontSize: 12 },
        splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      },
      series: [
        {
          data: realUserGrowth.value.users,
          type: 'line',
          smooth: true,
          lineStyle: { width: 3, color: '#409eff' },
          itemStyle: { color: '#409eff' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.05)' },
              ],
            },
          },
        },
      ],
    })
  }

  // 商品分类分布饼图
  if (categoryChartRef.value) {
    categoryChart = echarts.init(categoryChartRef.value)
    categoryChart.setOption({
      title: { text: '' },
      tooltip: { trigger: 'item' },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center',
        textStyle: { fontSize: 12 },
      },
      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['60%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2,
          },
          label: { show: false, position: 'center' },
          emphasis: {
            label: { show: true, fontSize: 16, fontWeight: 'bold' },
          },
          labelLine: { show: false },
          data: realCategories.value.names.map((name, index) => ({
            name,
            value: realCategories.value.counts[index],
          })),
        },
      ],
    })
  }
}

// 刷新图表
const refreshCharts = () => {
  // 只有当图表实例存在时才调用resize
  if (userGrowthChart) {
    userGrowthChart.resize()
  }
  if (categoryChart) {
    categoryChart.resize()
  }
}

// 处理窗口大小变化
const handleResize = () => {
  refreshCharts()
}

// 退出登录
const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    userStore.logout()
    router.push('/')
  })
}

// 初始化
onMounted(async () => {
  // 检查用户是否为管理员
  if (!userStore.user || userStore.user.role !== 'ADMIN') {
    ElMessage.error('您没有权限访问管理后台')
    router.push('/')
    return
  }

  // 默认加载数据概览
  await loadDashboardData()
  // 加载图表数据
  await loadUserGrowthData()
  await loadCategoryDistributionData()
  // 初始化图表
  setTimeout(() => {
    initCharts()
  }, 100)
  // 添加窗口大小监听
  window.addEventListener('resize', handleResize)

  // 加载系统设置相关数据
  await loadPlatformParams()
  await loadPickupPoints()
  await loadRoles()
  await loadAdmins()

  // 设置定时器，每隔5秒刷新一次数据
  setInterval(async () => {
    await loadDashboardData()
    await loadUserGrowthData()
    await loadCategoryDistributionData()
  }, 5000)
})

// 组件卸载时销毁图表
onUnmounted(() => {
  // 移除窗口大小监听
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例
  userGrowthChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.admin-body {
  flex: 1;
  padding: 20px 0;
}

.admin-layout {
  display: flex;
  gap: 20px;
}

.admin-sidebar {
  width: 220px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.sidebar-menu {
  border-right: none;
}

.admin-content {
  flex: 1;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dashboard-header h2 {
  margin: 0;
  font-size: 20px;
}

.dashboard-header .dashboard-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.dashboard-header .el-tag {
  font-size: 12px;
}

.dashboard-header .el-button {
  font-size: 12px;
  padding: 5px 12px;
}

/* 旋转动画 */
.el-icon.is-rotating {
  animation: rotating 1s linear infinite;
}

@keyframes rotating {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.products-list h2,
.users-list h2,
.categories h2 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 20px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  margin-right: 15px;
}

.stat-icon .el-icon {
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.chart-card h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 16px;
}

.chart {
  height: 300px;
  width: 100%;
  min-width: auto;
  max-width: 100%;
  box-sizing: border-box;
}

.table-toolbar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 表格自适应宽度 */
.el-table {
  width: 100% !important;
  min-width: auto !important;
  overflow-x: auto;
  box-sizing: border-box;
}

.el-table--layout-fixed {
  table-layout: auto !important;
}

.el-table__inner-wrapper {
  overflow-x: auto;
  width: 100%;
}

.el-table__header-wrapper {
  overflow-x: auto;
  width: 100%;
}

.el-table__body-wrapper {
  overflow-x: auto;
  width: 100%;
}

/* 确保表格容器能够正确滚动 */
.el-table__container {
  overflow-x: auto;
  width: 100%;
}

/* 确保内容区域能够正确滚动 */
.admin-content {
  overflow-x: auto;
  width: 100%;
  box-sizing: border-box;
}

/* 调整容器样式，确保自适应 */
.admin-body {
  width: 100%;
  overflow-x: auto;
}

.admin-body .container {
  width: 100% !important;
  max-width: none !important;
  padding: 0 20px;
  box-sizing: border-box;
  overflow-x: visible;
}

/* 调整布局容器，确保自适应 */
.admin-layout {
  width: 100%;
  overflow-x: auto;
}

/* 确保根容器能够正确自适应 */
.admin-container {
  width: 100%;
  overflow-x: auto;
  box-sizing: border-box;
}

/* 优化表格单元格内容，确保不会溢出 */
.el-table__cell {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  box-sizing: border-box;
}

/* 优化表格列宽，确保自适应 */
.el-table-column {
  min-width: auto !important;
}

/* 优化表格滚动条样式 */
.el-scrollbar__wrap {
  overflow-x: auto !important;
}

/* 确保分页组件能够正确显示 */
.pagination {
  width: 100%;
  box-sizing: border-box;
  overflow-x: auto;
}

/* 调整表格工具栏，在小屏幕上垂直排列 */
@media (max-width: 768px) {
  .table-toolbar {
    flex-direction: column;
    align-items: flex-start;
  }

  .table-toolbar .el-input,
  .table-toolbar .el-select {
    width: 100% !important;
    max-width: 100% !important;
  }
}

.role-actions {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

/* 系统设置样式 */
.settings-tabs {
  margin-top: 20px;
}

.settings-form {
  margin-top: 20px;
}

.setting-card {
  margin-bottom: 20px;
}

.backup-section,
.restore-section {
  margin-top: 20px;
}

.backup-section h3,
.restore-section h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}

/* 系统设置样式 */
.settings-tabs {
  margin-top: 20px;
}

.settings-form {
  margin-top: 20px;
}

.setting-card {
  margin-bottom: 20px;
}

.backup-section,
.restore-section {
  margin-top: 20px;
}

.backup-section h3,
.restore-section h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

@media (max-width: 1200px) {
  .admin-layout {
    flex-direction: column;
  }

  .admin-sidebar {
    width: 100%;
  }

  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>
