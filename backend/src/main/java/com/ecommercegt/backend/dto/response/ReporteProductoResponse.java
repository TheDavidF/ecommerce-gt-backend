package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteProductoResponse {
    private UUID productoId;
    private String nombreProducto;
    private Long totalVendido;
    private BigDecimal ingresosTotales;
}