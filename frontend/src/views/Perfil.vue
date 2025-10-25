<template>
  <div class="min-h-screen bg-gray-100">
    <NavBar />
    <div class="max-w-7xl mx-auto px-4 py-8">
      <h1 class="text-3xl font-bold mb-4">Mi Perfil</h1>
      <div class="card">
        <p>Usuario: {{ authStore.username }}</p>
        <p>Email: {{ authStore.user?.email }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import NavBar from '../components/layout/NavBar.vue'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
</script>