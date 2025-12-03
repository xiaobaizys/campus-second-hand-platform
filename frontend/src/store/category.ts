import { defineStore } from 'pinia'
import { ref } from 'vue'
import { categoryApi } from '@/api/category'
import type { Category } from '@/types'

export const useCategoryStore = defineStore('category', () => {
  // 状态
  const categories = ref<Category[]>([])
  const currentCategory = ref<Category | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  
  // 获取所有分类
  const fetchCategories = async () => {
    loading.value = true
    error.value = null
    
    try {
      const response = await categoryApi.getAllCategories()
      categories.value = response.data || response
      return response
    } catch (err: any) {
      error.value = err.message || '获取分类列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取分类详情
  const fetchCategoryById = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await categoryApi.getCategoryById(id)
      currentCategory.value = response.data || response
      return response
    } catch (err: any) {
      error.value = err.message || '获取分类详情失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 创建分类（管理员）
  const createCategory = async (categoryData: any) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await categoryApi.createCategory(categoryData)
      // 如果成功，将新分类添加到列表中
      if (response.data || response) {
        const newCategory = response.data || response
        categories.value.push(newCategory)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '创建分类失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 更新分类（管理员）
  const updateCategory = async (id: number, categoryData: any) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await categoryApi.updateCategory(id, categoryData)
      // 如果成功，更新列表中的分类
      if (response.data || response) {
        const updatedCategory = response.data || response
        const index = categories.value.findIndex(c => c.id === id)
        if (index !== -1) {
          categories.value[index] = updatedCategory
        }
        // 如果是当前分类，也更新它
        if (currentCategory.value && currentCategory.value.id === id) {
          currentCategory.value = updatedCategory
        }
      }
      return response
    } catch (err: any) {
      error.value = err.message || '更新分类失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 删除分类（管理员）
  const deleteCategory = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await categoryApi.deleteCategory(id)
      // 如果成功，从列表中移除分类
      categories.value = categories.value.filter(c => c.id !== id)
      // 如果是当前分类，清空它
      if (currentCategory.value && currentCategory.value.id === id) {
        currentCategory.value = null
      }
      return response
    } catch (err: any) {
      error.value = err.message || '删除分类失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 清空当前分类
  const clearCurrentCategory = () => {
    currentCategory.value = null
  }
  
  return {
    // 状态
    categories,
    currentCategory,
    loading,
    error,
    
    // 方法
    fetchCategories,
    fetchCategoryById,
    createCategory,
    updateCategory,
    deleteCategory,
    clearCurrentCategory
  }
})