<template>
  <div class="card-moderacion">
    <div class="card-header">
      <div class="review-info">
        <h3 class="review-titulo">{{ review.titulo }}</h3>
        <p class="review-comentario">{{ review.comentario }}</p>
        <div class="review-meta">
          <span><strong>Producto:</strong> {{ review.productoNombre }}</span>
          <span><strong>Usuario:</strong> {{ review.usuarioNombre }}</span>
          <span><strong>Calificación:</strong> {{ review.calificacion }} ⭐</span>
          <span><strong>Fecha:</strong> {{ formatDate(review.fechaCreacion) }}</span>
        </div>
      </div>
      <div class="solicitud-meta">
        <span class="badge badge-warning">Pendiente</span>
      </div>
    </div>
    <div class="card-footer">
      <button @click="$emit('aprobar', review.id)" class="btn-success">✓ Aprobar</button>
      <button @click="$emit('rechazar', review.id)" class="btn-danger">✗ Rechazar</button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  review: {
    type: Object,
    required: true
  }
})

defineEmits(['aprobar', 'rechazar'])

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('es-GT', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.card-moderacion {
  background: #fff;
  border-radius: 0.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 1rem;
  border: 1px solid #e5e7eb;
}
.card-header {
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
}
.review-info {
  margin-bottom: 0.5rem;
}
.review-titulo {
  font-size: 1.1rem;
  font-weight: bold;
  color: #1f2937;
}
.review-comentario {
  font-size: 0.95rem;
  color: #374151;
  margin-bottom: 0.5rem;
}
.review-meta {
  font-size: 0.85rem;
  color: #6b7280;
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}
.badge-warning {
  background: #fef3c7;
  color: #92400e;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 500;
}
.card-footer {
  padding: 1rem;
  display: flex;
  gap: 1rem;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
}
.btn-success {
  background: #16a34a;
  color: #fff;
  border: none;
  border-radius: 0.375rem;
  padding: 0.5rem 1rem;
  font-size: 0.95rem;
  cursor: pointer;
}
.btn-danger {
  background: #dc2626;
  color: #fff;
  border: none;
  border-radius: 0.375rem;
  padding: 0.5rem 1rem;
  font-size: 0.95rem;
  cursor: pointer;
}
</style>