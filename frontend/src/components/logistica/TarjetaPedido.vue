<template>
  <div class="bg-white rounded-lg shadow-md border border-gray-200 mb-4 overflow-hidden">
    <!-- Header -->
    <div class="bg-gray-50 px-6 py-4 border-b border-gray-200 flex justify-between items-center">
      <div class="flex items-center gap-3">
        <span
          :class="[
            'px-3 py-1 rounded-full text-xs font-semibold',
            obtenerClaseEstado(pedido.estado)
          ]"
        >
          {{ pedido.estado }}
        </span>
        <h3 class="text-lg font-bold text-gray-800">{{ pedido.numeroOrden }}</h3>
      </div>
      <div class="text-xl font-bold text-blue-600">
        Q{{ pedido.total.toFixed(2) }}
      </div>
    </div>

    <!-- Body -->
    <div class="p-6">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Información del cliente -->
        <div class="space-y-3">
          <div class="flex items-start gap-2">
            <span class="text-gray-500 font-medium min-w-[120px]">Cliente:</span>
            <span class="text-gray-800">{{ pedido.usuarioNombre }}</span>
          </div>
          <div class="flex items-start gap-2">
            <span class="text-gray-500 font-medium min-w-[120px]">Email:</span>
            <span class="text-gray-800">{{ pedido.usuarioEmail }}</span>
          </div>
          <div class="flex items-start gap-2">
            <span class="text-gray-500 font-medium min-w-[120px]">Teléfono:</span>
            <span class="text-gray-800">{{ pedido.telefonoContacto }}</span>
          </div>
          <div class="flex items-start gap-2">
            <span class="text-gray-500 font-medium min-w-[120px]">Dirección:</span>
            <span class="text-gray-800">{{ pedido.direccionEnvio }}</span>
          </div>
        </div>

        <!-- Fechas e información -->
        <div class="space-y-3">
          <div class="flex items-start gap-2">
            <span class="text-gray-500 font-medium min-w-[140px]">Fecha pedido:</span>
            <span class="text-gray-800">{{ formatearFecha(pedido.fechaPedido) }}</span>
          </div>
          <div v-if="pedido.fechaEntregaEstimada" class="flex items-start gap-2">
            <span class="text-gray-500 font-medium min-w-[140px]">Entrega estimada:</span>
            <span :class="estaProximoVencer ? 'text-orange-600 font-semibold' : 'text-gray-800'">
              {{ formatearFecha(pedido.fechaEntregaEstimada) }}
            </span>
          </div>
          <div v-if="pedido.fechaEntrega" class="flex items-start gap-2">
            <span class="text-gray-500 font-medium min-w-[140px]">Entregado:</span>
            <span class="text-green-600 font-semibold">{{ formatearFecha(pedido.fechaEntrega) }}</span>
          </div>
          <div class="flex items-start gap-2">
            <span class="text-gray-500 font-medium min-w-[140px]">Items:</span>
            <span class="text-gray-800">{{ pedido.cantidadTotalItems }}</span>
          </div>
        </div>
      </div>

      <!-- Alerta si está próximo a vencer -->
      <div
        v-if="estaProximoVencer"
        class="mt-4 bg-orange-50 border border-orange-200 rounded-lg p-3 flex items-center gap-2"
      >
        <span class="text-orange-600"></span>
        <span class="text-orange-800 text-sm font-medium">
          Este pedido vence en menos de 24 horas
        </span>
      </div>
    </div>

    <!-- Footer con botones -->
    <div class="bg-gray-50 px-6 py-4 border-t border-gray-200 flex flex-wrap gap-2">
      <button
        @click="$emit('ver-detalle', pedido)"
        class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium text-sm"
      >
        Ver Detalle
      </button>

      <button
        v-if="pedido.estado !== 'ENTREGADO' && pedido.estado !== 'CANCELADO'"
        @click="$emit('modificar-fecha', pedido)"
        class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-medium text-sm"
      >
        Modificar Fecha
      </button>

      <button
        v-if="pedido.estado === 'ENVIADO'"
        @click="$emit('marcar-entregado', pedido)"
        class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-medium text-sm"
      >
        Marcar Entregado
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  pedido: {
    type: Object,
    required: true
  }
});

defineEmits(['ver-detalle', 'modificar-fecha', 'marcar-entregado']);

const obtenerClaseEstado = (estado) => {
  const clases = {
    PENDIENTE: 'bg-orange-100 text-orange-700',
    CONFIRMADO: 'bg-blue-100 text-blue-700',
    EN_PREPARACION: 'bg-indigo-100 text-indigo-700',
    ENVIADO: 'bg-green-100 text-green-700',
    ENTREGADO: 'bg-gray-100 text-gray-700',
    CANCELADO: 'bg-red-100 text-red-700'
  };
  return clases[estado] || 'bg-gray-100 text-gray-700';
};

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

const estaProximoVencer = computed(() => {
  if (!props.pedido.fechaEntregaEstimada) return false;
  try {
    const fechaEntrega = new Date(props.pedido.fechaEntregaEstimada);
    const ahora = new Date();
    const horasRestantes = (fechaEntrega - ahora) / (1000 * 60 * 60);
    return horasRestantes <= 24 && horasRestantes >= 0;
  } catch (error) {
    return false;
  }
});
</script>