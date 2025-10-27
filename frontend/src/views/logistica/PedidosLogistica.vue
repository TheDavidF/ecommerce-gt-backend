<template>
  <div class="container mx-auto px-4 py-6">
    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-800">Gestión de Logística</h1>
    </div>

    <!-- Alertas de pedidos próximos a vencer -->
    <div v-if="pedidosProximosVencer.length > 0" class="mb-6">
      <div class="bg-orange-50 border border-orange-200 rounded-lg p-4">
        <div class="flex items-center gap-2 mb-2">
          <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
          </svg>
          <h3 class="text-lg font-bold text-orange-800">Pedidos próximos a vencer</h3>
        </div>
        <p class="text-orange-700">
          Hay {{ pedidosProximosVencer.length }} pedido(s) con entrega estimada en menos de 24 horas.
        </p>
      </div>
    </div>

    <!-- Filtros por estado -->
    <FiltroPedidos
      v-model="estadoFiltro"
      :contadores="contadoresPorEstadoFiltrados"
      :estados="['TODOS', 'ENVIADO', 'ENTREGADO']"
    />

    <!-- Loading -->
    <div v-if="cargando" class="flex flex-col items-center justify-center py-12">
      <div class="w-16 h-16 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
      <p class="mt-4 text-gray-600">Cargando pedidos...</p>
    </div>

    <!-- Lista de pedidos -->
    <div v-if="pedidosFiltrados.length > 0">
      <TarjetaPedido
        v-for="pedido in pedidosFiltrados"
        :key="pedido.id"
        :pedido="pedido"
        @ver-detalle="verDetallePedido"
        @modificar-fecha="abrirModalFecha"
        @marcar-entregado="confirmarEntrega"
      />
    </div>

    <!-- Sin resultados -->
    <div v-else class="text-center py-12">
      <svg class="w-24 h-24 mx-auto text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path>
      </svg>
      <h3 class="text-xl font-semibold text-gray-600 mt-4">No hay pedidos para mostrar</h3>
    </div>

    <!-- Modal para modificar fecha -->
    <ModalFechaEntrega
      v-if="pedidoSeleccionado && (pedidoSeleccionado.estado === 'ENVIADO' || pedidoSeleccionado.estado === 'ENTREGADO')"
      v-model="modalFechaVisible"
      :pedido="pedidoSeleccionado"
      @guardar="guardarNuevaFecha"
    />

    <!-- Modal de detalle de pedido -->
    <div v-if="modalDetalleVisible" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40">
      <div class="bg-white rounded-lg shadow-lg max-w-xl w-full p-6 relative">
        <button @click="cerrarModalDetalle" class="absolute top-2 right-2 text-gray-500 hover:text-gray-700 text-xl">&times;</button>
        <h2 class="text-2xl font-bold mb-4">Detalle del pedido</h2>
        <div v-if="detallePedido">
          <div class="mb-2"><strong>ID:</strong> {{ detallePedido.id }}</div>
          <div class="mb-2"><strong>Cliente:</strong> {{ detallePedido.clienteNombre || detallePedido.cliente?.nombre || 'N/A' }}</div>
          <div class="mb-2"><strong>Estado:</strong> {{ detallePedido.estado }}</div>
          <div class="mb-2"><strong>Dirección de envío:</strong> {{ detallePedido.direccionEnvio }}</div>
          <div class="mb-2"><strong>Teléfono:</strong> {{ detallePedido.telefonoContacto }}</div>
          <div class="mb-2"><strong>Método de pago:</strong> {{ detallePedido.metodoPago }}</div>
          <div class="mb-2"><strong>Fecha de creación:</strong> {{ detallePedido.fechaCreacion }} </div>
          <div class="mb-4"><strong>Total:</strong> Q {{ formatPrice(detallePedido.total) }}</div>
          <div>
            <strong>Productos:</strong>
            <ul class="list-disc pl-6">
              <li v-for="item in detallePedido.items || detallePedido.productos || []" :key="item.id">
                {{ item.productoNombre || item.nombre || 'Producto' }} - Cantidad: {{ item.cantidad }} - Q {{ formatPrice(item.precioUnitario || item.precio || 0) }}
              </li>
            </ul>
          </div>
          <!-- Solo mostrar acción si el estado es ENVIADO -->
          <div v-if="detallePedido.estado === 'ENVIADO'" class="mt-6">
            <button @click="confirmarEntrega(detallePedido)" class="btn-primary w-full">Marcar como entregado</button>
          </div>
        </div>
        <div v-else class="text-center text-gray-500">Cargando detalle...</div>
      </div>
    </div>

    <!-- Snackbar para notificaciones -->
    <Transition name="snackbar">
      <div
        v-if="snackbar.visible"
        :class="[
          'fixed top-4 right-4 px-6 py-4 rounded-lg shadow-lg z-50',
          snackbar.color === 'success' ? 'bg-green-600' : 'bg-red-600',
          'text-white font-medium'
        ]"
      >
        {{ snackbar.mensaje }}
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import FiltroPedidos from '@/components/logistica/FiltroPedidos.vue';
import TarjetaPedido from '@/components/logistica/TarjetaPedido.vue';
import ModalFechaEntrega from '@/components/logistica/ModalFechaEntrega.vue';
import {
  obtenerPedidosEnCurso,
  obtenerPedidosProximosVencer,
  modificarFechaEntrega,
  marcarComoEntregado
} from '@/services/logisticaService';

const router = useRouter();

// Estados
const cargando = ref(false);
const pedidos = ref([]);
const pedidosProximosVencer = ref([]);
const estadoFiltro = ref('TODOS');
const modalFechaVisible = ref(false);
const pedidoSeleccionado = ref(null);
const modalDetalleVisible = ref(false);
const detallePedido = ref(null);

const snackbar = ref({
  visible: false,
  mensaje: '',
  color: 'success'
});

// Computed
const pedidosFiltrados = computed(() => {
  // Mostrar ENVIADO y ENTREGADO, o ambos si filtro es TODOS
  if (estadoFiltro.value === 'TODOS') {
    return pedidos.value.filter(p => p.estado === 'ENVIADO' || p.estado === 'ENTREGADO');
  }
  return pedidos.value.filter(p => p.estado === estadoFiltro.value);
});

const contadoresPorEstadoFiltrados = computed(() => {
  // Contar ENVIADO, ENTREGADO y TODOS
  const contadores = {
    TODOS: 0,
    ENVIADO: 0,
    ENTREGADO: 0
  };
  pedidos.value.forEach(pedido => {
    if (pedido.estado === 'ENVIADO') contadores.ENVIADO++;
    if (pedido.estado === 'ENTREGADO') contadores.ENTREGADO++;
  });
  contadores.TODOS = contadores.ENVIADO + contadores.ENTREGADO;
  return contadores;
});

// Métodos
const cargarPedidos = async () => {
  cargando.value = true;
  try {
    const [pedidosData, proximosData] = await Promise.all([
      obtenerPedidosEnCurso(),
      obtenerPedidosProximosVencer()
    ]);
    
    pedidos.value = pedidosData;
    pedidosProximosVencer.value = proximosData;
  } catch (error) {
    mostrarSnackbar('Error al cargar pedidos: ' + error.message, 'error');
  } finally {
    cargando.value = false;
  }
};

const verDetallePedido = async (pedido) => {
  modalDetalleVisible.value = true;
  detallePedido.value = null;
  try {
    // Usar el servicio de pedidos para obtener el detalle
    const resp = await import('@/services/pedidoService').then(m => m.default.getPedidoDetalle(pedido.id));
    detallePedido.value = resp;
  } catch (error) {
    detallePedido.value = null;
    mostrarSnackbar('Error al cargar detalle: ' + error.message, 'error');
  }
};

const cerrarModalDetalle = () => {
  modalDetalleVisible.value = false;
  detallePedido.value = null;
};

const abrirModalFecha = (pedido) => {
  pedidoSeleccionado.value = pedido;
  modalFechaVisible.value = true;
};

const guardarNuevaFecha = async (fechaNueva) => {
  try {
    await modificarFechaEntrega(pedidoSeleccionado.value.id, fechaNueva);
    mostrarSnackbar('Fecha de entrega actualizada correctamente', 'success');
    await cargarPedidos();
  } catch (error) {
    mostrarSnackbar('Error al actualizar fecha: ' + error.message, 'error');
  }
};

const confirmarEntrega = async (pedido) => {
  if (!confirm(`¿Marcar pedido ${pedido.numeroOrden} como entregado?`)) {
    return;
  }

  try {
    await marcarComoEntregado(pedido.id);
    mostrarSnackbar('Pedido marcado como entregado', 'success');
    await cargarPedidos();
  } catch (error) {
    mostrarSnackbar('Error al marcar como entregado: ' + error.message, 'error');
  }
};

const mostrarSnackbar = (mensaje, color = 'success') => {
  snackbar.value = {
    visible: true,
    mensaje,
    color
  };

  setTimeout(() => {
    snackbar.value.visible = false;
  }, 3000);
};

// Lifecycle
onMounted(() => {
  cargarPedidos();
});

const formatPrice = (price) => {
  return new Intl.NumberFormat('es-GT', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(price || 0);
};
</script>

<style scoped>
.snackbar-enter-active,
.snackbar-leave-active {
  transition: all 0.3s ease;
}

.snackbar-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.snackbar-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>