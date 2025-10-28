<template>
  <div class="notificaciones-view">
    <ReporteNotificaciones />
    <HistorialNotificaciones />
  </div>
</template>

<script>
import ReporteNotificaciones from '../components/ReporteNotificaciones.vue';
import HistorialNotificaciones from '../components/HistorialNotificaciones.vue';

export default {
  name: 'Notificaciones',
  components: {
    ReporteNotificaciones,
    HistorialNotificaciones
  }
}
</script>

<style scoped>
.notificaciones-view {
  max-width: 700px;
  margin: 0 auto;
  padding: 32px 0 24px 0;
  background: #f4f6fa;
  min-height: 100vh;
}
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px #0002;
  padding: 24px 32px;
  margin-bottom: 24px;
}
.reporte-card {
  min-width: 260px;
  max-width: 320px;
}
.historial-card {
  flex: 1;
  min-width: 340px;
}
</style>
