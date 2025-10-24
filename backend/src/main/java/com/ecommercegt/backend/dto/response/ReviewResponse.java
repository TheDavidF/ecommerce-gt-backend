package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO Response para una review
 * Incluye información del producto, usuario y estadísticas de votos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    
    /**
     * ID de la review
     */
    private Long id;
    
    /**
     * ID del producto
     */
    private UUID productoId;
    
    /**
     * Nombre del producto
     */
    private String productoNombre;
    
    /**
     * ID del usuario autor
     */
    private UUID usuarioId;
    
    /**
     * Nombre del usuario autor
     */
    private String usuarioNombre;
    
    /**
     * Calificación (1-5 estrellas)
     */
    private Integer calificacion;
    
    /**
     * Título de la reseña
     */
    private String titulo;
    
    /**
     * Comentario de la reseña
     */
    private String comentario;
    
    /**
     * Cantidad de votos útiles
     */
    private Long votosUtiles;
    
    /**
     * Cantidad de votos no útiles
     */
    private Long votosNoUtiles;
    
    /**
     * Balance de votos (útiles - no útiles)
     */
    private Long balanceVotos;
    
    /**
     * Indica si es compra verificada
     */
    private Boolean verificado;
    
    /**
     * Indica si está aprobada por moderador
     */
    private Boolean aprobado;
    
    /**
     * Fecha de creación
     */
    private LocalDateTime fechaCreacion;
    
    /**
     * Fecha de actualización
     */
    private LocalDateTime fechaActualizacion;
    
    /**
     * Fecha de aprobación (si aplica)
     */
    private LocalDateTime fechaAprobacion;
    
    /**
     * Nombre del moderador que aprobó (si aplica)
     */
    private String moderadorNombre;
    
    /**
     * Indica si el usuario actual puede editar esta review
     */
    private Boolean puedeEditar;
    
    /**
     * Indica si el usuario actual puede eliminar esta review
     */
    private Boolean puedeEliminar;
    
    /**
     * Voto del usuario actual (si existe)
     * null = no ha votado
     * true = votó útil
     * false = votó no útil
     */
    private Boolean miVoto;
}