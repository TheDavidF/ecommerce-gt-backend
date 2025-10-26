package com.ecommercegt.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPopularResponse {
    private UUID id;
    private String nombre;
    private BigDecimal precio;
    private Long cantidadVendida;
    private String imagenUrl;
}