import api from './api'

export default {
  // Obtener carrito del usuario actual
  async getCart() {
    const response = await api.get('/carrito')
    return response.data
  },

  // Agregar producto al carrito
  // Agregar producto al carrito
    async addToCart(productoId, cantidad = 1) {
    console.log('Adding to cart:', { productoId, cantidad })  // ← DEBUG
    
    const response = await api.post('/carrito/items', {
        productoId: String(productoId),  // ← Asegurar que es string
        cantidad: Number(cantidad)       // ← Asegurar que es número
    })
    return response.data
    },

  // Actualizar cantidad de un item
  async updateItemQuantity(itemId, cantidad) {
    const response = await api.put(`/carrito/items/${itemId}`, { 
      cantidad 
    })
    return response.data
  },

  // Eliminar item del carrito
  async removeItem(itemId) {
    const response = await api.delete(`/carrito/items/${itemId}`)
    return response.data
  },

  // Limpiar carrito
  async clearCart() {
    const response = await api.delete('/carrito/limpiar')
    return response.data
  },

  // Obtener total del carrito
  async getTotal() {
    const response = await api.get('/carrito/total')
    return response.data
  },

  // Obtener cantidad de items
  async getItemCount() {
    const response = await api.get('/carrito/cantidad')
    return response.data
  },

  // Verificar stock disponible
  async verifyStock() {
    const response = await api.get('/carrito/verificar-stock')
    return response.data
  }
}