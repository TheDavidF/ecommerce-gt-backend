import { defineStore } from 'pinia'
import axios from 'axios'
import { useToast } from 'vue-toastification'

const toast = useToast()
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

export const useEstadisticasStore = defineStore('estadisticas', {
  state: () => ({
    estadisticas: {
      totalUsuarios: 0,
      totalProductos: 0,
      totalPedidos: 0,
      totalVentas: 0,
      usuariosActivos: 0,
      productosPendientes: 0,
      pedidosPendientes: 0,
      ventasMes: 0,
      ventasDelMes: [],
      productosPopulares: [],
      usuariosPorRol: []
    },
    loading: false,
    error: null
  }),

  getters: {
    hasData: (state) => state.estadisticas.totalUsuarios > 0
  },

  actions: {
    async fetchEstadisticas() {
      this.loading = true
      this.error = null
      
      try {
        const token = JSON.parse(localStorage.getItem('user')).token
        
        const response = await axios.get(`${API_URL}/admin/estadisticas`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        
        this.estadisticas = response.data
        
      } catch (error) {
        console.error('Error al cargar estadísticas:', error)
        this.error = error.response?.data?.message || 'Error al cargar estadísticas'
        toast.error('Error al cargar estadísticas')
      } finally {
        this.loading = false
      }
    }
  }
})