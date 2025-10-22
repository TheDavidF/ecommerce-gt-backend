package com.ecommercegt.backend.dto.request;

import com.ecommercegt.backend.models.enums.EstadoProducto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoUpdateRequest {
    
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;
    
    private String descripcion;
    
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    @DecimalMin(value = "0.01", message = "El precio de descuento debe ser mayor a 0")
    private BigDecimal precioDescuento;
    
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;
    
    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    private String modelo;
    
    private Integer categoriaId;
    
    private EstadoProducto estado;
    
    private Boolean destacado;
}