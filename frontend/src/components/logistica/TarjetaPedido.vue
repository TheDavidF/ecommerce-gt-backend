<template>
  <v-card class="mb-3" elevation="2">
    <v-card-title class="d-flex justify-space-between align-center">
      <div>
        <v-chip :color="obtenerColorEstado(pedido.estado)" size="small" class="mr-2">
          {{ pedido.estado }}
        </v-chip>
        <span class="text-h6">{{ pedido.numeroOrden }}</span>
      </div>
      <v-chip color="primary" variant="outlined">
        Q{{ pedido.total.toFixed(2) }}
      </v-chip>
    </v-card-title>

    <v-card-text>
      <v-row>
        <!-- Información del cliente -->
        <v-col cols="12" md="6">
          <div class="mb-2">
            <v-icon size="small" class="mr-2">mdi-account</v-icon>
            <strong>Cliente:</strong> {{ pedido.usuarioNombre }}
          </div>
          <div class="mb-2">
            <v-icon size="small" class="mr-2">mdi-email</v-icon>
            {{ pedido.usuarioEmail }}
          </div>
          <div class="mb-2">
            <v-icon size="small" class="mr-2">mdi-phone</v-icon>
            {{ pedido.telefonoContacto }}
          </div>
          <div class="mb-2">
            <v-icon size="small" class="mr-2">mdi-map-marker</v-icon>
            {{ pedido.direccionEnvio }}
          </div>
        </v-col>

        <!-- Fechas -->
        <v-col cols="12" md="6">
          <div class="mb-2">
            <v-icon size="small" class="mr-2">mdi-calendar</v-icon>
            <strong>Fecha pedido:</strong> {{ formatearFecha(pedido.fechaPedido) }}
          </div>
          <div v-if="pedido.fechaEntregaEstimada" class="mb-2">
            <v-icon size="small" class="mr-2" :color="estaProximoVencer ? 'warning' : 'default'">
              mdi-clock-alert
            </v-icon>
            <strong>Entrega estimada:</strong>
            <span :class="{ 'text-warning': estaProximoVencer }">
              {{ formatearFecha(pedido.fechaEntregaEstimada) }}
            </span>
          </div>
          <div v-if="pedido.fechaEntrega" class="mb-2">
            <v-icon size="small" class="mr-2" color="success">mdi-check-circle</v-icon>
            <strong>Entregado:</strong> {{ formatearFecha(pedido.fechaEntrega) }}
          </div>
          <div class="mb-2">
            <v-icon size="small" class="mr-2">mdi-package-variant</v-icon>
            <strong>Items:</strong> {{ pedido.cantidadTotalItems }}
          </div>
        </v-col>
      </v-row>

      <!-- Alerta si está próximo a vencer -->
      <v-alert
        v-if="estaProximoVencer"
        type="warning"
        variant="tonal"
        density="compact"
        class="mt-3"
      >
        <v-icon size="small">mdi-alert</v-icon>
        Este pedido vence en menos de 24 horas
      </v-alert>
    </v-card-text>

    <v-card-actions>
      <v-btn
        color="primary"
        variant="text"
        size="small"
        @click="$emit('ver-detalle', pedido)"
      >
        <v-icon start>mdi-eye</v-icon>
        Ver Detalle
      </v-btn>

      <v-btn
        v-if="pedido.estado !== 'ENTREGADO' && pedido.estado !== 'CANCELADO'"
        color="info"
        variant="text"
        size="small"
        @click="$emit('modificar-fecha', pedido)"
      >
        <v-icon start>mdi-calendar-edit</v-icon>
        Modificar Fecha
      </v-btn>

      <v-btn
        v-if="pedido.estado === 'ENVIADO'"
        color="success"
        variant="text"
        size="small"
        @click="$emit('marcar-entregado', pedido)"
      >
        <v-icon start>mdi-check-circle</v-icon>
        Marcar Entregado
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup>
import { computed } from 'vue';
import { format, parseISO, differenceInHours } from 'date-fns';
import { es } from 'date-fns/locale';

const props = defineProps({
  pedido: {
    type: Object,
    required: true
  }
});

defineEmits(['ver-detalle', 'modificar-fecha', 'marcar-entregado']);

const obtenerColorEstado = (estado) => {
  const colores = {
    PENDIENTE: 'orange',
    CONFIRMADO: 'blue',
    EN_PREPARACION: 'indigo',
    ENVIADO: 'green',
    ENTREGADO: 'success',
    CANCELADO: 'error'
  };
  return colores[estado] || 'grey';
};

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  try {
    return format(parseISO(fecha), "dd/MM/yyyy HH:mm", { locale: es });
  } catch (error) {
    return fecha;
  }
};

const estaProximoVencer = computed(() => {
  if (!props.pedido.fechaEntregaEstimada) return false;
  try {
    const horasRestantes = differenceInHours(
      parseISO(props.pedido.fechaEntregaEstimada),
      new Date()
    );
    return horasRestantes <= 24 && horasRestantes >= 0;
  } catch (error) {
    return false;
  }
});
</script>

<style scoped>
.text-warning {
  color: #fb8c00;
  font-weight: bold;
}
</style>