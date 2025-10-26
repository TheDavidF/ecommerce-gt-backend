import { defineStore } from 'pinia'
import adminService from '../services/adminService'
import { useToast } from 'vue-toastification'

const toast = useToast()

export const useAdminStore = defineStore('admin', {
  state: () => ({
    usuarios: [],
    totalUsuarios: 0,
    totalPages: 0,
    currentPage: 0,
    loading: false,
    estadisticas: null,
    
    // Filtros
    filters: {
      rol: null,
      page: 0,
      size: 10,
      sortBy: 'fechaCreacion',
      direction: 'desc'
    }
  }),

  getters: {
    hasUsuarios: (state) => state.usuarios.length > 0,
    
    usuariosActivos: (state) => state.usuarios.filter(u => u.activo),
    
    usuariosInactivos: (state) => state.usuarios.filter(u => !u.activo),
    
    usuariosPorRol: (state) => (rol) => {
      return state.usuarios.filter(u => u.roles.includes(rol))
    }
  },

  actions: {
    // ==================== GESTIÓN DE USUARIOS ====================
    
    async fetchUsuarios(filters = {}) {
      this.loading = true
      try {
        const mergedFilters = { ...this.filters, ...filters }
        const response = await adminService.getUsuarios(mergedFilters)
        
        this.usuarios = response.content || []
        this.totalUsuarios = response.totalElements || 0
        this.totalPages = response.totalPages || 0
        this.currentPage = response.number || 0
        this.filters = mergedFilters
        
      } catch (error) {
        console.error('Error al cargar usuarios:', error)
        toast.error('Error al cargar usuarios')
        this.usuarios = []
      } finally {
        this.loading = false
      }
    },

    async getUsuarioById(id) {
      this.loading = true
      try {
        const usuario = await adminService.getUsuarioById(id)
        return usuario
      } catch (error) {
        console.error('Error al obtener usuario:', error)
        toast.error('Error al obtener usuario')
        throw error
      } finally {
        this.loading = false
      }
    },

    async crearUsuario(userData) {
      this.loading = true
      try {
        const nuevoUsuario = await adminService.crearUsuario(userData)
        toast.success('Usuario creado exitosamente')
        await this.fetchUsuarios()
        return nuevoUsuario
      } catch (error) {
        console.error('Error al crear usuario:', error)
        const mensaje = error.response?.data?.message || 'Error al crear usuario'
        toast.error(mensaje)
        throw error
      } finally {
        this.loading = false
      }
    },

    async actualizarUsuario(id, userData) {
      this.loading = true
      try {
        const usuarioActualizado = await adminService.actualizarUsuario(id, userData)
        toast.success('Usuario actualizado exitosamente')
        await this.fetchUsuarios()
        return usuarioActualizado
      } catch (error) {
        console.error('Error al actualizar usuario:', error)
        const mensaje = error.response?.data?.message || 'Error al actualizar usuario'
        toast.error(mensaje)
        throw error
      } finally {
        this.loading = false
      }
    },

    async toggleEstadoUsuario(id, activo) {
      this.loading = true
      try {
        if (activo) {
          await adminService.desactivarUsuario(id)
          toast.success('Usuario desactivado')
        } else {
          await adminService.activarUsuario(id)
          toast.success('Usuario activado')
        }
        await this.fetchUsuarios()
      } catch (error) {
        console.error('Error al cambiar estado:', error)
        toast.error('Error al cambiar estado del usuario')
        throw error
      } finally {
        this.loading = false
      }
    },

    // ==================== ESTADÍSTICAS ====================
    
    async fetchEstadisticas() {
      this.loading = true
      try {
        this.estadisticas = await adminService.getEstadisticas()
      } catch (error) {
        console.error('Error al cargar estadísticas:', error)
        toast.error('Error al cargar estadísticas')
      } finally {
        this.loading = false
      }
    },

    // ==================== FILTROS ====================
    
    setFilter(key, value) {
      this.filters[key] = value
      this.fetchUsuarios()
    },

    resetFilters() {
      this.filters = {
        rol: null,
        page: 0,
        size: 10,
        sortBy: 'fechaCreacion',
        direction: 'desc'
      }
      this.fetchUsuarios()
    },

    changePage(page) {
      this.filters.page = page
      this.fetchUsuarios()
    }
  }
})