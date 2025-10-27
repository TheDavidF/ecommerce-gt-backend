import api from './api'

export default {
  /**
   * Obtener todas las categorías
   */
  async getCategories() {
    const response = await api.get('/categorias')
    return response.data
  },

  /**
   * Obtener categoría por ID
   */
  async getCategoryById(id) {
    const response = await api.get(`/categorias/${id}`)
    return response.data
  },

  /**
   * Crear categoría (solo admin/moderador)
   */
  async createCategory(categoryData) {
    const response = await api.post('/categorias', categoryData)
    return response.data
  },

  /**
   * Actualizar categoría (solo admin/moderador)
   */
  async updateCategory(id, categoryData) {
    const response = await api.put(`/categorias/${id}`, categoryData)
    return response.data
  },

  /**
   * Eliminar categoría (solo admin)
   */
  async deleteCategory(id) {
    const response = await api.delete(`/categorias/${id}`)
    return response.data
  }
}