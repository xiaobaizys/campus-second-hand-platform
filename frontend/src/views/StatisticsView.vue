<template>
  <div class="statistics-container">
    <AppHeader />
    <div class="statistics-content">
      <div class="statistics-header">
        <h1>数据统计</h1>
        <div class="date-range-picker">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" @change="handleDateRangeChange" />
          <el-button type="primary" @click="refreshStatistics">刷新数据</el-button>
        </div>
      </div>

      <div class="statistics-tabs">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="商品销量统计" name="sales">
            <div class="chart-container">
              <h3>商品销量趋势</h3>
              <div ref="salesChartRef" class="chart"></div>
            </div>
            <div class="chart-container">
              <h3>销量TOP10商品</h3>
              <div ref="topProductsChartRef" class="chart"></div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="用户增长统计" name="users">
            <div class="chart-container">
              <h3>用户增长趋势</h3>
              <div ref="userGrowthChartRef" class="chart"></div>
            </div>
            <div class="chart-container">
              <h3>用户增长分布（最近7天）</h3>
              <div ref="userGrowthDistributionChartRef" class="chart"></div>
            </div>
            <div class="chart-container">
              <h3>用户角色分布</h3>
              <div ref="userRoleDistributionChartRef" class="chart"></div>
            </div>
            <div class="chart-container">
              <h3>用户校园分布</h3>
              <div ref="userCampusDistributionChartRef" class="chart"></div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="订单趋势统计" name="orders">
            <div class="chart-container">
              <h3>订单量趋势</h3>
              <div ref="orderTrendChartRef" class="chart"></div>
            </div>
            <div class="chart-container">
              <h3>订单金额分布</h3>
              <div ref="orderAmountChartRef" class="chart"></div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="校区交易统计" name="campus">
            <div class="chart-container">
              <h3>各校区交易量</h3>
              <div ref="campusTradeChartRef" class="chart"></div>
            </div>
            <div class="chart-container">
              <h3>校区交易金额对比</h3>
              <div ref="campusAmountChartRef" class="chart"></div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div class="export-section">
        <h3>数据导出</h3>
        <div class="export-buttons">
          <el-button type="success" @click="exportExcel">导出Excel</el-button>
          <el-button type="warning" @click="exportPDF">导出PDF</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import * as echarts from 'echarts'
import AppHeader from '@/components/AppHeader.vue'
import { statisticsApi } from '@/api/statistics'

// 日期范围
const dateRange = ref<string[]>([])
// 当前激活的标签页
const activeTab = ref('sales')

// 图表引用
const salesChartRef = ref<HTMLElement>()
const topProductsChartRef = ref<HTMLElement>()
const userGrowthChartRef = ref<HTMLElement>()
const userChannelChartRef = ref<HTMLElement>()
const orderTrendChartRef = ref<HTMLElement>()
const orderAmountChartRef = ref<HTMLElement>()
const campusTradeChartRef = ref<HTMLElement>()
const campusAmountChartRef = ref<HTMLElement>()
const userGrowthDistributionChartRef = ref<HTMLElement>()
const userRoleDistributionChartRef = ref<HTMLElement>()
const userCampusDistributionChartRef = ref<HTMLElement>()

// 统计数据状态
const statisticsData = reactive({
  growthDistribution: {
    last7Days: [],
    last30Days: [],
    byMonth: [],
    byRole: [],
    byCampus: [],
  },
})

// 图表实例
let salesChart: echarts.ECharts | null = null
let topProductsChart: echarts.ECharts | null = null
let userGrowthChart: echarts.ECharts | null = null
let userChannelChart: echarts.ECharts | null = null
let orderTrendChart: echarts.ECharts | null = null
let orderAmountChart: echarts.ECharts | null = null
let campusTradeChart: echarts.ECharts | null = null
let campusAmountChart: echarts.ECharts | null = null
let userGrowthDistributionChart: echarts.ECharts | null = null
let userRoleDistributionChart: echarts.ECharts | null = null
let userCampusDistributionChart: echarts.ECharts | null = null

// 模拟数据
const mockSalesData = {
  dates: ['1月', '2月', '3月', '4月', '5月', '6月'],
  sales: [120, 200, 150, 80, 70, 110],
}

const mockTopProducts = {
  names: ['商品A', '商品B', '商品C', '商品D', '商品E'],
  sales: [120, 90, 80, 70, 60],
}

const mockUserGrowth = {
  dates: ['1月', '2月', '3月', '4月', '5月', '6月'],
  users: [100, 150, 200, 180, 250, 300],
}

const mockUserChannels = {
  channels: ['微信', 'QQ', '邮箱', '其他'],
  counts: [40, 30, 20, 10],
}

const mockOrderTrend = {
  dates: ['1月', '2月', '3月', '4月', '5月', '6月'],
  orders: [80, 120, 90, 150, 110, 130],
}

const mockOrderAmount = {
  ranges: ['0-50', '50-100', '100-200', '200-500', '500+'],
  counts: [30, 25, 20, 15, 10],
}

const mockCampusTrade = {
  campuses: ['校区1', '校区2', '校区3', '校区4'],
  trades: [150, 120, 90, 80],
}

const mockCampusAmount = {
  campuses: ['校区1', '校区2', '校区3', '校区4'],
  amounts: [5000, 4000, 3000, 2500],
}

// 初始化图表
const initCharts = () => {
  // 商品销量趋势图
  if (salesChartRef.value) {
    salesChart = echarts.init(salesChartRef.value)
    salesChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: mockSalesData.dates,
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed',
          },
        },
      },
      series: [
        {
          data: mockSalesData.sales,
          type: 'line',
          smooth: true,
          lineStyle: {
            width: 3,
            color: '#409eff',
          },
          itemStyle: {
            color: '#409eff',
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: 'rgba(64, 158, 255, 0.3)',
                },
                {
                  offset: 1,
                  color: 'rgba(64, 158, 255, 0.05)',
                },
              ],
            },
          },
        },
      ],
    })
  }

  // 销量TOP10商品
  if (topProductsChartRef.value) {
    topProductsChart = echarts.init(topProductsChartRef.value)
    topProductsChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: mockTopProducts.names,
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
          rotate: 30,
        },
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed',
          },
        },
      },
      series: [
        {
          data: mockTopProducts.sales,
          type: 'bar',
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: '#67c23a',
                },
                {
                  offset: 1,
                  color: '#85ce61',
                },
              ],
            },
          },
        },
      ],
    })
  }

  // 用户增长趋势
  if (userGrowthChartRef.value) {
    userGrowthChart = echarts.init(userGrowthChartRef.value)
    userGrowthChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: mockUserGrowth.dates,
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed',
          },
        },
      },
      series: [
        {
          data: mockUserGrowth.users,
          type: 'line',
          smooth: true,
          lineStyle: {
            width: 3,
            color: '#e6a23c',
          },
          itemStyle: {
            color: '#e6a23c',
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: 'rgba(230, 162, 60, 0.3)',
                },
                {
                  offset: 1,
                  color: 'rgba(230, 162, 60, 0.05)',
                },
              ],
            },
          },
        },
      ],
    })
  }

  // 用户注册渠道分布
  if (userChannelChartRef.value) {
    userChannelChart = echarts.init(userChannelChartRef.value)
    userChannelChart.setOption({
      title: { text: '' },
      tooltip: { trigger: 'item' },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center',
        textStyle: {
          fontSize: 12,
        },
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
          label: {
            show: false,
            position: 'center',
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold',
            },
          },
          labelLine: {
            show: false,
          },
          data: mockUserChannels.channels.map((channel, index) => ({
            name: channel,
            value: mockUserChannels.counts[index],
          })),
        },
      ],
    })
  }

  // 订单量趋势
  if (orderTrendChartRef.value) {
    orderTrendChart = echarts.init(orderTrendChartRef.value)
    orderTrendChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: mockOrderTrend.dates,
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed',
          },
        },
      },
      series: [
        {
          data: mockOrderTrend.orders,
          type: 'line',
          smooth: true,
          lineStyle: {
            width: 3,
            color: '#f56c6c',
          },
          itemStyle: {
            color: '#f56c6c',
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: 'rgba(245, 108, 108, 0.3)',
                },
                {
                  offset: 1,
                  color: 'rgba(245, 108, 108, 0.05)',
                },
              ],
            },
          },
        },
      ],
    })
  }

  // 订单金额分布
  if (orderAmountChartRef.value) {
    orderAmountChart = echarts.init(orderAmountChartRef.value)
    orderAmountChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center',
        textStyle: {
          fontSize: 12,
        },
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
          label: {
            show: false,
            position: 'center',
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold',
            },
          },
          labelLine: {
            show: false,
          },
          data: mockOrderAmount.ranges.map((range, index) => ({
            name: range,
            value: mockOrderAmount.counts[index],
          })),
        },
      ],
    })
  }

  // 各校区交易量
  if (campusTradeChartRef.value) {
    campusTradeChart = echarts.init(campusTradeChartRef.value)
    campusTradeChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: mockCampusTrade.campuses,
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed',
          },
        },
      },
      series: [
        {
          data: mockCampusTrade.trades,
          type: 'bar',
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: '#909399',
                },
                {
                  offset: 1,
                  color: '#c0c4cc',
                },
              ],
            },
          },
        },
      ],
    })
  }

  // 校区交易金额对比
  if (campusAmountChartRef.value) {
    campusAmountChart = echarts.init(campusAmountChartRef.value)
    campusAmountChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: mockCampusAmount.campuses,
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed',
          },
        },
      },
      series: [
        {
          data: mockCampusAmount.amounts,
          type: 'bar',
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: '#d48265',
                },
                {
                  offset: 1,
                  color: '#e6a23c',
                },
              ],
            },
          },
        },
      ],
    })
  }

  // 用户增长分布（最近7天）
  if (userGrowthDistributionChartRef.value) {
    userGrowthDistributionChart = echarts.init(userGrowthDistributionChartRef.value)
    userGrowthDistributionChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: [],
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed',
          },
        },
      },
      series: [
        {
          data: [],
          type: 'line',
          smooth: true,
          lineStyle: {
            width: 3,
            color: '#409eff',
          },
          itemStyle: {
            color: '#409eff',
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: 'rgba(64, 158, 255, 0.3)',
                },
                {
                  offset: 1,
                  color: 'rgba(64, 158, 255, 0.05)',
                },
              ],
            },
          },
        },
      ],
    })
  }

  // 用户角色分布
  if (userRoleDistributionChartRef.value) {
    userRoleDistributionChart = echarts.init(userRoleDistributionChartRef.value)
    userRoleDistributionChart.setOption({
      title: { text: '' },
      tooltip: { trigger: 'item' },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center',
        textStyle: {
          fontSize: 12,
        },
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
          label: {
            show: false,
            position: 'center',
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold',
            },
          },
          labelLine: {
            show: false,
          },
          data: [],
        },
      ],
    })
  }

  // 用户校园分布
  if (userCampusDistributionChartRef.value) {
    userCampusDistributionChart = echarts.init(userCampusDistributionChartRef.value)
    userCampusDistributionChart.setOption({
      title: { text: '' },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: '#ccc',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: [],
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
          rotate: 30,
        },
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#999',
          },
        },
        axisLabel: {
          fontSize: 12,
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed',
          },
        },
      },
      series: [
        {
          data: [],
          type: 'bar',
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                {
                  offset: 0,
                  color: '#67c23a',
                },
                {
                  offset: 1,
                  color: '#85ce61',
                },
              ],
            },
          },
        },
      ],
    })
  }
}

// 刷新图表
const refreshCharts = () => {
  salesChart?.resize()
  topProductsChart?.resize()
  userGrowthChart?.resize()
  userChannelChart?.resize()
  orderTrendChart?.resize()
  orderAmountChart?.resize()
  campusTradeChart?.resize()
  campusAmountChart?.resize()
  userGrowthDistributionChart?.resize()
  userRoleDistributionChart?.resize()
  userCampusDistributionChart?.resize()
}

// 处理日期范围变化
const handleDateRangeChange = () => {
  console.log('日期范围变化:', dateRange.value)
  // 这里可以根据日期范围重新获取数据并更新图表
}

// 获取用户增长分布数据
const fetchGrowthDistribution = async () => {
  try {
    const response = await statisticsApi.getGrowthDistribution()
    if (response) {
      statisticsData.growthDistribution = response
      // 更新图表数据
      updateGrowthCharts()
    }
  } catch (error) {
    console.error('获取用户增长分布数据失败:', error)
  }
}

// 更新增长相关图表
const updateGrowthCharts = () => {
  if (userGrowthDistributionChart) {
    // 处理最近7天数据
    const last7Days = statisticsData.growthDistribution.last7Days || []
    const dates = last7Days.map((item) => item[0])
    const counts = last7Days.map((item) => item[1])

    userGrowthDistributionChart.setOption({
      xAxis: { data: dates },
      series: [{ data: counts }],
    })
  }

  if (userRoleDistributionChart) {
    // 处理角色分布数据
    const roleData = statisticsData.growthDistribution.byRole || []
    const roleChartData = roleData.map((item) => ({
      name: item[0],
      value: item[1],
    }))

    userRoleDistributionChart.setOption({
      series: [{ data: roleChartData }],
    })
  }

  if (userCampusDistributionChart) {
    // 处理校园分布数据
    const campusData = statisticsData.growthDistribution.byCampus || []
    const campusNames = campusData.map((item) => item[0])
    const campusCounts = campusData.map((item) => item[1])

    userCampusDistributionChart.setOption({
      xAxis: { data: campusNames },
      series: [{ data: campusCounts }],
    })
  }
}

// 刷新数据
const refreshStatistics = () => {
  console.log('刷新数据')
  fetchGrowthDistribution()
}

// 导出Excel
const exportExcel = () => {
  console.log('导出Excel')
  // 这里实现Excel导出功能
}

// 导出PDF
const exportPDF = () => {
  console.log('导出PDF')
  // 这里实现PDF导出功能
}

// 监听窗口大小变化
const handleResize = () => {
  refreshCharts()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
  // 获取用户增长分布数据
  fetchGrowthDistribution()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  salesChart?.dispose()
  topProductsChart?.dispose()
  userGrowthChart?.dispose()
  userChannelChart?.dispose()
  orderTrendChart?.dispose()
  orderAmountChart?.dispose()
  campusTradeChart?.dispose()
  campusAmountChart?.dispose()
  userGrowthDistributionChart?.dispose()
  userRoleDistributionChart?.dispose()
  userCampusDistributionChart?.dispose()
})
</script>

<style scoped>
.statistics-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.statistics-content {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.statistics-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.statistics-header h1 {
  font-size: 24px;
  margin: 0;
}

.date-range-picker {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.statistics-tabs {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.chart-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 100%;
  box-sizing: border-box;
}

.chart-container h3 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #303133;
}

.chart {
  height: 300px;
  width: 100%;
  min-width: 0;
  max-width: 100%;
  box-sizing: border-box;
}

.export-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.export-section h3 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #303133;
}

.export-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .statistics-content {
    padding: 15px;
  }
}

@media (max-width: 768px) {
  .statistics-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .statistics-header h1 {
    font-size: 20px;
  }

  .date-range-picker {
    width: 100%;
    justify-content: space-between;
  }

  .date-range-picker .el-date-picker {
    flex: 1;
    min-width: 200px;
  }

  .chart-container {
    padding: 15px;
    margin-bottom: 15px;
  }

  .chart {
    height: 250px;
  }

  .chart-container h3 {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .statistics-content {
    padding: 10px;
  }

  .statistics-header h1 {
    font-size: 18px;
  }

  .date-range-picker {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .date-range-picker .el-date-picker {
    min-width: auto;
  }

  .chart-container {
    padding: 12px;
    margin-bottom: 12px;
  }

  .chart {
    height: 200px;
  }

  .export-buttons {
    flex-direction: column;
  }

  .export-buttons .el-button {
    width: 100%;
  }
}
</style>
