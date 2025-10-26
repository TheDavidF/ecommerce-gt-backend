<script setup>
import { ref, computed, onMounted } from 'vue'
import { useReportesStore } from '@/stores/reportes'
import { storeToRefs } from 'pinia'

const reportesStore = useReportesStore()
const {
  productosMasVendidos,
  clientesPorGanancias,
  clientesPorVentas,
  clientesPorPedidos,
  clientesPorProductos,
  loading,
  error
} = storeToRefs(reportesStore)

const reporteActivo = ref('productos')

const fechaInicio = ref('')
const fechaFin = ref('')

const fechasValidas = computed(() => {
  return fechaInicio.value && fechaFin.value
})

onMounted(() => {
  const hoy = new Date()
  const hace30Dias = new Date()
  hace30Dias.setDate(hoy.getDate() - 30)
  
  fechaInicio.value = hace30Dias.toISOString().split('T')[0]
  fechaFin.value = hoy.toISOString().split('T')[0]
  
  cargarReporte()
})

async function cargarReporte() {
  if (!fechasValidas.value && reporteActivo.value !== 'productosVenta') {
    return
  }

  switch (reporteActivo.value) {
    case 'productos':
      await reportesStore.fetchProductosMasVendidos(fechaInicio.value, fechaFin.value)
      break
    case 'ganancias':
      await reportesStore.fetchClientesPorGanancias(fechaInicio.value, fechaFin.value)
      break
    case 'ventas':
      await reportesStore.fetchClientesPorVentas(fechaInicio.value, fechaFin.value)
      break
    case 'pedidos':
      await reportesStore.fetchClientesPorPedidos(fechaInicio.value, fechaFin.value)
      break
    case 'productosVenta':
      await reportesStore.fetchClientesPorProductos()
      break
  }
}

function cambiarReporte(nuevoReporte) {
  reporteActivo.value = nuevoReporte
  cargarReporte()
}

function formatearMoneda(valor) {
  return new Intl.NumberFormat('es-GT', {
    style: 'currency',
    currency: 'GTQ'
  }).format(valor)
}
</script>

<template>
  <div class="admin-reportes">
    <div class="header">
      <h1>Reportes</h1>
      <p class="subtitle">Analisis y estadisticas del sistema</p>
    </div>

    <div class="filtros-fecha" v-if="reporteActivo !== 'productosVenta'">
      <div class="fecha-grupo">
        <label for="fechaInicio">Fecha Inicio:</label>
        <input
          type="date"
          id="fechaInicio"
          v-model="fechaInicio"
          @change="cargarReporte"
        />
      </div>

      <div class="fecha-grupo">
        <label for="fechaFin">Fecha Fin:</label>
        <input
          type="date"
          id="fechaFin"
          v-model="fechaFin"
          @change="cargarReporte"
        />
      </div>

      <button class="btn-aplicar" @click="cargarReporte">
        Aplicar Filtros
      </button>
    </div>

    <div class="tabs">
      <button
        :class="['tab', { active: reporteActivo === 'productos' }]"
        @click="cambiarReporte('productos')"
      >
        Top 10 Productos
      </button>
      <button
        :class="['tab', { active: reporteActivo === 'ganancias' }]"
        @click="cambiarReporte('ganancias')"
      >
        Top 5 Clientes (Ganancias)
      </button>
      <button
        :class="['tab', { active: reporteActivo === 'ventas' }]"
        @click="cambiarReporte('ventas')"
      >
        Top 5 Vendedores
      </button>
      <button
        :class="['tab', { active: reporteActivo === 'pedidos' }]"
        @click="cambiarReporte('pedidos')"
      >
        Top 10 Pedidos
      </button>
      <button
        :class="['tab', { active: reporteActivo === 'productosVenta' }]"
        @click="cambiarReporte('productosVenta')"
      >
        Top 10 Productos en Venta
      </button>
    </div>

    <div v-if="loading" class="loading">
      <p>Cargando reporte...</p>
    </div>

    <div v-else-if="error" class="error">
      <p>{{ error }}</p>
    </div>

    <div v-else class="reporte-contenido">
      <div v-if="reporteActivo === 'productos'" class="tabla-wrapper">
        <h2>Top 10 Productos Mas Vendidos</h2>
        <table class="tabla-reporte">
          <thead>
            <tr>
              <th>#</th>
              <th>Producto</th>
              <th>Cantidad Vendida</th>
              <th>Ingresos Totales</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(producto, index) in productosMasVendidos" :key="producto.productoId">
              <td>{{ index + 1 }}</td>
              <td>{{ producto.nombreProducto }}</td>
              <td>{{ producto.totalVendido }}</td>
              <td>{{ formatearMoneda(producto.ingresosTotales) }}</td>
            </tr>
          </tbody>
        </table>
        <p v-if="productosMasVendidos.length === 0" class="sin-datos">
          No hay datos para mostrar
        </p>
      </div>

      <div v-if="reporteActivo === 'ganancias'" class="tabla-wrapper">
        <h2>Top 5 Clientes por Ganancias Generadas</h2>
        <table class="tabla-reporte">
          <thead>
            <tr>
              <th>#</th>
              <th>Cliente</th>
              <th>Total Gastado</th>
              <th>Cantidad Pedidos</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(cliente, index) in clientesPorGanancias" :key="cliente.usuarioId">
              <td>{{ index + 1 }}</td>
              <td>{{ cliente.nombreCompleto }}</td>
              <td>{{ formatearMoneda(cliente.totalGastado) }}</td>
              <td>{{ cliente.cantidadPedidos }}</td>
            </tr>
          </tbody>
        </table>
        <p v-if="clientesPorGanancias.length === 0" class="sin-datos">
          No hay datos para mostrar
        </p>
      </div>

      <div v-if="reporteActivo === 'ventas'" class="tabla-wrapper">
        <h2>Top 5 Clientes que Mas Han Vendido</h2>
        <table class="tabla-reporte">
          <thead>
            <tr>
              <th>#</th>
              <th>Vendedor</th>
              <th>Productos Vendidos</th>
              <th>Ingresos Generados</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(cliente, index) in clientesPorVentas" :key="cliente.usuarioId">
              <td>{{ index + 1 }}</td>
              <td>{{ cliente.nombreCompleto }}</td>
              <td>{{ cliente.totalProductosVendidos }}</td>
              <td>{{ formatearMoneda(cliente.ingresosGenerados) }}</td>
            </tr>
          </tbody>
        </table>
        <p v-if="clientesPorVentas.length === 0" class="sin-datos">
          No hay datos para mostrar
        </p>
      </div>

      <div v-if="reporteActivo === 'pedidos'" class="tabla-wrapper">
        <h2>Top 10 Clientes con Mas Pedidos</h2>
        <table class="tabla-reporte">
          <thead>
            <tr>
              <th>#</th>
              <th>Cliente</th>
              <th>Cantidad Pedidos</th>
              <th>Total Gastado</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(cliente, index) in clientesPorPedidos" :key="cliente.usuarioId">
              <td>{{ index + 1 }}</td>
              <td>{{ cliente.nombreCompleto }}</td>
              <td>{{ cliente.cantidadPedidos }}</td>
              <td>{{ formatearMoneda(cliente.totalGastado) }}</td>
            </tr>
          </tbody>
        </table>
        <p v-if="clientesPorPedidos.length === 0" class="sin-datos">
          No hay datos para mostrar
        </p>
      </div>

      <div v-if="reporteActivo === 'productosVenta'" class="tabla-wrapper">
        <h2>Top 10 Clientes con Mas Productos a la Venta</h2>
        <table class="tabla-reporte">
          <thead>
            <tr>
              <th>#</th>
              <th>Cliente</th>
              <th>Total Productos</th>
              <th>Productos Aprobados</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(cliente, index) in clientesPorProductos" :key="cliente.usuarioId">
              <td>{{ index + 1 }}</td>
              <td>{{ cliente.nombreCompleto }}</td>
              <td>{{ cliente.cantidadProductos }}</td>
              <td>{{ cliente.productosAprobados }}</td>
            </tr>
          </tbody>
        </table>
        <p v-if="clientesPorProductos.length === 0" class="sin-datos">
          No hay datos para mostrar
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-reportes {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  margin-bottom: 2rem;
}

.header h1 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
}

.subtitle {
  color: #7f8c8d;
  margin: 0;
}

.filtros-fecha {
  display: flex;
  gap: 1rem;
  align-items: flex-end;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.fecha-grupo {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.fecha-grupo label {
  font-weight: 500;
  color: #2c3e50;
  font-size: 0.9rem;
}

.fecha-grupo input {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.btn-aplicar {
  padding: 0.75rem 1.5rem;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.3s;
}

.btn-aplicar:hover {
  background: #2980b9;
}

.tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.tab {
  padding: 0.75rem 1.5rem;
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  color: #555;
  transition: all 0.3s;
}

.tab:hover {
  border-color: #3498db;
  color: #3498db;
}

.tab.active {
  background: #3498db;
  border-color: #3498db;
  color: white;
}

.loading,
.error {
  padding: 2rem;
  text-align: center;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.error {
  color: #e74c3c;
}

.reporte-contenido {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.tabla-wrapper {
  padding: 2rem;
}

.tabla-wrapper h2 {
  margin: 0 0 1.5rem 0;
  color: #2c3e50;
}

.tabla-reporte {
  width: 100%;
  border-collapse: collapse;
}

.tabla-reporte thead {
  background: #f8f9fa;
}

.tabla-reporte th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #e0e0e0;
}

.tabla-reporte td {
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
  color: #555;
}

.tabla-reporte tbody tr:hover {
  background: #f8f9fa;
}

.tabla-reporte tbody tr:last-child td {
  border-bottom: none;
}

.sin-datos {
  text-align: center;
  padding: 2rem;
  color: #7f8c8d;
}

@media (max-width: 768px) {
  .filtros-fecha {
    flex-direction: column;
    align-items: stretch;
  }

  .tabs {
    flex-direction: column;
  }

  .tabla-reporte {
    font-size: 0.9rem;
  }

  .tabla-reporte th,
  .tabla-reporte td {
    padding: 0.75rem 0.5rem;
  }
}
</style>