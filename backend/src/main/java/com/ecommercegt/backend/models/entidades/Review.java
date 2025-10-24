package com.ecommercegt.backend.models.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Review - Reseña de un producto por un usuario
 * 
 * Características:
 * - Solo usuarios que compraron el producto pueden dejar review (verificado=true)
 * - Una review por usuario por producto
 * - Calificación de 1 a 5 estrellas
 * - Votos útiles/no útiles de otros usuarios
 * - Moderación por ADMIN/MODERADOR (aprobado)
 * 
 * Relaciones:
 * - N:1 con Producto (un producto tiene muchas reviews)
 * - N:1 con Usuario (un usuario puede dejar muchas reviews)
 * - 1:N con VotoUtil (una review tiene muchos votos)
 */
@Entity
@Table(name = "reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"producto_id", "usuario_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Producto siendo reseñado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    /**
     * Usuario que dejó la reseña
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    /**
     * Calificación de 1 a 5 estrellas
     */
    @Column(nullable = false)
    private Integer calificacion;
    
    /**
     * Título de la reseña
     */
    @Column(nullable = false, length = 200)
    private String titulo;
    
    /**
     * Comentario detallado
     */
    @Column(nullable = false, length = 2000)
    private String comentario;
    
    /**
     * Indica si el usuario compró el producto
     * true = "Compra Verificada"
     */
    @Column(nullable = false)
    private Boolean verificado = false;
    
    /**
     * Indica si la reseña fue aprobada por un moderador
     * Solo reviews aprobadas son visibles públicamente
     */
    @Column(nullable = false)
    private Boolean aprobado = false;
    
    /**
     * Fecha de creación de la reseña
     */
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    /**
     * Última actualización de la reseña
     */
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    /**
     * Fecha de aprobación (si aplica)
     */
    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;
    
    /**
     * Moderador que aprobó la reseña
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderador_id")
    private Usuario moderador;
    
    // ==================== MÉTODOS DE UTILIDAD ====================
    
    /**
     * Aprobar reseña
     */
    public void aprobar(Usuario moderador) {
        this.aprobado = true;
        this.fechaAprobacion = LocalDateTime.now();
        this.moderador = moderador;
    }
    
    /**
     * Rechazar reseña
     */
    public void rechazar() {
        this.aprobado = false;
        this.fechaAprobacion = null;
        this.moderador = null;
    }
    
    /**
     * Validar calificación (1-5)
     */
    public void validarCalificacion() {
        if (calificacion == null || calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
    }
}