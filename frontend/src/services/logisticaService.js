import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Instancia de axios sin interceptor de autenticación para endpoints públicos
const publicAxios = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'ngrok-skip-browser-warning': 'true'
  }
});

/**
 * Obtener pedidos en curso (PENDIENTE, CONFIRMADO, EN_PREPARACION, ENVIADO)
 */
export const obtenerPedidosEnCurso = async () => {
  const response = await publicAxios.get(`pedidos/en-curso`);
  return response.data;
};

/**
 * Obtener pedidos próximos a vencer (menos de 24h)
 */
export const obtenerPedidosProximosVencer = async () => {
  const response = await publicAxios.get(`pedidos/proximos-vencer`);
  return response.data;
};

/**
 * Modificar fecha de entrega estimada
 */
export const modificarFechaEntrega = async (pedidoId, fechaEntregaEstimada) => {
  const response = await publicAxios.put(
    `pedidos/${pedidoId}/fecha-entrega`,
    { fechaEntregaEstimada }
  );
  return response.data;
};

/**
 * Marcar pedido como entregado
 */
export const marcarComoEntregado = async (pedidoId) => {
  const response = await publicAxios.put(
    `pedidos/${pedidoId}/marcar-entregado`,
    {}
  );
  return response.data;
};