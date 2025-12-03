export interface Product {
  id: number
  title: string
  description: string
  price: number
  originalPrice?: number
  category: Category
  seller: User
  images: string[]
  status: 'AVAILABLE' | 'SOLD' | 'RESERVED' | 'REMOVED'
  location?: string
  viewCount: number
  createdAt: string
  updatedAt: string
}

export interface Category {
  id: number
  name: string
  description?: string
}