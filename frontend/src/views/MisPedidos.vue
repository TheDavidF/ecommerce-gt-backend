<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-5xl mx-auto px-4">
      <div class="mb-6 flex justify-between items-center">
        <h1 class="text-3xl font-bold text-gray-900">Mis Pedidos</h1>
        <router-link to="/" class="text-blue-600 hover:text-blue-800">← Volver al inicio</router-link>
      </div>

      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-blue-600 border-t-transparent"></div>
        <p class="text-gray-600 mt-4">Cargando pedidos...</p>
      </div>

      <div v-else-if="pedidosFiltrados.length === 0" class="bg-white rounded-lg shadow-sm p-12 text-center">
        <h3 class="text-xl font-semibold text-gray-900 mb-2">No tienes pedidos aún</h3>
        <p class="text-gray-600 mb-6">¡Realiza tu primera compra!</p>
        <router-link to="/productos" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition">
          Ir al catálogo
        </router-link>
      </div>

      <div v-else class="grid gap-6">
        <div
          v-for="pedido in pedidosFiltrados"
          :key="pedido.id"
          class="bg-white rounded-lg shadow-sm hover:shadow-md transition p-6"
        >
          <div class="flex justify-between items-center mb-2">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">Pedido: {{ pedido.codigoPedido }}</h3>
              <p class="text-sm text-gray-600">Fecha: {{ formatearFecha(pedido.fechaPedido || pedido.fechaCreacion) }}</p>
              <p class="text-sm text-gray-600">Dirección: {{ pedido.direccionEntrega }}</p>
              <p class="text-sm text-gray-600">Teléfono: {{ pedido.telefono }}</p>
              <p class="text-sm text-gray-600">Método: {{ pedido.metodoPago }}</p>
            </div>
            <span :class="getEstadoBadge(pedido.estado)">
              {{ pedido.estado }}
            </span>
          </div>
          <p class="text-gray-700 text-sm mb-3">
            Total: Q{{ pedido.total != null ? Number(pedido.total).toFixed(2) : '---' }}
          </p>
          <router-link
            :to="`/pedido/${pedido.id}`"
            class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition text-sm font-medium"
          >
            Ver detalle
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import pedidoService from '@/services/pedidoService'

const loading = ref(true)
const pedidos = ref([])
const pedidosFiltrados = ref([])

onMounted(async () => {
  try {
    const resultado = await pedidoService.getMyPedidos()
    pedidos.value = resultado
    pedidosFiltrados.value = resultado.filter(p => p.estado === 'ENVIADO' || p.estado === 'ENTREGADO')
  } catch (err) {
    pedidos.value = []
    pedidosFiltrados.value = []
  } finally {
    loading.value = false
  }
})

const formatearFecha = (fecha) => {
  if (!fecha) return "---"
  return new Date(fecha).toLocaleString('es-GT', { year: "numeric", month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" })
}

const getEstadoBadge = (estado) => {
  const badges = {
    PENDIENTE: 'px-3 py-1 rounded-full text-xs font-semibold bg-yellow-100 text-yellow-800',
    EN_PREPARACION: 'px-3 py-1 rounded-full text-xs font-semibold bg-orange-100 text-orange-800',
    ENVIADO: 'px-3 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-800',
    ENTREGADO: 'px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800',
    CONFIRMADO: 'px-3 py-1 rounded-full text-xs font-semibold bg-cyan-100 text-cyan-800',
    CANCELADO: 'px-3 py-1 rounded-full text-xs font-semibold bg-red-100 text-red-800'
  }
  return badges[estado] || 'px-3 py-1 rounded-full text-xs font-semibold bg-gray-100 text-gray-800'
}
</script>