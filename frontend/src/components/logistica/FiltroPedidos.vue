<template>
  <div class="filtro-pedidos">
    <v-chip-group
      v-model="estadoSeleccionado"
      mandatory
      selected-class="text-primary"
      @update:model-value="cambiarFiltro"
    >
      <v-chip
        v-for="estado in estados"
        :key="estado.valor"
        :value="estado.valor"
        :color="estado.color"
        variant="outlined"
        class="ma-1"
      >
        <v-icon start :icon="estado.icon"></v-icon>
        {{ estado.label }}
        <v-badge
          v-if="contadores[estado.valor]"
          :content="contadores[estado.valor]"
          inline
          color="primary"
          class="ml-2"
        ></v-badge>
      </v-chip>
    </v-chip-group>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: 'TODOS'
  },
  contadores: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:modelValue']);

const estadoSeleccionado = ref(props.modelValue);

const estados = [
  {
    valor: 'TODOS',
    label: 'Todos',
    icon: 'mdi-view-list',
    color: 'grey'
  },
  {
    valor: 'PENDIENTE',
    label: 'Pendientes',
    icon: 'mdi-clock-outline',
    color: 'orange'
  },
  {
    valor: 'CONFIRMADO',
    label: 'Confirmados',
    icon: 'mdi-check-circle-outline',
    color: 'blue'
  },
  {
    valor: 'EN_PREPARACION',
    label: 'En Preparación',
    icon: 'mdi-package-variant',
    color: 'indigo'
  },
  {
    valor: 'ENVIADO',
    label: 'Enviados',
    icon: 'mdi-truck-delivery',
    color: 'green'
  }
];

const cambiarFiltro = (valor) => {
  emit('update:modelValue', valor);
};

watch(() => props.modelValue, (newVal) => {
  estadoSeleccionado.value = newVal;
});
</script>

<style scoped>
.filtro-pedidos {
  margin-bottom: 20px;
}
</style>