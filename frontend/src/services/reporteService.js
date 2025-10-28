import axios from 'axios'

// Instancia de axios sin interceptor de autenticación para endpoints públicos
const publicAxios = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
    'ngrok-skip-browser-warning': 'true'
  }
})

const reporteService = {
  async getProductosMasVendidos(fechaInicio, fechaFin) {
    const response = await publicAxios.get(`reportes/productos-mas-vendidos`, {
      params: { fechaInicio, fechaFin }
    })
    return response.data
  },

  async getClientesPorGanancias(fechaInicio, fechaFin) {
    const response = await publicAxios.get(`reportes/clientes-por-ganancias`, {
      params: { fechaInicio, fechaFin }
    })
    return response.data
  },

  async getClientesPorVentas(fechaInicio, fechaFin) {
    const response = await publicAxios.get(`reportes/clientes-por-ventas`, {
      params: { fechaInicio, fechaFin }
    })
    return response.data
  },

  async getClientesPorPedidos(fechaInicio, fechaFin) {
    const response = await publicAxios.get(`reportes/clientes-por-pedidos`, {
      params: { fechaInicio, fechaFin }
    })
    return response.data
  },

  async getClientesPorProductos() {
    const response = await publicAxios.get(`reportes/clientes-por-productos`)
    return response.data
  },

  async getHistorialSanciones(page = 0, size = 20) {
    const response = await publicAxios.get(`reportes/historial-sanciones`, {
      params: { page, size }
    })
    return response.data
  },

  async getHistorialNotificaciones(page = 0, size = 20) {
    const response = await publicAxios.get(`reportes/historial-notificaciones`, {
      params: { page, size }
    })
    return response.data
  }
}

export default reporteService   