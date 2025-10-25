<template>
  <div class="bg-white rounded-lg shadow-sm p-4 flex gap-4 items-center hover:shadow-md transition-shadow">
    <!-- Imagen del producto -->
    <div class="w-24 h-24 bg-gray-200 rounded-lg flex-shrink-0 overflow-hidden">
      <img
        v-if="item.producto.imagenUrl"
        :src="item.producto.imagenUrl"
        :alt="item.producto.nombre"
        class="w-full h-full object-cover"
      />
      <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-2xl">
        
      </div>
    </div>

    <!-- Información del producto -->
    <div class="flex-1 min-w-0">
      <h3 class="font-semibold text-gray-900 truncate">
        {{ item.producto.nombre }}
      </h3>
      <p class="text-sm text-gray-600 mt-1">
        Precio: Q {{ formatPrice(item.precio) }}
      </p>
      <p v-if="item.producto.stock < 5" class="text-xs text-orange-600 mt-1">
         Solo quedan {{ item.producto.stock }} unidades
      </p>
    </div>

    <!-- Controles de cantidad -->
    <div class="flex items-center gap-3">
      <button
        @click="$emit('decrement')"
        :disabled="loading"
        class="w-8 h-8 rounded-full bg-gray-200 hover:bg-gray-300 flex items-center justify-center transition-colors disabled:opacity-50"
      >
        <span class="text-lg font-bold">−</span>
      </button>
      
      <span class="w-12 text-center font-semibold">
        {{ item.cantidad }}
      </span>
      
      <button
        @click="$emit('increment')"
        :disabled="loading || item.cantidad >= item.producto.stock"
        class="w-8 h-8 rounded-full bg-blue-600 hover:bg-blue-700 text-white flex items-center justify-center transition-colors disabled:opacity-50"
      >
        <span class="text-lg font-bold">+</span>
      </button>
    </div>

    <!-- Subtotal -->
    <div class="text-right min-w-[100px]">
      <p class="text-lg font-bold text-gray-900">
        Q {{ formatPrice(item.precio * item.cantidad) }}
      </p>
    </div>

    <!-- Botón eliminar -->
    <button
      @click="$emit('remove')"
      :disabled="loading"
      class="text-red-600 hover:text-red-700 p-2 disabled:opacity-50"
      title="Eliminar del carrito"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
      </svg>
    </button>
  </div>
</template>

<script setup>
defineProps({
  item: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['increment', 'decrement', 'remove'])

const formatPrice = (price) => {
  return new Intl.NumberFormat('es-GT', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(price)
}
</script>