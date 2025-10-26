<template>
  <div class="stat-card" :class="colorClass">
    <div class="stat-icon">
      <component :is="iconComponent" class="icon" />
    </div>
    <div class="stat-content">
      <p class="stat-label">{{ label }}</p>
      <p class="stat-value">{{ formattedValue }}</p>
      <p v-if="subtitle" class="stat-subtitle">{{ subtitle }}</p>
    </div>
    <div v-if="trend" class="stat-trend" :class="trendClass">
      <svg v-if="trend > 0" xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
        <path fill-rule="evenodd" d="M12 7a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0V8.414l-4.293 4.293a1 1 0 01-1.414 0L8 10.414l-4.293 4.293a1 1 0 01-1.414-1.414l5-5a1 1 0 011.414 0L11 10.586 14.586 7H12z" clip-rule="evenodd" />
      </svg>
      <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
        <path fill-rule="evenodd" d="M12 13a1 1 0 100 2h5a1 1 0 001-1V9a1 1 0 10-2 0v2.586l-4.293-4.293a1 1 0 00-1.414 0L8 9.586 3.707 5.293a1 1 0 00-1.414 1.414l5 5a1 1 0 001.414 0L11 9.414 14.586 13H12z" clip-rule="evenodd" />
      </svg>
      <span>{{ Math.abs(trend) }}%</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  label: {
    type: String,
    required: true
  },
  value: {
    type: [Number, String],
    required: true
  },
  icon: {
    type: String,
    default: 'chart'
  },
  color: {
    type: String,
    default: 'blue',
    validator: (value) => ['blue', 'green', 'yellow', 'red', 'purple', 'indigo'].includes(value)
  },
  subtitle: {
    type: String,
    default: null
  },
  trend: {
    type: Number,
    default: null
  },
  isCurrency: {
    type: Boolean,
    default: false
  }
})

const colorClass = computed(() => `stat-card-${props.color}`)

const trendClass = computed(() => ({
  'trend-up': props.trend > 0,
  'trend-down': props.trend < 0
}))

const formattedValue = computed(() => {
  if (props.isCurrency) {
    return `Q${parseFloat(props.value).toFixed(2)}`
  }
  return props.value.toLocaleString()
})

const iconComponent = computed(() => {
  const icons = {
    users: 'UsersIcon',
    products: 'PackageIcon',
    orders: 'ShoppingCartIcon',
    sales: 'CurrencyIcon',
    chart: 'ChartIcon'
  }
  return icons[props.icon] || 'ChartIcon'
})
</script>

<style scoped>
.stat-card {
  @apply bg-white rounded-lg shadow-lg p-6 relative overflow-hidden transition-all hover:shadow-xl;
}

.stat-card::before {
  content: '';
  @apply absolute top-0 left-0 w-full h-1;
}

.stat-card-blue::before {
  @apply bg-blue-500;
}

.stat-card-green::before {
  @apply bg-green-500;
}

.stat-card-yellow::before {
  @apply bg-yellow-500;
}

.stat-card-red::before {
  @apply bg-red-500;
}

.stat-card-purple::before {
  @apply bg-purple-500;
}

.stat-card-indigo::before {
  @apply bg-indigo-500;
}

.stat-icon {
  @apply w-12 h-12 rounded-full flex items-center justify-center mb-4;
}

.stat-card-blue .stat-icon {
  @apply bg-blue-100 text-blue-600;
}

.stat-card-green .stat-icon {
  @apply bg-green-100 text-green-600;
}

.stat-card-yellow .stat-icon {
  @apply bg-yellow-100 text-yellow-600;
}

.stat-card-red .stat-icon {
  @apply bg-red-100 text-red-600;
}

.stat-card-purple .stat-icon {
  @apply bg-purple-100 text-purple-600;
}

.stat-card-indigo .stat-icon {
  @apply bg-indigo-100 text-indigo-600;
}

.icon {
  @apply w-6 h-6;
}

.stat-content {
  @apply mb-2;
}

.stat-label {
  @apply text-sm font-medium text-gray-600 mb-2;
}

.stat-value {
  @apply text-3xl font-bold text-gray-900;
}

.stat-subtitle {
  @apply text-xs text-gray-500 mt-1;
}

.stat-trend {
  @apply flex items-center gap-1 text-sm font-medium;
}

.trend-up {
  @apply text-green-600;
}

.trend-down {
  @apply text-red-600;
}
</style>