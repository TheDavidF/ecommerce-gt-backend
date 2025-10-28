import { defineStore } from 'pinia'
import { ref } from 'vue'
import reporteService from '@/services/reporteService'

export const useReportesStore = defineStore('reportes', () => {
  const productosMasVendidos = ref([])
  const clientesPorGanancias = ref([])
  const clientesPorVentas = ref([])
  const clientesPorPedidos = ref([])
  const clientesPorProductos = ref([])
  const historialSanciones = ref([])
  const historialNotificaciones = ref([])
  
  const loading = ref(false)
  const error = ref(null)

  async function fetchProductosMasVendidos(fechaInicio, fechaFin) {
    loading.value = true
    error.value = null
    try {
      productosMasVendidos.value = await reporteService.getProductosMasVendidos(
        fechaInicio,
        fechaFin
      )
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al cargar reporte'
      console.error('Error fetchProductosMasVendidos:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchClientesPorGanancias(fechaInicio, fechaFin) {
    loading.value = true
    error.value = null
    try {
      clientesPorGanancias.value = await reporteService.getClientesPorGanancias(
        fechaInicio,
        fechaFin
      )
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al cargar reporte'
      console.error('Error fetchClientesPorGanancias:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchClientesPorVentas(fechaInicio, fechaFin) {
    loading.value = true
    error.value = null
    try {
      clientesPorVentas.value = await reporteService.getClientesPorVentas(
        fechaInicio,
        fechaFin
      )
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al cargar reporte'
      console.error('Error fetchClientesPorVentas:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchClientesPorPedidos(fechaInicio, fechaFin) {
    loading.value = true
    error.value = null
    try {
      clientesPorPedidos.value = await reporteService.getClientesPorPedidos(
        fechaInicio,
        fechaFin
      )
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al cargar reporte'
      console.error('Error fetchClientesPorPedidos:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchClientesPorProductos() {
    loading.value = true
    error.value = null
    try {
      clientesPorProductos.value = await reporteService.getClientesPorProductos()
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al cargar reporte'
      console.error('Error fetchClientesPorProductos:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchHistorialSanciones(page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      historialSanciones.value = await reporteService.getHistorialSanciones(page, size)
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al cargar historial de sanciones'
      console.error('Error fetchHistorialSanciones:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchHistorialNotificaciones(page = 0, size = 20) {
    loading.value = true
    error.value = null
    try {
      historialNotificaciones.value = await reporteService.getHistorialNotificaciones(page, size)
    } catch (err) {
      error.value = err.response?.data?.message || 'Error al cargar historial de notificaciones'
      console.error('Error fetchHistorialNotificaciones:', err)
    } finally {
      loading.value = false
    }
  }

  return {
    productosMasVendidos,
    clientesPorGanancias,
    clientesPorVentas,
    clientesPorPedidos,
    clientesPorProductos,
    loading,
    error,
    fetchProductosMasVendidos,
    fetchClientesPorGanancias,
    fetchClientesPorVentas,
    fetchClientesPorPedidos,
    fetchClientesPorProductos
  }
})