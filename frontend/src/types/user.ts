export interface User {
  id: number
  username: string
  email: string
  phone?: string
  realName?: string
  avatar?: string
  avatarUrl?: string
  bio?: string
  role: 'USER' | 'ADMIN'
  createdAt?: string
}