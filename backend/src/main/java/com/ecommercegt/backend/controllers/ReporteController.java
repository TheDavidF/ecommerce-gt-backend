package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.response.*;
import com.ecommercegt.backend.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteController {
    
    private final ReporteService reporteService;
    
    @GetMapping("/productos-mas-vendidos")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReporteProductoResponse>> getProductosMasVendidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        List<ReporteProductoResponse> reporte = reporteService
            .getTop10ProductosMasVendidos(inicio, fin);
        
        return ResponseEntity.ok(reporte);
    }
    
    @GetMapping("/clientes-por-ganancias")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReporteClienteGananciasResponse>> getClientesPorGanancias(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        List<ReporteClienteGananciasResponse> reporte = reporteService
            .getTop5ClientesPorGanancias(inicio, fin);
        
        return ResponseEntity.ok(reporte);
    }
    
    @GetMapping("/clientes-por-ventas")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReporteClienteVentasResponse>> getClientesPorVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        List<ReporteClienteVentasResponse> reporte = reporteService
            .getTop5ClientesPorVentas(inicio, fin);
        
        return ResponseEntity.ok(reporte);
    }
    
    @GetMapping("/clientes-por-pedidos")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReporteClientePedidosResponse>> getClientesPorPedidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        List<ReporteClientePedidosResponse> reporte = reporteService
            .getTop10ClientesPorPedidos(inicio, fin);
        
        return ResponseEntity.ok(reporte);
    }
    
    @GetMapping("/clientes-por-productos")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ReporteClienteProductosResponse>> getClientesPorProductos() {
        
        List<ReporteClienteProductosResponse> reporte = reporteService
            .getTop10ClientesPorProductos();
        
        return ResponseEntity.ok(reporte);
    }
}