package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.response.EstadisticasGeneralesResponse;
import com.ecommercegt.backend.models.entidades.Pedido;
import com.ecommercegt.backend.models.enums.EstadoPedido;
import com.ecommercegt.backend.models.enums.RolNombre;
import com.ecommercegt.backend.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Administración
 * Gestiona estadísticas y métricas del sistema
 */
@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    /**
     * Obtener estadísticas generales para el dashboard de admin
     */
    @Transactional(readOnly = true)
    public EstadisticasGeneralesResponse obtenerEstadisticasGenerales() {
        EstadisticasGeneralesResponse stats = new EstadisticasGeneralesResponse();

        // ==================== ESTADÍSTICAS DE USUARIOS ====================
        stats.setTotalUsuarios(usuarioRepository.count());

        // Usuarios con al menos un pedido
        Long usuariosConPedidos = pedidoRepository.findAll().stream()
                .map(p -> p.getUsuario().getId())
                .distinct()
                .count();
        stats.setUsuariosActivos(usuariosConPedidos);

        // Vendedores (usuarios con rol VENDEDOR)
        Long moderadores = usuarioRepository.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getNombre() == RolNombre.MODERADOR))
                .count();
        stats.setTotalVendedores(moderadores);

        // ==================== ESTADÍSTICAS DE PRODUCTOS ====================
        stats.setTotalProductos(productoRepository.count());
        stats.setProductosAprobados(productoRepository.count());
        stats.setProductosPendientes(0L);

        // Productos con stock bajo (< 10)
        stats.setProductosStockBajo(productoRepository.countByStockLessThan(10));

        // ==================== ESTADÍSTICAS DE PEDIDOS ====================
        stats.setTotalPedidos(pedidoRepository.count());
        stats.setPedidosPendientes(pedidoRepository.countByEstado(EstadoPedido.PENDIENTE));
        stats.setPedidosEnPreparacion(pedidoRepository.countByEstado(EstadoPedido.EN_PREPARACION));
        stats.setPedidosEnviados(pedidoRepository.countByEstado(EstadoPedido.ENVIADO));
        stats.setPedidosEntregados(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO));
        stats.setPedidosCancelados(pedidoRepository.countByEstado(EstadoPedido.CANCELADO));

        // ==================== ESTADÍSTICAS DE VENTAS ====================

        // Total de ventas (pedidos ENTREGADOS)
        List<Pedido> pedidosEntregados = pedidoRepository
                .findByUsuarioIdAndEstado(null, EstadoPedido.ENTREGADO);

        BigDecimal totalVentas = pedidosEntregados.stream()
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalVentas(totalVentas);

        // Ventas del mes actual
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = LocalDate.now().plusDays(1).atStartOfDay();

        List<Pedido> pedidosMes = pedidoRepository.findByFechaPedidoBetween(inicioMes, finMes);
        BigDecimal ventasMes = pedidosMes.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO)
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setVentasMes(ventasMes);

        // Ventas de hoy
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().plusDays(1).atStartOfDay();

        List<Pedido> pedidosHoy = pedidoRepository.findByFechaPedidoBetween(inicioHoy, finHoy);
        BigDecimal ventasHoy = pedidosHoy.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO)
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setVentasHoy(ventasHoy);

        // Ticket promedio
        if (stats.getTotalPedidos() > 0) {
            stats.setTicketPromedio(
                    totalVentas.divide(
                            BigDecimal.valueOf(stats.getTotalPedidos()),
                            2,
                            RoundingMode.HALF_UP));
        } else {
            stats.setTicketPromedio(BigDecimal.ZERO);
        }

        // ==================== ESTADÍSTICAS DE REVIEWS ====================
        stats.setTotalReviews(reviewRepository.count());
        stats.setReviewsPendientes(reviewRepository.countByAprobadoFalse());

        // Calificación promedio general (todas las reviews aprobadas)
        Double calificacionPromedio = reviewRepository
                .findAll()
                .stream()
                .filter(r -> r.getAprobado())
                .mapToDouble(r -> r.getCalificacion())
                .average()
                .orElse(0.0);
        stats.setCalificacionPromedioGeneral(calificacionPromedio);

        // ==================== PRODUCTOS MÁS VENDIDOS ====================

        List<Object[]> productosMasVendidos = itemPedidoRepository.findProductosMasVendidos(
                PageRequest.of(0, 10) // ← Top 10 productos
        );

        List<EstadisticasGeneralesResponse.ProductoMasVendido> topProductos = productosMasVendidos.stream()
                .limit(5)
                .map(row -> {
                    // [0] = productoId (UUID)
                    // [1] = totalVendido (Long)
                    String productoNombre = "Producto"; // Placeholder
                    Long cantidadVendida = ((Number) row[1]).longValue();
                    BigDecimal ingresos = BigDecimal.ZERO; // Calcular si es necesario

                    return new EstadisticasGeneralesResponse.ProductoMasVendido(
                            productoNombre,
                            cantidadVendida,
                            ingresos);
                })
                .collect(Collectors.toList());

        stats.setProductosMasVendidos(topProductos);

        // ==================== ÚLTIMOS PEDIDOS ====================
        List<Pedido> ultimosPedidos = pedidoRepository
                .findAll(PageRequest.of(0, 10))
                .getContent();

        List<EstadisticasGeneralesResponse.UltimoPedido> ultimos = ultimosPedidos.stream()
                .map(p -> new EstadisticasGeneralesResponse.UltimoPedido(
                        p.getNumeroOrden(),
                        p.getUsuario().getNombreCompleto(),
                        p.getTotal(),
                        p.getEstado().toString()))
                .collect(Collectors.toList());

        stats.setUltimosPedidos(ultimos);

        return stats;
    }
}