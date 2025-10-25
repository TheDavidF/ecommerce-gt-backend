<template>
  <div class="min-h-screen bg-gradient-to-br from-red-50 to-orange-100 flex items-center justify-center px-4">
    <div class="max-w-md w-full text-center">
      <div class="mb-8">
        <h1 class="text-9xl font-bold text-red-600">403</h1>
        <h2 class="text-3xl font-semibold text-gray-900 mt-4">Acceso Denegado</h2>
        <p class="text-gray-600 mt-4">
          No tienes permiso para acceder a esta página.
        </p>
      </div>

      <div class="card">
        <div class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
          <p class="text-sm text-red-800">
            <strong>Razones comunes:</strong>
          </p>
          <ul class="text-sm text-red-700 mt-2 text-left list-disc list-inside space-y-1">
            <li>No tienes el rol requerido</li>
            <li>Tu cuenta no está activa</li>
            <li>No tienes los permisos necesarios</li>
          </ul>
        </div>

        <div class="space-y-4">
          <router-link to="/" class="btn-primary w-full block py-3">
            Volver al Inicio
          </router-link>
          <button @click="logout" class="btn-secondary w-full py-3">
            Cerrar Sesión
          </button>
        </div>
      </div>

      <div class="mt-8">
        <p class="text-sm text-gray-500">
          ¿Crees que esto es un error? 
          <a href="#" class="text-red-600 hover:text-red-700">Contáctanos</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>