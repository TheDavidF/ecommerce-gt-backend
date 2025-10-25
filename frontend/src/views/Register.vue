<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full">
      <!-- Logo y título -->
      <div class="text-center mb-8">
        <router-link to="/" class="inline-block">
          <h1 class="text-4xl font-bold text-blue-600 mb-2 hover:text-blue-700 transition-colors">
             EcommerceGT
          </h1>
        </router-link>
        <h2 class="text-2xl font-semibold text-gray-900">Crear Cuenta</h2>
        <p class="text-gray-600 mt-2">Regístrate para empezar a comprar</p>
      </div>

      <!-- Formulario -->
      <div class="card">
        <form @submit.prevent="handleRegister" class="space-y-4">
          <!-- Nombre Usuario -->
          <div>
            <label for="nombreUsuario" class="block text-sm font-medium text-gray-700 mb-2">
              Nombre de Usuario *
            </label>
            <input
              id="nombreUsuario"
              v-model="form.nombreUsuario"
              type="text"
              required
              class="input-field"
              :class="{ 'border-red-500': errors.nombreUsuario, 'border-green-500': form.nombreUsuario && !errors.nombreUsuario }"
              placeholder="tu_usuario"
              :disabled="loading"
              @blur="validateUsername"
              @input="errors.nombreUsuario = ''"
            />
            <p v-if="errors.nombreUsuario" class="mt-1 text-sm text-red-600">
              {{ errors.nombreUsuario }}
            </p>
            <p v-else-if="form.nombreUsuario && !errors.nombreUsuario" class="mt-1 text-sm text-green-600">
              ✓ Usuario disponible
            </p>
          </div>

          <!-- Email -->
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-2">
              Email *
            </label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              class="input-field"
              :class="{ 'border-red-500': errors.email, 'border-green-500': form.email && !errors.email && isValidEmail }"
              placeholder="tu@email.com"
              :disabled="loading"
              @blur="validateEmail"
              @input="errors.email = ''"
            />
            <p v-if="errors.email" class="mt-1 text-sm text-red-600">
              {{ errors.email }}
            </p>
            <p v-else-if="form.email && isValidEmail" class="mt-1 text-sm text-green-600">
              ✓ Email válido
            </p>
          </div>

          <!-- Contraseña -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-2">
              Contraseña *
            </label>
            <div class="relative">
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                required
                minlength="6"
                class="input-field pr-10"
                :class="{ 'border-red-500': errors.password }"
                placeholder="Mínimo 6 caracteres"
                :disabled="loading"
                @input="validatePassword"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
              >
                <span v-if="!showPassword"></span>
                <span v-else></span>
              </button>
            </div>
            <!-- Password strength indicator -->
            <div class="mt-2 space-y-1">
              <div class="flex gap-1">
                <div 
                  v-for="i in 4" 
                  :key="i"
                  class="h-1 flex-1 rounded"
                  :class="i <= passwordStrength ? strengthColor : 'bg-gray-200'"
                ></div>
              </div>
              <p class="text-xs" :class="strengthTextClass">
                {{ strengthText }}
              </p>
            </div>
          </div>

          <!-- Nombre Completo -->
          <div>
            <label for="nombreCompleto" class="block text-sm font-medium text-gray-700 mb-2">
              Nombre Completo *
            </label>
            <input
              id="nombreCompleto"
              v-model="form.nombreCompleto"
              type="text"
              required
              class="input-field"
              :class="{ 'border-red-500': errors.nombreCompleto }"
              placeholder="Juan Pérez"
              :disabled="loading"
              @blur="validateNombreCompleto"
              @input="errors.nombreCompleto = ''"
            />
            <p v-if="errors.nombreCompleto" class="mt-1 text-sm text-red-600">
              {{ errors.nombreCompleto }}
            </p>
          </div>

          <!-- Teléfono -->
          <div>
            <label for="telefono" class="block text-sm font-medium text-gray-700 mb-2">
              Teléfono (opcional)
            </label>
            <input
              id="telefono"
              v-model="form.telefono"
              type="tel"
              class="input-field"
              placeholder="5555-5555"
              :disabled="loading"
            />
          </div>

          <!-- Dirección -->
          <div>
            <label for="direccion" class="block text-sm font-medium text-gray-700 mb-2">
              Dirección (opcional)
            </label>
            <textarea
              id="direccion"
              v-model="form.direccion"
              rows="2"
              class="input-field"
              placeholder="Tu dirección"
              :disabled="loading"
            ></textarea>
          </div>

          <!-- Términos y condiciones -->
          <div class="flex items-start">
            <input
              id="terms"
              v-model="acceptTerms"
              type="checkbox"
              class="h-4 w-4 mt-1 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              required
            />
            <label for="terms" class="ml-2 block text-sm text-gray-700">
              Acepto los <a href="#" class="text-blue-600 hover:text-blue-700">términos y condiciones</a> y la <a href="#" class="text-blue-600 hover:text-blue-700">política de privacidad</a>
            </label>
          </div>

          <!-- Botón submit -->
          <div>
            <button
              type="submit"
              class="w-full btn-primary py-3 text-lg"
              :disabled="loading || !isFormValid"
              :class="{ 'opacity-50 cursor-not-allowed': loading || !isFormValid }"
            >
              <span v-if="!loading">Registrarse</span>
              <span v-else class="flex items-center justify-center">
                <svg class="animate-spin h-5 w-5 mr-3" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Creando cuenta...
              </span>
            </button>
          </div>

          <!-- Error message -->
          <div v-if="error" class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg text-sm">
            <p class="font-semibold"> Error al registrarse</p>
            <p>{{ error }}</p>
          </div>

          <!-- Success message -->
          <div v-if="success" class="bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-lg text-sm">
            <p class="font-semibold"> {{ success }}</p>
            <p class="text-xs mt-1">Redirigiendo al login...</p>
          </div>
        </form>

        <!-- Link a login -->
        <div class="mt-6 text-center">
          <p class="text-sm text-gray-600">
            ¿Ya tienes cuenta?
            <router-link to="/login" class="text-blue-600 hover:text-blue-700 font-medium">
              Inicia sesión aquí
            </router-link>
          </p>
        </div>

        <!-- Link a home -->
        <div class="mt-4 text-center">
          <router-link to="/" class="text-sm text-gray-500 hover:text-gray-700">
            ← Volver al inicio
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  nombreUsuario: '',
  email: '',
  password: '',
  nombreCompleto: '',
  telefono: '',
  direccion: '',
})

const errors = ref({
  nombreUsuario: '',
  email: '',
  password: '',
  nombreCompleto: '',
})

const loading = ref(false)
const error = ref('')
const success = ref('')
const showPassword = ref(false)
const acceptTerms = ref(false)

// Validaciones
const validateUsername = () => {
  if (!form.value.nombreUsuario) {
    errors.value.nombreUsuario = 'El usuario es requerido'
    return false
  }
  if (form.value.nombreUsuario.length < 3) {
    errors.value.nombreUsuario = 'Mínimo 3 caracteres'
    return false
  }
  if (!/^[a-zA-Z0-9_]+$/.test(form.value.nombreUsuario)) {
    errors.value.nombreUsuario = 'Solo letras, números y guión bajo'
    return false
  }
  errors.value.nombreUsuario = ''
  return true
}

const isValidEmail = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(form.value.email)
})

const validateEmail = () => {
  if (!form.value.email) {
    errors.value.email = 'El email es requerido'
    return false
  }
  if (!isValidEmail.value) {
    errors.value.email = 'Email inválido'
    return false
  }
  errors.value.email = ''
  return true
}

const passwordStrength = computed(() => {
  const pwd = form.value.password
  if (!pwd) return 0
  if (pwd.length < 6) return 1
  
  let strength = 1
  if (pwd.length >= 8) strength++
  if (/[A-Z]/.test(pwd) && /[a-z]/.test(pwd)) strength++
  if (/[0-9]/.test(pwd)) strength++
  if (/[^A-Za-z0-9]/.test(pwd)) strength++
  
  return Math.min(strength, 4)
})

const strengthColor = computed(() => {
  const colors = ['bg-red-500', 'bg-red-500', 'bg-yellow-500', 'bg-green-500', 'bg-green-600']
  return colors[passwordStrength.value]
})

const strengthText = computed(() => {
  const texts = ['', 'Muy débil', 'Débil', 'Buena', 'Fuerte']
  return texts[passwordStrength.value]
})

const strengthTextClass = computed(() => {
  const classes = ['', 'text-red-600', 'text-yellow-600', 'text-green-600', 'text-green-700']
  return classes[passwordStrength.value]
})

const validatePassword = () => {
  if (!form.value.password) {
    errors.value.password = 'La contraseña es requerida'
    return false
  }
  if (form.value.password.length < 6) {
    errors.value.password = 'Mínimo 6 caracteres'
    return false
  }
  errors.value.password = ''
  return true
}

const validateNombreCompleto = () => {
  if (!form.value.nombreCompleto) {
    errors.value.nombreCompleto = 'El nombre completo es requerido'
    return false
  }
  if (form.value.nombreCompleto.length < 3) {
    errors.value.nombreCompleto = 'Nombre muy corto'
    return false
  }
  errors.value.nombreCompleto = ''
  return true
}

const isFormValid = computed(() => {
  return form.value.nombreUsuario.length >= 3 &&
         isValidEmail.value &&
         form.value.password.length >= 6 &&
         form.value.nombreCompleto.length >= 3 &&
         acceptTerms.value
})

const handleRegister = async () => {
  // Validar todo
  const validations = [
    validateUsername(),
    validateEmail(),
    validatePassword(),
    validateNombreCompleto(),
  ]

  if (!validations.every(v => v)) {
    return
  }

  loading.value = true
  error.value = ''
  success.value = ''

  try {
    await authStore.register(form.value)
    success.value = '¡Registro exitoso! Redirigiendo al login...'
    
    // Esperar 2 segundos y redirigir
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (err) {
    console.error('Error registro:', err)
    error.value = err.response?.data?.message || 'Error al registrarse. El usuario o email ya existe.'
  } finally {
    loading.value = false
  }
}
</script>