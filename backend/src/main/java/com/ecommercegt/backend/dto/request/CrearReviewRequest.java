package com.ecommercegt.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO Request para crear una nueva review
 * 
 * Validaciones:
 * - Producto debe existir
 * - Usuario debe haber comprado el producto
 * - Usuario no debe tener review previa para ese producto
 * - Calificación debe estar entre 1 y 5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearReviewRequest {
    
    /**
     * ID del producto a reseñar
     */
    @NotNull(message = "El ID del producto es obligatorio")
    private UUID productoId;
    
    /**
     * Calificación de 1 a 5 estrellas
     */
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;
    
    /**
     * Título de la reseña
     */
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String titulo;
    
    /**
     * Comentario detallado de la reseña
     */
    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 20, max = 2000, message = "El comentario debe tener entre 20 y 2000 caracteres")
    private String comentario;
}