package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteClientePedidosResponse {
    private UUID usuarioId;
    private String nombreCompleto;
    private Long cantidadPedidos;
    private BigDecimal totalGastado;
}