package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponse {
    
    private Integer id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private Integer cantidadProductos;
}