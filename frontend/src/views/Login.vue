<template>
  <div
    class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8"
  >
    <div class="max-w-md w-full">
      <!-- Logo y título -->
      <div class="text-center mb-8">
        <router-link to="/" class="inline-block">
          <h1
            class="text-4xl font-bold text-blue-600 mb-2 hover:text-blue-700 transition-colors"
          >
            EcommerceGT
          </h1>
        </router-link>
        <h2 class="text-2xl font-semibold text-gray-900">Iniciar Sesión</h2>
        <p class="text-gray-600 mt-2">
          Ingresa tus credenciales para continuar
        </p>
      </div>

      <!-- Formulario -->
      <div class="card">
        <form @submit.prevent="handleLogin" class="space-y-6">
          <!-- Email/Usuario -->
          <div>
            <label
              for="nombreUsuario"
              class="block text-sm font-medium text-gray-700 mb-2"
            >
              Usuario o Email *
            </label>
            <input
              id="nombreUsuario"
              v-model="form.nombreUsuario"
              type="text"
              required
              class="input-field"
              :class="{ 'border-red-500': errors.nombreUsuario }"
              placeholder="tu_usuario"
              :disabled="loading"
              @blur="validateUsername"
              @input="errors.nombreUsuario = ''"
            />
            <p v-if="errors.nombreUsuario" class="mt-1 text-sm text-red-600">
              {{ errors.nombreUsuario }}
            </p>
          </div>

          <!-- Contraseña -->
          <div>
            <label
              for="password"
              class="block text-sm font-medium text-gray-700 mb-2"
            >
              Contraseña *
            </label>
            <div class="relative">
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                required
                class="input-field pr-10"
                :class="{ 'border-red-500': errors.password }"
                placeholder="••••••••"
                :disabled="loading"
                @blur="validatePassword"
                @input="errors.password = ''"
              />
              <button
                type="button"
                @click="togglePassword"
                class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
                :disabled="loading"
              >
                <span v-if="!showPassword"></span>
                <span v-else></span>
              </button>
            </div>
            <p v-if="errors.password" class="mt-1 text-sm text-red-600">
              {{ errors.password }}
            </p>
          </div>

          <!-- Recordarme -->
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input
                id="remember"
                v-model="rememberMe"
                type="checkbox"
                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label for="remember" class="ml-2 block text-sm text-gray-700">
                Recordarme
              </label>
            </div>
            <div class="text-sm">
              <a href="#" class="text-blue-600 hover:text-blue-700 font-medium">
                ¿Olvidaste tu contraseña?
              </a>
            </div>
          </div>

          <!-- Botón submit -->
          <div>
            <button
              type="submit"
              class="w-full btn-primary py-3 text-lg relative"
              :disabled="loading || !isFormValid"
              :class="{
                'opacity-50 cursor-not-allowed': loading || !isFormValid,
              }"
            >
              <span v-if="!loading">Iniciar Sesión</span>
              <span v-else class="flex items-center justify-center">
                <svg class="animate-spin h-5 w-5 mr-3" viewBox="0 0 24 24">
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                    fill="none"
                  ></circle>
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  ></path>
                </svg>
                Cargando...
              </span>
            </button>
          </div>

          <!-- Error message -->
          <div
            v-if="error"
            class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg flex items-start"
          >
            <span class="text-xl mr-2">⚠️</span>
            <div>
              <p class="font-semibold">Error al iniciar sesión</p>
              <p class="text-sm">{{ error }}</p>
            </div>
          </div>
        </form>

        <!-- Link a registro -->
        <div class="mt-6 text-center">
          <p class="text-sm text-gray-600">
            ¿No tienes cuenta?
            <router-link
              to="/register"
              class="text-blue-600 hover:text-blue-700 font-medium"
            >
              Regístrate aquí
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

      <!-- Info de prueba (SOLO PARA DESARROLLO) -->
      <div class="mt-6 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
        <p class="text-xs text-yellow-800 font-semibold mb-2">
          🔧 Credenciales de prueba:
        </p>
        <div class="space-y-1">
          <p class="text-xs text-yellow-700">
            <strong>Admin:</strong> admin / admin123
          </p>
          <p class="text-xs text-yellow-700">
            <strong>Vendedor:</strong> vendedor / vendedor123
          </p>
          <p class="text-xs text-yellow-700">
            <strong>Cliente:</strong> cliente / cliente123
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const authStore = useAuthStore();

const form = ref({
  nombreUsuario: "",
  password: "",
});

const errors = ref({
  nombreUsuario: "",
  password: "",
});

const loading = ref(false);
const error = ref("");
const showPassword = ref(false);
const rememberMe = ref(false);

// Validaciones
const validateUsername = () => {
  if (!form.value.nombreUsuario) {
    errors.value.nombreUsuario = "El usuario es requerido";
    return false;
  }
  if (form.value.nombreUsuario.length < 3) {
    errors.value.nombreUsuario = "El usuario debe tener al menos 3 caracteres";
    return false;
  }
  errors.value.nombreUsuario = "";
  return true;
};

const validatePassword = () => {
  if (!form.value.password) {
    errors.value.password = "La contraseña es requerida";
    return false;
  }
  if (form.value.password.length < 6) {
    errors.value.password = "La contraseña debe tener al menos 6 caracteres";
    return false;
  }
  errors.value.password = "";
  return true;
};

const isFormValid = computed(() => {
  return (
    form.value.nombreUsuario.length >= 3 && form.value.password.length >= 6
  );
});

const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

const handleLogin = async () => {
  // Validar antes de enviar
  const isUsernameValid = validateUsername();
  const isPasswordValid = validatePassword();

  if (!isUsernameValid || !isPasswordValid) {
    return;
  }

  loading.value = true;
  error.value = "";

  try {
    await authStore.login(form.value);

    if (rememberMe.value) {
      localStorage.setItem("rememberMe", "true");
    }

    if (authStore.isAdmin) {
      router.push("/admin/dashboard");
    } else if (authStore.isVendedor) {
      router.push("/vendedor/dashboard");
    } else {
      router.push("/");
    }
  } catch (err) {
    console.error("Error login:", err);
    error.value = "Credenciales incorrectas";
  } finally {
    loading.value = false;
  }
};
</script>
