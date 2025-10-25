<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center px-4">
    <div class="max-w-md w-full text-center">
      <div class="mb-8">
        <h1 class="text-9xl font-bold text-blue-600">404</h1>
        <h2 class="text-3xl font-semibold text-gray-900 mt-4">Página no encontrada</h2>
        <p class="text-gray-600 mt-4">
          Lo sentimos, la página que buscas no existe o ha sido movida.
        </p>
      </div>

      <div class="card">
        <div class="space-y-4">
          <router-link to="/" class="btn-primary w-full block py-3">
            Volver al Inicio
          </router-link>
          <button @click="goBack" class="btn-secondary w-full py-3">
            Volver Atrás
          </button>
        </div>
      </div>

      <div class="mt-8">
        <p class="text-sm text-gray-500">
          ¿Necesitas ayuda? 
          <a href="#" class="text-blue-600 hover:text-blue-700">Contáctanos</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}
</script>