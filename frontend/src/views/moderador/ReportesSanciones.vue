<template>
  <div class="sanciones-panel">
    <h2 class="text-2xl font-bold mb-4">Historial de sanciones</h2>
    <div v-if="moderadorStore.loadingSanciones" class="text-center py-8">
      <div class="inline-block animate-spin rounded-full h-10 w-10 border-b-2 border-yellow-600"></div>
      <p class="mt-2 text-gray-600">Cargando sanciones...</p>
    </div>
    <div v-else-if="moderadorStore.sanciones.length > 0">
      <table class="min-w-full bg-white rounded-lg shadow">
        <thead>
          <tr>
            <th>Usuario</th>
            <th>Moderador</th>
            <th>Motivo</th>
            <th>Fecha inicio</th>
            <th>Fecha fin</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="sancion in moderadorStore.sanciones" :key="sancion.id">
            <td>{{ sancion.usuarioNombre || sancion.usuario?.nombreUsuario || sancion.usuario?.nombre || 'N/A' }}</td>
            <td>{{ sancion.moderadorNombre || sancion.moderador?.nombreUsuario || sancion.moderador?.nombre || 'N/A' }}</td>
            <td>{{ sancion.razon }}</td>
            <td>{{ formatDate(sancion.fechaInicio) }}</td>
            <td>{{ sancion.fechaFin ? formatDate(sancion.fechaFin) : 'Indefinida' }}</td>
            <td>
              <span :class="sancion.activa ? 'text-green-600' : 'text-gray-500'">
                {{ sancion.activa ? 'Activa' : 'Inactiva' }}
              </span>
            </td>
            <td>
              <button v-if="sancion.activa" @click="desactivar(sancion.id)" class="btn-danger">Desactivar</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else class="bg-white rounded-lg shadow p-8 text-center">
      <h3 class="text-lg font-medium text-gray-900 mb-2">No hay sanciones registradas</h3>
      <p class="text-gray-600">Aún no se han aplicado sanciones.</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useModeradorStore } from '../../stores/moderador'

const moderadorStore = useModeradorStore()

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

const desactivar = async (id) => {
  await moderadorStore.desactivarSancion(id)
}

onMounted(() => {
  moderadorStore.fetchSanciones()
})
</script>

<style scoped>
.sanciones-panel {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 1rem;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 0.75rem;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
}
th {
  background: #f9fafb;
  font-weight: 600;
}
.btn-danger {
  background: #dc2626;
  color: #fff;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-danger:hover {
  background: #b91c1c;
}
</style>
