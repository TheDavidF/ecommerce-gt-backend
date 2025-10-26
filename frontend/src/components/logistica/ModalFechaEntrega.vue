<template>
  <Transition name="modal">
    <div
      v-if="modelValue"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
      @click.self="cerrar"
    >
      <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
        <!-- Header -->
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-xl font-bold text-gray-800">
            Modificar Fecha de Entrega
          </h2>
          <p v-if="pedido" class="text-sm text-gray-600 mt-1">
            Pedido: {{ pedido.numeroOrden }}
          </p>
        </div>

        <!-- Body -->
        <div class="px-6 py-4">
          <form @submit.prevent="guardar">
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Fecha y hora de entrega estimada
              </label>
              <input
                v-model="fechaSeleccionada"
                type="datetime-local"
                :min="fechaMinima"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
              />
            </div>

            <div
              v-if="pedido?.fechaEntregaEstimada"
              class="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-4"
            >
              <p class="text-sm text-blue-800">
                <strong>Fecha actual:</strong> {{ formatearFecha(pedido.fechaEntregaEstimada) }}
              </p>
            </div>

            <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-3 mb-4">
              <p class="text-sm text-red-800">{{ error }}</p>
            </div>
          </form>
        </div>

        <!-- Footer -->
        <div class="px-6 py-4 border-t border-gray-200 flex justify-end gap-3">
          <button
            @click="cerrar"
            :disabled="cargando"
            class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors font-medium disabled:opacity-50"
          >
            Cancelar
          </button>
          <button
            @click="guardar"
            :disabled="cargando || !fechaSeleccionada"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium disabled:opacity-50 flex items-center gap-2"
          >
            <span v-if="cargando" class="inline-block w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
            Guardar
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  pedido: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['update:modelValue', 'guardar']);

const fechaSeleccionada = ref('');
const cargando = ref(false);
const error = ref('');

const fechaMinima = computed(() => {
  const ahora = new Date();
  ahora.setHours(ahora.getHours() + 1);
  return ahora.toISOString().slice(0, 16);
});

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  try {
    const date = new Date(fecha);
    return date.toLocaleString('es-GT', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    return fecha;
  }
};

const cerrar = () => {
  if (!cargando.value) {
    emit('update:modelValue', false);
    fechaSeleccionada.value = '';
    error.value = '';
  }
};

const guardar = async () => {
  error.value = '';

  if (!fechaSeleccionada.value) {
    error.value = 'La fecha es requerida';
    return;
  }

  const fechaSelec = new Date(fechaSeleccionada.value);
  const ahora = new Date();

  if (fechaSelec <= ahora) {
    error.value = 'La fecha debe ser futura';
    return;
  }

  cargando.value = true;
  try {
    const fechaISO = fechaSelec.toISOString();
    emit('guardar', fechaISO);
    cerrar();
  } catch (err) {
    error.value = 'Error al guardar la fecha';
    console.error('Error al guardar fecha:', err);
  } finally {
    cargando.value = false;
  }
};

watch(() => props.modelValue, (isOpen) => {
  if (isOpen && props.pedido?.fechaEntregaEstimada) {
    try {
      const fecha = new Date(props.pedido.fechaEntregaEstimada);
      fechaSeleccionada.value = fecha.toISOString().slice(0, 16);
    } catch (err) {
      console.error('Error al parsear fecha:', err);
    }
  } else if (!isOpen) {
    fechaSeleccionada.value = '';
    error.value = '';
  }
});
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .bg-white,
.modal-leave-active .bg-white {
  transition: transform 0.3s ease;
}

.modal-enter-from .bg-white,
.modal-leave-to .bg-white {
  transform: scale(0.9);
}
</style>