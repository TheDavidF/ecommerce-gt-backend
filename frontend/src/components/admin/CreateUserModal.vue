<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="close">
    <div class="modal-container">
      <div class="modal-header">
        <h2>Crear Nuevo Usuario</h2>
        <button @click="close" class="btn-close">&times;</button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-body">
        <!-- Nombre de usuario -->
        <div class="form-group">
          <label for="nombreUsuario">Nombre de Usuario *</label>
          <input
            id="nombreUsuario"
            v-model="form.nombreUsuario"
            type="text"
            required
            minlength="3"
            maxlength="20"
            placeholder="Ej: moderador1"
            class="form-control"
          />
        </div>

        <!-- Correo -->
        <div class="form-group">
          <label for="correo">Correo Electrónico *</label>
          <input
            id="correo"
            v-model="form.correo"
            type="email"
            required
            placeholder="usuario@ecommerce.gt"
            class="form-control"
          />
        </div>

        <!-- Contraseña -->
        <div class="form-group">
          <label for="password">Contraseña *</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            required
            minlength="6"
            placeholder="Mínimo 6 caracteres"
            class="form-control"
          />
        </div>

        <!-- Nombre y Apellido -->
        <div class="form-row">
          <div class="form-group">
            <label for="nombre">Nombre *</label>
            <input
              id="nombre"
              v-model="form.nombre"
              type="text"
              required
              minlength="2"
              placeholder="Juan"
              class="form-control"
            />
          </div>

          <div class="form-group">
            <label for="apellido">Apellido *</label>
            <input
              id="apellido"
              v-model="form.apellido"
              type="text"
              required
              minlength="2"
              placeholder="Pérez"
              class="form-control"
            />
          </div>
        </div>

        <!-- Teléfono -->
        <div class="form-group">
          <label for="telefono">Teléfono</label>
          <input
            id="telefono"
            v-model="form.telefono"
            type="tel"
            pattern="[0-9]{8}"
            placeholder="12345678"
            class="form-control"
          />
          <small class="form-text">8 dígitos</small>
        </div>

        <!-- Rol -->
        <div class="form-group">
          <label for="rol">Rol *</label>
          <select
            id="rol"
            v-model="form.rol"
            required
            class="form-control"
          >
            <option value="">Seleccionar rol</option>
            <option value="COMUN">Usuario Común</option>
            <option value="MODERADOR">Moderador</option>
            <option value="LOGISTICA">Logística</option>
            <option value="ADMIN">Administrador</option>
          </select>
        </div>

        <!-- Botones -->
        <div class="modal-footer">
          <button
            type="button"
            @click="close"
            class="btn-secondary"
            :disabled="loading"
          >
            Cancelar
          </button>
          <button
            type="submit"
            class="btn-primary"
            :disabled="loading"
          >
            <span v-if="loading">Creando...</span>
            <span v-else>Crear Usuario</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useAdminStore } from '../../stores/admin'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'created'])

const adminStore = useAdminStore()
const loading = ref(false)

const form = ref({
  nombreUsuario: '',
  correo: '',
  password: '',
  nombre: '',
  apellido: '',
  telefono: '',
  rol: ''
})

const resetForm = () => {
  form.value = {
    nombreUsuario: '',
    correo: '',
    password: '',
    nombre: '',
    apellido: '',
    telefono: '',
    rol: ''
  }
}

const handleSubmit = async () => {
  loading.value = true
  try {
    await adminStore.crearUsuario(form.value)
    emit('created')
    close()
  } catch (error) {
    // Error manejado por el store
  } finally {
    loading.value = false
  }
}

const close = () => {
  resetForm()
  emit('close')
}

watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    resetForm()
  }
})
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

.modal-container {
  background: white;
  border-radius: 0.5rem;
  max-width: 600px;
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

.form-group {
  margin-bottom: 1.25rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #374151;
  font-size: 0.875rem;
}

.form-control {
  width: 100%;
  padding: 0.625rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-text {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: #6b7280;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
  margin-top: 1.5rem;
}

.btn-primary,
.btn-secondary {
  padding: 0.625rem 1.25rem;
  border-radius: 0.375rem;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn-secondary:hover:not(:disabled) {
  background: #f9fafb;
}

@media (max-width: 640px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>