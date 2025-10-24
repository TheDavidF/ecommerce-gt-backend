package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    @Query("SELECT i.productoId, SUM(i.cantidad) as totalVendido " +
           "FROM ItemPedido i " +
           "JOIN i.pedido p " +
           "WHERE p.estado = com.ecommercegt.backend.models.enums.EstadoPedido.ENTREGADO " +
           "GROUP BY i.productoId " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos();
    
    /**
     * Calcular ingresos generados por un producto
     */
    @Query("SELECT SUM(i.subtotal) FROM ItemPedido i " +
           "JOIN i.pedido p " +
           "WHERE i.productoId = :productoId " +
           "AND p.estado = com.ecommercegt.backend.models.enums.EstadoPedido.ENTREGADO")
    Double calcularIngresosDeProducto(@Param("productoId") UUID productoId);
}