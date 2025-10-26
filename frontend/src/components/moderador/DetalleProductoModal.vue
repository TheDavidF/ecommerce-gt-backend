<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="close">
    <div class="modal-container-large">
      <div class="modal-header">
        <h2>Detalles del Producto</h2>
        <button @click="close" class="btn-close">&times;</button>
      </div>

      <div v-if="loading" class="modal-body text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Cargando detalles...</p>
      </div>

      <div v-else-if="producto" class="modal-body">
        <!-- Estado de la solicitud -->
        <div class="estado-banner" :class="getEstadoBannerClass(solicitud.estado)">
          <span class="estado-icon">{{ getEstadoIcon(solicitud.estado) }}</span>
          <div>
            <p class="estado-label">Estado de la Solicitud</p>
            <p class="estado-value">{{ getEstadoLabel(solicitud.estado) }}</p>
          </div>
        </div>

        <!-- Información del producto -->
        <div class="producto-section">
          <h3 class="section-title">Información del Producto</h3>
          
          <div class="producto-grid">
            <!-- Imagen -->
            <div class="producto-imagen-container">
              <img 
                v-if="producto.imagenUrl" 
                :src="producto.imagenUrl" 
                :alt="producto.nombre"
                class="producto-imagen-grande"
              />
              <div v-else class="producto-imagen-placeholder-grande">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
            </div>

            <!-- Datos -->
            <div class="producto-datos">
              <div class="dato-grupo">
                <label>Nombre del Producto</label>
                <p class="dato-valor">{{ producto.nombre }}</p>
              </div>

              <div class="dato-grupo">
                <label>Descripción</label>
                <p class="dato-valor">{{ producto.descripcion }}</p>
              </div>

              <div class="dato-row">
                <div class="dato-grupo">
                  <label>Precio</label>
                  <p class="dato-valor precio">Q{{ formatPrice(producto.precio) }}</p>
                </div>

                <div class="dato-grupo">
                  <label>Stock Disponible</label>
                  <p class="dato-valor">{{ producto.stock }} unidades</p>
                </div>
              </div>

              <div class="dato-grupo">
                <label>Categoría</label>
                <p class="dato-valor">{{ producto.categoriaNombre || 'Sin categoría' }}</p>
              </div>

              <div class="dato-grupo">
                <label>Estado del Producto</label>
                <span :class="getEstadoProductoBadgeClass(producto.estado)" class="badge">
                  {{ producto.estado }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Información del vendedor -->
        <div class="vendedor-section">
          <h3 class="section-title">Información del Vendedor</h3>
          <div class="vendedor-info">
            <div class="dato-grupo">
              <label>Nombre</label>
              <p class="dato-valor">{{ solicitud.solicitanteNombre }}</p>
            </div>
            <div class="dato-grupo">
              <label>Fecha de Solicitud</label>
              <p class="dato-valor">{{ formatDate(solicitud.fechaSolicitud) }}</p>
            </div>
          </div>
        </div>

        <!-- Comentarios de moderación -->
        <div v-if="solicitud.comentarioModerador" class="comentario-section">
          <h3 class="section-title">Comentario del Moderador</h3>
          <div class="comentario-box">
            <p>{{ solicitud.comentarioModerador }}</p>
          </div>
        </div>

        <!-- Acciones -->
        <div v-if="solicitud.estado === 'PENDIENTE'" class="acciones-section">
          <h3 class="section-title">Acciones de Moderación</h3>
          <div class="acciones-grupo">
            <button 
              @click="handleAprobar"
              class="btn-action btn-success"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
              </svg>
              Aprobar Producto
            </button>

            <button 
              @click="handleSolicitarCambios"
              class="btn-action btn-warning"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
              </svg>
              Solicitar Cambios
            </button>

            <button 
              @click="handleRechazar"
              class="btn-action btn-danger"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
              </svg>
              Rechazar Producto
            </button>
          </div>
        </div>
      </div>

      <div v-else class="modal-body text-center py-12">
        <p class="text-gray-600">No se pudieron cargar los detalles del producto</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useModeradorStore } from '../../stores/moderador'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  solicitud: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'aprobar', 'rechazar', 'solicitar-cambios'])

const moderadorStore = useModeradorStore()

const loading = computed(() => moderadorStore.loading)
const producto = computed(() => moderadorStore.productoActual)

const formatPrice = (price) => {
  if (!price) return '0.00'
  return parseFloat(price).toFixed(2)
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('es-GT', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getEstadoLabel = (estado) => {
  const labels = {
    'PENDIENTE': 'Pendiente de Revisión',
    'APROBADO': 'Aprobado',
    'RECHAZADO': 'Rechazado',
    'CAMBIOS_SOLICITADOS': 'Cambios Solicitados'
  }
  return labels[estado] || estado
}

const getEstadoIcon = (estado) => {
  const icons = {
    'PENDIENTE': '',
    'APROBADO': '',
    'RECHAZADO': '',
    'CAMBIOS_SOLICITADOS': ''
  }
  return icons[estado] || ' '
}

const getEstadoBannerClass = (estado) => {
  const classes = {
    'PENDIENTE': 'estado-warning',
    'APROBADO': 'estado-success',
    'RECHAZADO': 'estado-danger',
    'CAMBIOS_SOLICITADOS': 'estado-info'
  }
  return classes[estado] || ''
}

const getEstadoProductoBadgeClass = (estado) => {
  const classes = {
    'PENDIENTE_REVISION': 'badge-warning',
    'APROBADO': 'badge-success',
    'RECHAZADO': 'badge-danger',
    'ACTIVO': 'badge-success'
  }
  return classes[estado] || 'badge-secondary'
}

const handleAprobar = () => {
  emit('aprobar', props.solicitud.id)
  close()
}

const handleRechazar = () => {
  emit('rechazar', props.solicitud.id)
  close()
}

const handleSolicitarCambios = () => {
  emit('solicitar-cambios', props.solicitud.id)
  close()
}

const close = () => {
  emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-container-large {
  background: white;
  border-radius: 0.5rem;
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.modal-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.btn-close {
  background: none;
  border: none;
  font-size: 2rem;
  color: #6b7280;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  width: 32px;
  height: 32px;
}

.btn-close:hover {
  color: #111827;
}

.modal-body {
  padding: 1.5rem;
}

.estado-banner {
  @apply flex items-center gap-4 p-4 rounded-lg mb-6 border-2;
}

.estado-warning {
  @apply bg-yellow-50 border-yellow-200;
}

.estado-success {
  @apply bg-green-50 border-green-200;
}

.estado-danger {
  @apply bg-red-50 border-red-200;
}

.estado-info {
  @apply bg-blue-50 border-blue-200;
}

.estado-icon {
  @apply text-3xl;
}

.estado-label {
  @apply text-sm text-gray-600 font-medium;
}

.estado-value {
  @apply text-lg font-bold text-gray-900;
}

.section-title {
  @apply text-lg font-semibold text-gray-900 mb-4;
}

.producto-section,
.vendedor-section,
.comentario-section,
.acciones-section {
  @apply mb-6 pb-6 border-b border-gray-200;
}

.acciones-section {
  @apply border-b-0;
}

.producto-grid {
  @apply grid grid-cols-1 md:grid-cols-2 gap-6;
}

.producto-imagen-container {
  @apply flex items-start;
}

.producto-imagen-grande {
  @apply w-full h-auto max-h-96 object-cover rounded-lg border border-gray-200;
}

.producto-imagen-placeholder-grande {
  @apply w-full h-64 bg-gray-200 rounded-lg flex items-center justify-center text-gray-400;
}

.producto-datos {
  @apply space-y-4;
}

.dato-grupo {
  @apply space-y-1;
}

.dato-row {
  @apply grid grid-cols-2 gap-4;
}

.dato-grupo label {
  @apply block text-sm font-medium text-gray-600;
}

.dato-valor {
  @apply text-base text-gray-900;
}

.dato-valor.precio {
  @apply text-2xl font-bold text-green-600;
}

.vendedor-info {
  @apply grid grid-cols-1 md:grid-cols-2 gap-4;
}

.comentario-box {
  @apply bg-gray-50 p-4 rounded-lg text-sm text-gray-700;
}

.acciones-grupo {
  @apply grid grid-cols-1 md:grid-cols-3 gap-4;
}

.btn-action {
  @apply flex items-center justify-center gap-2 px-6 py-3 rounded-lg font-medium transition-all;
}

.btn-success {
  @apply bg-green-600 text-white hover:bg-green-700;
}

.btn-warning {
  @apply bg-yellow-600 text-white hover:bg-yellow-700;
}

.btn-danger {
  @apply bg-red-600 text-white hover:bg-red-700;
}

.badge {
  @apply inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium;
}

.badge-success {
  @apply bg-green-100 text-green-800;
}

.badge-warning {
  @apply bg-yellow-100 text-yellow-800;
}

.badge-danger {
  @apply bg-red-100 text-red-800;
}

.badge-secondary {
  @apply bg-gray-100 text-gray-800;
}

@media (max-width: 768px) {
  .acciones-grupo {
    @apply grid-cols-1;
  }
}
</style>