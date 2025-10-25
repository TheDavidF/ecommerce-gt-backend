package com.ecommercegt.backend.dto.response;

import com.ecommercegt.backend.models.enums.EstadoProducto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoModeracionResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String imagenUrl;
    private List<String> imagenes;
    private EstadoProducto estado;
    private String motivoRechazo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Información de la categoría
    private UUID categoriaId;
    private String categoriaNombre;
    
    // Información del vendedor/usuario
    private UUID vendedorId;
    private String vendedorNombre;
    private String vendedorEmail;
}