<template>
  <div class="aplicar-sancion-form bg-white rounded-lg shadow p-6 max-w-md mx-auto">
    <h2 class="text-xl font-bold mb-4">Aplicar sanción a usuario</h2>
    <form @submit.prevent="aplicarSancion">
      <div class="mb-4">
        <label class="block mb-1 font-semibold">Selecciona usuario</label>
        <select v-model="usuarioId" class="input w-full" required>
          <option value="" disabled>Selecciona un usuario...</option>
          <option v-for="usuario in usuarios" :key="usuario.id" :value="usuario.id">
            {{ usuario.nombreUsuario || usuario.nombreCompleto || usuario.email }}
          </option>
        </select>
      </div>
      <div class="mb-4">
        <label class="block mb-1 font-semibold">Motivo</label>
        <input v-model="razon" type="text" class="input w-full" placeholder="Motivo de la sanción" required />
      </div>
      <div class="mb-4">
        <label class="block mb-1 font-semibold">Fecha fin (opcional)</label>
        <input v-model="fechaFin" type="datetime-local" class="input w-full" />
      </div>
      <button type="submit" class="btn-primary w-full" :disabled="loading">
        {{ loading ? 'Aplicando...' : 'Aplicar sanción' }}
      </button>
      <div v-if="error" class="text-red-600 mt-2">{{ error }}</div>
      <div v-if="success" class="text-green-600 mt-2">Sanción aplicada correctamente</div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useModeradorStore } from '@/stores/moderador'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'

const usuarioId = ref('')
const razon = ref('')
const fechaFin = ref('')
const loading = ref(false)
const error = ref('')
const success = ref(false)
const usuarios = ref([])

const moderadorStore = useModeradorStore()
const authStore = useAuthStore()

onMounted(async () => {
  try {
    const res = await api.get('/api/moderador/usuarios')
    usuarios.value = res.data || []
  } catch (e) {
    error.value = 'No se pudieron cargar los usuarios.'
  }
})
async function aplicarSancion() {
  error.value = ''
  success.value = false
  loading.value = true
  try {
    const payload = {
      usuarioId: usuarioId.value,
      moderadorId: authStore.user?.id || authStore.user?.uuid,
      razon: razon.value
    }
    if (fechaFin.value) {
      payload.fechaFin = new Date(fechaFin.value).toISOString()
    }
    await moderadorStore.crearSancion(payload)
    success.value = true
    usuarioId.value = ''
    razon.value = ''
    fechaFin.value = ''
  } catch (e) {
    error.value = e.message || 'Error al aplicar sanción'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.input {
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 8px;
}
.btn-primary {
  background: #f59e42;
  color: white;
  padding: 10px;
  border-radius: 4px;
  font-weight: bold;
  border: none;
  cursor: pointer;
}
.btn-primary:disabled {
  background: #fbbf24;
  cursor: not-allowed;
}
</style>
