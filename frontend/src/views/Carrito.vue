<template>
  <div class="min-h-screen bg-gray-100">
    <NavBar />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">
           Mi Carrito
        </h1>
        <p class="text-gray-600">
          {{ cartStore.itemCount }} {{ cartStore.itemCount === 1 ? 'producto' : 'productos' }} en tu carrito
        </p>
      </div>

      <!-- Loading state -->
      <div v-if="cartStore.loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Cargando carrito...</p>
      </div>

      <!-- Carrito vacío -->
      <EmptyState
        v-else-if="!cartStore.hasItems"
        icon="🛒"
        title="Tu carrito está vacío"
        description="Agrega productos desde nuestra tienda"
        action-text="Ver productos"
        @action="$router.push('/productos')"
      />

      <!-- Carrito con items -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Lista de items -->
        <div class="lg:col-span-2 space-y-4">
          <CartItem
            v-for="item in cartStore.items"
            :key="item.id"
            :item="item"
            :loading="cartStore.loading"
            @increment="cartStore.incrementItem(item.id)"
            @decrement="cartStore.decrementItem(item.id)"
            @remove="handleRemove(item)"
          />

          <!-- Botón limpiar carrito -->
          <button
            @click="handleClearCart"
            :disabled="cartStore.loading"
            class="btn-secondary w-full"
          >
            Vaciar carrito
          </button>
        </div>

        <!-- Resumen del carrito -->
        <div class="lg:col-span-1">
          <div class="card sticky top-24">
            <h2 class="text-xl font-bold text-gray-900 mb-4">
              Resumen del pedido
            </h2>

            <!-- Desglose -->
            <div class="space-y-3 mb-6">
              <div class="flex justify-between text-gray-700">
                <span>Subtotal:</span>
                <span>Q {{ formatPrice(cartStore.subtotal) }}</span>
              </div>
              
              <div class="flex justify-between text-gray-700">
                <span>IVA (12%):</span>
                <span>Q {{ formatPrice(cartStore.subtotal * 0.12) }}</span>
              </div>
              
              <div class="flex justify-between text-gray-700">
                <span>Envío:</span>
                <span v-if="cartStore.subtotal > 500" class="text-green-600">
                  GRATIS
                </span>
                <span v-else>Q 25.00</span>
              </div>
              
              <div class="border-t pt-3 flex justify-between text-xl font-bold text-gray-900">
                <span>Total:</span>
                <span>Q {{ formatPrice(calculateTotal) }}</span>
              </div>
            </div>

            <!-- Mensaje de envío gratis -->
            <div v-if="cartStore.subtotal > 500" class="bg-green-50 border border-green-200 rounded-lg p-3 mb-4">
              <p class="text-sm text-green-800">
                 ¡Tienes envío gratis!
              </p>
            </div>
            <div v-else class="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-4">
              <p class="text-sm text-blue-800">
                 Agrega Q {{ formatPrice(500 - cartStore.subtotal) }} más para envío gratis
              </p>
            </div>

            <!-- Botón proceder al checkout -->
            <button
              @click="handleCheckout"
              :disabled="cartStore.loading || !cartStore.hasItems"
              class="btn-primary w-full py-3 text-lg"
            >
              Proceder al pago
            </button>

            <button
              @click="$router.push('/productos')"
              class="btn-secondary w-full mt-3"
            >
              Seguir comprando
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import NavBar from '../components/layout/NavBar.vue'
import CartItem from '../components/cart/CartItem.vue'
import EmptyState from '../components/common/EmptyState.vue'
import { useToast } from 'vue-toastification'

const router = useRouter()
const toast = useToast()
const cartStore = useCartStore()

const calculateTotal = computed(() => {
  const subtotal = cartStore.subtotal
  const iva = subtotal * 0.12
  const envio = subtotal > 500 ? 0 : 25
  return subtotal + iva + envio
})

const formatPrice = (price) => {
  return new Intl.NumberFormat('es-GT', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(price)
}

const handleRemove = async (item) => {
  if (confirm(`¿Eliminar "${item.producto.nombre}" del carrito?`)) {
    await cartStore.removeItem(item.id)
  }
}

const handleClearCart = async () => {
  if (confirm('¿Estás seguro de vaciar el carrito?')) {
    await cartStore.clearCart()
  }
}

const handleCheckout = async () => {
  // Verificar stock antes de proceder
  const stockAvailable = await cartStore.verifyStock()
  
  if (!stockAvailable) {
    toast.error('Algunos productos no tienen stock suficiente')
    await cartStore.fetchCart() // Actualizar carrito
    return
  }
  
  // Redirigir a checkout
  router.push('/checkout')
}

onMounted(() => {
  cartStore.fetchCart()
})
</script>