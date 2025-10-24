package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para Review
 * Gestiona reseñas de productos
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Buscar todas las reviews de un producto
     */
    List<Review> findByProductoId(UUID productoId);

    /**
     * Buscar reviews aprobadas de un producto
     * Ordenadas por votos útiles (más populares primero)
     */
    @Query("SELECT r FROM Review r " +
            "LEFT JOIN VotoUtil v ON v.review.id = r.id AND v.esUtil = true " +
            "WHERE r.producto.id = :productoId AND r.aprobado = true " +
            "GROUP BY r.id " +
            "ORDER BY COUNT(v.id) DESC, r.fechaCreacion DESC")
    Page<Review> findByProductoIdAndAprobadoTrueOrderByVotosUtiles(
            @Param("productoId") UUID productoId,
            Pageable pageable);

    /**
     * Buscar reviews de un usuario
     */
    Page<Review> findByUsuarioIdOrderByFechaCreacionDesc(UUID usuarioId, Pageable pageable);

    /**
     * Buscar review específica de un usuario para un producto
     */
    Optional<Review> findByProductoIdAndUsuarioId(UUID productoId, UUID usuarioId);

    /**
     * Verificar si un usuario ya dejó review en un producto
     */
    boolean existsByProductoIdAndUsuarioId(UUID productoId, UUID usuarioId);

    /**
     * Contar reviews aprobadas de un producto
     */
    Long countByProductoIdAndAprobadoTrue(UUID productoId);

    /**
     * Contar todas las reviews de un producto
     */
    Long countByProductoId(UUID productoId);

    /**
     * Calcular promedio de calificación de un producto
     * Solo considera reviews aprobadas
     */
    @Query("SELECT AVG(r.calificacion) FROM Review r " +
            "WHERE r.producto.id = :productoId AND r.aprobado = true")
    Double calcularPromedioCalificacion(@Param("productoId") UUID productoId);

    /**
     * Contar reviews por calificación (para distribución de estrellas)
     */
    @Query("SELECT r.calificacion, COUNT(r) FROM Review r " +
            "WHERE r.producto.id = :productoId AND r.aprobado = true " +
            "GROUP BY r.calificacion")
    List<Object[]> contarPorCalificacion(@Param("productoId") UUID productoId);

    /**
     * Contar reviews NO aprobadas (pendientes de moderación)
     */
    Long countByAprobadoFalse();

    /**
     * Contar reviews verificadas (compra confirmada)
     */
    Long countByProductoIdAndAprobadoTrueAndVerificadoTrue(UUID productoId);

    /**
     * Buscar reviews pendientes de aprobación
     */
    Page<Review> findByAprobadoFalseOrderByFechaCreacionDesc(Pageable pageable);

    /**
     * Buscar reviews de un producto por estado de aprobación
     */
    List<Review> findByProductoIdAndAprobado(UUID productoId, Boolean aprobado);

    /**
     * Obtener reviews más útiles (top rated)
     */
    @Query("SELECT r FROM Review r " +
            "LEFT JOIN VotoUtil v ON v.review.id = r.id AND v.esUtil = true " +
            "WHERE r.aprobado = true " +
            "GROUP BY r.id " +
            "HAVING COUNT(v.id) >= :minVotos " +
            "ORDER BY COUNT(v.id) DESC")
    Page<Review> findReviewsMasUtiles(
            @Param("minVotos") Long minVotos,
            Pageable pageable);
}
