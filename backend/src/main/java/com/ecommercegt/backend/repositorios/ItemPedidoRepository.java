package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para ItemPedido
 * Gestiona items (productos) dentro de pedidos
 */
@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

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
     * Contar cuántas veces se ha vendido un producto
     */
    @Query("SELECT SUM(i.cantidad) FROM ItemPedido i WHERE i.productoId = :productoId")
    Long countCantidadVendidaDeProducto(@Param("productoId") UUID productoId);

    /**
     * Eliminar todos los items de un pedido
     * (usado cuando se cancela un pedido)
     */
    void deleteByPedidoId(UUID pedidoId);

    /**
     * Obtener productos más vendidos
     * Retorna IDs de productos ordenados por cantidad vendida
     */
    @Query("SELECT ip.productoId, ip.productoNombre, SUM(ip.cantidad) as total " +
            "FROM ItemPedido ip " +
            "JOIN ip.pedido p " +
            "WHERE p.estado = 'ENTREGADO' " +
            "GROUP BY ip.productoId, ip.productoNombre " +
            "ORDER BY total DESC")
    List<Object[]> findProductosMasVendidos(Pageable pageable);

    /**
     * Calcular ingresos generados por un producto
     */
    @Query("SELECT SUM(i.subtotal) FROM ItemPedido i " +
            "JOIN i.pedido p " +
            "WHERE i.productoId = :productoId " +
            "AND p.estado = com.ecommercegt.backend.models.enums.EstadoPedido.ENTREGADO")
    Double calcularIngresosDeProducto(@Param("productoId") UUID productoId);

    /**
     * Obtener productos más vendidos
     * Retorna: producto_id, producto_nombre, total_vendido, cantidad_ventas
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
    List<Object[]> findProductosMasVendidos(@Param("limite") int limite);

}