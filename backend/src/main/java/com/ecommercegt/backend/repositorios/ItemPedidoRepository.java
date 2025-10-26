package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para ItemPedido
 * Gestiona items (productos) dentro de pedidos
 */
@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    // ==================== BÚSQUEDAS BÁSICAS ====================

    /**
     * Buscar items de un pedido específico
     */
    List<ItemPedido> findByPedidoId(UUID pedidoId);

    /**
     * Buscar items que contengan un producto específico
     * Útil para ver historial de ventas de un producto
     */
    List<ItemPedido> findByProductoId(UUID productoId);

    /**
     * Eliminar todos los items de un pedido
     * (usado cuando se cancela un pedido)
     */
    void deleteByPedidoId(UUID pedidoId);

    // ==================== ESTADÍSTICAS DE PRODUCTOS ====================

    /**
     * Contar cuántas veces se ha vendido un producto
     */
    @Query("SELECT SUM(i.cantidad) FROM ItemPedido i WHERE i.productoId = :productoId")
    Long countCantidadVendidaDeProducto(@Param("productoId") UUID productoId);

    /**
     * Calcular ingresos generados por un producto
     */
    @Query("SELECT SUM(i.subtotal) FROM ItemPedido i " +
            "JOIN i.pedido p " +
            "WHERE i.productoId = :productoId " +
            "AND p.estado = com.ecommercegt.backend.models.enums.EstadoPedido.ENTREGADO")
    Double calcularIngresosDeProducto(@Param("productoId") UUID productoId);

    // ==================== PRODUCTOS MÁS VENDIDOS ====================

    /**
     * ✅ MÉTODO RECOMENDADO: Productos más vendidos con JOIN a tabla productos
     * Retorna: producto_id, nombre, total_vendido, precio, imagen_url
     */
    @Query(value = "SELECT " +
            "p.id, " +
            "p.nombre, " +
            "SUM(ip.cantidad) as total_vendido, " +
            "p.precio, " +
            "(SELECT url_imagen FROM imagenes_producto WHERE producto_id = p.id ORDER BY orden LIMIT 1) as imagen_url "
            +
            "FROM items_pedido ip " +
            "INNER JOIN productos p ON ip.producto_id = p.id " +
            "INNER JOIN pedidos ped ON ip.pedido_id = ped.id " +
            "WHERE ped.estado = 'ENTREGADO' " +
            "GROUP BY p.id, p.nombre, p.precio " +
            "ORDER BY total_vendido DESC", nativeQuery = true)
    List<Object[]> findProductosMasVendidos(Pageable pageable);

    /**
     * ALTERNATIVA: Productos más vendidos usando solo datos desnormalizados
     * Retorna: producto_id, producto_nombre, total_vendido, cantidad_ventas,
     * ingresos_totales
     */
    @Query(value = "SELECT " +
            "ip.producto_id, " +
            "ip.producto_nombre, " +
            "SUM(ip.cantidad) as total_vendido, " +
            "COUNT(DISTINCT ip.pedido_id) as cantidad_ventas, " +
            "SUM(ip.subtotal) as ingresos_totales " +
            "FROM items_pedido ip " +
            "INNER JOIN pedidos p ON ip.pedido_id = p.id " +
            "WHERE p.estado = 'ENTREGADO' " +
            "GROUP BY ip.producto_id, ip.producto_nombre " +
            "ORDER BY total_vendido DESC " +
            "LIMIT :limite", nativeQuery = true)
    List<Object[]> findProductosMasVendidosConLimite(@Param("limite") int limite);

    /**
     * SIMPLE: Productos más vendidos usando datos desnormalizados (sin filtro de
     * estado)
     * Para pruebas y dashboard rápido
     * Retorna: producto_nombre, total_cantidad, total_ingresos
     */
    @Query("SELECT ip.productoNombre, SUM(ip.cantidad) as cantidad, SUM(ip.subtotal) as total " +
            "FROM ItemPedido ip " +
            "GROUP BY ip.productoNombre " +
            "ORDER BY cantidad DESC")
    List<Object[]> findProductosMasVendidosSimple(Pageable pageable);

    /**
     * Top 10 productos más vendidos en un rango de fechas
     * Retorna: producto_id, producto_nombre, total_vendido, ingresos_totales
     */
    @Query(value = "SELECT " +
            "ip.producto_id, " +
            "ip.producto_nombre, " +
            "SUM(ip.cantidad) as total_vendido, " +
            "SUM(ip.subtotal) as ingresos_totales " +
            "FROM items_pedido ip " +
            "INNER JOIN pedidos p ON ip.pedido_id = p.id " +
            "WHERE p.estado = 'ENTREGADO' " +
            "AND p.fecha_pedido BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY ip.producto_id, ip.producto_nombre " +
            "ORDER BY total_vendido DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Object[]> findTop10ProductosMasVendidosPorFecha(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}