import api from './api';

/**
 * Servicio de pedidos para consumir endpoints del backend
 * Compatible con endpoints definidos en PedidoController.java
 */
export default {
  /**
   * Obtener los pedidos del usuario autenticado (paginados)
   * GET /api/pedidos?page=0&size=10
   * @param {number} page 
   * @param {number} size 
   * @returns {Promise<Array>} Array de pedidos
   */
  async getMyPedidos(page = 0, size = 10) {
  const resp = await api.get('/pedidos', {
      params: { page, size }
    });
    // Si el backend devuelve 'content', retorna el array
    if (resp.data && Array.isArray(resp.data.content)) {
      return resp.data.content;
    }
    // Si el backend devuelve el array directo
    if (Array.isArray(resp.data)) {
      return resp.data;
    }
    // Si no, retorna vacío
    return [];
  },

  /**
   * Obtener el detalle de un pedido
   * GET /api/pedidos/{id}
   * @param {string} id UUID del pedido
   * @returns {Promise<Object>} Pedido
   */
  async getPedidoDetalle(id) {
  const resp = await api.get(`/pedidos/${id}`);
    return resp.data;
  },

  /**
   * Crear pedido desde el carrito
   * POST /api/pedidos/crear-desde-carrito
   * @param {Object} data - CrearPedidoRequest
   * @returns {Promise<Object>} Pedido creado
   */
  async crearPedidoDesdeCarrito(data) {
  const resp = await api.post('/pedidos/crear-desde-carrito', data);
    return resp.data;
  },

  /**
   * Cancelar un pedido (por el dueño)
   * DELETE /api/pedidos/{id}/cancelar
   * @param {string} id UUID del pedido
   * @param {string} motivo Motivo de cancelación opcional
   * @returns {Promise<Object>} Pedido cancelado
   */
  async cancelarPedido(id, motivo) {
  const resp = await api.delete(`/pedidos/${id}/cancelar`, {
      data: motivo ? motivo : ''
    });
    return resp.data;
  },

  /**
   * Actualizar estado de un pedido (Vendedor, Moderador, Admin)
   * PUT /api/pedidos/{id}/estado
   * @param {string} id UUID del pedido
   * @param {Object} data { estado: 'NUEVO_ESTADO' }
   * @returns {Promise<Object>} Pedido actualizado
   */
  async actualizarEstadoPedido(id, data) {
  const resp = await api.put(`/pedidos/${id}/estado`, data);
    return resp.data;
  },

  /**
   * Obtener pedidos como vendedor (productos vendidos)
   * GET /api/pedidos/vendedor?page=0&size=10
   * @param {number} page 
   * @param {number} size 
   * @returns {Promise<Array>} Array de pedidos
   */
  async getPedidosVendedor(page = 0, size = 10) {
  const resp = await api.get('/pedidos/vendedor', {
      params: { page, size }
    });
    if (resp.data && Array.isArray(resp.data.content)) {
      return resp.data.content;
    }
    if (Array.isArray(resp.data)) {
      return resp.data;
    }
    return [];
  },

  /**
   * Obtener todos los pedidos (admin)
   * GET /api/pedidos/admin/todos?page=0&size=10
   * @param {number} page 
   * @param {number} size 
   * @returns {Promise<Array>} Array de pedidos
   */
  async getTodosPedidos(page = 0, size = 10) {
  const resp = await api.get('/pedidos/admin/todos', {
      params: { page, size }
    });
    if (resp.data && Array.isArray(resp.data.content)) {
      return resp.data.content;
    }
    if (Array.isArray(resp.data)) {
      return resp.data;
    }
    return [];
  },

  /**
   * Obtener resumen de pedidos del usuario
   * GET /api/pedidos/resumen
   * @returns {Promise<Object>} Resumen de pedidos
   */
  async getResumenPedidos() {
  const resp = await api.get('/pedidos/resumen');
    return resp.data;
  },

  /**
   * Obtener pedidos en curso (Logística, Admin)
   * GET /api/pedidos/en-curso
   * @returns {Promise<Array>} Array de pedidos en curso
   */
  async getPedidosEnCurso() {
  const resp = await api.get('/pedidos/en-curso');
    return resp.data;
  },

  /**
   * Obtener pedidos próximos a vencer (Logística, Admin)
   * GET /api/pedidos/proximos-vencer
   * @returns {Promise<Array>} Array de pedidos a vencer
   */
  async getPedidosProximosVencer() {
  const resp = await api.get('/pedidos/proximos-vencer');
    return resp.data;
  },

  /**
   * Modificar fecha de entrega (Logística, Admin)
   * PUT /api/pedidos/{id}/fecha-entrega
   * @param {string} id UUID del pedido
   * @param {Object} data { fechaEntregaEstimada: 'YYYY-MM-DDTHH:mm:ss' }
   * @returns {Promise<Object>} Pedido actualizado
   */
  async modificarFechaEntrega(id, data) {
  const resp = await api.put(`/pedidos/${id}/fecha-entrega`, data);
    return resp.data;
  },

  /**
   * Marcar pedido como entregado (Logística, Admin)
   * PUT /api/pedidos/{id}/marcar-entregado
   * @param {string} id UUID del pedido
   * @returns {Promise<Object>} Pedido actualizado
   */
  async marcarComoEntregado(id) {
  const resp = await api.put(`/pedidos/${id}/marcar-entregado`);
    return resp.data;
  }
};