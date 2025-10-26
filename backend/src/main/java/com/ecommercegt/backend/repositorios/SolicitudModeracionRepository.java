package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.SolicitudModeracion;
import com.ecommercegt.backend.models.enums.EstadoSolicitudModeracion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolicitudModeracionRepository extends JpaRepository<SolicitudModeracion, UUID> {
    
    // Buscar solicitudes por estado
    Page<SolicitudModeracion> findByEstado(EstadoSolicitudModeracion estado, Pageable pageable);
    
    // Buscar solicitudes de un moderador
    Page<SolicitudModeracion> findByModeradorId(UUID moderadorId, Pageable pageable);
    
    // Buscar solicitud por producto
    Optional<SolicitudModeracion> findByProductoId(UUID productoId);
    
    // Buscar solicitudes de un solicitante
    Page<SolicitudModeracion> findBySolicitanteId(UUID solicitanteId, Pageable pageable);
    
    // Buscar solicitudes pendientes con producto eager
    @Query("SELECT s FROM SolicitudModeracion s " +
           "JOIN FETCH s.producto p " +
           "LEFT JOIN FETCH p.categoria " +
           "LEFT JOIN FETCH p.imagenes " +
           "JOIN FETCH s.solicitante " +
           "WHERE s.estado = :estado")
    Page<SolicitudModeracion> findByEstadoWithDetails(@Param("estado") EstadoSolicitudModeracion estado, Pageable pageable);
    
    //  CONTAR SOLICITUDES POR ESTADO
    Long countByEstado(EstadoSolicitudModeracion estado);
}