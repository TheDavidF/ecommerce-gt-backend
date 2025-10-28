<template>
  <div class="reporte-container">
    <h2 class="reporte-titulo">Reporte de Notificaciones</h2>
    <div class="reporte-datos">
      <div class="reporte-dato">
        <span class="icono">📋</span>
        Total: {{ total }}
      </div>
      <div class="reporte-dato">
        <span class="icono">📭</span>
        No leídas: {{ noLeidas }}
      </div>
      <div class="reporte-dato">
        <span class="icono">✅</span>
        Leídas: {{ leidas }}
      </div>
      <div class="reporte-dato">
        <span class="icono">⚠️</span>
        Sanciones: {{ sanciones }}
      </div>
    </div>
  </div>
</template>

<script>
import { obtenerHistorialNotificaciones } from '@/services/notificacionService.js';
export default {
  data() {
    return {
      total: 0,
      noLeidas: 0,
      leidas: 0,
      sanciones: 0
    };
  },
  methods: {
    async cargarReporte() {
      const res = await obtenerHistorialNotificaciones(0, 100);
      const notificaciones = res.data.content;
      this.total = notificaciones.length;
      this.noLeidas = notificaciones.filter(n => !n.leida).length;
      this.leidas = notificaciones.filter(n => n.leida).length;
      this.sanciones = notificaciones.filter(n => n.tipo === 'USUARIO_SANCIONADO').length;
    }
  },
  mounted() {
    this.cargarReporte();
  }
};
</script>

<style scoped>
.reporte-container {
  background: #f8fafc;
  border-radius: 10px;
  padding: 22px 28px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px #0001;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.reporte-titulo {
  font-size: 1.25em;
  font-weight: 600;
  color: #1976d2;
  margin-bottom: 10px;
}
.reporte-datos {
  display: flex;
  gap: 32px;
  margin-bottom: 8px;
}
.reporte-dato {
  background: #fff;
  border-radius: 6px;
  padding: 10px 18px;
  box-shadow: 0 1px 4px #0001;
  font-size: 1.08em;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}
.reporte-dato .icono {
  font-size: 1.2em;
  color: #1976d2;
}
</style>
