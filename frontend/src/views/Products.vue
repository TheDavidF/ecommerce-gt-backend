<template>
  <div class="min-h-screen bg-gray-100">
    <NavBar />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Productos</h1>
        <p class="text-gray-600">Explora nuestro catálogo de productos</p>
      </div>

      <!-- Buscador y Filtros -->
      <div class="card mb-6">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <!-- Búsqueda -->
          <div class="md:col-span-2">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Buscar productos..."
              class="input-field"
              @input="handleSearch"
            />
          </div>

          <!-- Ordenar -->
          <div>
            <select v-model="sortOrder" @change="handleSort" class="input-field">
              <option value="fecha_desc">Más recientes</option>
              <option value="fecha_asc">Más antiguos</option>
              <option value="precio_asc">Menor precio</option>
              <option value="precio_desc">Mayor precio</option>
              <option value="nombre_asc">Nombre A-Z</option>
              <option value="nombre_desc">Nombre Z-A</option>
            </select>
          </div>

          <!-- Filtros -->
          <div>
            <button
              @click="showFilters = !showFilters"
              class="btn-secondary w-full"
            >
              Filtros {{ showFilters ? '▲' : '▼' }}
            </button>
          </div>
        </div>

        <!-- Panel de filtros expandible -->
        <div v-if="showFilters" class="mt-4 pt-4 border-t border-gray-200">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <!-- Precio mínimo -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Precio mínimo
              </label>
              <input
                v-model.number="filters.precioMin"
                type="number"
                min="0"
                step="0.01"
                placeholder="Q 0.00"
                class="input-field"
              />
            </div>

            <!-- Precio máximo -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Precio máximo
              </label>
              <input
                v-model.number="filters.precioMax"
                type="number"
                min="0"
                step="0.01"
                placeholder="Q 9999.99"
                class="input-field"
              />
            </div>

            <!-- Categoría -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Categoría
              </label>
              <select v-model="filters.categoria" class="input-field">
                <option :value="null">Todas las categorías</option>
                <option value="electronica">Electrónica</option>
                <option value="ropa">Ropa</option>
                <option value="hogar">Hogar</option>
                <option value="deportes">Deportes</option>
                <option value="libros">Libros</option>
              </select>
            </div>
          </div>

          <!-- Botones de acción -->
          <div class="flex gap-2 mt-4">
            <button @click="applyFilters" class="btn-primary">
              Aplicar filtros
            </button>
            <button @click="resetFilters" class="btn-secondary">
              Limpiar filtros
            </button>
          </div>
        </div>
      </div>

      <!-- Resultados info -->
      <div class="mb-4 flex justify-between items-center">
        <p class="text-gray-600">
          Mostrando {{ products.length }} de {{ pagination.totalElements }} productos
        </p>
        <p class="text-sm text-gray-500">
          Página {{ pagination.page + 1 }} de {{ pagination.totalPages }}
        </p>
      </div>

      <!-- Loading state -->
      <div v-if="loading" class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <SkeletonCard v-for="i in 8" :key="i" />
      </div>

      <!-- Grid de productos -->
      <div
        v-else-if="products.length > 0"
        class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6 mb-8"
      >
        <ProductCard
          v-for="product in products"
          :key="product.id"
          :product="product"
          @add-to-cart="handleAddToCart"
          class="animate-fade-in"
        />
      </div>

      <!-- Empty state -->
      <EmptyState
        v-else
        icon="📦"
        title="No hay productos disponibles"
        description="No se encontraron productos que coincidan con tu búsqueda."
        action-text="Limpiar filtros"
        @action="resetFilters"
      />

      <!-- Paginación -->
      <div v-if="pagination.totalPages > 1" class="flex justify-center gap-2 mt-8">
        <button
          @click="previousPage"
          :disabled="pagination.page === 0"
          class="btn-secondary px-4 py-2"
          :class="{ 'opacity-50 cursor-not-allowed': pagination.page === 0 }"
        >
          Anterior
        </button>

        <span class="flex items-center px-4 text-gray-700">
          Página {{ pagination.page + 1 }} de {{ pagination.totalPages }}
        </span>

        <button
          @click="nextPage"
          :disabled="pagination.page >= pagination.totalPages - 1"
          class="btn-secondary px-4 py-2"
          :class="{ 'opacity-50 cursor-not-allowed': pagination.page >= pagination.totalPages - 1 }"
        >
          Siguiente
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useProductStore } from '../stores/product'
import NavBar from '../components/layout/NavBar.vue'
import ProductCard from '../components/products/ProductCard.vue'
import SkeletonCard from '../components/common/SkeletonCard.vue'
import EmptyState from '../components/common/EmptyState.vue'
import { useToast } from 'vue-toastification'

const toast = useToast()
const productStore = useProductStore()

const searchQuery = ref('')
const sortOrder = ref('fecha_desc')
const showFilters = ref(false)
const filters = ref({
  precioMin: null,
  precioMax: null,
  categoria: null
})

const products = computed(() => productStore.products)
const loading = computed(() => productStore.loading)
const pagination = computed(() => productStore.pagination)

let searchTimeout = null

const handleSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    if (searchQuery.value.trim()) {
      productStore.searchProducts(searchQuery.value)
    } else {
      productStore.fetchProducts()
    }
  }, 500)
}

const handleSort = () => {
  productStore.setFilters({ ordenar: sortOrder.value })
  productStore.fetchProducts()
}

const applyFilters = () => {
  productStore.setFilters(filters.value)
  productStore.fetchProducts()
  toast.info('Filtros aplicados')
}

const resetFilters = () => {
  filters.value = {
    precioMin: null,
    precioMax: null,
    categoria: null
  }
  searchQuery.value = ''
  sortOrder.value = 'fecha_desc'
  productStore.resetFilters()
  productStore.fetchProducts()
  toast.info('Filtros limpiados')
}

const nextPage = () => {
  productStore.nextPage()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const previousPage = () => {
  productStore.previousPage()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleAddToCart = (product) => {
  toast.success(`${product.nombre} agregado al carrito`)
  // TODO: Implementar store de carrito
}

onMounted(() => {
  productStore.fetchProducts()
})
</script>