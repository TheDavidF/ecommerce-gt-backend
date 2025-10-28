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
    getItemById: (state) => (itemId) => state.items.find(item => item.id === itemId),
    getItemByProductId: (state) => (productId) => state.items.find(item => item.producto?.id === productId),
    subtotal: (state) => state.items.reduce((sum, item) => {
      const precio = Number(item.precioUnitario) || 0
      const cantidad = Number(item.cantidad) || 0
      return sum + (precio * cantidad)
    }, 0)
  },
  actions: {
    async initCart() {
      const authStore = useAuthStore()
      if (authStore.isAuthenticated) {
        await this.fetchCart()
      }
    },
    async fetchCart() {
      const authStore = useAuthStore()
      if (!authStore.hasValidToken) {
        console.log('Usuario no autenticado, no se carga carrito')
        return
      }
      this.loading = true
      try {
        const cart = await cartService.getCart()
        this.items = cart.items || []
        this.total = cart.total || 0
        this.cantidadTotalItems = cart.cantidadTotalItems || 0
        this.vacio = cart.vacio !== undefined ? cart.vacio : this.items.length === 0
      } catch (error) {
        console.error('Error al cargar carrito:', error)
        if (error.response?.status === 401) {
          toast.error('Sesión expirada. Inicia sesión nuevamente.')
        }
        this.items = []
        this.total = 0
        this.cantidadTotalItems = 0
        this.vacio = true
      } finally {
        this.loading = false
      }
    },
    async addItem(producto, cantidad = 1) {
      const authStore = useAuthStore()
      if (!authStore.hasValidToken) {
        toast.error('Debes iniciar sesión para agregar productos al carrito')
        return
      }
      if (!producto || !producto.id) {
        toast.error('Error: Producto inválido')
        return
      }
      if (producto.stock < cantidad) {
        toast.error(`Solo hay ${producto.stock} unidades disponibles`)
        return
      }
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
    async updateQuantity(itemId, nuevaCantidad) {
      this.loading = true
      try {
        await cartService.updateItemQuantity(itemId, nuevaCantidad)
        await this.fetchCart()
        toast.success('Cantidad actualizada')
      } catch (error) {
        console.error('Error al actualizar cantidad:', error)
        toast.error('No se pudo actualizar la cantidad')
      } finally {
        this.loading = false
      }
    },
    async incrementItem(itemId) {
      const item = this.getItemById(itemId)
      if (item) {
        await this.updateQuantity(itemId, item.cantidad + 1)
      }
    },
    async decrementItem(itemId) {
      const item = this.getItemById(itemId)
      if (item) {
        await this.updateQuantity(itemId, item.cantidad - 1)
      }
    },
    async removeItem(itemId) {
      this.loading = true
      try {
        await cartService.removeItem(itemId)
        await this.fetchCart()
        toast.success('Producto eliminado del carrito')
      } catch (error) {
        console.error('Error al eliminar producto:', error)
        toast.error('No se pudo eliminar el producto')
      } finally {
        this.loading = false
      }
    },
    async verifyStock() {
      const authStore = useAuthStore()
      if (!authStore.hasValidToken) {
        toast.error('Debes iniciar sesión para verificar el stock')
        return false
      }
      try {
        const response = await cartService.verifyStock()
        return response.stockDisponible
      } catch (error) {
        console.error('Error al verificar stock:', error)
        if (error.response?.status === 401) {
          toast.error('Sesión expirada. Inicia sesión nuevamente.')
        }
        return false
      }
    }
  }
})