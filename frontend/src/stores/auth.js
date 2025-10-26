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
    // Getters actualizados para roles SIN prefijo ROLE_
    isAdmin: (state) => state.user?.roles?.includes('ADMIN'),
    isModerador: (state) => state.user?.roles?.includes('MODERADOR'),
    isLogistica: (state) => state.user?.roles?.includes('LOGISTICA'),
    isVendedor: (state) => state.user?.roles?.includes('VENDEDOR'),
    isCliente: (state) => state.user?.roles?.includes('COMUN'),
    
    username: (state) => state.user?.nombreUsuario,
    userName: (state) => state.user?.nombreCompleto || state.user?.nombreUsuario || '',
    
    currentUser: (state) => state.user,
    userRoles: (state) => state.user?.roles || []
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

    /**
     * Verificar si el usuario tiene un rol específico
     */
    hasRole(rol) {
      if (!this.user || !this.user.roles) return false
      return this.user.roles.includes(rol)
    },
    
    /**
     * Verificar si el usuario tiene alguno de los roles especificados
     */
    hasAnyRole(roles) {
      if (!this.user || !this.user.roles) return false
      return roles.some(rol => this.user.roles.includes(rol))
    }
  },
})