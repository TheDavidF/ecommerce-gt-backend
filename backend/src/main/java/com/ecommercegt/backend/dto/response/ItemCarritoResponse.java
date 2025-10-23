package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoResponse {
    
    private Integer id;
    
    // Información del producto
    private UUID productoId;
    private String productoNombre;
    private String productoImagen;
    private Integer productoStock;
    
    // Información del item
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    private LocalDateTime fechaAgregado;
}