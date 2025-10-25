import { defineStore } from 'pinia'
import authService from '../services/authService'
import { useToast } from 'vue-toastification'

const toast = useToast()

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: authService.getUser(),
    token: authService.getToken(),
    isAuthenticated: authService.isAuthenticated(),
  }),

  getters: {
    isAdmin: (state) => state.user?.roles?.includes('ROLE_ADMIN'),
    isVendedor: (state) => state.user?.roles?.includes('ROLE_VENDEDOR'),
    isCliente: (state) => state.user?.roles?.includes('ROLE_CLIENTE'),
    username: (state) => state.user?.nombreUsuario,
  },

  actions: {
    async login(credentials) {
      try {
        const response = await authService.login(credentials)
        this.user = response
        this.token = response.token
        this.isAuthenticated = true
        toast.success('¡Bienvenido!')
        return response
      } catch (error) {
        const errorMsg = error.response?.data?.message || 'Credenciales incorrectas'
        toast.error(errorMsg)
        throw error
      }
    },

    async register(userData) {
      try {
        const response = await authService.register(userData)
        toast.success('Registro exitoso. Por favor inicia sesión.')
        return response
      } catch (error) {
        const errorMsg = error.response?.data?.message || 'Error al registrarse'
        toast.error(errorMsg)
        throw error
      }
    },

    logout() {
      authService.logout()
      this.user = null
      this.token = null
      this.isAuthenticated = false
      toast.info('Sesión cerrada')
    },

    async fetchCurrentUser() {
      try {
        const user = await authService.getCurrentUser()
        this.user = user
        return user
      } catch (error) {
        this.logout()
        throw error
      }
    },
  },
})