import axios from 'axios'

const API_URL = 'http://localhost:8080/api/reportes'

const getAuthHeader = () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (user && user.token) {
    return { Authorization: `Bearer ${user.token}` }
  }
  return {}
}

const reporteService = {
  async getProductosMasVendidos(fechaInicio, fechaFin) {
    const response = await axios.get(`${API_URL}/productos-mas-vendidos`, {
      params: { fechaInicio, fechaFin },
      headers: getAuthHeader()
    })
    return response.data
  },

  async getClientesPorGanancias(fechaInicio, fechaFin) {
    const response = await axios.get(`${API_URL}/clientes-por-ganancias`, {
      params: { fechaInicio, fechaFin },
      headers: getAuthHeader()
    })
    return response.data
  },

  async getClientesPorVentas(fechaInicio, fechaFin) {
    const response = await axios.get(`${API_URL}/clientes-por-ventas`, {
      params: { fechaInicio, fechaFin },
      headers: getAuthHeader()
    })
    return response.data
  },

  async getClientesPorPedidos(fechaInicio, fechaFin) {
    const response = await axios.get(`${API_URL}/clientes-por-pedidos`, {
      params: { fechaInicio, fechaFin },
      headers: getAuthHeader()
    })
    return response.data
  },

  async getClientesPorProductos() {
    const response = await axios.get(`${API_URL}/clientes-por-productos`, {
      headers: getAuthHeader()
    })
    return response.data
  },

  async getHistorialSanciones(page = 0, size = 20) {
    const response = await axios.get(`${API_URL}/historial-sanciones`, {
      params: { page, size },
      headers: getAuthHeader()
    })
    return response.data
  },

  async getHistorialNotificaciones(page = 0, size = 20) {
    const response = await axios.get(`${API_URL}/historial-notificaciones`, {
      params: { page, size },
      headers: getAuthHeader()
    })
    return response.data
  }
}

export default reporteService   