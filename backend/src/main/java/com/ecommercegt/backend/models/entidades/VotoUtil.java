package com.ecommercegt.backend.models.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad VotoUtil - Voto de utilidad en una reseña
 * 
 * Permite a los usuarios votar si una reseña les resultó útil o no
 * 
 * Reglas:
 * - Un usuario puede votar UNA SOLA VEZ por reseña
 * - Puede cambiar su voto (útil ↔ no útil)
 * - No puede votar su propia reseña
 * 
 * Relaciones:
 * - N:1 con Review (una reseña tiene muchos votos)
 * - N:1 con Usuario (un usuario puede votar muchas reseñas)
 */
@Entity
@Table(name = "votos_utiles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"review_id", "usuario_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotoUtil {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Review siendo votada
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
    
    /**
     * Usuario que votó
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    /**
     * Tipo de voto
     * true = útil (upvote)
     * false = no útil (downvote)
     */
    @Column(name = "es_util", nullable = false)
    private Boolean esUtil;
    
    /**
     * Fecha del voto
     */
    @CreationTimestamp
    @Column(name = "fecha_voto", nullable = false, updatable = false)
    private LocalDateTime fechaVoto;
}