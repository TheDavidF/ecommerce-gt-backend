import api from './api';

export function obtenerHistorialNotificaciones(page = 0, size = 20) {
  return api.get(`/notificaciones?page=${page}&size=${size}`)
    .then(res => {
      console.log('Respuesta notificaciones:', res);
      return res;
    })
    .catch(err => {
      console.error('Error al obtener notificaciones:', err);
      throw err;
    });
}

export function marcarNotificacionComoLeida(id) {
  return api.put(`/notificaciones/${id}/leer`);
}

export function eliminarNotificacion(id) {
  return api.delete(`/notificaciones/${id}`);
}
