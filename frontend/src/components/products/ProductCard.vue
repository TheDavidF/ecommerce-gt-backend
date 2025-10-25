<template>
  <div class="card hover-lift cursor-pointer" @click="goToProduct">
    <!-- Imagen -->
    <div class="relative overflow-hidden rounded-lg mb-4 bg-gray-200 h-48">
      <img
        v-if="product.imagenUrl"
        :src="product.imagenUrl"
        :alt="product.nombre"
        class="w-full h-full object-cover"
      />
      <div v-else class="flex items-center justify-center h-full text-gray-400">
        Sin imagen
      </div>

      <!-- Badge de stock -->
      <div class="absolute top-2 right-2">
        <span
          v-if="product.stock > 0"
          class="px-2 py-1 text-xs font-semibold rounded-full bg-green-500 text-white"
        >
          Disponible
        </span>
        <span
          v-else
          class="px-2 py-1 text-xs font-semibold rounded-full bg-red-500 text-white"
        >
          Agotado
        </span>
      </div>
    </div>

    <!-- Información -->
    <div>
      <h3 class="text-lg font-semibold text-gray-900 mb-2 line-clamp-2">
        {{ product.nombre }}
      </h3>
      
      <p class="text-sm text-gray-600 mb-3 line-clamp-2">
        {{ product.descripcion }}
      </p>

      <!-- Precio -->
      <div class="flex items-center justify-between mb-3">
        <div>
          <p class="text-2xl font-bold text-blue-600">
            Q {{ formatPrice(product.precio) }}
          </p>
          <p class="text-xs text-gray-500">
            Stock: {{ product.stock }}
          </p>
        </div>
      </div>

      <!-- Acciones -->
      <div class="flex gap-2">
        <button
          v-if="product.stock > 0"
          @click.stop="addToCart"
          class="btn-primary flex-1 py-2 text-sm"
        >
          Agregar al carrito
        </button>
        <button
          v-else
          disabled
          class="btn-secondary flex-1 py-2 text-sm opacity-50 cursor-not-allowed"
        >
          Sin stock
        </button>
      </div>

      <!-- Vendedor -->
      <div class="mt-3 pt-3 border-t border-gray-200">
        <p class="text-xs text-gray-500">
          Vendedor: <span class="font-medium">{{ product.vendedor?.nombreUsuario || 'Desconocido' }}</span>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['add-to-cart'])

const router = useRouter()

const formatPrice = (price) => {
  return new Intl.NumberFormat('es-GT', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(price)
}

const goToProduct = () => {
  router.push(`/productos/${props.product.id}`)
}

const addToCart = () => {
  emit('add-to-cart', props.product)
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