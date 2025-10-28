import axios from 'axios'

const API_URL = 'http://localhost:8080/api'

// Instancia de axios sin interceptor de autenticación para endpoints públicos
const publicAxios = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'ngrok-skip-browser-warning': 'true'
  }
})

export default {
  // Listar usuarios
  async getUsuarios(filters = {}) {
    const params = {
      page: filters.page || 0,
      size: filters.size || 10,
      sortBy: filters.sortBy || 'fechaCreacion',
      direction: filters.direction || 'desc'
    }
    if (filters.rol) {
      params.rol = filters.rol
    }
    const response = await publicAxios.get('admin/usuarios', { params })
    return response.data
  },

  // Obtener usuario por ID
  async getUsuarioById(id) {
    const response = await publicAxios.get(`admin/usuarios/${id}`)
    return response.data
  },

  // Crear usuario
  async crearUsuario(userData) {
    const response = await publicAxios.post('admin/usuarios', userData)
    return response.data
  },

  // Actualizar usuario
  async actualizarUsuario(id, userData) {
    const response = await publicAxios.put(`admin/usuarios/${id}`, userData)
    return response.data
  },

  // Desactivar usuario
  async desactivarUsuario(id) {
    const response = await publicAxios.put(`admin/usuarios/${id}/desactivar`)
    return response.data
  },

  // Activar usuario
  async activarUsuario(id) {
    const response = await publicAxios.put(`admin/usuarios/${id}/activar`)
    return response.data
  },

  // ==================== ESTADÍSTICAS ====================

  // Obtener estadísticas generales
  async getEstadisticas() {
    const response = await publicAxios.get('admin/estadisticas')
    return response.data
  }
}