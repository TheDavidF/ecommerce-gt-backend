<template>
  <div class="min-h-screen bg-gray-100">
    <NavBar />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 mb-2">Panel de Moderación</h1>
          <p class="text-gray-600">
            Gestiona las solicitudes de publicación de productos
          </p>
        </div>
        <router-link
          to="/moderador/aplicar-sancion"
          class="btn-primary px-4 py-2 rounded-lg font-semibold text-white bg-yellow-600 hover:bg-yellow-700 transition-colors"
        >
          Aplicar sanción
        </router-link>
      </div>

      <!-- Estadísticas -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="stat-card bg-yellow-50 border-yellow-200">
          <div class="stat-icon bg-yellow-100 text-yellow-600">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div class="stat-content">
            <p class="stat-label">Pendientes</p>
            <p class="stat-value">{{ moderadorStore.estadisticas.pendientes || 0 }}</p>
          </div>
        </div>

        <div class="stat-card bg-green-50 border-green-200">
          <div class="stat-icon bg-green-100 text-green-600">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div class="stat-content">
            <p class="stat-label">Aprobadas</p>
            <p class="stat-value">{{ moderadorStore.estadisticas.aprobadas || 0 }}</p>
          </div>
        </div>

        <div class="stat-card bg-red-50 border-red-200">
          <div class="stat-icon bg-red-100 text-red-600">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div class="stat-content">
            <p class="stat-label">Rechazadas</p>
            <p class="stat-value">{{ moderadorStore.estadisticas.rechazadas || 0 }}</p>
          </div>
        </div>

        <div class="stat-card bg-blue-50 border-blue-200">
          <div class="stat-icon bg-blue-100 text-blue-600">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
            </svg>
          </div>
          <div class="stat-content">
            <p class="stat-label">Cambios Solicitados</p>
            <p class="stat-value">{{ moderadorStore.estadisticas.cambiosSolicitados || 0 }}</p>
          </div>
        </div>
      </div>

      <!-- Filtros -->
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <div class="flex flex-wrap gap-2">
          <button
            v-for="estado in estados"
            :key="estado.value"
            @click="filtrarPorEstado(estado.value)"
            :class="[
              'filter-btn',
              moderadorStore.estadoFiltro === estado.value ? estado.activeClass : 'filter-btn-inactive'
            ]"
          >
            {{ estado.label }}
          </button>
        </div>
      </div>

      <!-- Sección de reviews pendientes -->
      <div class="mb-12">
        <h2 class="text-2xl font-bold text-gray-900 mb-4">Reviews pendientes de aprobación</h2>
        <div v-if="moderadorStore.loadingReviews" class="text-center py-8">
          <div class="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-yellow-600"></div>
          <p class="mt-2 text-gray-600">Cargando reviews...</p>
        </div>
        <div v-else-if="moderadorStore.reviewsPendientes.length > 0" class="space-y-6">
          <ReviewCard
            v-for="review in moderadorStore.reviewsPendientes"
            :key="review.id"
            :review="review"
            @aprobar="moderadorStore.aprobarReview"
            @rechazar="moderadorStore.rechazarReview"
          />
          <div v-if="moderadorStore.totalReviewsPendientes > 10" class="text-sm text-gray-500 mt-4">Mostrando las primeras 10 reviews pendientes.</div>
        </div>
        <div v-else class="bg-white rounded-lg shadow p-8 text-center">
          <h3 class="text-lg font-medium text-gray-900 mb-2">No hay reviews pendientes</h3>
          <p class="text-gray-600">No se encontraron reviews pendientes de aprobación.</p>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="moderadorStore.loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Cargando solicitudes...</p>
      </div>

      <!-- Lista de solicitudes -->
      <div v-else-if="moderadorStore.hasSolicitudes" class="space-y-6">
        <ProductoModeracionCard
          v-for="solicitud in moderadorStore.solicitudes"
          :key="solicitud.id"
          :solicitud="solicitud"
          @ver-detalles="verDetalles"
          @aprobar="confirmarAprobar"
          @rechazar="mostrarModalRechazar"
          @solicitar-cambios="mostrarModalSolicitarCambios"
        />

        <!-- Paginación -->
        <div v-if="moderadorStore.totalPages > 1" class="bg-white rounded-lg shadow p-4">
          <div class="flex items-center justify-between">
            <div class="text-sm text-gray-700">
              Mostrando
              <span class="font-medium">{{ moderadorStore.solicitudes.length }}</span>
              de
              <span class="font-medium">{{ moderadorStore.totalSolicitudes }}</span>
              solicitudes
            </div>
            <div class="flex gap-2">
              <button
                @click="paginaAnterior"
                :disabled="moderadorStore.currentPage === 0"
                class="btn-pagination"
              >
                Anterior
              </button>
              <button
                @click="paginaSiguiente"
                :disabled="moderadorStore.currentPage >= moderadorStore.totalPages - 1"
                class="btn-pagination"
              >
                Siguiente
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Sin resultados -->
      <div v-else class="bg-white rounded-lg shadow p-12 text-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <h3 class="text-lg font-medium text-gray-900 mb-2">No hay solicitudes</h3>
        <p class="text-gray-600">
          No se encontraron solicitudes con el filtro seleccionado
        </p>
      </div>

      <!-- Enlace a historial de sanciones -->
      <div class="mb-8">
        <router-link to="/moderador/reportes-sanciones" class="inline-block bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition-colors">
          Ver historial de sanciones
        </router-link>
      </div>
    </div>

    <!-- Modal Rechazar -->
    <ModalRechazar
      :is-open="showModalRechazar"
      :solicitud-id="solicitudSeleccionada"
      @close="cerrarModalRechazar"
      @rechazar="handleRechazar"
    />

    <!-- Modal Solicitar Cambios -->
    <ModalSolicitarCambios
      :is-open="showModalSolicitarCambios"
      :solicitud-id="solicitudSeleccionada"
      @close="cerrarModalSolicitarCambios"
      @solicitar="handleSolicitarCambios"
    />

    <!-- Modal Detalles -->
    <DetalleProductoModal
      :is-open="showModalDetalles"
      :solicitud="solicitudActual"
      @close="cerrarModalDetalles"
      @aprobar="confirmarAprobar"
      @rechazar="mostrarModalRechazar"
      @solicitar-cambios="mostrarModalSolicitarCambios"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useModeradorStore } from '../../stores/moderador'
import NavBar from '../../components/layout/NavBar.vue'
import ProductoModeracionCard from '../../components/moderador/ProductoModeracionCard.vue'
import ModalRechazar from '../../components/moderador/ModalRechazar.vue'
import ModalSolicitarCambios from '../../components/moderador/ModalSolicitarCambios.vue'
import DetalleProductoModal from '../../components/moderador/DetalleProductoModal.vue'
import ReviewCard from '../../components/moderador/ReviewCard.vue'

const moderadorStore = useModeradorStore()

const showModalRechazar = ref(false)
const showModalSolicitarCambios = ref(false)
const showModalDetalles = ref(false)
const solicitudSeleccionada = ref(null)
const solicitudActual = ref(null)

const estados = [
  { value: 'PENDIENTE', label: 'Pendientes', activeClass: 'filter-btn-warning' },
  { value: 'APROBADO', label: 'Aprobadas', activeClass: 'filter-btn-success' },
  { value: 'RECHAZADO', label: 'Rechazadas', activeClass: 'filter-btn-danger' },
  { value: 'CAMBIOS_SOLICITADOS', label: 'Cambios Solicitados', activeClass: 'filter-btn-info' },
  { value: 'TODOS', label: 'Todas', activeClass: 'filter-btn-active' }
]

const filtrarPorEstado = (estado) => {
  moderadorStore.setEstadoFiltro(estado)
}

const paginaAnterior = () => {
  if (moderadorStore.currentPage > 0) {
    moderadorStore.changePage(moderadorStore.currentPage - 1)
  }
}

const paginaSiguiente = () => {
  if (moderadorStore.currentPage < moderadorStore.totalPages - 1) {
    moderadorStore.changePage(moderadorStore.currentPage + 1)
  }
}

const confirmarAprobar = async (id) => {
  if (confirm('¿Estás seguro de aprobar esta solicitud? El producto será publicado.')) {
    await moderadorStore.aprobarSolicitud(id)
  }
}

const mostrarModalRechazar = (id) => {
  solicitudSeleccionada.value = id
  showModalRechazar.value = true
}

const cerrarModalRechazar = () => {
  showModalRechazar.value = false
  solicitudSeleccionada.value = null
}

const handleRechazar = async (data) => {
  const success = await moderadorStore.rechazarSolicitud(data.id, data.motivo)
  if (success) {
    cerrarModalRechazar()
  }
}

const mostrarModalSolicitarCambios = (id) => {
  solicitudSeleccionada.value = id
  showModalSolicitarCambios.value = true
}

const cerrarModalSolicitarCambios = () => {
  showModalSolicitarCambios.value = false
  solicitudSeleccionada.value = null
}

const handleSolicitarCambios = async (data) => {
  const success = await moderadorStore.solicitarCambios(data.id, data.comentario)
  if (success) {
    cerrarModalSolicitarCambios()
  }
}

const verDetalles = async (solicitud) => {
  solicitudActual.value = solicitud
  await moderadorStore.fetchSolicitudById(solicitud.id)
  showModalDetalles.value = true
}

const cerrarModalDetalles = () => {
  showModalDetalles.value = false
  solicitudActual.value = null
  moderadorStore.resetSolicitudActual()
}

onMounted(async () => {
  await moderadorStore.fetchSolicitudes()
  await moderadorStore.fetchEstadisticas()
  await moderadorStore.fetchReviewsPendientes()
  console.log('Solicitudes cargadas:', moderadorStore.solicitudes)
  console.log('Reviews pendientes cargadas:', moderadorStore.reviewsPendientes)
})
</script>

<style scoped>
.stat-card {
  @apply bg-white rounded-lg shadow p-6 border-2 flex items-center gap-4;
}

.stat-icon {
  @apply p-3 rounded-lg;
}

.stat-content {
  @apply flex-1;
}

.stat-label {
  @apply text-sm font-medium text-gray-600 mb-1;
}

.stat-value {
  @apply text-2xl font-bold text-gray-900;
}

.filter-btn {
  @apply px-4 py-2 rounded-lg text-sm font-medium transition-colors;
}

.filter-btn-inactive {
  @apply bg-gray-100 text-gray-700 hover:bg-gray-200;
}

.filter-btn-active {
  @apply bg-blue-600 text-white;
}

.filter-btn-warning {
  @apply bg-yellow-600 text-white;
}

.filter-btn-success {
  @apply bg-green-600 text-white;
}

.filter-btn-danger {
  @apply bg-red-600 text-white;
}

.filter-btn-info {
  @apply bg-blue-600 text-white;
}

.btn-pagination {
  @apply px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors;
}
</style>