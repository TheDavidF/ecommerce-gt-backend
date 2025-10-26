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
 * - Gráficos (ventas del mes, usuarios por rol)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasGeneralesResponse {
    
    // ==================== ESTADÍSTICAS DE USUARIOS ====================
    
    /**
     * Total de usuarios registrados
     */
    private Long totalUsuarios = 0L;
    
    /**
     * Usuarios activos (con pedidos)
     */
    private Long usuariosActivos = 0L;
    
    /**
     * Total de vendedores
     */
    private Long totalVendedores = 0L;
    
    // ==================== ESTADÍSTICAS DE PRODUCTOS ====================
    
    /**
     * Total de productos
     */
    private Long totalProductos = 0L;
    
    /**
     * Productos aprobados
     */
    private Long productosAprobados = 0L;
    
    /**
     * Productos pendientes de aprobación
     */
    private Long productosPendientes = 0L;
    
    /**
     * Productos con stock bajo (< 10 unidades)
     */
    private Long productosStockBajo = 0L;
    
    // ==================== ESTADÍSTICAS DE PEDIDOS ====================
    
    /**
     * Total de pedidos
     */
    private Long totalPedidos = 0L;
    
    /**
     * Pedidos pendientes
     */
    private Long pedidosPendientes = 0L;
    
    /**
     * Pedidos en preparación
     */
    private Long pedidosEnPreparacion = 0L;
    
    /**
     * Pedidos enviados
     */
    private Long pedidosEnviados = 0L;
    
    /**
     * Pedidos entregados
     */
    private Long pedidosEntregados = 0L;
    
    /**
     * Pedidos cancelados
     */
    private Long pedidosCancelados = 0L;
    
    // ==================== ESTADÍSTICAS DE VENTAS ====================
    
    /**
     * Total de ventas (todos los tiempos)
     */
    private BigDecimal totalVentas = BigDecimal.ZERO;
    
    /**
     * Ventas del mes actual
     */
    private BigDecimal ventasMes = BigDecimal.ZERO;
    
    /**
     * Ventas de hoy
     */
    private BigDecimal ventasHoy = BigDecimal.ZERO;
    
    /**
     * Ticket promedio (monto promedio por pedido)
     */
    private BigDecimal ticketPromedio = BigDecimal.ZERO;
    
    // ==================== ESTADÍSTICAS DE REVIEWS ====================
    
    /**
     * Total de reviews
     */
    private Long totalReviews = 0L;
    
    /**
     * Reviews pendientes de aprobación
     */
    private Long reviewsPendientes = 0L;
    
    /**
     * Calificación promedio general
     */
    private Double calificacionPromedioGeneral = 0.0;
    
    // ==================== DATOS PARA GRÁFICOS Y TABLAS ====================
    
    /**
     * ✅ NUEVO: Productos más vendidos (para gráfico/tabla del dashboard)
     */
    private List<ProductoPopularResponse> productosPopulares = new ArrayList<>();
    
    /**
     * ✅ NUEVO: Usuarios agrupados por rol (para gráfico de barras)
     */
    private List<UsuariosPorRolResponse> usuariosPorRol = new ArrayList<>();
    
    /**
     * ✅ NUEVO: Ventas diarias del mes (para gráfico de líneas)
     */
    private List<VentaDelDiaResponse> ventasDelMes = new ArrayList<>();
    
    // ==================== COMPATIBILIDAD CON CÓDIGO ANTIGUO ====================
    
    /**
     * Top 5 productos más vendidos (formato antiguo)
     */
    private List<ProductoMasVendido> productosMasVendidos = new ArrayList<>();
    
    /**
     * DTO interno para productos más vendidos (formato antiguo)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoMasVendido {
        private String nombre;
        private Long cantidadVendida;
        private BigDecimal ingresos;
    }
    
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