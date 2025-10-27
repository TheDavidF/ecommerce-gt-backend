<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-3xl mx-auto px-4">
        <router-link 
        to="/mis-productos" 
        class="text-blue-600 hover:text-blue-800 flex items-center mb-4"
      >
        ← Volver a mis productos
      </router-link>
      <!-- Header -->
      <div class="mb-6">
        <router-link 
          to="/mis-productos" 
          class="text-blue-600 hover:text-blue-800 flex items-center mb-4"
        >
          ← Volver a mis productos
        </router-link>
        <h1 class="text-3xl font-bold text-gray-900">Editar Producto</h1>
        <p class="text-gray-600 mt-2">
          Al editar, el producto volverá a estado "Pendiente de revisión"
        </p>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-blue-600 border-t-transparent"></div>
        <p class="text-gray-600 mt-4">Cargando producto...</p>
      </div>

      <!-- Formulario -->
      <div v-else class="bg-white rounded-lg shadow-md p-6">
        <!-- Error general -->
        <div v-if="error" class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg mb-6">
          <p class="text-sm">{{ error }}</p>
        </div>

        <!-- Success -->
        <div v-if="success" class="bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-lg mb-6">
          <p class="text-sm">{{ success }}</p>
        </div>

        <form @submit.prevent="handleSubmit">
          <!-- Nombre -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Nombre del producto *
            </label>
            <input
              v-model="form.nombre"
              type="text"
              required
              maxlength="200"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <!-- Descripción -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Descripción *
            </label>
            <textarea
              v-model="form.descripcion"
              required
              rows="4"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            ></textarea>
          </div>

          <!-- Categoría -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Categoría *
            </label>
            <select
              v-model="form.categoriaId"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">Selecciona una categoría</option>
              <option v-for="cat in categorias" :key="cat.id" :value="cat.id">
                {{ cat.nombre }}
              </option>
            </select>
          </div>

          <!-- Precio y Descuento -->
          <div class="grid grid-cols-2 gap-4 mb-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Precio (Q) *
              </label>
              <input
                v-model.number="form.precio"
                type="number"
                step="0.01"
                min="0.01"
                required
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Precio con descuento (Q)
              </label>
              <input
                v-model.number="form.precioDescuento"
                type="number"
                step="0.01"
                min="0"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          <!-- Stock -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Stock disponible *
            </label>
            <input
              v-model.number="form.stock"
              type="number"
              min="0"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <!-- Marca y Modelo -->
          <div class="grid grid-cols-2 gap-4 mb-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Marca
              </label>
              <input
                v-model="form.marca"
                type="text"
                maxlength="100"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Modelo
              </label>
              <input
                v-model="form.modelo"
                type="text"
                maxlength="100"
                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          <!-- Botones -->
          <div class="flex gap-4 pt-4">
            <button
              type="submit"
              :disabled="saving"
              class="flex-1 bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition"
            >
              {{ saving ? 'Guardando...' : 'Guardar Cambios' }}
            </button>

            <router-link
              to="/mis-productos"
              class="px-6 py-3 border border-gray-300 rounded-lg font-semibold text-gray-700 hover:bg-gray-50 transition"
            >
              Cancelar
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import productService from '@/services/productService'
import categoryService from '@/services/categoryService'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const saving = ref(false)
const error = ref('')
const success = ref('')

const categorias = ref([])

const form = ref({
  nombre: '',
  descripcion: '',
  precio: null,
  precioDescuento: null,
  stock: 0,
  categoriaId: '',
  marca: '',
  modelo: ''
})

// Cargar producto
onMounted(async () => {
  try {
    // Cargar categorías
    const cats = await categoryService.getCategories()
    categorias.value = cats

    // Cargar producto
    const producto = await productService.getProductById(route.params.id)
    
    form.value = {
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      precio: producto.precio,
      precioDescuento: producto.precioDescuento,
      stock: producto.stock,
      categoriaId: producto.categoriaId,
      marca: producto.marca || '',
      modelo: producto.modelo || ''
    }

  } catch (err) {
    console.error('Error al cargar producto:', err)
    error.value = 'Error al cargar el producto'
  } finally {
    loading.value = false
  }
})

// Enviar formulario
const handleSubmit = async () => {
  error.value = ''
  success.value = ''
  saving.value = true

  try {
    await productService.updateProduct(route.params.id, form.value)

    success.value = 'Producto actualizado exitosamente. Volverá a revisión.'

    setTimeout(() => {
      router.push('/mis-productos')
    }, 2000)

  } catch (err) {
    console.error('Error al actualizar producto:', err)
    error.value = err.response?.data?.message || 'Error al actualizar el producto'
  } finally {
    saving.value = false
  }
}
</script>