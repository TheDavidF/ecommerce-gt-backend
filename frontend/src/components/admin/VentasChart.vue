<template>
  <div class="chart-container">
    <div class="chart-header">
      <h3 class="chart-title">{{ title }}</h3>
      <div class="chart-actions">
        <button
          v-for="periodo in periodos"
          :key="periodo.value"
          @click="$emit('cambiar-periodo', periodo.value)"
          :class="['periodo-btn', { active: periodoActual === periodo.value }]"
        >
          {{ periodo.label }}
        </button>
      </div>
    </div>
    <div class="chart-body">
      <Line v-if="!loading" :data="chartData" :options="chartOptions" />
      <div v-else class="loading-state">
        <div class="spinner"></div>
        <p>Cargando datos...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

const props = defineProps({
  title: {
    type: String,
    default: 'Ventas'
  },
  labels: {
    type: Array,
    required: true
  },
  data: {
    type: Array,
    required: true
  },
  periodoActual: {
    type: String,
    default: 'mes'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['cambiar-periodo'])

const periodos = [
  { value: 'semana', label: 'Semana' },
  { value: 'mes', label: 'Mes' },
  { value: 'trimestre', label: 'Trimestre' },
  { value: 'anio', label: 'Año' }
]

const chartData = computed(() => ({
  labels: props.labels,
  datasets: [
    {
      label: 'Ventas (Q)',
      data: props.data,
      borderColor: '#3b82f6',
      backgroundColor: 'rgba(59, 130, 246, 0.1)',
      tension: 0.4,
      fill: true,
      pointRadius: 4,
      pointHoverRadius: 6,
      pointBackgroundColor: '#3b82f6',
      pointBorderColor: '#fff',
      pointBorderWidth: 2
    }
  ]
}))

const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      mode: 'index',
      intersect: false,
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      padding: 12,
      titleColor: '#fff',
      bodyColor: '#fff',
      borderColor: '#3b82f6',
      borderWidth: 1,
      callbacks: {
        label: function(context) {
          return 'Ventas: Q' + context.parsed.y.toFixed(2)
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: function(value) {
          return 'Q' + value.toFixed(0)
        }
      },
      grid: {
        color: 'rgba(0, 0, 0, 0.05)'
      }
    },
    x: {
      grid: {
        display: false
      }
    }
  }
}))
</script>

<style scoped>
.chart-container {
  @apply bg-white rounded-lg shadow-lg p-6;
}

.chart-header {
  @apply flex items-center justify-between mb-6;
}

.chart-title {
  @apply text-xl font-bold text-gray-900;
}

.chart-actions {
  @apply flex gap-2;
}

.periodo-btn {
  @apply px-3 py-1.5 text-sm font-medium rounded-lg transition-colors;
  @apply text-gray-600 bg-gray-100 hover:bg-gray-200;
}

.periodo-btn.active {
  @apply bg-blue-600 text-white hover:bg-blue-700;
}

.chart-body {
  @apply relative;
  height: 300px;
}

.loading-state {
  @apply flex flex-col items-center justify-center h-full;
}

.spinner {
  @apply w-12 h-12 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin mb-4;
}
</style>