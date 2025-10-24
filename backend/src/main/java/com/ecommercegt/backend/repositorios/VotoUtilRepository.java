package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.VotoUtil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para VotoUtil
 * Gestiona votos de utilidad en reviews
 */
@Repository
public interface VotoUtilRepository extends JpaRepository<VotoUtil, Long> {
    
    /**
     * Buscar voto de un usuario en una review específica
     */
    Optional<VotoUtil> findByReviewIdAndUsuarioId(Long reviewId, UUID usuarioId);
    
    /**
     * Verificar si un usuario ya votó en una review
     */
    boolean existsByReviewIdAndUsuarioId(Long reviewId, UUID usuarioId);
    
    /**
     * Contar votos útiles (positivos) de una review
     */
    @Query("SELECT COUNT(v) FROM VotoUtil v " +
           "WHERE v.review.id = :reviewId AND v.esUtil = true")
    Long countVotosUtiles(@Param("reviewId") Long reviewId);
    
    /**
     * Contar votos no útiles (negativos) de una review
     */
    @Query("SELECT COUNT(v) FROM VotoUtil v " +
           "WHERE v.review.id = :reviewId AND v.esUtil = false")
    Long countVotosNoUtiles(@Param("reviewId") Long reviewId);
    
    /**
     * Eliminar voto de un usuario en una review
     */
    void deleteByReviewIdAndUsuarioId(Long reviewId, UUID usuarioId);
    
    /**
     * Eliminar todos los votos de una review
     */
    void deleteByReviewId(Long reviewId);
    
    /**
     * Contar total de votos de una review
     */
    Long countByReviewId(Long reviewId);
    
    /**
     * Obtener balance de votos (útiles - no útiles)
     */
    @Query("SELECT " +
           "(SELECT COUNT(v1) FROM VotoUtil v1 WHERE v1.review.id = :reviewId AND v1.esUtil = true) - " +
           "(SELECT COUNT(v2) FROM VotoUtil v2 WHERE v2.review.id = :reviewId AND v2.esUtil = false) " +
           "FROM VotoUtil v WHERE v.review.id = :reviewId")
    Long calcularBalance(@Param("reviewId") Long reviewId);
}