package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para mostrar filtros aplicados en la búsqueda
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltrosAplicados {
    private String termino;
    private String categoriaNombre;
    private Integer categoriaId;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private Integer calificacionMin;
    private Boolean enStock;
    private String ordenamiento;
}