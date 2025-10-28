import api from './api'

export default {
  async crearSancion({ usuarioId, moderadorId, razon, fechaFin }) {
    const payload = { usuarioId, moderadorId, razon }
    if (fechaFin) payload.fechaFin = fechaFin
    const res = await api.post('/api/moderador/sanciones', payload)
    return res.data
  },
  async listarSancionesPorUsuario(usuarioId, page = 0, size = 10) {
    const res = await api.get(`/api/moderador/sanciones/usuario/${usuarioId}?page=${page}&size=${size}`)
    return res.data
  },
  async listarSancionesPorModerador(moderadorId, page = 0, size = 10) {
    const res = await api.get(`/api/moderador/sanciones/moderador/${moderadorId}?page=${page}&size=${size}`)
    return res.data
  },
  async desactivarSancion(sancionId) {
    const res = await api.put(`/api/moderador/sanciones/${sancionId}/desactivar`)
    return res.data
  }
}
