import axios from 'axios'

// Instancia de axios sin interceptor de autenticación para endpoints públicos
const publicAxios = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
    'ngrok-skip-browser-warning': 'true'
  }
})

export default {
  // ==================== SOLICITUDES DE MODERACIÓN ====================
  
  /**
   * Obtener solicitudes pendientes
   */
  async getSolicitudesPendientes(params = {}) {
    const queryParams = {
      page: params.page || 0,
      size: params.size || 10
    }
    const response = await publicAxios.get('moderador/solicitudes/pendientes', { params: queryParams })
    console.log('Respuesta cruda solicitudes pendientes:', response)
    return response.data
  },

  /**
   * Obtener solicitudes por estado
   */
  async getSolicitudesPorEstado(estado, params = {}) {
    const queryParams = {
      page: params.page || 0,
      size: params.size || 10
    }
    
  const response = await publicAxios.get(`moderador/solicitudes/estado/${estado}`, { params: queryParams })
    return response.data
  },

  /**
   * Obtener todas las solicitudes
   */
  async getTodasSolicitudes(params = {}) {
    const queryParams = {
      page: params.page || 0,
      size: params.size || 10
    }
    
  const response = await publicAxios.get('moderador/solicitudes', { params: queryParams })
    return response.data
  },

  /**
   * Obtener detalles de una solicitud
   */
  async getSolicitudById(id) {
    const response = await publicAxios.get(`moderador/solicitudes/${id}`)
    return response.data
  },

  /**
   * Aprobar solicitud
   */
  async aprobarSolicitud(id, comentario = null) {
    const payload = comentario ? { comentario } : {}
    const response = await publicAxios.put(`moderador/solicitudes/${id}/aprobar`, payload)
    return response.data
  },

  /**
   * Rechazar solicitud
   */
  async rechazarSolicitud(id, motivo) {
    const payload = { motivo }
    const response = await publicAxios.put(`moderador/solicitudes/${id}/rechazar`, payload)
    return response.data
  },

  /**
   * Solicitar cambios
   */
  async solicitarCambios(id, comentario) {
    const payload = { comentario }
    const response = await publicAxios.put(`moderador/solicitudes/${id}/solicitar-cambios`, payload)
    return response.data
  },

  // ==================== PRODUCTOS ====================
  
  /**
   * Obtener producto por ID (con detalles completos)
   */
  async getProductoById(id) {
    const response = await publicAxios.get(`productos/${id}`)
    return response.data
  },

  /**
   * Obtener estadísticas de moderación
   */
  async getEstadisticasModeracion() {
    const response = await publicAxios.get('moderador/estadisticas')
    return response.data
  },

  /**
   * Obtener usuarios para sancionar
   */
  async getUsuariosParaSancion() {
    const response = await publicAxios.get('moderador/usuarios')
    return response.data
  }
}