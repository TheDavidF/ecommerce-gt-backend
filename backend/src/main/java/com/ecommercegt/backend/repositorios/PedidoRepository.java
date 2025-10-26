package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Pedido;
import com.ecommercegt.backend.models.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    // ==================== BÚSQUEDAS POR USUARIO ====================

    /**
     * Buscar pedidos de un usuario específico
     * Ordenados por fecha de pedido (más reciente primero)
     */
    Page<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(UUID usuarioId, Pageable pageable);

    /**
     * Buscar pedidos de un usuario sin paginación
     */
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(UUID usuarioId);

    List<Pedido> findByUsuarioId(UUID usuarioId);

    Page<Pedido> findByUsuarioId(UUID usuarioId, Pageable pageable);

    /**
     * Contar pedidos de un usuario
     */
    Long countByUsuarioId(UUID usuarioId);

    // ==================== BÚSQUEDAS POR ESTADO ====================

    /**
     * Buscar pedidos por estado
     */
    Page<Pedido> findByEstado(EstadoPedido estado, Pageable pageable);

    List<Pedido> findByEstado(EstadoPedido estado);

    /**
     * Buscar pedidos de un usuario con un estado específico
     */
    List<Pedido> findByUsuarioIdAndEstado(UUID usuarioId, EstadoPedido estado);

    /**
     * Contar pedidos por estado
     */
    Long countByEstado(EstadoPedido estado);

    /**
     * ✅ Contar pedidos por múltiples estados (ENUM)
     */
    Long countByEstadoIn(List<EstadoPedido> estados);

    // ==================== BÚSQUEDAS POR NÚMERO DE ORDEN ====================

    /**
     * Buscar pedido por número de orden
     */
    Optional<Pedido> findByNumeroOrden(String numeroOrden);

    /**
     * Verificar si existe un pedido con ese número de orden
     */
    boolean existsByNumeroOrden(String numeroOrden);

    /**
     * Buscar por número de orden (contiene)
     */
    List<Pedido> findByNumeroOrdenContaining(String numeroOrden);

    /**
     * Obtener último número de orden del día
     * Para generar número secuencial
     */
    @Query("SELECT p.numeroOrden FROM Pedido p " +
            "WHERE p.numeroOrden LIKE :prefijo " +
            "ORDER BY p.numeroOrden DESC")
    List<String> findUltimoNumeroOrdenDelDia(@Param("prefijo") String prefijo);

    // ==================== BÚSQUEDAS POR FECHA ====================

    /**
     * Buscar pedidos entre fechas
     */
    List<Pedido> findByFechaPedidoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Page<Pedido> findByFechaPedidoBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    /**
     * Buscar pedidos de un usuario entre fechas
     */
    List<Pedido> findByUsuarioIdAndFechaPedidoBetween(
            UUID usuarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);

    // ==================== QUERIES DE VENDEDOR ====================

    /**
     * Buscar pedidos que contengan productos de un vendedor específico
     * ✅ CORREGIDO: Usa i.productoId correctamente
     */
    @Query("SELECT DISTINCT p FROM Pedido p " +
            "JOIN p.items i " +
            "WHERE i.productoId IN " +
            "(SELECT prod.id FROM Producto prod WHERE prod.vendedor.id = :vendedorId) " +
            "ORDER BY p.fechaPedido DESC")
    Page<Pedido> findPedidosConProductosDeVendedor(
            @Param("vendedorId") UUID vendedorId,
            Pageable pageable);

    /**
     * Estadísticas: Total de ventas de productos de un vendedor
     * ✅ CORREGIDO: Usa i.subtotal correctamente
     */
    @Query("SELECT COALESCE(SUM(i.subtotal), 0) FROM ItemPedido i " +
            "JOIN i.pedido p " +
            "WHERE i.productoId IN " +
            "(SELECT prod.id FROM Producto prod WHERE prod.vendedor.id = :vendedorId) " +
            "AND p.estado = com.ecommercegt.backend.models.enums.EstadoPedido.ENTREGADO")
    Double calcularTotalVentasVendedor(@Param("vendedorId") UUID vendedorId);

    // ==================== CÁLCULOS DE VENTAS (JPQL) ====================

    /**
     * ✅ Suma total de todos los pedidos
     */
    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Pedido p")
    BigDecimal sumMontoTotal();

    /**
     * ✅ Suma de ventas después de una fecha
     */
    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Pedido p WHERE p.fechaPedido >= :fecha")
    BigDecimal sumMontoTotalByFechaPedidoAfter(@Param("fecha") LocalDateTime fecha);

    /**
     * Estadísticas: Total de compras de un usuario
     * ✅ CORREGIDO: Usa p.montoTotal
     */
    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Pedido p " +
            "WHERE p.usuario.id = :usuarioId " +
            "AND p.estado <> com.ecommercegt.backend.models.enums.EstadoPedido.CANCELADO")
    Double calcularTotalComprasUsuario(@Param("usuarioId") UUID usuarioId);

    // ==================== CÁLCULOS DE VENTAS (NATIVE SQL) ====================

    /**
     * ✅ Ventas agrupadas por día del mes
     */
    @Query(value = "SELECT DATE(fecha_pedido) as fecha, SUM(monto_total) as total " +
            "FROM pedidos " +
            "WHERE fecha_pedido >= :inicioMes " +
            "GROUP BY DATE(fecha_pedido) " +
            "ORDER BY fecha", nativeQuery = true)
    List<Object[]> findVentasPorDiaDelMes(@Param("inicioMes") LocalDateTime inicioMes);

    /**
     * Contar pedidos de hoy
     */
    @Query(value = "SELECT COUNT(*) FROM pedidos WHERE DATE(fecha_pedido) = CURRENT_DATE", nativeQuery = true)
    Long countPedidosHoy();

    /**
     * Contar pedidos de esta semana
     */
    @Query(value = "SELECT COUNT(*) FROM pedidos " +
            "WHERE fecha_pedido >= CURRENT_DATE - INTERVAL '7 days'", nativeQuery = true)
    Long countPedidosEstaSemana();

    /**
     * Contar pedidos de este mes
     */
    @Query(value = "SELECT COUNT(*) FROM pedidos " +
            "WHERE EXTRACT(MONTH FROM fecha_pedido) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM fecha_pedido) = EXTRACT(YEAR FROM CURRENT_DATE)", nativeQuery = true)
    Long countPedidosEsteMes();

    /**
     * Calcular ventas de hoy
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos " +
            "WHERE DATE(fecha_pedido) = CURRENT_DATE " +
            "AND estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasHoy();

    /**
     * Calcular ventas de esta semana
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos " +
            "WHERE fecha_pedido >= CURRENT_DATE - INTERVAL '7 days' " +
            "AND estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasEstaSemana();

    /**
     * Calcular ventas de este mes
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos " +
            "WHERE EXTRACT(MONTH FROM fecha_pedido) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM fecha_pedido) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasEsteMes();

    /**
     * Calcular ventas totales
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos WHERE estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasTotales();

    /**
     * Calcular ventas por período
     */
    @Query(value = "SELECT COALESCE(SUM(monto_total), 0) FROM pedidos " +
            "WHERE fecha_pedido BETWEEN :inicio AND :fin " +
            "AND estado = 'ENTREGADO'", nativeQuery = true)
    Double calcularVentasPorPeriodo(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Contar pedidos por período
     */
    @Query(value = "SELECT COUNT(*) FROM pedidos " +
            "WHERE fecha_pedido BETWEEN :inicio AND :fin", nativeQuery = true)
    Long countPedidosPorPeriodo(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Buscar pedidos por lista de estados
     * Usado para obtener pedidos "en curso"
     */
    List<Pedido> findByEstadoInOrderByFechaPedidoDesc(List<EstadoPedido> estados);

    /**
     * Pedidos próximos a vencer (fecha estimada entre ahora y las próximas 24h)
     * Solo pedidos en estados activos
     */
    @Query("SELECT p FROM Pedido p " +
            "WHERE p.estado IN ('PENDIENTE', 'EN_CAMINO') " +
            "AND p.fechaEntregaEstimada BETWEEN :inicio AND :fin " +
            "ORDER BY p.fechaEntregaEstimada ASC")
    List<Pedido> findPedidosProximosVencer(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}