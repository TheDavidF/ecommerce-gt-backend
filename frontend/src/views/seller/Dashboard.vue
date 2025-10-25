<template>
  <div class="min-h-screen bg-gray-100">
    <NavBar />
    
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Dashboard de Vendedor</h1>
        <p class="text-gray-600 mt-2">Bienvenido, {{ authStore.username }}</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="card">
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Mis Productos</h3>
          <p class="text-3xl font-bold text-blue-600">0</p>
          <p class="text-sm text-gray-600 mt-1">Productos publicados</p>
        </div>
        
        <div class="card">
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Ventas</h3>
          <p class="text-3xl font-bold text-green-600">Q 0.00</p>
          <p class="text-sm text-gray-600 mt-1">Total de ventas</p>
        </div>
        
        <div class="card">
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Pedidos</h3>
          <p class="text-3xl font-bold text-purple-600">0</p>
          <p class="text-sm text-gray-600 mt-1">Pedidos pendientes</p>
        </div>
      </div>

      <div class="card">
        <h2 class="text-xl font-semibold mb-4">Próximamente</h2>
        <p class="text-gray-600">
          Aquí podrás gestionar tus productos, ver ventas y más.
        </p>
        <button class="btn-primary mt-4">
          Crear Producto
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '../../stores/auth'
import NavBar from '../../components/layout/NavBar.vue'

const authStore = useAuthStore()
</script>