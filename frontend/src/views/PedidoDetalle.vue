<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-3xl mx-auto px-4">
      <div class="mb-6 flex justify-between items-center">
        <h1 class="text-2xl font-bold text-gray-900">Detalle del Pedido</h1>
        <router-link to="/mis-pedidos" class="text-blue-600 hover:text-blue-800">← Volver a mis pedidos</router-link>
      </div>
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-blue-600 border-t-transparent"></div>
        <p class="text-gray-600 mt-4">Cargando detalle...</p>
      </div>
      <div v-else-if="!pedido" class="bg-white rounded-lg shadow-sm p-12 text-center">
        <h3 class="text-xl font-semibold text-gray-900 mb-2">Pedido no encontrado</h3>
        <p class="text-gray-600 mb-6">Verifica el código o vuelve a la lista.</p>
      </div>
      <div v-else class="bg-white rounded-lg shadow-sm p-6">
        <h2 class="text-xl font-bold mb-2">Pedido: {{ pedido.codigoPedido }}</h2>
        <p class="text-sm text-gray-600 mb-1">Fecha: {{ formatearFecha(pedido.fechaPedido || pedido.fechaCreacion) }}</p>
        <p class="text-sm text-gray-600 mb-1">Dirección: {{ pedido.direccionEntrega }}</p>
        <p class="text-sm text-gray-600 mb-1">Teléfono: {{ pedido.telefono }}</p>
        <p class="text-sm text-gray-600 mb-1">Método de pago: {{ pedido.metodoPago }}</p>
        <p class="text-sm text-gray-600 mb-1">Estado: <span :class="getEstadoBadge(pedido.estado)">{{ pedido.estado }}</span></p>
        <p class="text-gray-700 text-lg font-semibold mt-4">Total: Q{{ pedido.total != null ? Number(pedido.total).toFixed(2) : '---' }}</p>
        <div v-if="pedido.productos && pedido.productos.length" class="mt-6">
          <h3 class="text-lg font-bold mb-2">Productos</h3>
          <ul>
            <li v-for="prod in pedido.productos" :key="prod.id" class="mb-2">
              <span class="font-semibold">{{ prod.nombre }}</span> - Cantidad: {{ prod.cantidad }} - Precio: Q{{ prod.precioUnitario }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import pedidoService from '@/services/pedidoService'

const route = useRoute()
const pedido = ref(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const id = route.params.id
    const detalle = await pedidoService.getPedidoDetalle(id)
    console.log('Detalle recibido:', detalle)
    pedido.value = detalle
  } catch (err) {
    console.error('Error al obtener detalle:', err)
    pedido.value = null
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
