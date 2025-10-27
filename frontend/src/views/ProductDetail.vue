<template>
  <div class="min-h-screen bg-gray-100">
    <NavBar />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Breadcrumb -->
      <nav class="mb-6">
        <ol class="flex items-center space-x-2 text-sm">
          <li>
            <router-link to="/" class="text-blue-600 hover:text-blue-700">
              Inicio
            </router-link>
          </li>
          <li class="text-gray-400">/</li>
          <li>
            <router-link to="/productos" class="text-blue-600 hover:text-blue-700">
              Productos
            </router-link>
          </li>
          <li class="text-gray-400">/</li>
          <li class="text-gray-600">{{ product?.nombre }}</li>
        </ol>
      </nav>

      <!-- Loading -->
      <div v-if="loading" class="card">
        <div class="animate-pulse grid grid-cols-1 md:grid-cols-2 gap-8">
          <div class="bg-gray-300 h-96 rounded-lg"></div>
          <div class="space-y-4">
            <div class="h-8 bg-gray-300 rounded w-3/4"></div>
            <div class="h-4 bg-gray-300 rounded w-1/2"></div>
            <div class="h-4 bg-gray-300 rounded"></div>
            <div class="h-4 bg-gray-300 rounded"></div>
          </div>
        </div>
      </div>

      <!-- Producto -->
      <div v-else-if="product" class="card">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <!-- Imagen -->
          <div>
            <div class="bg-gray-200 rounded-lg overflow-hidden h-96 flex items-center justify-center">
              <img
                v-if="product.imagenUrl"
                :src="product.imagenUrl"
                :alt="product.nombre"
                class="w-full h-full object-cover"
              />
              <div v-else class="text-gray-400 text-6xl">
                Sin imagen
              </div>
            </div>
          </div>

          <!-- Información -->
          <div>
            <h1 class="text-3xl font-bold text-gray-900 mb-4">
              {{ product.nombre }}
            </h1>

            <!-- Precio y stock -->
            <div class="mb-6">
              <p class="text-4xl font-bold text-blue-600 mb-2">
                Q {{ formatPrice(product.precio) }}
              </p>
              <div class="flex items-center gap-3">
                <span
                  v-if="product.stock > 0"
                  class="px-3 py-1 text-sm font-semibold rounded-full bg-green-100 text-green-800"
                >
                  Disponible ({{ product.stock }} unidades)
                </span>
                <span
                  v-else
                  class="px-3 py-1 text-sm font-semibold rounded-full bg-red-100 text-red-800"
                >
                  Sin stock
                </span>
              </div>
            </div>

            <!-- Descripción -->
            <div class="mb-6">
              <h2 class="text-lg font-semibold text-gray-900 mb-2">
                Descripción
              </h2>
              <p class="text-gray-700 whitespace-pre-line">
                {{ product.descripcion }}
              </p>
            </div>

            <!-- Vendedor -->
            <div class="mb-6 p-4 bg-gray-50 rounded-lg">
              <h3 class="text-sm font-semibold text-gray-900 mb-2">
                Información del vendedor
              </h3>
              <p class="text-gray-700">
                <strong>Vendedor:</strong> {{ product.vendedor?.nombreUsuario || 'Desconocido' }}
              </p>
              <p class="text-gray-700" v-if="product.vendedor?.nombreCompleto">
                <strong>Nombre:</strong> {{ product.vendedor.nombreCompleto }}
              </p>
            </div>

            <!-- Cantidad y agregar al carrito -->
            <div v-if="product.stock > 0" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  Cantidad
                </label>
                <div class="flex items-center gap-3">
                  <button
                    @click="decreaseQuantity"
                    class="btn-secondary px-4 py-2"
                    :disabled="quantity <= 1"
                  >
                    -
                  </button>
                  <input
                    v-model.number="quantity"
                    type="number"
                    min="1"
                    :max="product.stock"
                    class="input-field text-center w-20"
                  />
                  <button
                    @click="increaseQuantity"
                    class="btn-secondary px-4 py-2"
                    :disabled="quantity >= product.stock"
                  >
                    +
                  </button>
                </div>
              </div>

              <button
                @click="addToCart"
                class="btn-primary w-full py-3 text-lg"
              >
                Agregar al carrito - Q {{ formatPrice(product.precio * quantity) }}
              </button>
            </div>

            <div v-else class="p-4 bg-red-50 border border-red-200 rounded-lg">
              <p class="text-red-800 font-semibold">
                Este producto no está disponible en este momento
              </p>
            </div>

            <!-- Información adicional -->
            <div class="mt-6 pt-6 border-t border-gray-200">
              <div class="space-y-2 text-sm text-gray-600">
                <p>
                  <strong>Categoría:</strong> {{ product.categoria || 'Sin categoría' }}
                </p>
                <p>
                  <strong>Fecha de publicación:</strong> {{ formatDate(product.fechaCreacion) }}
                </p>
              </div>
            </div>

              <!-- Reseñas -->
              <div class="mt-10">
                <h2 class="text-xl font-bold mb-4">Reseñas de usuarios</h2>
                <div v-if="reviews.length === 0" class="text-gray-500 mb-4">Este producto aún no tiene reseñas.</div>
                <div v-else class="space-y-4 mb-8">
                  <div v-for="review in reviews" :key="review.id" class="p-4 bg-gray-50 rounded-lg border">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="font-semibold text-blue-700">{{ review.usuarioNombre || 'Usuario' }}</span>
                      <span class="text-yellow-500">★ {{ review.calificacion }}</span>
                    </div>
                    <div class="text-gray-700">{{ review.comentario }}</div>
                    <div class="text-xs text-gray-400 mt-1">{{ formatDate(review.fechaCreacion) }}</div>
                  </div>
                </div>

                <!-- Formulario de nueva reseña -->
                <div v-if="canReview" class="p-6 bg-white rounded-lg border shadow">
                  <h3 class="text-lg font-semibold mb-2">Deja tu reseña</h3>
                  <form @submit.prevent="submitReview" class="space-y-4">
                    <div>
                      <label class="block text-sm font-medium mb-1">Calificación</label>
                      <select v-model="newReview.calificacion" class="input-field w-32" required>
                        <option disabled value="">Selecciona</option>
                        <option v-for="n in 5" :key="n" :value="n">{{ n }} ★</option>
                      </select>
                    </div>
                    <div>
                      <label class="block text-sm font-medium mb-1">Título</label>
                      <input v-model="newReview.titulo" type="text" class="input-field w-full" maxlength="200" minlength="5" required placeholder="Ejemplo: Excelente producto" />
                    </div>
                    <div>
                      <label class="block text-sm font-medium mb-1">Comentario</label>
                      <textarea v-model="newReview.comentario" class="input-field w-full" rows="4" maxlength="2000" minlength="20" required placeholder="Escribe tu experiencia con al menos 20 caracteres..."></textarea>
                    </div>
                    <button type="submit" class="btn-primary">Enviar reseña</button>
                  </form>
                </div>
                <div v-else class="text-gray-400 text-sm mt-4">Solo puedes dejar una reseña si has comprado este producto y no has comentado antes.</div>
              </div>
          </div>
        </div>
      </div>

      <!-- Error -->
      <EmptyState
        v-else
        icon="❌"
        title="Producto no encontrado"
        description="El producto que buscas no existe o ha sido eliminado."
        action-text="Ver todos los productos"
        @action="$router.push('/productos')"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import reviewService from '../services/reviewService'
import { useRoute, useRouter } from 'vue-router'
import { useProductStore } from '../stores/product'
import { useCartStore } from '../stores/cart'
import NavBar from '../components/layout/NavBar.vue'
import EmptyState from '../components/common/EmptyState.vue'
import { useToast } from 'vue-toastification'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const productStore = useProductStore()
const cartStore = useCartStore()

const quantity = ref(1)

const product = computed(() => productStore.currentProduct)
const loading = computed(() => productStore.loading)

const reviews = ref([])
const canReview = ref(false)
const newReview = ref({ calificacion: '', titulo: '', comentario: '' })

const formatPrice = (price) => {
  return new Intl.NumberFormat('es-GT', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(price)
}

const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('es-GT', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const increaseQuantity = () => {
  if (quantity.value < product.value.stock) {
    quantity.value++
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const addToCart = () => {
  // Agregar al carrito usando el store
  cartStore.addItem(product.value, quantity.value)
  
  // Opcional: redirigir al carrito
  // router.push('/carrito')
}

onMounted(async () => {
  const productId = route.params.id
  try {
    await productStore.fetchProductById(productId)
    // Cargar reseñas
    reviews.value = await reviewService.getReviewsByProduct(productId)
    // Validar si el usuario puede comentar (esto normalmente se hace en backend, aquí lo simulamos)
    // Suponiendo que el backend no devuelve la posibilidad, lo simulamos:
    // Si el usuario ya tiene una reseña, no puede comentar
    // En producción, deberías obtener este dato del backend
    const userId = localStorage.getItem('userId') // Ajusta según tu auth
    canReview.value = !reviews.value.some(r => r.usuarioId === userId)
  } catch (error) {
    toast.error('Error al cargar el producto')
    router.push('/productos')
  }
})

const submitReview = async () => {
  const productId = route.params.id
  // Validar longitud mínima del comentario
  if (!newReview.value.comentario || newReview.value.comentario.length < 20) {
    toast.error('El comentario debe tener al menos 20 caracteres.')
    return
  }
  if (!newReview.value.titulo || newReview.value.titulo.length < 5) {
    toast.error('El título debe tener al menos 5 caracteres.')
    return
  }
  try {
    await reviewService.createReview({
      productoId: productId,
      comentario: newReview.value.comentario,
      calificacion: newReview.value.calificacion,
      titulo: newReview.value.titulo
    })
    toast.success('¡Reseña enviada!')
    // Recargar reseñas
    reviews.value = await reviewService.getReviewsByProduct(productId)
    canReview.value = false
    newReview.value = { calificacion: '', titulo: '', comentario: '' }
  } catch (error) {
    toast.error(error?.response?.data?.message || 'No se pudo enviar la reseña')
  }
}
</script>