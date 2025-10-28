<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
   
    <NavBar />

    <!-- Hero Section -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
      <div class="text-center">
        <h2 class="text-5xl font-extrabold text-gray-900 mb-4">
          Bienvenido a EcommerceGT
        </h2>
        <p class="text-xl text-gray-600 mb-8">
          Tu marketplace guatemalteco de confianza
        </p>
        
        <!-- Stats Cards -->
        <!-- Stats Cards con animaciones -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-12">
          <div class="card hover-lift animate-fade-in" style="animation-delay: 0.1s">
            <div class="text-4xl mb-2"></div>
            <h3 class="text-2xl font-bold text-gray-900">Productos</h3>
            <p class="text-gray-600">Miles de productos disponibles</p>
          </div>
          
          <div class="card hover-lift animate-fade-in" style="animation-delay: 0.2s">
            <div class="text-4xl mb-2"></div>
            <h3 class="text-2xl font-bold text-gray-900">Envío Rápido</h3>
            <p class="text-gray-600">Entrega a toda Guatemala</p>
          </div>
          
          <div class="card hover-lift animate-fade-in" style="animation-delay: 0.3s">
            <div class="text-4xl mb-2"></div>
            <h3 class="text-2xl font-bold text-gray-900">Calidad</h3>
            <p class="text-gray-600">Productos verificados</p>
          </div>
        </div>

        <!-- CTA Buttons -->
        <div class="mt-12 space-x-4">
          <router-link to="/login" class="btn-primary text-lg px-8 py-3 inline-block">
            Empezar Ahora
          </router-link>
          <button class="btn-secondary text-lg px-8 py-3">
            Ver Productos
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from '../components/layout/NavBar.vue'

const apiUrl = ref(import.meta.env.VITE_API_URL)
</script>