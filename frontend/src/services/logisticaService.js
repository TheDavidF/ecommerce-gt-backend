import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

/**
 * Obtener pedidos en curso (PENDIENTE, CONFIRMADO, EN_PREPARACION, ENVIADO)
 */
export const obtenerPedidosEnCurso = async () => {
  const token = localStorage.getItem('token');
  const response = await axios.get(`${API_URL}/pedidos/en-curso`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

/**
 * Obtener pedidos próximos a vencer (menos de 24h)
 */
export const obtenerPedidosProximosVencer = async () => {
  const token = localStorage.getItem('token');
  const response = await axios.get(`${API_URL}/pedidos/proximos-vencer`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

/**
 * Modificar fecha de entrega estimada
 */
export const modificarFechaEntrega = async (pedidoId, fechaEntregaEstimada) => {
  const token = localStorage.getItem('token');
  const response = await axios.put(
    `${API_URL}/pedidos/${pedidoId}/fecha-entrega`,
    { fechaEntregaEstimada },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

/**
 * Marcar pedido como entregado
 */
export const marcarComoEntregado = async (pedidoId) => {
  const token = localStorage.getItem('token');
  const response = await axios.put(
    `${API_URL}/pedidos/${pedidoId}/marcar-entregado`,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};