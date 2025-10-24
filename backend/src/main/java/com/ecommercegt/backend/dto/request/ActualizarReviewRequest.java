package com.ecommercegt.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Request para actualizar una review existente
 * 
 * Solo el autor de la review puede actualizarla
 * La review actualizada debe pasar por moderación de nuevo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarReviewRequest {
    
    /**
     * Nueva calificación (opcional)
     */
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;
    
    /**
     * Nuevo título (opcional)
     */
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String titulo;
    
    /**
     * Nuevo comentario (opcional)
     */
    @Size(min = 20, max = 2000, message = "El comentario debe tener entre 20 y 2000 caracteres")
    private String comentario;
}