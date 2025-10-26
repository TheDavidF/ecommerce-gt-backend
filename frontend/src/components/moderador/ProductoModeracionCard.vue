<template>
  <div class="card-moderacion">
    <div class="card-header">
      <div class="producto-info">
        <img 
          v-if="solicitud.producto?.imagenUrl" 
          :src="solicitud.producto.imagenUrl" 
          :alt="solicitud.producto.nombre"
          class="producto-imagen"
        />
        <div class="producto-imagen-placeholder" v-else>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
        
        <div class="producto-detalles">
          <h3 class="producto-nombre">{{ solicitud.producto?.nombre }}</h3>
          <p class="producto-descripcion">{{ truncateText(solicitud.producto?.descripcion, 100) }}</p>
          
          <div class="producto-meta">
            <span class="meta-item">
              <strong>Precio:</strong> Q{{ formatPrice(solicitud.producto?.precio) }}
            </span>
            <span class="meta-item">
              <strong>Stock:</strong> {{ solicitud.producto?.stock }} unidades
            </span>
            <span class="meta-item">
              <strong>Vendedor:</strong> {{ solicitud.solicitanteNombre }}
            </span>
          </div>
        </div>
      </div>

      <div class="solicitud-meta">
        <span :class="getEstadoBadgeClass(solicitud.estado)" class="badge">
          {{ getEstadoLabel(solicitud.estado) }}
        </span>
        <span class="fecha">
          {{ formatDate(solicitud.fechaSolicitud) }}
        </span>
      </div>
    </div>

    <div class="card-body">
      <div v-if="solicitud.comentarioModerador" class="comentario-box">
        <strong>Comentario del moderador:</strong>
        <p>{{ solicitud.comentarioModerador }}</p>
      </div>
    </div>

    <div class="card-footer">
      <button 
        @click="$emit('ver-detalles', solicitud)"
        class="btn-secondary"
      >
        Ver Detalles
      </button>

      <div v-if="solicitud.estado === 'PENDIENTE'" class="acciones-grupo">
        <button 
          @click="$emit('aprobar', solicitud.id)"
          class="btn-success"
        >
          ✓ Aprobar
        </button>
        <button 
          @click="$emit('solicitar-cambios', solicitud.id)"
          class="btn-warning"
        >
          ⚠ Solicitar Cambios
        </button>
        <button 
          @click="$emit('rechazar', solicitud.id)"
          class="btn-danger"
        >
          ✗ Rechazar
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  solicitud: {
    type: Object,
    required: true
  }
})

defineEmits(['ver-detalles', 'aprobar', 'rechazar', 'solicitar-cambios'])

const truncateText = (text, maxLength) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

const formatPrice = (price) => {
  if (!price) return '0.00'
  return parseFloat(price).toFixed(2)
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('es-GT', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getEstadoLabel = (estado) => {
  const labels = {
    'PENDIENTE': 'Pendiente',
    'APROBADO': 'Aprobado',
    'RECHAZADO': 'Rechazado',
    'CAMBIOS_SOLICITADOS': 'Cambios Solicitados'
  }
  return labels[estado] || estado
}

const getEstadoBadgeClass = (estado) => {
  const classes = {
    'PENDIENTE': 'badge-warning',
    'APROBADO': 'badge-success',
    'RECHAZADO': 'badge-danger',
    'CAMBIOS_SOLICITADOS': 'badge-info'
  }
  return classes[estado] || 'badge-secondary'
}
</script>

<style scoped>
.card-moderacion {
  @apply bg-white rounded-lg shadow-md overflow-hidden border border-gray-200 transition-all hover:shadow-lg;
}

.card-header {
  @apply p-4 border-b border-gray-200;
}

.producto-info {
  @apply flex gap-4 mb-3;
}

.producto-imagen {
  @apply w-24 h-24 object-cover rounded-lg flex-shrink-0;
}

.producto-imagen-placeholder {
  @apply w-24 h-24 bg-gray-200 rounded-lg flex items-center justify-center text-gray-400 flex-shrink-0;
}

.producto-detalles {
  @apply flex-1 min-w-0;
}

.producto-nombre {
  @apply text-lg font-semibold text-gray-900 mb-1 truncate;
}

.producto-descripcion {
  @apply text-sm text-gray-600 mb-2 line-clamp-2;
}

.producto-meta {
  @apply flex flex-wrap gap-3 text-xs text-gray-500;
}

.meta-item {
  @apply flex items-center gap-1;
}

.solicitud-meta {
  @apply flex items-center justify-between;
}

.badge {
  @apply inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium;
}

.badge-warning {
  @apply bg-yellow-100 text-yellow-800;
}

.badge-success {
  @apply bg-green-100 text-green-800;
}

.badge-danger {
  @apply bg-red-100 text-red-800;
}

.badge-info {
  @apply bg-blue-100 text-blue-800;
}

.badge-secondary {
  @apply bg-gray-100 text-gray-800;
}

.fecha {
  @apply text-sm text-gray-500;
}

.card-body {
  @apply p-4;
}

.comentario-box {
  @apply bg-gray-50 p-3 rounded-lg text-sm;
}

.comentario-box strong {
  @apply text-gray-700 block mb-1;
}

.comentario-box p {
  @apply text-gray-600;
}

.card-footer {
  @apply p-4 bg-gray-50 border-t border-gray-200 flex items-center justify-between gap-3;
}

.acciones-grupo {
  @apply flex gap-2;
}

.btn-secondary {
  @apply px-4 py-2 rounded-lg text-sm font-medium text-gray-700 bg-white border border-gray-300 hover:bg-gray-50 transition-colors;
}

.btn-success {
  @apply px-4 py-2 rounded-lg text-sm font-medium text-white bg-green-600 hover:bg-green-700 transition-colors;
}

.btn-warning {
  @apply px-4 py-2 rounded-lg text-sm font-medium text-white bg-yellow-600 hover:bg-yellow-700 transition-colors;
}

.btn-danger {
  @apply px-4 py-2 rounded-lg text-sm font-medium text-white bg-red-600 hover:bg-red-700 transition-colors;
}

@media (max-width: 640px) {
  .card-footer {
    @apply flex-col;
  }
  
  .acciones-grupo {
    @apply w-full flex-col;
  }
  
  .acciones-grupo button {
    @apply w-full;
  }
}
</style>