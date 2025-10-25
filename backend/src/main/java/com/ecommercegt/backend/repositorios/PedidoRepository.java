package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Pedido;
import com.ecommercegt.backend.models.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para Pedido
 * Gestiona pedidos de usuarios
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    /**
     * Buscar pedidos de un usuario específico
     * Ordenados por fecha de pedido (más reciente primero)
     */
    Page<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(UUID usuarioId, Pageable pageable);

    /**
     * Buscar pedidos de un usuario sin paginación
     */
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(UUID usuarioId);

    /**
     * Buscar pedidos por estado
     */
    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);

    /**
     * Buscar pedidos de un usuario con un estado específico
     */
    List<Pedido> findByUsuarioIdAndEstado(UUID usuarioId, EstadoPedido estado);

    /**
     * Contar pedidos por estado
     */
    Long countByEstado(EstadoPedido estado);

    /**
     * Contar pedidos de un usuario
     */
    Long countByUsuarioId(UUID usuarioId);

    /**
     * Buscar pedido por número de orden
     */
    Optional<Pedido> findByNumeroOrden(String numeroOrden);

    /**
     * Verificar si existe un pedido con ese número de orden
     */
    boolean existsByNumeroOrden(String numeroOrden);

    /**
     * Buscar pedidos entre fechas
     */
    List<Pedido> findByFechaPedidoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Buscar pedidos de un usuario entre fechas
     */
    List<Pedido> findByUsuarioIdAndFechaPedidoBetween(
            UUID usuarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);

    /**
     * Buscar pedidos que contengan productos de un vendedor específico
     * (productos vendidos por el vendedor)
     */
    @Query("SELECT DISTINCT p FROM Pedido p " +
            "JOIN p.items i " +
            "JOIN Producto prod ON i.productoId = prod.id " +
            "WHERE prod.vendedor.id = :vendedorId " +
            "ORDER BY p.fechaPedido DESC")
    Page<Pedido> findPedidosConProductosDeVendedor(
            @Param("vendedorId") UUID vendedorId,
            Pageable pageable);

    /**
     * Obtener último número de orden del día
     * Para generar número secuencial
     */
    @Query("SELECT p.numeroOrden FROM Pedido p " +
            "WHERE p.numeroOrden LIKE :prefijo " +
            "ORDER BY p.numeroOrden DESC")
    List<String> findUltimoNumeroOrdenDelDia(@Param("prefijo") String prefijo);

    /**
     * Estadísticas: Total de ventas por usuario
     */
    @Query("SELECT SUM(p.total) FROM Pedido p " +
            "WHERE p.usuario.id = :usuarioId " +
            "AND p.estado NOT IN (com.ecommercegt.backend.models.enums.EstadoPedido.CANCELADO)")
    Double calcularTotalComprasUsuario(@Param("usuarioId") UUID usuarioId);

    /**
     * Estadísticas: Total de ventas de productos de un vendedor
     */
    @Query("SELECT SUM(i.subtotal) FROM ItemPedido i " +
            "JOIN i.pedido p " +
            "JOIN Producto prod ON i.productoId = prod.id " +
            "WHERE prod.vendedor.id = :vendedorId " +
            "AND p.estado = com.ecommercegt.backend.models.enums.EstadoPedido.ENTREGADO")
    Double calcularTotalVentasVendedor(@Param("vendedorId") UUID vendedorId);

    /**
     * Contar pedidos de hoy (SQL nativo)
     */
    @Query(value = "SELECT COUNT(*) FROM pedidos WHERE DATE(fecha_pedido) = CURRENT_DATE", nativeQuery = true)
    Long countPedidosHoy();

    /**
     * Contar pedidos de esta semana (SQL nativo)
     */
    @Query(value = "SELECT COUNT(*) FROM pedidos " +
            "WHERE fecha_pedido >= CURRENT_DATE - INTERVAL '7 days'", nativeQuery = true)
    Long countPedidosEstaSemana();

    /**
     * Contar pedidos de este mes (SQL nativo)
     */
    @Query(value = "SELECT COUNT(*) FROM pedidos " +
            "WHERE EXTRACT(MONTH FROM fecha_pedido) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM fecha_pedido) = EXTRACT(YEAR FROM CURRENT_DATE)", nativeQuery = true)
    Long countPedidosEsteMes();

    /**
     * Calcular ventas de hoy (SQL nativo)
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos " +
            "WHERE DATE(fecha_pedido) = CURRENT_DATE " +
            "AND estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasHoy();

    /**
     * Calcular ventas de esta semana (SQL nativo)
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos " +
            "WHERE fecha_pedido >= CURRENT_DATE - INTERVAL '7 days' " +
            "AND estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasEstaSemana();

    /**
     * Calcular ventas de este mes (SQL nativo)
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos " +
            "WHERE EXTRACT(MONTH FROM fecha_pedido) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM fecha_pedido) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasEsteMes();

    /**
     * Calcular ventas totales (SQL nativo)
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos WHERE estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasTotales();

    /**
     * Calcular ventas por período (SQL nativo)
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos " +
            "WHERE fecha_pedido BETWEEN :inicio AND :fin " +
            "AND estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasPorPeriodo(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Contar pedidos por período (SQL nativo)
     */
    @Query(value = "SELECT COUNT(*) FROM pedidos " +
            "WHERE fecha_pedido BETWEEN :inicio AND :fin", nativeQuery = true)
    Long countPedidosPorPeriodo(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}