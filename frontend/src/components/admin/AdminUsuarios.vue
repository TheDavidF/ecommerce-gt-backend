<template>
  <div class="min-h-screen bg-gray-100">
    <NavBar />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="flex justify-between items-center mb-8">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Gestión de Usuarios</h1>
          <p class="text-gray-600 mt-2">
            {{ adminStore.totalUsuarios }} usuario{{ adminStore.totalUsuarios !== 1 ? 's' : '' }} registrado{{ adminStore.totalUsuarios !== 1 ? 's' : '' }}
          </p>
        </div>
        <button
          @click="openCreateModal"
          class="btn-primary flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
          </svg>
          Crear Usuario
        </button>
      </div>

      <!-- Filtros -->
      <div class="bg-white rounded-lg shadow p-4 mb-6">
        <div class="flex flex-wrap gap-4">
          <!-- Filtro por rol -->
          <div class="flex-1 min-w-[200px]">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Filtrar por Rol
            </label>
            <select
              v-model="selectedRol"
              @change="filterByRol"
              class="form-control"
            >
              <option :value="null">Todos los roles</option>
              <option value="COMUN">Usuario Común</option>
              <option value="MODERADOR">Moderador</option>
              <option value="LOGISTICA">Logística</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </div>

          <!-- Botón limpiar filtros -->
          <div class="flex items-end">
            <button
              @click="resetFilters"
              class="btn-secondary"
            >
              Limpiar Filtros
            </button>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="adminStore.loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Cargando usuarios...</p>
      </div>

      <!-- Tabla de usuarios -->
      <div v-else-if="adminStore.hasUsuarios" class="bg-white rounded-lg shadow overflow-hidden">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="table-header">Usuario</th>
                <th class="table-header">Correo</th>
                <th class="table-header">Nombre Completo</th>
                <th class="table-header">Roles</th>
                <th class="table-header">Estado</th>
                <th class="table-header">Fecha Creación</th>
                <th class="table-header">Acciones</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="usuario in adminStore.usuarios" :key="usuario.id">
                <td class="table-cell">
                  <div class="font-medium text-gray-900">
                    {{ usuario.nombreUsuario }}
                  </div>
                </td>
                <td class="table-cell">
                  <div class="text-gray-600">{{ usuario.correo }}</div>
                </td>
                <td class="table-cell">
                  <div class="text-gray-900">{{ usuario.nombreCompleto }}</div>
                </td>
                <td class="table-cell">
                  <div class="flex flex-wrap gap-1">
                    <span
                      v-for="rol in usuario.roles"
                      :key="rol"
                      :class="getRolClass(rol)"
                      class="badge"
                    >
                      {{ getRolLabel(rol) }}
                    </span>
                  </div>
                </td>
                <td class="table-cell">
                  <span
                    :class="usuario.activo ? 'badge-success' : 'badge-danger'"
                    class="badge"
                  >
                    {{ usuario.activo ? 'Activo' : 'Inactivo' }}
                  </span>
                </td>
                <td class="table-cell">
                  <div class="text-gray-600 text-sm">
                    {{ formatDate(usuario.fechaCreacion) }}
                  </div>
                </td>
                <td class="table-cell">
                  <div class="flex gap-2">
                    <button
                      @click="toggleEstado(usuario)"
                      :title="usuario.activo ? 'Desactivar' : 'Activar'"
                      class="btn-icon"
                    >
                      <svg v-if="usuario.activo" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M13.477 14.89A6 6 0 015.11 6.524l8.367 8.368zm1.414-1.414L6.524 5.11a6 6 0 018.367 8.367zM18 10a8 8 0 11-16 0 8 8 0 0116 0z" clip-rule="evenodd" />
                      </svg>
                      <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Paginación -->
        <div class="bg-gray-50 px-4 py-3 border-t border-gray-200">
          <div class="flex items-center justify-between">
            <div class="text-sm text-gray-700">
              Mostrando
              <span class="font-medium">{{ adminStore.usuarios.length }}</span>
              de
              <span class="font-medium">{{ adminStore.totalUsuarios }}</span>
              usuarios
            </div>
            <div class="flex gap-2">
              <button
                @click="previousPage"
                :disabled="adminStore.currentPage === 0"
                class="btn-pagination"
              >
                Anterior
              </button>
              <button
                @click="nextPage"
                :disabled="adminStore.currentPage >= adminStore.totalPages - 1"
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
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
        <h3 class="text-lg font-medium text-gray-900 mb-2">No hay usuarios</h3>
        <p class="text-gray-600">Crea el primer usuario para comenzar</p>
      </div>
    </div>

    <!-- Modal Crear Usuario -->
    <CreateUserModal
      :is-open="showCreateModal"
      @close="closeCreateModal"
      @created="handleUserCreated"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAdminStore } from '../../stores/admin'
import NavBar from '../../components/layout/NavBar.vue'
import CreateUserModal from '../../components/admin/CreateUserModal.vue'

const adminStore = useAdminStore()
const showCreateModal = ref(false)
const selectedRol = ref(null)

const openCreateModal = () => {
  showCreateModal.value = true
}

const closeCreateModal = () => {
  showCreateModal.value = false
}

const handleUserCreated = () => {
  adminStore.fetchUsuarios()
}

const filterByRol = () => {
  adminStore.setFilter('rol', selectedRol.value)
}

const resetFilters = () => {
  selectedRol.value = null
  adminStore.resetFilters()
}

const toggleEstado = async (usuario) => {
  const accion = usuario.activo ? 'desactivar' : 'activar'
  if (confirm(`¿Estás seguro de ${accion} a ${usuario.nombreUsuario}?`)) {
    await adminStore.toggleEstadoUsuario(usuario.id, usuario.activo)
  }
}

const previousPage = () => {
  if (adminStore.currentPage > 0) {
    adminStore.changePage(adminStore.currentPage - 1)
  }
}

const nextPage = () => {
  if (adminStore.currentPage < adminStore.totalPages - 1) {
    adminStore.changePage(adminStore.currentPage + 1)
  }
}

const getRolClass = (rol) => {
  const classes = {
    'ADMIN': 'badge-danger',
    'MODERADOR': 'badge-warning',
    'LOGISTICA': 'badge-info',
    'COMUN': 'badge-secondary'
  }
  return classes[rol] || 'badge-secondary'
}

const getRolLabel = (rol) => {
  const labels = {
    'ADMIN': 'Admin',
    'MODERADOR': 'Moderador',
    'LOGISTICA': 'Logística',
    'COMUN': 'Usuario'
  }
  return labels[rol] || rol
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('es-GT', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

onMounted(() => {
  adminStore.fetchUsuarios()
})
</script>

<style scoped>
.btn-primary {
  @apply bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition-colors;
}

.btn-secondary {
  @apply bg-white text-gray-700 px-4 py-2 rounded-lg font-medium border border-gray-300 hover:bg-gray-50 transition-colors;
}

.btn-icon {
  @apply p-2 rounded-lg text-gray-600 hover:bg-gray-100 transition-colors;
}

.form-control {
  @apply w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent;
}

.table-header {
  @apply px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider;
}

.table-cell {
  @apply px-6 py-4 whitespace-nowrap;
}

.badge {
  @apply inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium;
}

.badge-success {
  @apply bg-green-100 text-green-800;
}

.badge-danger {
  @apply bg-red-100 text-red-800;
}

.badge-warning {
  @apply bg-yellow-100 text-yellow-800;
}

.badge-info {
  @apply bg-blue-100 text-blue-800;
}

.badge-secondary {
  @apply bg-gray-100 text-gray-800;
}

.btn-pagination {
  @apply px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed;
}
</style>