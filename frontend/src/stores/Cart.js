import { defineStore } from 'pinia'
import cartService from '../services/cartService'
import { useToast } from 'vue-toastification'
import { useAuthStore } from './auth'

const toast = useToast()

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    total: 0,
    cantidadTotalItems: 0,
    vacio: true,
    loading: false
  }),

  getters: {
    itemCount: (state) => state.cantidadTotalItems,
    
    hasItems: (state) => !state.vacio && state.items.length > 0,

    getItemById: (state) => (itemId) => {
      return state.items.find(item => item.id === itemId)
    },

    getItemByProductId: (state) => (productId) => {
      return state.items.find(item => item.producto?.id === productId)
    },

    subtotal: (state) => {
      return state.items.reduce((sum, item) => {
        return sum + (item.precio * item.cantidad)
      }, 0)
    }
  },

  actions: {
    // Inicializar carrito
    async initCart() {
      const authStore = useAuthStore()
      if (authStore.isAuthenticated) {
        await this.fetchCart()
      }
    },

    // Obtener carrito del backend
    async fetchCart() {
      this.loading = true
      try {
        const cart = await cartService.getCart()
        
        this.items = cart.items || []
        this.total = cart.total || 0
        this.cantidadTotalItems = cart.cantidadTotalItems || 0
        this.vacio = cart.vacio !== undefined ? cart.vacio : this.items.length === 0
        
      } catch (error) {
        console.error('Error al cargar carrito:', error)
        // Si hay error, inicializar carrito vacío
        this.items = []
        this.total = 0
        this.cantidadTotalItems = 0
        this.vacio = true
      } finally {
        this.loading = false
      }
    },

    // Agregar producto al carrito
    async addItem(producto, cantidad = 1) {
    // Validar que el producto tiene ID
    if (!producto || !producto.id) {
        toast.error('Error: Producto inválido')
        return
    }

    // Validar stock
    if (producto.stock < cantidad) {
        toast.error(`Solo hay ${producto.stock} unidades disponibles`)
        return
    }

    // Verificar si ya existe en el carrito
    const existingItem = this.getItemByProductId(producto.id)
    if (existingItem) {
        const newQuantity = existingItem.cantidad + cantidad
        if (newQuantity > producto.stock) {
        toast.warning(`No puedes agregar más de ${producto.stock} unidades`)
        return
        }
    }

    this.loading = true
    try {
        await cartService.addToCart(producto.id, cantidad)
        await this.fetchCart()
        
        toast.success(`${producto.nombre} agregado al carrito`)
    } catch (error) {
        console.error('Error al agregar al carrito:', error)
        
        const errorMessage = error.response?.data?.message || 'No se pudo agregar el producto'
        
        if (error.response?.status === 400) {
        toast.error(errorMessage)
        } else if (error.response?.status === 401) {
        toast.error('Debes iniciar sesión para agregar al carrito')
        } else if (error.response?.status === 403) {
        toast.error('No tienes permiso para realizar esta acción')
        } else {
        toast.error('Error al agregar el producto al carrito')
        }
    } finally {
        this.loading = false
    }
    },

    // Limpiar carrito
    async clearCart() {
      this.loading = true
      try {
        await cartService.clearCart()
        
        this.items = []
        this.total = 0
        this.cantidadTotalItems = 0
        this.vacio = true
        
        toast.info('Carrito vaciado')
      } catch (error) {
        console.error('Error al limpiar carrito:', error)
        toast.error('Error al vaciar el carrito')
      } finally {
        this.loading = false
      }
    },

    // Incrementar cantidad
    async incrementItem(itemId) {
      const item = this.getItemById(itemId)
      if (item) {
        await this.updateQuantity(itemId, item.cantidad + 1)
      }
    },

    // Decrementar cantidad
    async decrementItem(itemId) {
      const item = this.getItemById(itemId)
      if (item) {
        await this.updateQuantity(itemId, item.cantidad - 1)
      }
    },

    // Verificar stock disponible
    async verifyStock() {
      try {
        const response = await cartService.verifyStock()
        return response.stockDisponible
      } catch (error) {
        console.error('Error al verificar stock:', error)
        return false
      }
    }
  }
})