<template>
  <v-dialog v-model="dialogVisible" max-width="500px" persistent>
    <v-card>
      <v-card-title class="text-h5">
        <v-icon class="mr-2">mdi-calendar-edit</v-icon>
        Modificar Fecha de Entrega
      </v-card-title>

      <v-card-subtitle v-if="pedido" class="mt-2">
        Pedido: {{ pedido.numeroOrden }}
      </v-card-subtitle>

      <v-card-text>
        <v-form ref="formRef" v-model="formularioValido">
          <v-text-field
            v-model="fechaSeleccionada"
            label="Fecha y hora de entrega estimada"
            type="datetime-local"
            :rules="[reglasValidacion.requerido, reglasValidacion.fechaFutura]"
            variant="outlined"
            prepend-icon="mdi-calendar-clock"
            :min="fechaMinima"
            required
          ></v-text-field>

          <v-alert
            v-if="pedido?.fechaEntregaEstimada"
            type="info"
            variant="tonal"
            density="compact"
            class="mb-3"
          >
            <strong>Fecha actual:</strong> {{ formatearFecha(pedido.fechaEntregaEstimada) }}
          </v-alert>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="grey"
          variant="text"
          @click="cerrar"
          :disabled="cargando"
        >
          Cancelar
        </v-btn>
        <v-btn
          color="primary"
          variant="elevated"
          @click="guardar"
          :loading="cargando"
          :disabled="!formularioValido"
        >
          Guardar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { format, parseISO } from 'date-fns';
import { es } from 'date-fns/locale';

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

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
});

const formRef = ref(null);
const formularioValido = ref(false);
const fechaSeleccionada = ref('');
const cargando = ref(false);

// Fecha mínima: ahora + 1 hora
const fechaMinima = computed(() => {
  const ahora = new Date();
  ahora.setHours(ahora.getHours() + 1);
  return ahora.toISOString().slice(0, 16);
});

// Reglas de validación
const reglasValidacion = {
  requerido: (value) => !!value || 'La fecha es requerida',
  fechaFutura: (value) => {
    if (!value) return true;
    const fechaSelec = new Date(value);
    const ahora = new Date();
    return fechaSelec > ahora || 'La fecha debe ser futura';
  }
};

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  try {
    return format(parseISO(fecha), "dd/MM/yyyy HH:mm", { locale: es });
  } catch (error) {
    return fecha;
  }
};

const cerrar = () => {
  dialogVisible.value = false;
  fechaSeleccionada.value = '';
};

const guardar = async () => {
  const { valid } = await formRef.value.validate();
  if (!valid) return;

  cargando.value = true;
  try {
    // Convertir a formato ISO para el backend
    const fechaISO = new Date(fechaSeleccionada.value).toISOString();
    emit('guardar', fechaISO);
    cerrar();
  } catch (error) {
    console.error('Error al guardar fecha:', error);
  } finally {
    cargando.value = false;
  }
};

// Cargar fecha actual del pedido cuando se abre el modal
watch(() => props.modelValue, (isOpen) => {
  if (isOpen && props.pedido?.fechaEntregaEstimada) {
    try {
      const fecha = new Date(props.pedido.fechaEntregaEstimada);
      fechaSeleccionada.value = fecha.toISOString().slice(0, 16);
    } catch (error) {
      console.error('Error al parsear fecha:', error);
    }
  } else if (!isOpen) {
    fechaSeleccionada.value = '';
  }
});
</script>

<style scoped>
/* Estilos adicionales si son necesarios */
</style>