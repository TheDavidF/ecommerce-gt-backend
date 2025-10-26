package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.response.*;
import com.ecommercegt.backend.repositorios.ItemPedidoRepository;
import com.ecommercegt.backend.repositorios.UsuarioReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReporteService {
    
    private final ItemPedidoRepository itemPedidoRepository;
    private final UsuarioReporteRepository usuarioReporteRepository;
    
    @Transactional(readOnly = true)
    public List<ReporteProductoResponse> getTop10ProductosMasVendidos(
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        
        List<ReporteProductoResponse> reportes = new ArrayList<>();
        
        try {
            List<Object[]> resultados = itemPedidoRepository
                .findTop10ProductosMasVendidosPorFecha(fechaInicio, fechaFin);
            
            for (Object[] row : resultados) {
                ReporteProductoResponse reporte = new ReporteProductoResponse();
                reporte.setProductoId((UUID) row[0]);
                reporte.setNombreProducto((String) row[1]);
                reporte.setTotalVendido(((Number) row[2]).longValue());
                reporte.setIngresosTotales(new BigDecimal(row[3].toString()));
                reportes.add(reporte);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener productos mas vendidos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reportes;
    }
    
    @Transactional(readOnly = true)
    public List<ReporteClienteGananciasResponse> getTop5ClientesPorGanancias(
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        
        List<ReporteClienteGananciasResponse> reportes = new ArrayList<>();
        
        try {
            List<Object[]> resultados = usuarioReporteRepository
                .findTop5ClientesPorGanancias(fechaInicio, fechaFin);
            
            for (Object[] row : resultados) {
                ReporteClienteGananciasResponse reporte = new ReporteClienteGananciasResponse();
                reporte.setUsuarioId((UUID) row[0]);
                reporte.setNombreCompleto((String) row[1]);
                reporte.setTotalGastado(new BigDecimal(row[2].toString()));
                reporte.setCantidadPedidos(((Number) row[3]).longValue());
                reportes.add(reporte);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener clientes por ganancias: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reportes;
    }
    
    @Transactional(readOnly = true)
    public List<ReporteClienteVentasResponse> getTop5ClientesPorVentas(
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        
        List<ReporteClienteVentasResponse> reportes = new ArrayList<>();
        
        try {
            List<Object[]> resultados = usuarioReporteRepository
                .findTop5ClientesPorVentas(fechaInicio, fechaFin);
            
            for (Object[] row : resultados) {
                ReporteClienteVentasResponse reporte = new ReporteClienteVentasResponse();
                reporte.setUsuarioId((UUID) row[0]);
                reporte.setNombreCompleto((String) row[1]);
                reporte.setTotalProductosVendidos(((Number) row[2]).longValue());
                reporte.setIngresosGenerados(new BigDecimal(row[3].toString()));
                reportes.add(reporte);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener clientes por ventas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reportes;
    }
    
    @Transactional(readOnly = true)
    public List<ReporteClientePedidosResponse> getTop10ClientesPorPedidos(
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        
        List<ReporteClientePedidosResponse> reportes = new ArrayList<>();
        
        try {
            List<Object[]> resultados = usuarioReporteRepository
                .findTop10ClientesPorPedidos(fechaInicio, fechaFin);
            
            for (Object[] row : resultados) {
                ReporteClientePedidosResponse reporte = new ReporteClientePedidosResponse();
                reporte.setUsuarioId((UUID) row[0]);
                reporte.setNombreCompleto((String) row[1]);
                reporte.setCantidadPedidos(((Number) row[2]).longValue());
                reporte.setTotalGastado(new BigDecimal(row[3].toString()));
                reportes.add(reporte);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener clientes por pedidos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reportes;
    }
    
    @Transactional(readOnly = true)
    public List<ReporteClienteProductosResponse> getTop10ClientesPorProductos() {
        
        List<ReporteClienteProductosResponse> reportes = new ArrayList<>();
        
        try {
            List<Object[]> resultados = usuarioReporteRepository
                .findTop10ClientesPorProductosEnVenta();
            
            for (Object[] row : resultados) {
                ReporteClienteProductosResponse reporte = new ReporteClienteProductosResponse();
                reporte.setUsuarioId((UUID) row[0]);
                reporte.setNombreCompleto((String) row[1]);
                reporte.setCantidadProductos(((Number) row[2]).longValue());
                reporte.setProductosAprobados(((Number) row[3]).longValue());
                reportes.add(reporte);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener clientes por productos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reportes;
    }
}