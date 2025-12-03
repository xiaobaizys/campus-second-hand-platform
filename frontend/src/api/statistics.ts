import request from '@/utils/request'

/**
 * 统计数据相关API
 */
export const statisticsApi = {
  /**
   * 获取用户增长分布统计
   */
  getGrowthDistribution: () => {
    return request.get('/api/statistics/growth-distribution')
  },
}
