package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Integer> {
    
    /**
     * Buscar items de un carrito
     */
    List<ItemCarrito> findByCarritoId(UUID carritoId);
    
    /**
     * Buscar item específico en un carrito por producto
     */
    Optional<ItemCarrito> findByCarritoIdAndProductoId(UUID carritoId, UUID productoId);
    
    /**
     * Verificar si producto ya está en el carrito
     */
    Boolean existsByCarritoIdAndProductoId(UUID carritoId, UUID productoId);
    
    /**
     * Eliminar todos los items de un carrito
     */
    void deleteByCarritoId(UUID carritoId);
    
    /**
     * Contar items en un carrito
     */
    Long countByCarritoId(UUID carritoId);
    
    /**
     * Buscar items con producto cargado (eager)
     */
    @Query("SELECT i FROM ItemCarrito i JOIN FETCH i.producto WHERE i.carrito.id = :carritoId")
    List<ItemCarrito> findByCarritoIdWithProducto(@Param("carritoId") UUID carritoId);
}