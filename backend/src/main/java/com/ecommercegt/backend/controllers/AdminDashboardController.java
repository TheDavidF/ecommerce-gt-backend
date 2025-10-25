package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.response.DashboardAdminResponse;
import com.ecommercegt.backend.dto.response.EstadisticasVentasResponse;
import com.ecommercegt.backend.dto.response.ProductoMasVendidoResponse;
import com.ecommercegt.backend.dto.response.ProductoTopCalificadoResponse;
import com.ecommercegt.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de Dashboard para Administradores
 * Proporciona estadísticas y métricas del sistema
 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * Obtener dashboard completo del administrador
     * GET /api/admin/dashboard
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DashboardAdminResponse> obtenerDashboard() {
        DashboardAdminResponse dashboard = dashboardService.obtenerDashboardAdmin();
        return ResponseEntity.ok(dashboard);
    }
    
    /**
     * Obtener estadísticas de ventas por período
     * GET /api/admin/dashboard/estadisticas/ventas?fechaInicio=2025-10-01&fechaFin=2025-10-24
     */
    @GetMapping("/estadisticas/ventas")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EstadisticasVentasResponse> obtenerEstadisticasVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        EstadisticasVentasResponse estadisticas = 
            dashboardService.obtenerEstadisticasVentas(fechaInicio, fechaFin);
        
        return ResponseEntity.ok(estadisticas);
    }
    
    /**
     * Obtener productos más vendidos
     * GET /api/admin/dashboard/productos/mas-vendidos?limite=10
     */
    @GetMapping("/productos/mas-vendidos")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ProductoMasVendidoResponse>> obtenerProductosMasVendidos(
            @RequestParam(defaultValue = "10") int limite) {
        
        List<ProductoMasVendidoResponse> productos = 
            dashboardService.obtenerProductosMasVendidos(limite);
        
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtener productos mejor calificados
     * GET /api/admin/dashboard/productos/mejor-calificados?limite=10
     */
    @GetMapping("/productos/mejor-calificados")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ProductoTopCalificadoResponse>> obtenerProductosMejorCalificados(
            @RequestParam(defaultValue = "10") int limite) {
        
        List<ProductoTopCalificadoResponse> productos = 
            dashboardService.obtenerProductosMejorCalificados(limite);
        
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtener estadísticas del último mes
     * GET /api/admin/dashboard/estadisticas/mes-actual
     */
    @GetMapping("/estadisticas/mes-actual")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EstadisticasVentasResponse> obtenerEstadisticasMesActual() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        
        EstadisticasVentasResponse estadisticas = 
            dashboardService.obtenerEstadisticasVentas(inicioMes, hoy);
        
        return ResponseEntity.ok(estadisticas);
    }
    
    /**
     * Obtener estadísticas de la última semana
     * GET /api/admin/dashboard/estadisticas/semana-actual
     */
    @GetMapping("/estadisticas/semana-actual")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EstadisticasVentasResponse> obtenerEstadisticasSemanaActual() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.minusDays(7);
        
        EstadisticasVentasResponse estadisticas = 
            dashboardService.obtenerEstadisticasVentas(inicioSemana, hoy);
        
        return ResponseEntity.ok(estadisticas);
    }
}