package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Producto;
import com.ecommercegt.backend.models.enums.EstadoProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio de Productos
 * Con JpaSpecificationExecutor para búsquedas complejas
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID>, 
                                            JpaSpecificationExecutor<Producto> {
    
    // ==================== BÚSQUEDA BÁSICA ====================
    
    /**
     * Buscar por nombre (case insensitive)
     */
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    
    /**
     * Búsqueda general (nombre, descripción, marca, modelo)
     */
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.modelo) LIKE LOWER(CONCAT('%', :termino, '%'))")
    Page<Producto> buscarProductos(@Param("termino") String termino, Pageable pageable);
    
    // ==================== FILTROS POR CATEGORÍA ====================
    
    /**
     * Buscar productos por categoría (Integer)
     */
    Page<Producto> findByCategoriaId(Integer categoriaId, Pageable pageable);
    
    /**
     * Contar por categoría (Integer)
     */
    Long countByCategoriaId(Integer categoriaId);
    
    // ==================== FILTROS POR VENDEDOR ====================
    
    /**
     * Buscar productos por vendedor
     */
    Page<Producto> findByVendedorId(UUID vendedorId, Pageable pageable);
    
    /**
     * Contar productos por vendedor
     */
    Long countByVendedorId(UUID vendedorId);
    
    // ==================== FILTROS POR ESTADO ====================
    
    /**
     * Buscar productos por estado
     */
    Page<Producto> findByEstado(EstadoProducto estado, Pageable pageable);
    
    /**
     * Buscar por estado con stock mayor a X
     */
    Page<Producto> findByEstadoAndStockGreaterThan(EstadoProducto estado, Integer stock, Pageable pageable);
    
    /**
     * Contar productos por estado
     */
    Long countByEstado(EstadoProducto estado);
    
    /**
     * Contar productos aprobados
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.estado = 'APROBADO'")
    Long countAprobados();
    
    /**
     * Contar productos pendientes de revisión
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.estado = 'PENDIENTE_REVISION'")
    Long countPendientes();
    
    // ==================== FILTROS POR PRECIO ====================
    
    /**
     * Buscar por rango de precio
     */
    Page<Producto> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);
    
    /**
     * Buscar por precio menor o igual
     */
    Page<Producto> findByPrecioLessThanEqual(BigDecimal precio, Pageable pageable);
    
    /**
     * Buscar por precio mayor o igual
     */
    Page<Producto> findByPrecioGreaterThanEqual(BigDecimal precio, Pageable pageable);
    
    // ==================== FILTROS POR STOCK ====================
    
    /**
     * Contar productos con stock bajo (menor a umbral)
     */
    Long countByStockLessThan(Integer stock);
    
    /**
     * Contar productos con stock menor o igual
     */
    Long countByStockLessThanEqual(Integer stock);
    
    /**
     * Buscar productos con stock bajo
     */
    @Query("SELECT p FROM Producto p WHERE p.stock < :umbral AND p.stock > 0")
    List<Producto> findProductosConStockBajo(@Param("umbral") Integer umbral);
    
    /**
     * Buscar productos sin stock
     */
    Page<Producto> findByStock(Integer stock, Pageable pageable);
    
    // ==================== PRODUCTOS DESTACADOS ====================
    
    /**
     * Buscar productos destacados
     */
    Page<Producto> findByDestacadoTrue(Pageable pageable);
    
    /**
     * Buscar destacados por estado
     */
    List<Producto> findByDestacadoTrueAndEstado(EstadoProducto estado);
    
    /**
     * Buscar destacados aprobados (paginado)
     */
    Page<Producto> findByDestacadoTrueAndEstado(EstadoProducto estado, Pageable pageable);
    
    // ==================== TOP PRODUCTOS ====================
    
    /**
     * Productos más vendidos (por reviews)
     */
    @Query("SELECT p FROM Producto p WHERE p.estado = 'APROBADO' ORDER BY p.cantidadReviews DESC")
    List<Producto> findTopProductos(Pageable pageable);
    
    /**
     * Productos mejor calificados
     */
    @Query("SELECT p FROM Producto p WHERE p.estado = 'APROBADO' AND p.cantidadReviews > 0 " +
           "ORDER BY p.calificacionPromedio DESC, p.cantidadReviews DESC")
    List<Producto> findTopCalificados(Pageable pageable);
    
    /**
     * Productos más recientes
     */
    Page<Producto> findByEstadoOrderByFechaCreacionDesc(EstadoProducto estado, Pageable pageable);
    
    // ==================== ESTADÍSTICAS ====================
    
    /**
     * Contar todos los productos
     */
    @Query("SELECT COUNT(p) FROM Producto p")
    Long contarTodos();
    
    /**
     * Calcular precio promedio
     */
    @Query("SELECT AVG(p.precio) FROM Producto p WHERE p.estado = 'APROBADO'")
    BigDecimal calcularPrecioPromedio();
}