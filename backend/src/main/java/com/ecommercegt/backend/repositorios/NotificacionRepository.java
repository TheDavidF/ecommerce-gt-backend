package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Notificacion;
import com.ecommercegt.backend.models.enums.TipoNotificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    /**
     * Obtener notificaciones de un usuario (paginadas)
     */
    Page<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(UUID usuarioId, Pageable pageable);
    
    /**
     * Obtener notificaciones no leídas de un usuario
     */
    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(UUID usuarioId);
    
    /**
     * Contar notificaciones no leídas de un usuario
     */
    Long countByUsuarioIdAndLeidaFalse(UUID usuarioId);
    
    /**
     * Obtener notificaciones por tipo
     */
    Page<Notificacion> findByUsuarioIdAndTipoOrderByFechaCreacionDesc(
        UUID usuarioId, TipoNotificacion tipo, Pageable pageable);
    
    /**
     * Marcar todas como leídas
     */
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true, n.fechaLectura = CURRENT_TIMESTAMP " +
           "WHERE n.usuario.id = :usuarioId AND n.leida = false")
    int marcarTodasComoLeidas(@Param("usuarioId") UUID usuarioId);
    
    /**
     * Eliminar notificaciones antiguas (más de X días)
     */
    @Modifying
    @Query("DELETE FROM Notificacion n WHERE n.fechaCreacion < :fecha")
    int eliminarAntiguasPorFecha(@Param("fecha") java.time.LocalDateTime fecha);
    
    /**
     * Obtener últimas notificaciones (para vista rápida)
     */
    List<Notificacion> findTop5ByUsuarioIdOrderByFechaCreacionDesc(UUID usuarioId);
}