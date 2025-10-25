import api from './api'

export default {
  // Listar productos con paginación y ordenamiento
  async getProducts(params = {}) {
    const response = await api.get('/productos', { params })
    return response.data
  },

  // Obtener producto por ID
  async getProductById(id) {
    const response = await api.get(`/productos/${id}`)
    return response.data
  },

  // Buscar productos - USA 'q' como parámetro
  async searchProducts(query, params = {}) {
    const response = await api.get('/productos/buscar', {
      params: { 
        q: query,  // ← CAMBIADO: usa 'q' en lugar de 'query'
        ...params 
      }
    })
    return response.data
  },

  // Filtrar por precio
  async filterByPrice(min, max, params = {}) {
    const response = await api.get('/productos/filtrar/precio', {
      params: {
        min,
        max,
        ...params
      }
    })
    return response.data
  },

  // Crear producto (vendedor)
  async createProduct(productData) {
    const response = await api.post('/productos', productData)
    return response.data
  },

  // Actualizar producto (vendedor)
  async updateProduct(id, productData) {
    const response = await api.put(`/productos/${id}`, productData)
    return response.data
  },

  // Eliminar producto (vendedor)
  async deleteProduct(id) {
    const response = await api.delete(`/productos/${id}`)
    return response.data
  },

  // Obtener productos por categoría
  async getProductsByCategory(categoryId, params = {}) {
    const response = await api.get(`/productos/categoria/${categoryId}`, { params })
    return response.data
  },

  // Obtener mis productos (vendedor)
  async getMyProducts(params = {}) {
    const response = await api.get('/productos/mis-productos', { params })
    return response.data
  },

  // Obtener productos disponibles (tienda)
  async getAvailableProducts(params = {}) {
    const response = await api.get('/productos/disponibles', { params })
    return response.data
  },

  // Obtener productos destacados
  async getFeaturedProducts() {
    const response = await api.get('/productos/destacados')
    return response.data
  },

  // Subir imagen de producto
  async uploadImage(productId, imageFile) {
    const formData = new FormData()
    formData.append('image', imageFile)
    const response = await api.post(`/productos/${productId}/imagen`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    return response.data
  }
}