<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-6xl mx-auto px-4">
      <!-- Loading -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-blue-600 border-t-transparent"></div>
        <p class="text-gray-600 mt-4">Cargando producto...</p>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg">
        <p class="text-sm">{{ error }}</p>
      </div>

      <!-- Producto -->
      <div v-else class="bg-white rounded-lg shadow-md p-8">
        <div class="grid md:grid-cols-2 gap-8">
          <!-- Imagen -->
          <div>
            <div class="bg-gray-100 rounded-lg overflow-hidden aspect-square">
              <img
                v-if="producto.imagenPrincipal"
                :src="producto.imagenPrincipal"
                :alt="producto.nombre"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
                <svg class="w-32 h-32" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
            </div>
          </div>

          <!-- Info -->
          <div>
            <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ producto.nombre }}</h1>
            <p class="text-gray-600 mb-4">{{ producto.categoriaNombre }}</p>

            <!-- Precio -->
            <div class="mb-6">
              <div v-if="producto.precioDescuento" class="flex items-baseline gap-3">
                <span class="text-3xl font-bold text-red-600">Q{{ producto.precioFinal.toFixed(2) }}</span>
                <span class="text-xl text-gray-400 line-through">Q{{ producto.precio.toFixed(2) }}</span>
              </div>
              <div v-else>
                <span class="text-3xl font-bold text-gray-900">Q{{ producto.precio.toFixed(2) }}</span>
              </div>
            </div>

            <!-- Descripción -->
            <div class="mb-6">
              <h3 class="font-semibold text-gray-900 mb-2">Descripción</h3>
              <p class="text-gray-700 whitespace-pre-line">{{ producto.descripcion }}</p>
            </div>

            <!-- Detalles -->
            <div class="border-t border-gray-200 pt-4 mb-6 space-y-2">
              <div v-if="producto.marca" class="flex justify-between">
                <span class="text-gray-600">Marca:</span>
                <span class="font-semibold">{{ producto.marca }}</span>
              </div>
              <div v-if="producto.modelo" class="flex justify-between">
                <span class="text-gray-600">Modelo:</span>
                <span class="font-semibold">{{ producto.modelo }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Stock:</span>
                <span class="font-semibold">{{ producto.stock }} unidades</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Vendedor:</span>
                <span class="font-semibold">{{ producto.vendedorNombre }}</span>
              </div>
            </div>

            <!-- Botones (solo si está aprobado) -->
            <div v-if="producto.estado === 'APROBADO'" class="flex gap-4">
              <button class="flex-1 bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition">
                Agregar al carrito
              </button>
            </div>

            <!-- Estado no aprobado -->
            <div v-else class="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded-lg">
              <p class="text-sm">Este producto aún no está disponible para la venta.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import productService from '@/services/productService'

const route = useRoute()

const loading = ref(true)
const error = ref('')
const producto = ref(null)

onMounted(async () => {
  try {
    producto.value = await productService.getProductById(route.params.id)
  } catch (err) {
    console.error('Error al cargar producto:', err)
    error.value = 'Producto no encontrado'
  } finally {
    loading.value = false
  }
})
</script>