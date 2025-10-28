<template>
  <div>
    <h2>Historial de notificaciones</h2>
    <div style="margin-bottom:10px;">
      <strong>Usuario autenticado:</strong>
      <span>{{ usuarioJWT }}</span>
    </div>
    <div v-if="notificaciones.length === 0">
      <span v-if="pagina === 0">No tienes notificaciones.</span>
      <span v-else>Esta página no tiene notificaciones.</span>
    </div>
    <div v-for="n in notificaciones" :key="n.id" class="notificacion-card" :class="{'leida': n.leida}">
      <strong>{{ n.titulo }}</strong>
      <p>{{ n.mensaje }}</p>
  <small>{{ n.tiempoRelativo || n.fechaCreacion }}</small>
      <div style="margin-top:8px;">
        <span :class="['badge', n.leida ? 'badge-success' : 'badge-warning']">
          {{ n.leida ? 'Leída' : 'No leída' }}
        </span>
  <button v-if="!n.leida" @click="marcarComoLeida(n.id)" class="btn-leer">Marcar como leída</button>
      </div>
    </div>
    <div class="paginacion">
      <button @click="irAPrimera" :disabled="pagina === 0">Primera</button>
      <button @click="cambiarPagina(pagina - 1)" :disabled="pagina === 0">Anterior</button>
      <span>Página {{ pagina + 1 }} de {{ totalPaginas }}</span>
      <button @click="cambiarPagina(pagina + 1)" :disabled="pagina + 1 >= totalPaginas">Siguiente</button>
      <button @click="irAUltima" :disabled="pagina + 1 >= totalPaginas">Última</button>
    </div>
  </div>
</template>

<script>
import { obtenerHistorialNotificaciones, marcarNotificacionComoLeida, eliminarNotificacion } from '@/services/notificacionService.js';
export default {
  data() {
    return {
      notificaciones: [],
      pagina: 0,
      totalPaginas: 1,
      usuarioJWT: ''
    };
  },
  methods: {
    cargarNotificaciones() {
      obtenerHistorialNotificaciones(this.pagina, 20)
        .then(res => {
          if (res.data && res.data.content) {
            this.notificaciones = res.data.content;
            this.totalPaginas = res.data.totalPages || 1;
          } else {
            this.notificaciones = [];
            this.totalPaginas = 1;
            this.$notify ? this.$notify({ type: 'error', text: 'Respuesta inesperada del servidor.' }) : alert('Respuesta inesperada del servidor.');
          }
        })
        .catch(err => {
          let msg = 'Error al cargar notificaciones.';
          if (err.response && err.response.status === 401) {
            msg = 'No estás autenticado. Inicia sesión.';
          } else if (err.response && err.response.status === 403) {
            msg = 'No tienes permisos para ver las notificaciones.';
          }
          this.notificaciones = [];
          this.totalPaginas = 1;
          this.$notify ? this.$notify({ type: 'error', text: msg }) : alert(msg);
        });
    },
    cambiarPagina(nuevaPagina) {
      this.pagina = nuevaPagina;
      this.cargarNotificaciones();
    },
    marcarComoLeida(id) {
      marcarNotificacionComoLeida(id).then(() => {
        this.cargarNotificaciones();
      });
    },
    eliminarNotificacion(id) {
      eliminarNotificacion(id).then(() => {
        this.cargarNotificaciones();
      });
    },
    irAnterior() {
      if (this.pagina > 0) this.cambiarPagina(this.pagina - 1);
    },
    irSiguiente() {
      if (this.pagina + 1 < this.totalPaginas) this.cambiarPagina(this.pagina + 1);
    },
    irAPrimera() {
      this.cambiarPagina(0);
    },
    irAUltima() {
      this.cambiarPagina(this.totalPaginas - 1);
    }
  },
  mounted() {
    this.cargarNotificaciones();
    this.usuarioJWT = this.obtenerUsuarioJWT();
  },
  methods: {
    obtenerUsuarioJWT() {
      const token = localStorage.getItem('token');
      if (!token) return 'No autenticado';
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.username || payload.sub || payload.email || JSON.stringify(payload);
      } catch (e) {
        return 'Token inválido';
      }
    },
    cargarNotificaciones() {
      obtenerHistorialNotificaciones(this.pagina, 20)
        .then(res => {
          if (res.data && res.data.content) {
            this.notificaciones = res.data.content;
            this.totalPaginas = res.data.totalPages || 1;
          } else {
            this.notificaciones = [];
            this.totalPaginas = 1;
            this.$notify ? this.$notify({ type: 'error', text: 'Respuesta inesperada del servidor.' }) : alert('Respuesta inesperada del servidor.');
          }
        })
        .catch(err => {
          let msg = 'Error al cargar notificaciones.';
          if (err.response && err.response.status === 401) {
            msg = 'No estás autenticado. Inicia sesión.';
          } else if (err.response && err.response.status === 403) {
            msg = 'No tienes permisos para ver las notificaciones.';
          }
          this.notificaciones = [];
          this.totalPaginas = 1;
          this.$notify ? this.$notify({ type: 'error', text: msg }) : alert(msg);
        });
    },
    cambiarPagina(nuevaPagina) {
      this.pagina = nuevaPagina;
      this.cargarNotificaciones();
    },
    marcarComoLeida(id) {
      marcarNotificacionComoLeida(id).then(() => {
        this.cargarNotificaciones();
      });
    },
    eliminarNotificacion(id) {
      eliminarNotificacion(id).then(() => {
        this.cargarNotificaciones();
      });
    },
    irAnterior() {
      if (this.pagina > 0) this.cambiarPagina(this.pagina - 1);
    },
    irSiguiente() {
      if (this.pagina + 1 < this.totalPaginas) this.cambiarPagina(this.pagina + 1);
    },
    irAPrimera() {
      this.cambiarPagina(0);
    },
    irAUltima() {
      this.cambiarPagina(this.totalPaginas - 1);
    }
  }
};
</script>

<style scoped>
.notificacion-card {
  border: 1px solid #e3e6ee;
  padding: 16px 18px;
  margin-bottom: 14px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 2px 8px #0001;
  position: relative;
  transition: box-shadow 0.2s, background 0.2s;
}
.notificacion-card.leida {
  background: #f0f4f8;
  opacity: 0.85;
}
.notificacion-card:hover {
  box-shadow: 0 4px 16px #0002;
}
.notificacion-card strong {
  font-size: 1.08em;
  color: #2c3e50;
}
.notificacion-card p {
  margin: 6px 0 2px 0;
  color: #444;
}
.notificacion-card small {
  color: #888;
  font-size: 0.92em;
}
.badge {
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 0.85em;
  margin-right: 8px;
  font-weight: 500;
}
.badge-warning { background: #ffe082; color: #7c6f00; }
.badge-success { background: #b2f2bb; color: #155724; }
.btn-leer, .btn-eliminar {
  margin-left: 8px;
  padding: 4px 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95em;
  font-weight: 500;
  transition: background 0.2s;
}
.btn-leer { background: #1976d2; color: #fff; }
.btn-leer:hover { background: #1565c0; }
.btn-eliminar { background: #e53935; color: #fff; }
.btn-eliminar:hover { background: #b71c1c; }
.paginacion {
  margin-top: 18px;
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: center;
}
.paginacion button {
  background: #f4f6fa;
  border: 1px solid #e3e6ee;
  border-radius: 6px;
  padding: 6px 18px;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s;
}
.paginacion button:disabled {
  background: #eee;
  color: #aaa;
  cursor: not-allowed;
}
</style>
