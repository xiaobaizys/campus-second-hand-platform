import { defineStore } from 'pinia'
import { ref } from 'vue'
import { productApi } from '@/api/product'
import type { Product, Category } from '@/types'

export const useProductStore = defineStore('product', () => {
  // 状态
  const products = ref<Product[]>([])
  const categories = ref<Category[]>([])
  const currentProduct = ref<Product | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  
  // 获取商品列表
  const fetchProducts = async (params?: {
    page?: number
    pageSize?: number
    category?: string
    search?: string
    sortBy?: string
    sortDir?: string
  }) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.getProducts(params)
      // 处理Spring Data JPA的Page对象，提取商品列表
      let productsData = []
      if (response && response.content) {
        productsData = response.content
      } else {
        productsData = response.data || response
      }
      
      // 处理商品图片字段名转换：将imageUrls转换为images，并确保是数组格式
      products.value = productsData.map((product: any) => {
        // 检查并处理imageUrls字段
        if (product.imageUrls) {
          // 检查imageUrls是否为字符串，如果是则尝试解析为JSON数组
          let imageUrls = product.imageUrls;
          if (typeof imageUrls === 'string') {
            try {
              // 尝试解析JSON字符串
              imageUrls = JSON.parse(imageUrls);
            } catch (e) {
              // 如果解析失败，使用空数组
              imageUrls = [];
            }
          }
          // 确保imageUrls是数组
          if (!Array.isArray(imageUrls)) {
            imageUrls = [];
          }
          product.images = imageUrls;
        }
        
        // 检查并处理images字段，确保其格式正确
        if (product.images) {
          // 确保images是数组
          if (!Array.isArray(product.images)) {
            product.images = [];
          } else {
            // 检查images数组中的每个元素是否为字符串，如果是JSON字符串则尝试解析
            product.images = product.images.map((image: any) => {
              if (typeof image === 'string') {
                try {
                  // 尝试解析JSON字符串
                  const parsedImage = JSON.parse(image);
                  // 如果解析结果是数组，返回第一个元素，否则返回原始字符串
                  return Array.isArray(parsedImage) ? parsedImage[0] : parsedImage;
                } catch (e) {
                  // 如果解析失败，返回原始字符串
                  return image;
                }
              }
              return image;
            });
          }
        } else {
          // 如果images字段不存在，设置为空数组
          product.images = [];
        }
        
        // 确保卖家信息兼容不同页面的数据结构需求
        // 首先检查sellerId是否存在，因为sellerId是必填字段
        if (product.sellerId) {
          // 如果有sellerId，确保同时有seller对象和直接的sellerName、sellerAvatar字段
          product.seller = {
            id: product.sellerId,
            username: product.sellerName || '未知卖家',
            avatar: product.sellerAvatar || ''
          };
          product.sellerName = product.sellerName || '未知卖家';
          product.sellerAvatar = product.sellerAvatar || '';
        } 
        // 如果有seller对象，确保同时有直接的sellerId、sellerName和sellerAvatar字段
        else if (product.seller) {
          product.sellerId = product.seller.id;
          product.sellerName = product.seller.username || '未知卖家';
          product.sellerAvatar = product.seller.avatar || '';
        } 
        // 如果两者都没有，设置默认值
        else {
          product.sellerId = 0;
          product.sellerName = '未知卖家';
          product.sellerAvatar = '';
          product.seller = {
            id: 0,
            username: '未知卖家',
            avatar: ''
          };
        }
        
        return product;
      })
      
      return response
    } catch (err: any) {
      error.value = err.message || '获取商品列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取商品详情
  const fetchProductById = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.getProductById(id)
      let productData = response.data || response
      
      // 处理商品图片字段名转换：将imageUrls转换为images，并确保是数组格式
      if (productData.imageUrls) {
        // 检查imageUrls是否为字符串，如果是则尝试解析为JSON数组
        let imageUrls = productData.imageUrls;
        if (typeof imageUrls === 'string') {
          try {
            // 尝试解析JSON字符串
            imageUrls = JSON.parse(imageUrls);
          } catch (e) {
            // 如果解析失败，使用空数组
            imageUrls = [];
          }
        }
        // 确保imageUrls是数组
        if (!Array.isArray(imageUrls)) {
          imageUrls = [];
        }
        productData.images = imageUrls;
      }
      
      // 检查并处理images字段，确保其格式正确
      if (productData.images) {
        // 确保images是数组
        if (!Array.isArray(productData.images)) {
          productData.images = [];
        } else {
          // 检查images数组中的每个元素是否为字符串，如果是JSON字符串则尝试解析
          productData.images = productData.images.map((image: any) => {
            if (typeof image === 'string') {
              try {
                // 尝试解析JSON字符串
                const parsedImage = JSON.parse(image);
                // 如果解析结果是数组，返回第一个元素，否则返回原始字符串
                return Array.isArray(parsedImage) ? parsedImage[0] : parsedImage;
              } catch (e) {
                // 如果解析失败，返回原始字符串
                return image;
              }
            }
            return image;
          });
        }
      } else {
        // 如果images字段不存在，设置为空数组
        productData.images = [];
      }
      
      // 确保卖家信息兼容不同页面的数据结构需求
      // 如果有sellerName和sellerAvatar字段，确保同时有seller对象
      if (productData.sellerName || productData.sellerAvatar) {
        productData.seller = {
          id: productData.sellerId || 0,
          username: productData.sellerName || '未知卖家',
          avatar: productData.sellerAvatar || ''
        };
      } 
      // 如果有seller对象，确保同时有直接的sellerName和sellerAvatar字段
      else if (productData.seller) {
        productData.sellerId = productData.seller.id;
        productData.sellerName = productData.seller.username || '未知卖家';
        productData.sellerAvatar = productData.seller.avatar || '';
      } 
      // 如果两者都没有，设置默认值
      else {
        productData.sellerId = 0;
        productData.sellerName = '未知卖家';
        productData.sellerAvatar = '';
        productData.seller = {
          id: 0,
          username: '未知卖家',
          avatar: ''
        };
      }
      
      currentProduct.value = productData
      return response
    } catch (err: any) {
      error.value = err.message || '获取商品详情失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 搜索商品
  const searchProducts = async (keyword: string, params?: any) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.searchProducts(keyword, params)
      // 处理Spring Data JPA的Page对象，提取商品列表
      let productsData = []
      if (response && response.content) {
        productsData = response.content
      } else {
        productsData = response.data || response
      }
      
      // 处理商品图片字段名转换：将imageUrls转换为images，并确保是数组格式
      products.value = productsData.map((product: any) => {
        if (product.imageUrls && !product.images) {
          // 检查imageUrls是否为字符串，如果是则尝试解析为JSON数组
          let imageUrls = product.imageUrls;
          if (typeof imageUrls === 'string') {
            try {
              // 尝试解析JSON字符串
              imageUrls = JSON.parse(imageUrls);
            } catch (e) {
              // 如果解析失败，使用空数组
              imageUrls = [];
            }
          }
          // 确保imageUrls是数组
          if (!Array.isArray(imageUrls)) {
            imageUrls = [];
          }
          product.images = imageUrls;
        }
        
        // 确保卖家信息兼容不同页面的数据结构需求
        // 首先检查sellerId是否存在，因为sellerId是必填字段
        if (product.sellerId) {
          // 如果有sellerId，确保同时有seller对象和直接的sellerName、sellerAvatar字段
          product.seller = {
            id: product.sellerId,
            username: product.sellerName || '未知卖家',
            avatar: product.sellerAvatar || ''
          };
          product.sellerName = product.sellerName || '未知卖家';
          product.sellerAvatar = product.sellerAvatar || '';
        } 
        // 如果有seller对象，确保同时有直接的sellerId、sellerName和sellerAvatar字段
        else if (product.seller) {
          product.sellerId = product.seller.id;
          product.sellerName = product.seller.username || '未知卖家';
          product.sellerAvatar = product.seller.avatar || '';
        } 
        // 如果两者都没有，设置默认值
        else {
          product.sellerId = 0;
          product.sellerName = '未知卖家';
          product.sellerAvatar = '';
          product.seller = {
            id: 0,
            username: '未知卖家',
            avatar: ''
          };
        }
        
        return product;
      })
      
      return response
    } catch (err: any) {
      error.value = err.message || '搜索商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 高级搜索商品
  const advancedSearchProducts = async (params?: {
    page?: number
    pageSize?: number
    keyword?: string
    categoryId?: number
    minPrice?: number
    maxPrice?: number
    location?: string
    isNegotiable?: boolean
    isNew?: boolean
    deliveryMethod?: string
    sortBy?: string
    sortDir?: string
  }) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.advancedSearchProducts(params)
      // 处理Spring Data JPA的Page对象，提取商品列表
      let productsData = []
      if (response && response.content) {
        productsData = response.content
      } else {
        productsData = response.data || response
      }
      
      // 处理商品图片字段名转换：将imageUrls转换为images
      // 处理商品图片字段名转换：将imageUrls转换为images，并确保是数组格式
      products.value = productsData.map((product: any) => {
        if (product.imageUrls && !product.images) {
          // 检查imageUrls是否为字符串，如果是则尝试解析为JSON数组
          let imageUrls = product.imageUrls;
          if (typeof imageUrls === 'string') {
            try {
              // 尝试解析JSON字符串
              imageUrls = JSON.parse(imageUrls);
            } catch (e) {
              // 如果解析失败，使用空数组
              imageUrls = [];
            }
          }
          // 确保imageUrls是数组
          if (!Array.isArray(imageUrls)) {
            imageUrls = [];
          }
          product.images = imageUrls;
        }
        
        // 确保卖家信息兼容不同页面的数据结构需求
        // 如果有sellerName和sellerAvatar字段，确保同时有seller对象
        if (product.sellerName || product.sellerAvatar) {
          product.seller = {
            id: product.sellerId || 0,
            username: product.sellerName || '未知卖家',
            avatar: product.sellerAvatar || ''
          };
        } 
        // 如果有seller对象，确保同时有直接的sellerName和sellerAvatar字段
        else if (product.seller) {
          product.sellerId = product.seller.id;
          product.sellerName = product.seller.username || '未知卖家';
          product.sellerAvatar = product.seller.avatar || '';
        } 
        // 如果两者都没有，设置默认值
        else {
          product.sellerId = 0;
          product.sellerName = '未知卖家';
          product.sellerAvatar = '';
          product.seller = {
            id: 0,
            username: '未知卖家',
            avatar: ''
          };
        }
        
        return product;
      })
      
      return response
    } catch (err: any) {
      error.value = err.message || '高级搜索商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 按分类获取商品
  const fetchProductsByCategory = async (categoryId: number, params?: any) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.getProductsByCategory(categoryId, params)
      // 处理Spring Data JPA的Page对象，提取商品列表
      let productsData = []
      if (response && response.content) {
        productsData = response.content
      } else {
        productsData = response.data || response
      }
      
      // 处理商品图片字段名转换：将imageUrls转换为images
      products.value = productsData.map((product: any) => {
        if (product.imageUrls && !product.images) {
          product.images = product.imageUrls
        }
        
        // 确保卖家信息兼容不同页面的数据结构需求
        // 如果有sellerName和sellerAvatar字段，确保同时有seller对象
        if (product.sellerName || product.sellerAvatar) {
          product.seller = {
            id: product.sellerId || 0,
            username: product.sellerName || '未知卖家',
            avatar: product.sellerAvatar || ''
          };
        } 
        // 如果有seller对象，确保同时有直接的sellerName和sellerAvatar字段
        else if (product.seller) {
          product.sellerId = product.seller.id;
          product.sellerName = product.seller.username || '未知卖家';
          product.sellerAvatar = product.seller.avatar || '';
        } 
        // 如果两者都没有，设置默认值
        else {
          product.sellerId = 0;
          product.sellerName = '未知卖家';
          product.sellerAvatar = '';
          product.seller = {
            id: 0,
            username: '未知卖家',
            avatar: ''
          };
        }
        
        return product
      })
      
      return response
    } catch (err: any) {
      error.value = err.message || '获取分类商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取用户发布的商品
  const fetchUserProducts = async (userId?: number, params?: any) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.getUserProducts(userId, params)
      // 处理Spring Data JPA的Page对象，提取商品列表
      let productsData = response
      if (response && response.content) {
        productsData = response.content
      } else if (response && response.data) {
        productsData = response.data
      }
      
      // 处理商品图片字段名转换：将imageUrls转换为images，并确保是数组格式
      productsData = productsData.map((product: any) => {
        if (product.imageUrls && !product.images) {
          // 检查imageUrls是否为字符串，如果是则尝试解析为JSON数组
          let imageUrls = product.imageUrls;
          if (typeof imageUrls === 'string') {
            try {
              // 尝试解析JSON字符串
              imageUrls = JSON.parse(imageUrls);
            } catch (e) {
              // 如果解析失败，使用空数组
              imageUrls = [];
            }
          }
          // 确保imageUrls是数组
          if (!Array.isArray(imageUrls)) {
            imageUrls = [];
          }
          product.images = imageUrls;
        }
        
        // 确保卖家信息兼容不同页面的数据结构需求
        // 如果有sellerName和sellerAvatar字段，确保同时有seller对象
        if (product.sellerName || product.sellerAvatar) {
          product.seller = {
            id: product.sellerId || 0,
            username: product.sellerName || '未知卖家',
            avatar: product.sellerAvatar || ''
          };
        } 
        // 如果有seller对象，确保同时有直接的sellerName和sellerAvatar字段
        else if (product.seller) {
          product.sellerId = product.seller.id;
          product.sellerName = product.seller.username || '未知卖家';
          product.sellerAvatar = product.seller.avatar || '';
        } 
        // 如果两者都没有，设置默认值
        else {
          product.sellerId = 0;
          product.sellerName = '未知卖家';
          product.sellerAvatar = '';
          product.seller = {
            id: 0,
            username: '未知卖家',
            avatar: ''
          };
        }
        
        return product;
      })
      
      products.value = productsData
      return productsData
    } catch (err: any) {
      error.value = err.message || '获取用户商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 创建商品
  const createProduct = async (productData: any) => {
    loading.value = true
    error.value = null
    
    try {
      // 处理商品图片字段名转换：将images转换为imageUrls
      const formattedProductData = {
        ...productData,
        imageUrls: productData.images
      }
      delete formattedProductData.images
      
      const response = await productApi.createProduct(formattedProductData)
      // 如果成功，将新商品添加到列表中
      if (response.data || response) {
        let newProduct = response.data || response
        
        // 处理商品图片字段名转换：将imageUrls转换为images，并确保是数组格式
        if (newProduct.imageUrls && !newProduct.images) {
          // 检查imageUrls是否为字符串，如果是则尝试解析为JSON数组
          let imageUrls = newProduct.imageUrls;
          if (typeof imageUrls === 'string') {
            try {
              // 尝试解析JSON字符串
              imageUrls = JSON.parse(imageUrls);
            } catch (e) {
              // 如果解析失败，使用空数组
              imageUrls = [];
            }
          }
          // 确保imageUrls是数组
          if (!Array.isArray(imageUrls)) {
            imageUrls = [];
          }
          newProduct.images = imageUrls;
        }
        
        // 确保卖家信息兼容不同页面的数据结构需求
        // 如果有sellerName和sellerAvatar字段，确保同时有seller对象
        if (newProduct.sellerName || newProduct.sellerAvatar) {
          newProduct.seller = {
            id: newProduct.sellerId || 0,
            username: newProduct.sellerName || '未知卖家',
            avatar: newProduct.sellerAvatar || ''
          };
        } 
        // 如果有seller对象，确保同时有直接的sellerName和sellerAvatar字段
        else if (newProduct.seller) {
          newProduct.sellerId = newProduct.seller.id;
          newProduct.sellerName = newProduct.seller.username || '未知卖家';
          newProduct.sellerAvatar = newProduct.seller.avatar || '';
        } 
        // 如果两者都没有，设置默认值
        else {
          newProduct.sellerId = 0;
          newProduct.sellerName = '未知卖家';
          newProduct.sellerAvatar = '';
          newProduct.seller = {
            id: 0,
            username: '未知卖家',
            avatar: ''
          };
        }
        
        products.value.unshift(newProduct)
      }
      return response
    } catch (err: any) {
      error.value = err.message || '创建商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 更新商品
  const updateProduct = async (id: number, productData: any) => {
    loading.value = true
    error.value = null
    
    try {
      // 处理商品图片字段名转换：将images转换为imageUrls
      const formattedProductData = {
        ...productData,
        imageUrls: productData.images
      }
      delete formattedProductData.images
      
      const response = await productApi.updateProduct(id, formattedProductData)
      // 如果成功，更新列表中的商品
      if (response.data || response) {
        let updatedProduct = response.data || response
        
        // 处理商品图片字段名转换：将imageUrls转换为images，并确保是数组格式
        if (updatedProduct.imageUrls && !updatedProduct.images) {
          // 检查imageUrls是否为字符串，如果是则尝试解析为JSON数组
          let imageUrls = updatedProduct.imageUrls;
          if (typeof imageUrls === 'string') {
            try {
              // 尝试解析JSON字符串
              imageUrls = JSON.parse(imageUrls);
            } catch (e) {
              // 如果解析失败，使用空数组
              imageUrls = [];
            }
          }
          // 确保imageUrls是数组
          if (!Array.isArray(imageUrls)) {
            imageUrls = [];
          }
          updatedProduct.images = imageUrls;
        }
        
        // 确保卖家信息兼容不同页面的数据结构需求
        // 如果有sellerName和sellerAvatar字段，确保同时有seller对象
        if (updatedProduct.sellerName || updatedProduct.sellerAvatar) {
          updatedProduct.seller = {
            id: updatedProduct.sellerId || 0,
            username: updatedProduct.sellerName || '未知卖家',
            avatar: updatedProduct.sellerAvatar || ''
          };
        } 
        // 如果有seller对象，确保同时有直接的sellerName和sellerAvatar字段
        else if (updatedProduct.seller) {
          updatedProduct.sellerId = updatedProduct.seller.id;
          updatedProduct.sellerName = updatedProduct.seller.username || '未知卖家';
          updatedProduct.sellerAvatar = updatedProduct.seller.avatar || '';
        } 
        // 如果两者都没有，设置默认值
        else {
          updatedProduct.sellerId = 0;
          updatedProduct.sellerName = '未知卖家';
          updatedProduct.sellerAvatar = '';
          updatedProduct.seller = {
            id: 0,
            username: '未知卖家',
            avatar: ''
          };
        }
        
        const index = products.value.findIndex(p => p.id === id)
        if (index !== -1) {
          products.value[index] = updatedProduct
        }
        // 如果是当前商品，也更新它
        if (currentProduct.value && currentProduct.value.id === id) {
          currentProduct.value = updatedProduct
        }
      }
      return response
    } catch (err: any) {
      error.value = err.message || '更新商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 删除商品
  const deleteProduct = async (id: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.deleteProduct(id)
      // 如果成功，从列表中移除商品
      products.value = products.value.filter(p => p.id !== id)
      // 如果是当前商品，清空它
      if (currentProduct.value && currentProduct.value.id === id) {
        currentProduct.value = null
      }
      return response
    } catch (err: any) {
      error.value = err.message || '删除商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 上传商品图片
  const uploadProductImage = async (formData: FormData) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.uploadImage(formData)
      return response
    } catch (err: any) {
      error.value = err.message || '上传图片失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 清空当前商品
  const clearCurrentProduct = () => {
    currentProduct.value = null
  }
  
  // 获取用户收藏的商品
  const fetchFavoriteProducts = async () => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.getFavoriteProducts()
      return response.data || response
    } catch (err: any) {
      error.value = err.message || '获取收藏商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 添加收藏
  const addToFavorites = async (productId: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.addToFavorites(productId)
      return response
    } catch (err: any) {
      error.value = err.message || '添加收藏失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 取消收藏
  const removeFromFavorites = async (productId: number) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.removeFromFavorites(productId)
      return response
    } catch (err: any) {
      error.value = err.message || '取消收藏失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取商品数量（管理员）
  const getProductCount = async () => {
    loading.value = true
    error.value = null
    
    try {
      const response = await productApi.getProductCount()
      // 处理不同格式的响应
      let count = 0
      if (typeof response === 'number') {
        count = response
      } else if (response && typeof response === 'object') {
        count = response.count || response.data?.count || 0
      }
      return count
    } catch (err: any) {
      error.value = err.message || '获取商品数量失败'
      // 不再抛出错误，而是返回0，避免影响其他功能
      return 0
    } finally {
      loading.value = false
    }
  }
  
  return {
    // 状态
    products,
    categories,
    currentProduct,
    loading,
    error,
    
    // 方法
    fetchProducts,
    fetchProductById,
    searchProducts,
    advancedSearchProducts,
    fetchProductsByCategory,
    fetchUserProducts,
    createProduct,
    updateProduct,
    deleteProduct,
    uploadProductImage,
    clearCurrentProduct,
    fetchFavoriteProducts,
    addToFavorites,
    removeFromFavorites,
    getProductCount
  }
})