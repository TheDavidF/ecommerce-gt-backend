package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponse {
    
    private UUID id;
    private UUID usuarioId;
    private String usuarioNombre;
    
    private List<ItemCarritoResponse> items = new ArrayList<>();
    
    private Integer cantidadTotalItems;
    private BigDecimal total;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    private Boolean vacio;
}