package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para Dashboard del Administrador
 * Contiene todas las métricas principales del sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAdminResponse {
    
    // ==================== MÉTRICAS GENERALES ====================
    
    /**
     * Total de usuarios registrados
     */
    private Long totalUsuarios;
    
    /**
     * Usuarios registrados hoy
     */
    private Long usuariosHoy;
    
    /**
     * Usuarios registrados esta semana
     */
    private Long usuariosSemana;
    
    /**
     * Total de productos en el sistema
     */
    private Long totalProductos;
    
    /**
     * Productos pendientes de aprobación
     */
    private Long productosPendientes;
    
    /**
     * Productos aprobados
     */
    private Long productosAprobados;
    
    /**
     * Total de categorías
     */
    private Long totalCategorias;
    
    // ==================== MÉTRICAS DE PEDIDOS ====================
    
    /**
     * Total de pedidos en el sistema
     */
    private Long totalPedidos;
    
    /**
     * Pedidos pendientes
     */
    private Long pedidosPendientes;
    
    /**
     * Pedidos confirmados
     */
    private Long pedidosConfirmados;
    
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
    
    /**
     * Pedidos de hoy
     */
    private Long pedidosHoy;
    
    /**
     * Pedidos de esta semana
     */
    private Long pedidosSemana;
    
    /**
     * Pedidos de este mes
     */
    private Long pedidosMes;
    
    // ==================== MÉTRICAS DE VENTAS ====================
    
    /**
     * Ventas totales (monto acumulado)
     */
    private BigDecimal ventasTotales;
    
    /**
     * Ventas de hoy
     */
    private BigDecimal ventasHoy;
    
    /**
     * Ventas de esta semana
     */
    private BigDecimal ventasSemana;
    
    /**
     * Ventas de este mes
     */
    private BigDecimal ventasMes;
    
    /**
     * Ticket promedio (valor promedio de pedido)
     */
    private BigDecimal ticketPromedio;
    
    // ==================== MÉTRICAS DE REVIEWS ====================
    
    /**
     * Total de reviews en el sistema
     */
    private Long totalReviews;
    
    /**
     * Reviews pendientes de aprobación
     */
    private Long reviewsPendientes;
    
    /**
     * Reviews aprobadas
     */
    private Long reviewsAprobadas;
    
    /**
     * Calificación promedio general
     */
    private Double calificacionPromedio;
    
    // ==================== TOP PRODUCTOS ====================
    
    /**
     * Top 5 productos más vendidos
     */
    private List<ProductoMasVendidoResponse> productosTopVentas;
    
    /**
     * Top 5 productos mejor calificados
     */
    private List<ProductoTopCalificadoResponse> productosTopCalificados;
    
    // ==================== MÉTRICAS ADICIONALES ====================
    
    /**
     * Total de vendedores activos
     */
    private Long totalVendedores;
    
    /**
     * Productos con stock bajo (< 5)
     */
    private Long productosStockBajo;
    
    /**
     * Productos sin stock
     */
    private Long productosSinStock;
}