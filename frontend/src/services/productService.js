import api from './api'

export default {
  // Listar productos con paginación y ordenamiento
  async getProducts(params = {}) {
    const response = await api.get('/api/productos', { params })
    return response.data
  },

  // Obtener producto por ID
  async getProductById(id) {
    const response = await api.get(`/api/productos/${id}`)
    return response.data
  },

  // Buscar productos
  async searchProducts(query, params = {}) {
    const response = await api.get('/api/productos/buscar', {
      params: { q: query, ...params }
    })
    return response.data
  },

  // Filtrar por precio
  async filterByPrice(min, max, params = {}) {
    const response = await api.get('/api/productos/filtrar/precio', {
      params: { min, max, ...params }
    })
    return response.data
  },

  // Crear producto
  async createProduct(productData) {
    const response = await api.post('/api/productos', productData)
    return response.data
  },

  // Actualizar producto
  async updateProduct(id, productData) {
    const response = await api.put(`/api/productos/${id}`, productData)
    return response.data
  },

  // Eliminar producto
  async deleteProduct(id) {
    const response = await api.delete(`/api/productos/${id}`)
    return response.data
  },

  // Obtener productos por categoría
  async getProductsByCategory(categoryId, params = {}) {
    const response = await api.get(`/api/productos/categoria/${categoryId}`, { params })
    return response.data
  },

  // Obtener mis productos
  async getMyProducts(params = {}) {
    const response = await api.get('/api/productos/mis-productos', { params })
    return response.data
  },

  // Obtener productos disponibles (aprobados y con stock)
  async getAvailableProducts(params = {}) {
    const response = await api.get('/api/productos/disponibles', { params })
    return response.data
  },

  // Obtener productos destacados
  async getFeaturedProducts() {
    const response = await api.get('/api/productos/destacados')
    return response.data
  },

  // Subir imagen de producto
  async uploadImage(productId, imageFile) {
    const formData = new FormData()
    formData.append('image', imageFile)
    const response = await api.post(`/api/productos/${productId}/imagen`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return response.data
  },

  // ==================== MODERACIÓN ====================

  // Obtener productos por estado (para moderadores)
  async getProductsByStatus(status, params = {}) {
    const response = await api.get(`/api/productos/estado/${status}`, { params })
    return response.data
  },

  // Aprobar producto (moderadores)
  async approveProduct(id) {
    const response = await api.post(`/api/productos/${id}/aprobar`)
    return response.data
  },

  // Rechazar producto (moderadores)
  async rejectProduct(id) {
    const response = await api.post(`/api/productos/${id}/rechazar`)
    return response.data
  }
}