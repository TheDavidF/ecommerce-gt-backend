import api from './api'

export default {
  async getReviewsByProduct(productId) {
  const res = await api.get(`/reviews/producto/${productId}`)
    return res.data.content || []
  },
  async createReview({ productoId, comentario, calificacion, titulo }) {
  const res = await api.post('/reviews', {
      productoId,
      comentario,
      calificacion,
      titulo
    })
    return res.data
  },
  async getReviewsPendientes({ page = 0, size = 10 } = {}) {
  const res = await api.get(`/reviews/pendientes?page=${page}&size=${size}`)
    return res.data
  },
  async aprobarReview(id) {
  const res = await api.put(`/reviews/${id}/aprobar`)
    return res.data
  },
  async rechazarReview(id) {
  const res = await api.put(`/reviews/${id}/rechazar`)
    return res.data
  }
}
