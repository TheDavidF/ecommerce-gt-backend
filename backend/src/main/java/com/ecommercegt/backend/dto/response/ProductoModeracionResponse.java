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
public class ProductoModeracionResponse {
    
    // ID de la solicitud
    private UUID id;
    
    // Estado de la solicitud
    private String estado;
    
    // Fechas de la solicitud
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRevision;
    
    // Comentario del moderador
    private String comentarioModerador;
    
    // ID del producto
    private UUID productoId;
    
    // Información del producto (objeto anidado)
    private ProductoInfo producto;
    
    // Información del solicitante (vendedor)
    private UUID solicitanteId;
    private String solicitanteNombre;
    
    // Información del moderador (si ya fue revisado)
    private UUID moderadorId;
    private String moderadorNombre;
    
    /**
     * Clase interna para información básica del producto
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoInfo {
        private UUID id;
        private String nombre;
        private String descripcion;
        private BigDecimal precio;
        private Integer stock;
        private String imagenUrl;
        private String categoriaNombre;
        private String estado;
    }
}