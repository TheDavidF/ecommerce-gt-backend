<template>
  <div class="cart-item">
    <div class="cart-item-image">
      <img 
        :src="item.productoImagen || '/placeholder.jpg'" 
        :alt="item.productoNombre"
        @error="handleImageError"
      >
    </div>
    
    <div class="cart-item-details">
      <h3 class="cart-item-title">{{ item.productoNombre }}</h3>
      <p class="cart-item-price">Q{{ formatPrice(item.precioUnitario) }}</p>
      <p class="cart-item-stock">Stock disponible: {{ item.productoStock }}</p>
    </div>
    
    <div class="cart-item-quantity">
      <button 
        @click="decrementQuantity" 
        :disabled="loading || item.cantidad <= 1"
        class="quantity-btn"
      >
        -
      </button>
      <span class="quantity-value">{{ item.cantidad }}</span>
      <button 
        @click="incrementQuantity" 
        :disabled="loading || item.cantidad >= item.productoStock"
        class="quantity-btn"
      >
        +
      </button>
    </div>
    
    <div class="cart-item-subtotal">
      <p class="subtotal-label">Subtotal</p>
      <p class="subtotal-value">Q{{ formatPrice(item.subtotal) }}</p>
    </div>
    
    <div class="cart-item-actions">
      <button 
        @click="removeItem" 
        :disabled="loading"
        class="remove-btn"
        title="Eliminar del carrito"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useCartStore } from '../../stores/cart'
import { useToast } from 'vue-toastification'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const cartStore = useCartStore()
const toast = useToast()
const loading = ref(false)

const formatPrice = (price) => {
  return new Intl.NumberFormat('es-GT', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(price)
}

const incrementQuantity = async () => {
  if (props.item.cantidad >= props.item.productoStock) {
    toast.warning('No hay más stock disponible')
    return
  }
  
  loading.value = true
  try {
    await cartStore.updateQuantity(props.item.id, props.item.cantidad + 1)
  } catch (error) {
    console.error('Error al incrementar cantidad:', error)
  } finally {
    loading.value = false
  }
}

const decrementQuantity = async () => {
  if (props.item.cantidad <= 1) {
    return
  }
  
  loading.value = true
  try {
    await cartStore.updateQuantity(props.item.id, props.item.cantidad - 1)
  } catch (error) {
    console.error('Error al decrementar cantidad:', error)
  } finally {
    loading.value = false
  }
}

const removeItem = async () => {
  if (!confirm(`¿Estás seguro de eliminar "${props.item.productoNombre}" del carrito?`)) {
    return
  }
  
  loading.value = true
  try {
    await cartStore.removeItem(props.item.id)
    toast.success('Producto eliminado del carrito')
  } catch (error) {
    console.error('Error al eliminar item:', error)
    toast.error('Error al eliminar el producto')
  } finally {
    loading.value = false
  }
}

const handleImageError = (event) => {
  event.target.src = 'https://via.placeholder.com/150?text=Sin+Imagen'
}
</script>

<style scoped>
.cart-item {
  display: grid;
  grid-template-columns: 100px 1fr auto auto auto;
  gap: 1.5rem;
  align-items: center;
  padding: 1.5rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
}

.cart-item-image {
  width: 100px;
  height: 100px;
  border-radius: 0.5rem;
  overflow: hidden;
  background: #f3f4f6;
}

.cart-item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-item-details {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.cart-item-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.cart-item-price {
  font-size: 1rem;
  color: #3b82f6;
  font-weight: 600;
  margin: 0;
}

.cart-item-stock {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0;
}

.cart-item-quantity {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem;
  background: #f9fafb;
  border-radius: 0.5rem;
}

.quantity-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 0.375rem;
  font-size: 1.25rem;
  cursor: pointer;
  transition: all 0.2s;
}

.quantity-btn:hover:not(:disabled) {
  background: #f3f4f6;
  border-color: #3b82f6;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-value {
  min-width: 40px;
  text-align: center;
  font-weight: 600;
  font-size: 1rem;
}

.cart-item-subtotal {
  text-align: right;
}

.subtotal-label {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0 0 0.25rem 0;
}

.subtotal-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.cart-item-actions {
  display: flex;
  align-items: center;
}

.remove-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fee2e2;
  border: none;
  border-radius: 0.5rem;
  color: #dc2626;
  cursor: pointer;
  transition: all 0.2s;
}

.remove-btn:hover:not(:disabled) {
  background: #fecaca;
  transform: scale(1.05);
}

.remove-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.icon {
  width: 20px;
  height: 20px;
}

/* Responsive */
@media (max-width: 768px) {
  .cart-item {
    grid-template-columns: 80px 1fr;
    gap: 1rem;
    padding: 1rem;
  }

  .cart-item-image {
    width: 80px;
    height: 80px;
  }

  .cart-item-quantity {
    grid-column: 1 / -1;
    justify-content: center;
  }

  .cart-item-subtotal {
    grid-column: 1 / -1;
    text-align: center;
  }

  .cart-item-actions {
    grid-column: 1 / -1;
    justify-content: center;
  }
}
</style>