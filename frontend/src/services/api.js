import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

console.log('API_URL configurada:', API_URL)

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - agregar token
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const token = authStore.token
    
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    console.log('Request:', config.method.toUpperCase(), config.url, config.data)
    
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    console.log('Response:', response.config.method.toUpperCase(), response.config.url, response.status)
    return response
  },
  (error) => {
    console.error('API Error:', error.response?.status, error.config?.url)
    console.error('Error details:', error.response?.data)
    console.error('Error message:', error.message)
    
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      window.location.href = '/login'
    }
    
    return Promise.reject(error)
  }
)

export default api