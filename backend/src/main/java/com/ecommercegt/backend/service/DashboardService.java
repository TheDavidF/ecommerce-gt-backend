package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.response.*;
import com.ecommercegt.backend.models.enums.EstadoProducto;
import com.ecommercegt.backend.models.enums.EstadoPedido;
import com.ecommercegt.backend.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    /**
     * Obtener dashboard completo para administradores
     */
    @Transactional(readOnly = true)
    public DashboardAdminResponse obtenerDashboardAdmin() {
        DashboardAdminResponse dashboard = new DashboardAdminResponse();

        // ==================== MÉTRICAS DE USUARIOS ====================
        dashboard.setTotalUsuarios(usuarioRepository.count());
        dashboard.setUsuariosHoy(usuarioRepository.countUsuariosRegistradosHoy());
        dashboard.setUsuariosSemana(usuarioRepository.countUsuariosRegistradosEstaSemana());
        dashboard.setTotalVendedores(usuarioRepository.countVendedores());

        // ==================== MÉTRICAS DE PRODUCTOS ====================
        dashboard.setTotalProductos(productoRepository.count());
        // Productos pendientes (PENDIENTE_REVISION en el enum)
        dashboard.setProductosPendientes(productoRepository.countByEstado(EstadoProducto.PENDIENTE_REVISION));

        // Productos aprobados
        dashboard.setProductosAprobados(productoRepository.countByEstado(EstadoProducto.APROBADO));
        dashboard.setProductosStockBajo(productoRepository.countProductosStockBajo());
        dashboard.setProductosSinStock(productoRepository.countProductosSinStock());

        // ==================== MÉTRICAS DE CATEGORÍAS ====================
        dashboard.setTotalCategorias(categoriaRepository.count());

        // ==================== MÉTRICAS DE PEDIDOS ====================
        dashboard.setTotalPedidos(pedidoRepository.count());
        dashboard.setPedidosPendientes(pedidoRepository.countByEstado(EstadoPedido.PENDIENTE));
        dashboard.setPedidosConfirmados(pedidoRepository.countByEstado(EstadoPedido.CONFIRMADO));
        dashboard.setPedidosEnPreparacion(pedidoRepository.countByEstado(EstadoPedido.EN_PREPARACION));
        dashboard.setPedidosEnviados(pedidoRepository.countByEstado(EstadoPedido.ENVIADO));
        dashboard.setPedidosEntregados(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO));
        dashboard.setPedidosCancelados(pedidoRepository.countByEstado(EstadoPedido.CANCELADO));

        dashboard.setPedidosHoy(pedidoRepository.countPedidosHoy());
        dashboard.setPedidosSemana(pedidoRepository.countPedidosEstaSemana());
        dashboard.setPedidosMes(pedidoRepository.countPedidosEsteMes());

        // ==================== MÉTRICAS DE VENTAS ====================
        Double ventasTotalesDouble = pedidoRepository.calcularVentasTotales();
        dashboard.setVentasTotales(
                ventasTotalesDouble != null ? BigDecimal.valueOf(ventasTotalesDouble).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);

        Double ventasHoyDouble = pedidoRepository.calcularVentasHoy();
        dashboard.setVentasHoy(
                ventasHoyDouble != null ? BigDecimal.valueOf(ventasHoyDouble).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);

        Double ventasSemanaDouble = pedidoRepository.calcularVentasEstaSemana();
        dashboard.setVentasSemana(
                ventasSemanaDouble != null ? BigDecimal.valueOf(ventasSemanaDouble).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);

        Double ventasMesDouble = pedidoRepository.calcularVentasEsteMes();
        dashboard.setVentasMes(
                ventasMesDouble != null ? BigDecimal.valueOf(ventasMesDouble).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);

        // Ticket promedio
        Long totalPedidosEntregados = pedidoRepository.countByEstado(EstadoPedido.ENTREGADO);
        if (totalPedidosEntregados > 0 && dashboard.getVentasTotales().compareTo(BigDecimal.ZERO) > 0) {
            dashboard.setTicketPromedio(
                    dashboard.getVentasTotales()
                            .divide(BigDecimal.valueOf(totalPedidosEntregados), 2, RoundingMode.HALF_UP));
        } else {
            dashboard.setTicketPromedio(BigDecimal.ZERO);
        }

        // ==================== MÉTRICAS DE REVIEWS ====================
        dashboard.setTotalReviews(reviewRepository.count());
        dashboard.setReviewsPendientes(reviewRepository.countReviewsPendientes());
        dashboard.setReviewsAprobadas(reviewRepository.countReviewsAprobadas());

        Double calificacionPromedio = reviewRepository.calcularCalificacionPromedio();
        dashboard.setCalificacionPromedio(
                calificacionPromedio != null ? calificacionPromedio : 0.0);

        // ==================== PRODUCTOS TOP ====================
        dashboard.setProductosTopVentas(obtenerProductosMasVendidos(5));
        dashboard.setProductosTopCalificados(obtenerProductosMejorCalificados(5));

        return dashboard;
    }

    /**
     * Obtener estadísticas de ventas por período
     */
    @Transactional(readOnly = true)
    public EstadisticasVentasResponse obtenerEstadisticasVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        EstadisticasVentasResponse estadisticas = new EstadisticasVentasResponse();

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        // Totales del período
        Long totalPedidos = pedidoRepository.countPedidosPorPeriodo(inicio, fin);
        Double ventasTotales = pedidoRepository.calcularVentasPorPeriodo(inicio, fin);

        estadisticas.setTotalPedidos(totalPedidos);
        estadisticas.setVentasTotales(
                ventasTotales != null ? BigDecimal.valueOf(ventasTotales).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);

        if (totalPedidos > 0) {
            estadisticas.setTicketPromedio(
                    estadisticas.getVentasTotales()
                            .divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP));
        } else {
            estadisticas.setTicketPromedio(BigDecimal.ZERO);
        }

        // Ventas por día (simplificado)
        estadisticas.setVentasPorDia(new ArrayList<>());

        return estadisticas;
    }

    /**
     * Obtener productos más vendidos
     */
    @Transactional(readOnly = true)
    public List<ProductoMasVendidoResponse> obtenerProductosMasVendidos(int limite) {
        List<ProductoMasVendidoResponse> productos = new ArrayList<>();

        try {

            List<Object[]> resultados = itemPedidoRepository.findProductosMasVendidosConLimite(limite);

            for (Object[] row : resultados) {
                ProductoMasVendidoResponse producto = new ProductoMasVendidoResponse();
                producto.setProductoId((UUID) row[0]);
                producto.setNombre((String) row[1]);
                producto.setTotalVendido(((Number) row[2]).longValue());
                producto.setCantidadVentas(((Number) row[3]).longValue());
                producto.setIngresosTotales(
                        BigDecimal.valueOf(((Number) row[4]).doubleValue())
                                .setScale(2, RoundingMode.HALF_UP));
                productos.add(producto);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener productos más vendidos: " + e.getMessage());
            e.printStackTrace();
        }

        return productos;
    }

    /**
     * Obtener productos mejor calificados
     */
    @Transactional(readOnly = true)
    public List<ProductoTopCalificadoResponse> obtenerProductosMejorCalificados(int limite) {
        List<ProductoTopCalificadoResponse> productos = new ArrayList<>();

        try {
            List<Object[]> resultados = reviewRepository.findProductosMejorCalificados(limite);

            for (Object[] row : resultados) {
                ProductoTopCalificadoResponse producto = new ProductoTopCalificadoResponse();
                producto.setProductoId((UUID) row[0]);
                producto.setNombre((String) row[1]);
                producto.setCalificacionPromedio(((Number) row[2]).doubleValue());
                producto.setCantidadReviews(((Number) row[3]).intValue());
                producto.setPrecio(
                        BigDecimal.valueOf(((Number) row[4]).doubleValue())
                                .setScale(2, RoundingMode.HALF_UP));
                productos.add(producto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productos;
    }
}