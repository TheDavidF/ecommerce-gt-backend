<template>
  <div class="mb-6">
    <div class="flex flex-wrap gap-2">
      <button
        v-for="estado in estadosFiltrados"
        :key="estado.valor"
        @click="cambiarFiltro(estado.valor)"
        :class="[
          'px-4 py-2 rounded-lg font-medium transition-all duration-200 flex items-center gap-2',
          modelValue === estado.valor
            ? estado.bgActive + ' text-white shadow-lg'
            : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-300'
        ]"
      >
        <span>{{ estado.label }}</span>
        <span
          v-if="contadores[estado.valor] !== undefined"
          :class="[
            'text-xs font-bold px-2 py-1 rounded-full',
            modelValue === estado.valor
              ? 'bg-white ' + estado.textActive
              : estado.bgBadge + ' ' + estado.textBadge
          ]"
        >
          {{ contadores[estado.valor] }}
        </span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, computed } from 'vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: 'TODOS'
  },
  contadores: {
    type: Object,
    default: () => ({})
  },
  estados: {
    type: Array,
    default: () => ['TODOS', 'ENVIADO', 'ENTREGADO']
  }
});

const emit = defineEmits(['update:modelValue']);

const estadosBase = [
  {
    valor: 'TODOS',
    label: 'Todos',
    bgActive: 'bg-gray-600',
    textActive: 'text-gray-600',
    bgBadge: 'bg-gray-100',
    textBadge: 'text-gray-700'
  },
  {
    valor: 'ENVIADO',
    label: 'Enviados',
    bgActive: 'bg-green-600',
    textActive: 'text-green-600',
    bgBadge: 'bg-green-100',
    textBadge: 'text-green-700'
  },
  {
    valor: 'ENTREGADO',
    label: 'Entregados',
    bgActive: 'bg-blue-600',
    textActive: 'text-blue-600',
    bgBadge: 'bg-blue-100',
    textBadge: 'text-blue-700'
  }
];

const estadosFiltrados = computed(() => {
  return estadosBase.filter(e => props.estados.includes(e.valor));
});

const cambiarFiltro = (valor) => {
  emit('update:modelValue', valor);
};
</script>