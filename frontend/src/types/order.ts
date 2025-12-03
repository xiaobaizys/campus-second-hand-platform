export interface Order {
  id: number
  orderNumber: string
  product: Product
  buyer: User
  seller: User
  price: number
  finalPrice: number
  status: OrderStatus
  contactInfo: string
  deliveryAddress: string
  deliveryMethod: string
  paymentMethod: string
  remarks?: string
  createdAt: string
  updatedAt: string
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED'
}

export interface OrderCreateDTO {
  productId: number
  price: number
  contactInfo: string
  deliveryAddress: string
  deliveryMethod: string
  paymentMethod: string
  remarks?: string
}

export interface OrderUpdateDTO {
  contactInfo?: string
  deliveryAddress?: string
  deliveryMethod?: string
  paymentMethod?: string
  remarks?: string
}

export interface OrderStatistics {
  totalOrders: number
  pendingOrders: number
  confirmedOrders: number
  completedOrders: number
  cancelledOrders: number
  totalAmount: number
  monthlyOrders: number
  monthlyAmount: number
}

// 导入其他相关类型
import { Product } from './product'
import { User } from './user'