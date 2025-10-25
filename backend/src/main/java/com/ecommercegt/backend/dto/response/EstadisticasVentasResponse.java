package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para estadísticas de ventas por período
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasVentasResponse {
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal ventasTotales;
    private Long totalPedidos;
    private BigDecimal ticketPromedio;
    private List<VentaDiariaResponse> ventasPorDia;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentaDiariaResponse {
        private LocalDate fecha;
        private BigDecimal ventas;
        private Long pedidos;
    }
}