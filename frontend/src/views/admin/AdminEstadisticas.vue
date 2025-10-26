<template>
  <div class="min-h-screen bg-gray-100">
    <NavBar />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Dashboard de Administración</h1>
        <p class="text-gray-600">
          Resumen general del sistema y métricas clave
        </p>
      </div>

      <!-- Loading State -->
      <div v-if="estadisticasStore.loading && !estadisticasStore.hasData" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Cargando estadísticas...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="estadisticasStore.error" class="bg-red-50 border border-red-200 rounded-lg p-6">
        <div class="flex items-center gap-3">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <div>
            <h3 class="font-semibold text-red-900">Error al cargar estadísticas</h3>
            <p class="text-sm text-red-700">{{ estadisticasStore.error }}</p>
          </div>
        </div>
        <button
          @click="cargarEstadisticas"
          class="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
        >
          Reintentar
        </button>
      </div>

      <!-- Dashboard Content -->
      <div v-else>
        <!-- Tarjetas de Estadísticas Principales -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <EstadisticaCard
            label="Total Usuarios"
            :value="stats.totalUsuarios"
            icon="users"
            color="blue"
            :subtitle="`${stats.usuariosActivos} activos`"
          />
          
          <EstadisticaCard
            label="Total Productos"
            :value="stats.totalProductos"
            icon="products"
            color="green"
            :subtitle="`${stats.productosPendientes} pendientes`"
          />
          
          <EstadisticaCard
            label="Total Pedidos"
            :value="stats.totalPedidos"
            icon="orders"
            color="purple"
            :subtitle="`${stats.pedidosPendientes} en proceso`"
          />
          
          <EstadisticaCard
            label="Ventas Totales"
            :value="stats.totalVentas"
            icon="sales"
            color="yellow"
            is-currency
            :subtitle="`Q${stats.ventasMes} este mes`"
          />
        </div>

        <!-- Gráficos -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <!-- Gráfico de Ventas -->
          <VentasChart
            title="Ventas del Mes"
            :labels="ventasLabels"
            :data="ventasData"
            periodo-actual="mes"
            :loading="estadisticasStore.loading"
            @cambiar-periodo="cambiarPeriodoVentas"
          />

          <!-- Productos Populares -->
          <div class="bg-white rounded-lg shadow-lg p-6">
            <h3 class="text-xl font-bold text-gray-900 mb-4">
              Productos Más Vendidos
            </h3>
            <div v-if="stats.productosPopulares && stats.productosPopulares.length > 0" class="space-y-4">
              <div
                v-for="(producto, index) in stats.productosPopulares.slice(0, 5)"
                :key="producto.id"
                class="producto-item"
              >
                <div class="flex items-center gap-4">
                  <div class="rank">{{ index + 1 }}</div>
                  <img
                    v-if="producto.imagenUrl"
                    :src="producto.imagenUrl"
                    :alt="producto.nombre"
                    class="producto-img"
                  />
                  <div class="flex-1 min-w-0">
                    <p class="producto-nombre">{{ producto.nombre }}</p>
                    <p class="producto-ventas">{{ producto.cantidadVendida }} vendidos</p>
                  </div>
                  <div class="producto-precio">
                    Q{{ formatPrice(producto.precio) }}
                  </div>
                </div>
                <div class="progress-bar">
                  <div
                    class="progress-fill"
                    :style="{ width: calcularPorcentaje(producto.cantidadVendida, stats.productosPopulares[0].cantidadVendida) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-8 text-gray-500">
              No hay datos de productos populares
            </div>
          </div>
        </div>

        <!-- Usuarios por Rol y Actividad Reciente -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <!-- Usuarios por Rol -->
          <div class="bg-white rounded-lg shadow-lg p-6">
            <h3 class="text-xl font-bold text-gray-900 mb-4">
              Usuarios por Rol
            </h3>
            <div v-if="stats.usuariosPorRol && stats.usuariosPorRol.length > 0" class="space-y-3">
              <div
                v-for="rol in stats.usuariosPorRol"
                :key="rol.rol"
                class="rol-item"
              >
                <div class="flex items-center justify-between mb-2">
                  <span class="rol-nombre">{{ formatRol(rol.rol) }}</span>
                  <span class="rol-cantidad">{{ rol.cantidad }} usuarios</span>
                </div>
                <div class="progress-bar">
                  <div
                    class="progress-fill"
                    :class="getRolColor(rol.rol)"
                    :style="{ width: calcularPorcentaje(rol.cantidad, stats.totalUsuarios) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-8 text-gray-500">
              No hay datos de usuarios por rol
            </div>
          </div>

          <!-- Métricas Adicionales -->
          <div class="bg-white rounded-lg shadow-lg p-6">
            <h3 class="text-xl font-bold text-gray-900 mb-4">
              Métricas del Sistema
            </h3>
            <div class="space-y-4">
              <div class="metrica-item">
                <div class="flex items-center justify-between">
                  <span class="metrica-label">Tasa de Conversión</span>
                  <span class="metrica-value text-green-600">
                    {{ calcularTasaConversion() }}%
                  </span>
                </div>
              </div>
              
              <div class="metrica-item">
                <div class="flex items-center justify-between">
                  <span class="metrica-label">Valor Promedio del Pedido</span>
                  <span class="metrica-value text-blue-600">
                    Q{{ calcularPromedioVenta() }}
                  </span>
                </div>
              </div>
              
              <div class="metrica-item">
                <div class="flex items-center justify-between">
                  <span class="metrica-label">Productos Activos</span>
                  <span class="metrica-value text-purple-600">
                    {{ stats.totalProductos - stats.productosPendientes }}
                  </span>
                </div>
              </div>
              
              <div class="metrica-item">
                <div class="flex items-center justify-between">
                  <span class="metrica-label">Usuarios Registrados Hoy</span>
                  <span class="metrica-value text-indigo-600">
                    {{ stats.usuariosHoy || 0 }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Botón de actualizar -->
        <div class="mt-8 text-center">
          <button
            @click="cargarEstadisticas"
            :disabled="estadisticasStore.loading"
            class="btn-refresh"
          >
            <svg v-if="!estadisticasStore.loading" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd" />
            </svg>
            <div v-else class="spinner-small mr-2"></div>
            {{ estadisticasStore.loading ? 'Actualizando...' : 'Actualizar Datos' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useEstadisticasStore } from '../../stores/estadisticas'
import NavBar from '../../components/layout/NavBar.vue'
import EstadisticaCard from '../../components/admin/EstadisticaCard.vue'
import VentasChart from '../../components/admin/VentasChart.vue'

const estadisticasStore = useEstadisticasStore()

const stats = computed(() => estadisticasStore.estadisticas)

const ventasLabels = computed(() => {
  if (stats.value.ventasDelMes && stats.value.ventasDelMes.length > 0) {
    return stats.value.ventasDelMes.map(v => v.fecha || v.dia)
  }
  return ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom']
})

const ventasData = computed(() => {
  if (stats.value.ventasDelMes && stats.value.ventasDelMes.length > 0) {
    return stats.value.ventasDelMes.map(v => v.total || 0)
  }
  return [0, 0, 0, 0, 0, 0, 0]
})

const formatPrice = (price) => {
  return parseFloat(price || 0).toFixed(2)
}

const formatRol = (rol) => {
  const roles = {
    'ADMIN': 'Administradores',
    'MODERADOR': 'Moderadores',
    'LOGISTICA': 'Logística',
    'COMUN': 'Usuarios Comunes'
  }
  return roles[rol] || rol
}

const getRolColor = (rol) => {
  const colors = {
    'ADMIN': 'bg-red-500',
    'MODERADOR': 'bg-blue-500',
    'LOGISTICA': 'bg-green-500',
    'COMUN': 'bg-gray-500'
  }
  return colors[rol] || 'bg-gray-500'
}

const calcularPorcentaje = (valor, total) => {
  if (!total || total === 0) return 0
  return Math.round((valor / total) * 100)
}

const calcularTasaConversion = () => {
  if (stats.value.totalUsuarios === 0) return 0
  const tasa = (stats.value.totalPedidos / stats.value.totalUsuarios) * 100
  return tasa.toFixed(2)
}

const calcularPromedioVenta = () => {
  if (stats.value.totalPedidos === 0) return '0.00'
  const promedio = stats.value.totalVentas / stats.value.totalPedidos
  return promedio.toFixed(2)
}

const cambiarPeriodoVentas = (periodo) => {
  console.log('Cambiar periodo a:', periodo)
  // TODO: Implementar cambio de periodo cuando el backend lo soporte
}

const cargarEstadisticas = async () => {
  await estadisticasStore.fetchEstadisticas()
}

onMounted(() => {
  cargarEstadisticas()
})
</script>

<style scoped>
.producto-item {
  @apply border-b border-gray-100 pb-4 last:border-b-0;
}

.rank {
  @apply w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-blue-600 text-white flex items-center justify-center font-bold text-sm flex-shrink-0;
}

.producto-img {
  @apply w-12 h-12 object-cover rounded-lg flex-shrink-0;
}

.producto-nombre {
  @apply font-medium text-gray-900 truncate;
}

.producto-ventas {
  @apply text-sm text-gray-500;
}

.producto-precio {
  @apply text-lg font-bold text-gray-900;
}

.progress-bar {
  @apply w-full h-2 bg-gray-200 rounded-full overflow-hidden mt-2;
}

.progress-fill {
  @apply h-full bg-gradient-to-r from-blue-500 to-blue-600 transition-all duration-500;
}

.rol-item {
  @apply py-2;
}

.rol-nombre {
  @apply font-medium text-gray-900;
}

.rol-cantidad {
  @apply text-sm text-gray-600;
}

.metrica-item {
  @apply py-3 border-b border-gray-100 last:border-b-0;
}

.metrica-label {
  @apply text-sm font-medium text-gray-600;
}

.metrica-value {
  @apply text-lg font-bold;
}

.btn-refresh {
  @apply flex items-center justify-center px-6 py-3 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed mx-auto;
}

.spinner-small {
  @apply w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin;
}
</style>