import api from './api'

export default {
  // Registro de usuario
  async register(userData) {
    const response = await api.post('/auth/registro', userData)
    return response.data
  },

  // Login
  async login(credentials) {
    const response = await api.post('/auth/login', credentials)
    if (response.data.token) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data))
    }
    return response.data
  },

  // Logout
  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },

  // Obtener usuario actual
  async getCurrentUser() {
    const response = await api.get('/auth/me')
    return response.data
  },

  // Verificar si está autenticado
  isAuthenticated() {
    return !!localStorage.getItem('token')
  },

  // Obtener token
  getToken() {
    return localStorage.getItem('token')
  },

  // Obtener usuario del localStorage
  getUser() {
    const user = localStorage.getItem('user')
    return user ? JSON.parse(user) : null
  },

  // Verificar si tiene rol
  hasRole(role) {
    const user = this.getUser()
    return user?.roles?.includes(role)
  },
}