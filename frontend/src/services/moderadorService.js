import api from './api'

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
    const response = await api.get('/api/moderador/solicitudes/pendientes', { params: queryParams })
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
    
  const response = await api.get(`/api/moderador/solicitudes/estado/${estado}`, { params: queryParams })
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
    
  const response = await api.get('/api/moderador/solicitudes', { params: queryParams })
    return response.data
  },

  /**
   * Obtener detalles de una solicitud
   */
  async getSolicitudById(id) {
  const response = await api.get(`/api/moderador/solicitudes/${id}`)
    return response.data
  },

  /**
   * Aprobar solicitud
   */
  async aprobarSolicitud(id, comentario = null) {
    const payload = comentario ? { comentario } : {}
  const response = await api.put(`/api/moderador/solicitudes/${id}/aprobar`, payload)
    return response.data
  },

  /**
   * Rechazar solicitud
   */
  async rechazarSolicitud(id, motivo) {
    const payload = { motivo }
  const response = await api.put(`/api/moderador/solicitudes/${id}/rechazar`, payload)
    return response.data
  },

  /**
   * Solicitar cambios
   */
  async solicitarCambios(id, comentario) {
    const payload = { comentario }
  const response = await api.put(`/api/moderador/solicitudes/${id}/solicitar-cambios`, payload)
    return response.data
  },

  // ==================== PRODUCTOS ====================
  
  /**
   * Obtener producto por ID (con detalles completos)
   */
  async getProductoById(id) {
  const response = await api.get(`/api/productos/${id}`)
    return response.data
  },

  /**
   * Obtener estadísticas de moderación
   */
  async getEstadisticasModeracion() {
  const response = await api.get('/api/moderador/estadisticas')
    return response.data
  }
}