<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Mis Productos</h1>
          <p class="text-gray-600 mt-2">Administra tus productos y su estado de aprobación</p>
        </div>

        <router-link
          to="/crear-producto"
          class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition"
        >
          + Crear Producto
        </router-link>
      </div>

      <!-- Filtros rápidos -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-6">
        <div class="flex gap-4">
          <button
            @click="filtroEstado = null"
            :class="[
              'px-4 py-2 rounded-lg font-medium transition',
              filtroEstado === null 
                ? 'bg-blue-600 text-white' 
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            ]"
          >
            Todos ({{ total }})
          </button>
          <button
            @click="filtroEstado = 'PENDIENTE_REVISION'"
            :class="[
              'px-4 py-2 rounded-lg font-medium transition',
              filtroEstado === 'PENDIENTE_REVISION' 
                ? 'bg-yellow-600 text-white' 
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            ]"
          >
            Pendientes
          </button>
          <button
            @click="filtroEstado = 'APROBADO'"
            :class="[
              'px-4 py-2 rounded-lg font-medium transition',
              filtroEstado === 'APROBADO' 
                ? 'bg-green-600 text-white' 
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            ]"
          >
            Aprobados
          </button>
          <button
            @click="filtroEstado = 'RECHAZADO'"
            :class="[
              'px-4 py-2 rounded-lg font-medium transition',
              filtroEstado === 'RECHAZADO' 
                ? 'bg-red-600 text-white' 
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            ]"
          >
            Rechazados
          </button>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-blue-600 border-t-transparent"></div>
        <p class="text-gray-600 mt-4">Cargando productos...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg">
        <p class="text-sm">{{ error }}</p>
      </div>

      <!-- Sin productos -->
      <div v-else-if="productosFiltrados.length === 0" class="bg-white rounded-lg shadow-sm p-12 text-center">
        <div class="text-gray-400 mb-4">
          <svg class="mx-auto h-24 w-24" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2">No tienes productos aún</h3>
        <p class="text-gray-600 mb-6">Comienza creando tu primer producto para vender</p>
        <router-link
          to="/crear-producto"
          class="inline-block bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition"
        >
          Crear mi primer producto
        </router-link>
      </div>

      <!-- Lista de productos -->
      <div v-else class="grid gap-6">
        <div
          v-for="producto in productosFiltrados"
          :key="producto.id"
          class="bg-white rounded-lg shadow-sm hover:shadow-md transition p-6"
        >
          <div class="flex gap-6">
            <!-- Imagen -->
            <div class="w-32 h-32 flex-shrink-0 bg-gray-100 rounded-lg overflow-hidden">
              <img
                v-if="producto.imagenPrincipal"
                :src="producto.imagenPrincipal"
                :alt="producto.nombre"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
                <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
            </div>

            <!-- Info -->
            <div class="flex-1">
              <div class="flex justify-between items-start mb-2">
                <div>
                  <h3 class="text-xl font-semibold text-gray-900">{{ producto.nombre }}</h3>
                  <p class="text-sm text-gray-600">{{ producto.categoriaNombre }}</p>
                </div>

                <!-- Badge de estado -->
                <span :class="getEstadoBadge(producto.estado)">
                  {{ getEstadoTexto(producto.estado) }}
                </span>
              </div>

              <p class="text-gray-700 text-sm mb-3 line-clamp-2">{{ producto.descripcion }}</p>

              <div class="flex items-center gap-6 text-sm text-gray-600 mb-4">
                <span class="font-semibold text-lg text-gray-900">
                  Q{{ producto.precioFinal ? producto.precioFinal.toFixed(2) : producto.precio.toFixed(2) }}
                </span>
                <span>Stock: {{ producto.stock }}</span>
                <span v-if="producto.marca">{{ producto.marca }}</span>
                <span class="text-xs text-gray-500">
                  Creado: {{ formatearFecha(producto.fechaCreacion) }}
                </span>
              </div>

              <!-- Acciones -->
              <div class="flex gap-3">
                <router-link
                  :to="`/editar-producto/${producto.id}`"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition text-sm font-medium"
                >
                  Editar
                </router-link>

                <button
                  @click="confirmarEliminar(producto)"
                  class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition text-sm font-medium"
                >
                  Eliminar
                </button>

                <router-link
                  :to="`/producto/${producto.id}`"
                  class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition text-sm font-medium"
                >
                  Ver detalle
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Paginación -->
      <div v-if="totalPages > 1" class="mt-8 flex justify-center gap-2">
        <button
          v-for="page in totalPages"
          :key="page"
          @click="currentPage = page - 1"
          :class="[
            'px-4 py-2 rounded-lg font-medium transition',
            currentPage === page - 1
              ? 'bg-blue-600 text-white'
              : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-300'
          ]"
        >
          {{ page }}
        </button>
      </div>
    </div>

    <!-- Modal de confirmación de eliminación -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <h3 class="text-xl font-semibold text-gray-900 mb-4">¿Eliminar producto?</h3>
        <p class="text-gray-600 mb-6">
          ¿Estás seguro de que deseas eliminar "{{ productoAEliminar?.nombre }}"? Esta acción no se puede deshacer.
        </p>
        <div class="flex gap-4">
          <button
            @click="eliminarProducto"
            :disabled="deletingProduct"
            class="flex-1 bg-red-600 text-white py-2 rounded-lg font-semibold hover:bg-red-700 disabled:bg-gray-400 transition"
          >
            {{ deletingProduct ? 'Eliminando...' : 'Sí, eliminar' }}
          </button>
          <button
            @click="showDeleteModal = false"
            class="flex-1 border border-gray-300 text-gray-700 py-2 rounded-lg font-semibold hover:bg-gray-50 transition"
          >
            Cancelar
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import productService from '@/services/productService'

const loading = ref(false)
const error = ref('')
const productos = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const total = ref(0)
const filtroEstado = ref(null)

const showDeleteModal = ref(false)
const productoAEliminar = ref(null)
const deletingProduct = ref(false)

// Productos filtrados
const productosFiltrados = computed(() => {
  if (!filtroEstado.value) return productos.value
  return productos.value.filter(p => p.estado === filtroEstado.value)
})

// Cargar productos
const cargarProductos = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await productService.getMyProducts({
      page: currentPage.value,
      size: 10
    })

    productos.value = response.content || []
    totalPages.value = response.totalPages || 0
    total.value = response.totalElements || 0

    console.log('Productos cargados:', productos.value)
  } catch (err) {
    console.error('Error al cargar productos:', err)
    error.value = 'Error al cargar tus productos'
  } finally {
    loading.value = false
  }
}

// Watch para recargar cuando cambie la página
watch(currentPage, () => {
  cargarProductos()
})

onMounted(() => {
  cargarProductos()
})

// Helpers
const getEstadoBadge = (estado) => {
  const badges = {
    PENDIENTE_REVISION: 'px-3 py-1 rounded-full text-xs font-semibold bg-yellow-100 text-yellow-800',
    APROBADO: 'px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800',
    RECHAZADO: 'px-3 py-1 rounded-full text-xs font-semibold bg-red-100 text-red-800',
    PAUSADO: 'px-3 py-1 rounded-full text-xs font-semibold bg-gray-100 text-gray-800',
    AGOTADO: 'px-3 py-1 rounded-full text-xs font-semibold bg-orange-100 text-orange-800'
  }
  return badges[estado] || 'px-3 py-1 rounded-full text-xs font-semibold bg-gray-100 text-gray-800'
}

const getEstadoTexto = (estado) => {
  const textos = {
    PENDIENTE_REVISION: 'Pendiente',
    APROBADO: 'Aprobado',
    RECHAZADO: 'Rechazado',
    PAUSADO: 'Pausado',
    AGOTADO: 'Agotado'
  }
  return textos[estado] || estado
}

const formatearFecha = (fecha) => {
  return new Date(fecha).toLocaleDateString('es-GT', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const confirmarEliminar = (producto) => {
  productoAEliminar.value = producto
  showDeleteModal.value = true
}

const eliminarProducto = async () => {
  deletingProduct.value = true

  try {
    await productService.deleteProduct(productoAEliminar.value.id)
    console.log('Producto eliminado')
    
    showDeleteModal.value = false
    productoAEliminar.value = null
    
    // Recargar productos
    await cargarProductos()
  } catch (err) {
    console.error('Error al eliminar producto:', err)
    error.value = 'Error al eliminar el producto'
  } finally {
    deletingProduct.value = false
  }
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>