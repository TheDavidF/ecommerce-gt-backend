<template>
  <Modal :show="show" :title="title" @close="cancel">
    <div class="mb-4">
      <p class="text-gray-700">{{ message }}</p>
    </div>

    <template #footer>
      <div class="flex justify-end space-x-3">
        <button 
          @click="cancel" 
          class="btn-secondary px-4 py-2"
          :disabled="loading"
        >
          {{ cancelText }}
        </button>
        <button 
          @click="confirm" 
          :class="dangerMode ? 'btn-danger' : 'btn-primary'"
          class="px-4 py-2"
          :disabled="loading"
        >
          <span v-if="!loading">{{ confirmText }}</span>
          <span v-else>Procesando...</span>
        </button>
      </div>
    </template>
  </Modal>
</template>

<script setup>
import { ref } from 'vue'
import Modal from './Modal.vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'Confirmar acción'
  },
  message: {
    type: String,
    required: true
  },
  confirmText: {
    type: String,
    default: 'Confirmar'
  },
  cancelText: {
    type: String,
    default: 'Cancelar'
  },
  dangerMode: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['confirm', 'cancel'])
const loading = ref(false)

const confirm = async () => {
  loading.value = true
  emit('confirm')
  loading.value = false
}

const cancel = () => {
  emit('cancel')
}
</script>