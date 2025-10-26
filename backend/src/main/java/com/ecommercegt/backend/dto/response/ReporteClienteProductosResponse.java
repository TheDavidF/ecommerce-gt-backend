package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteClienteProductosResponse {
    private UUID usuarioId;
    private String nombreCompleto;
    private Long cantidadProductos;
    private Long productosAprobados;
}