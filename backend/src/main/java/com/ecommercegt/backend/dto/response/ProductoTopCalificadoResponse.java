package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoTopCalificadoResponse {
    private UUID productoId;
    private String nombre;
    private Double calificacionPromedio;
    private Integer cantidadReviews;
    private BigDecimal precio;
}