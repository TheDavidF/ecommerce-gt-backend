package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO Response para estadísticas generales del admin panel
 * 
 * Incluye métricas de:
 * - Usuarios
 * - Productos
 * - Pedidos
 * - Ventas
 * - Productos más vendidos
 * - Últimos pedidos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasGeneralesResponse {
    
    // ==================== ESTADÍSTICAS DE USUARIOS ====================
    
    /**
     * Total de usuarios registrados
     */
    private Long totalUsuarios;
    
    /**
     * Usuarios activos (con pedidos)
     */
    private Long usuariosActivos;
    
    /**
     * Total de vendedores
     */
    private Long totalVendedores;
    
    // ==================== ESTADÍSTICAS DE PRODUCTOS ====================
    
    /**
     * Total de productos
     */
    private Long totalProductos;
    
    /**
     * Productos aprobados
     */
    private Long productosAprobados;
    
    /**
     * Productos pendientes de aprobación
     */
    private Long productosPendientes;
    
    /**
     * Productos con stock bajo (< 10 unidades)
     */
    private Long productosStockBajo;
    
    // ==================== ESTADÍSTICAS DE PEDIDOS ====================
    
    /**
     * Total de pedidos
     */
    private Long totalPedidos;
    
    /**
     * Pedidos pendientes
     */
    private Long pedidosPendientes;
    
    /**
     * Pedidos en preparación
     */
    private Long pedidosEnPreparacion;
    
    /**
     * Pedidos enviados
     */
    private Long pedidosEnviados;
    
    /**
     * Pedidos entregados
     */
    private Long pedidosEntregados;
    
    /**
     * Pedidos cancelados
     */
    private Long pedidosCancelados;
    
    // ==================== ESTADÍSTICAS DE VENTAS ====================
    
    /**
     * Total de ventas (todos los tiempos)
     */
    private BigDecimal totalVentas;
    
    /**
     * Ventas del mes actual
     */
    private BigDecimal ventasMes;
    
    /**
     * Ventas de hoy
     */
    private BigDecimal ventasHoy;
    
    /**
     * Ticket promedio (monto promedio por pedido)
     */
    private BigDecimal ticketPromedio;
    
    // ==================== ESTADÍSTICAS DE REVIEWS ====================
    
    /**
     * Total de reviews
     */
    private Long totalReviews;
    
    /**
     * Reviews pendientes de aprobación
     */
    private Long reviewsPendientes;
    
    /**
     * Calificación promedio general
     */
    private Double calificacionPromedioGeneral;
    
    // ==================== PRODUCTOS MÁS VENDIDOS ====================
    
    /**
     * Top 5 productos más vendidos
     */
    private List<ProductoMasVendido> productosMasVendidos = new ArrayList<>();
    
    /**
     * DTO interno para productos más vendidos
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoMasVendido {
        private String nombre;
        private Long cantidadVendida;
        private BigDecimal ingresos;
    }
    
    // ==================== ÚLTIMOS PEDIDOS ====================
    
    /**
     * Últimos 10 pedidos
     */
    private List<UltimoPedido> ultimosPedidos = new ArrayList<>();
    
    /**
     * DTO interno para últimos pedidos
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UltimoPedido {
        private String numeroOrden;
        private String cliente;
        private BigDecimal total;
        private String estado;
    }
}