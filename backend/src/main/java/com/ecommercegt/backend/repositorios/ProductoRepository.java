package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Producto;
import com.ecommercegt.backend.models.enums.EstadoProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    
    // Buscar productos por categoría
    Page<Producto> findByCategoriaId(Integer categoriaId, Pageable pageable);
    
    // Buscar productos por vendedor
    Page<Producto> findByVendedorId(UUID vendedorId, Pageable pageable);
    
    // Buscar productos por estado
    Page<Producto> findByEstado(EstadoProducto estado, Pageable pageable);
    
    // Buscar productos aprobados con stock
    Page<Producto> findByEstadoAndStockGreaterThan(EstadoProducto estado, Integer stock, Pageable pageable);
    
    // Buscar productos por nombre o descripción
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Producto> buscarProductos(@Param("query") String query, Pageable pageable);
    
    // Buscar productos por rango de precio
    Page<Producto> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);
    
    // Productos destacados
    List<Producto> findByDestacadoTrueAndEstado(EstadoProducto estado);
    
    // Contar productos por vendedor
    Long countByVendedorId(UUID vendedorId);
    
    // Contar productos por estado
    Long countByEstado(EstadoProducto estado);

    Long countByCategoriaId(Integer categoriaId);
}