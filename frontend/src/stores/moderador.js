import { defineStore } from 'pinia'
import moderadorService from '../services/moderadorService'
import { useToast } from 'vue-toastification'

const toast = useToast()

export const useModeradorStore = defineStore('moderador', {
  state: () => ({
    solicitudes: [],
    totalSolicitudes: 0,
    totalPages: 0,
    currentPage: 0,
    loading: false,
    estadoFiltro: 'PENDIENTE', // PENDIENTE, APROBADO, RECHAZADO, CAMBIOS_SOLICITADOS, TODOS
    // Solicitud actual (para modal)
    solicitudActual: null,
    productoActual: null,
    // Estadísticas
    estadisticas: {
      pendientes: 0,
      aprobadas: 0,
      rechazadas: 0,
      cambiosSolicitados: 0
    },
    // Reviews pendientes
    reviewsPendientes: [],
    totalReviewsPendientes: 0,
    loadingReviews: false
  }),

  getters: {
    hasSolicitudes: (state) => state.solicitudes.length > 0,
    
    solicitudesPendientes: (state) => 
      state.solicitudes.filter(s => s.estado === 'PENDIENTE'),
    
    solicitudesAprobadas: (state) => 
      state.solicitudes.filter(s => s.estado === 'APROBADO'),
    
    solicitudesRechazadas: (state) => 
      state.solicitudes.filter(s => s.estado === 'RECHAZADO')
  },

  actions: {
    // ==================== REVIEWS PENDIENTES ====================
    async fetchReviewsPendientes(page = 0, size = 10) {
      this.loadingReviews = true
      try {
        const response = await import('../services/reviewService').then(m => m.default.getReviewsPendientes({ page, size }))
        this.reviewsPendientes = response.content || []
        this.totalReviewsPendientes = response.totalElements || 0
      } catch (error) {
        console.error('Error al cargar reviews pendientes:', error)
        this.reviewsPendientes = []
      } finally {
        this.loadingReviews = false
      }
    },

    async aprobarReview(id) {
      try {
        await import('../services/reviewService').then(m => m.default.aprobarReview(id))
        await this.fetchReviewsPendientes()
        return true
      } catch (error) {
        console.error('Error al aprobar review:', error)
        return false
      }
    },

    async rechazarReview(id) {
      try {
        await import('../services/reviewService').then(m => m.default.rechazarReview(id))
        await this.fetchReviewsPendientes()
        return true
      } catch (error) {
        console.error('Error al rechazar review:', error)
        return false
      }
    },
    // ==================== CARGAR SOLICITUDES ====================
    
    async fetchSolicitudes(page = 0, size = 10) {
      this.loading = true
      try {
        let response
        if (this.estadoFiltro === 'TODOS') {
          response = await moderadorService.getTodasSolicitudes({ page, size })
        } else if (this.estadoFiltro === 'PENDIENTE') {
          response = await moderadorService.getSolicitudesPendientes({ page, size })
        } else {
          response = await moderadorService.getSolicitudesPorEstado(this.estadoFiltro, { page, size })
        }
        // Log para debug: mostrar estructura de la respuesta
        console.log('Respuesta en fetchSolicitudes:', response)
        // Asignar correctamente el array y los totales
        this.solicitudes = response.content || []
        this.totalSolicitudes = response.totalElements || 0
        this.totalPages = response.totalPages || 0
        this.currentPage = response.number || 0
      } catch (error) {
        console.error('Error al cargar solicitudes:', error)
        toast.error('Error al cargar solicitudes')
        this.solicitudes = []
      } finally {
        this.loading = false
      }
    },

    async fetchSolicitudById(id) {
      this.loading = true
      try {
        this.solicitudActual = await moderadorService.getSolicitudById(id)
        
        // Cargar producto completo
        if (this.solicitudActual.productoId) {
          this.productoActual = await moderadorService.getProductoById(
            this.solicitudActual.productoId
          )
        }
        
        return this.solicitudActual
      } catch (error) {
        console.error('Error al cargar solicitud:', error)
        toast.error('Error al cargar detalles de la solicitud')
        throw error
      } finally {
        this.loading = false
      }
    },

    // ==================== ACCIONES DE MODERACIÓN ====================
    
    async aprobarSolicitud(id, comentario = null) {
      this.loading = true
      try {
        await moderadorService.aprobarSolicitud(id, comentario)
        toast.success('Solicitud aprobada exitosamente')
        await this.fetchSolicitudes(this.currentPage)
        return true
      } catch (error) {
        console.error('Error al aprobar solicitud:', error)
        const mensaje = error.response?.data?.message || 'Error al aprobar solicitud'
        toast.error(mensaje)
        return false
      } finally {
        this.loading = false
      }
    },

    async rechazarSolicitud(id, motivo) {
      this.loading = true
      try {
        await moderadorService.rechazarSolicitud(id, motivo)
        toast.success('Solicitud rechazada')
        await this.fetchSolicitudes(this.currentPage)
        return true
      } catch (error) {
        console.error('Error al rechazar solicitud:', error)
        const mensaje = error.response?.data?.message || 'Error al rechazar solicitud'
        toast.error(mensaje)
        return false
      } finally {
        this.loading = false
      }
    },

    async solicitarCambios(id, comentario) {
      this.loading = true
      try {
        await moderadorService.solicitarCambios(id, comentario)
        toast.success('Cambios solicitados al vendedor')
        await this.fetchSolicitudes(this.currentPage)
        return true
      } catch (error) {
        console.error('Error al solicitar cambios:', error)
        const mensaje = error.response?.data?.message || 'Error al solicitar cambios'
        toast.error(mensaje)
        return false
      } finally {
        this.loading = false
      }
    },

    // ==================== FILTROS Y PAGINACIÓN ====================
    
    setEstadoFiltro(estado) {
      this.estadoFiltro = estado
      this.fetchSolicitudes(0)
    },

    changePage(page) {
      this.fetchSolicitudes(page)
    },

    // ==================== ESTADÍSTICAS ====================
    
    async fetchEstadisticas() {
      try {
        this.estadisticas = await moderadorService.getEstadisticasModeracion()
      } catch (error) {
        console.error('Error al cargar estadísticas:', error)
      }
    },

    // ==================== RESET ====================
    
    resetSolicitudActual() {
      this.solicitudActual = null
      this.productoActual = null
    }
  }
})