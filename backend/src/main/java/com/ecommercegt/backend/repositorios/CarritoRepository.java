package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, UUID> {
    
    /**
     * Buscar carrito por usuario ID
     */
    Optional<Carrito> findByUsuarioId(UUID usuarioId);
    
    /**
     * Verificar si usuario tiene carrito
     */
    Boolean existsByUsuarioId(UUID usuarioId);
    
    /**
     * Buscar carrito con items cargados (eager loading)
     */
    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.items WHERE c.usuario.id = :usuarioId")
    Optional<Carrito> findByUsuarioIdWithItems(@Param("usuarioId") UUID usuarioId);
    
    /**
     * Eliminar carrito por usuario ID
     */
    void deleteByUsuarioId(UUID usuarioId);
}