package com.ecommercegt.backend.dto.response;

import com.ecommercegt.backend.models.enums.EstadoProducto;
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
public class ProductoResponse {
    
    private UUID id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal precioDescuento;
    private BigDecimal precioFinal;
    private Integer stock;
    private String marca;
    private String modelo;
    private EstadoProducto estado;
    private Boolean destacado;
    
    // Información de categoría
    private Integer categoriaId;
    private String categoriaNombre;
    
    // Información de vendedor
    private UUID vendedorId;
    private String vendedorNombre;
    
    // Imágenes
    private List<String> imagenes = new ArrayList<>();
    private String imagenPrincipal;
    
    // Fechas
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
