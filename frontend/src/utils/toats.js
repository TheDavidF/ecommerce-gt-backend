import { useToast } from 'vue-toastification'

const toast = useToast()

export const showToast = {
  success(message, options = {}) {
    toast.success(message, {
      timeout: 3000,
      closeOnClick: true,
      pauseOnHover: true,
      icon: '✅',
      ...options
    })
  },

  error(message, options = {}) {
    toast.error(message, {
      timeout: 4000,
      closeOnClick: true,
      pauseOnHover: true,
      icon: '❌',
      ...options
    })
  },

  warning(message, options = {}) {
    toast.warning(message, {
      timeout: 3500,
      closeOnClick: true,
      pauseOnHover: true,
      icon: '⚠️',
      ...options
    })
  },

  info(message, options = {}) {
    toast.info(message, {
      timeout: 3000,
      closeOnClick: true,
      pauseOnHover: true,
      icon: 'ℹ️',
      ...options
    })
  }
}