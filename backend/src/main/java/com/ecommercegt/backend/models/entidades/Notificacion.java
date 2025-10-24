package com.ecommercegt.backend.models.entidades;

import com.ecommercegt.backend.models.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Notificación
 * Almacena notificaciones para usuarios sobre eventos del sistema
 */
@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Usuario que recibe la notificación
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    /**
     * Tipo de notificación
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoNotificacion tipo;
    
    /**
     * Título de la notificación (corto)
     */
    @Column(nullable = false, length = 200)
    private String titulo;
    
    /**
     * Mensaje descriptivo
     */
    @Column(columnDefinition = "TEXT")
    private String mensaje;
    
    /**
     * URL de redirección (opcional)
     * Ejemplo: /pedidos/123, /productos/uuid, /reviews/1
     */
    @Column(length = 500)
    private String url;
    
    /**
     * Estado de lectura
     */
    @Column(nullable = false)
    private Boolean leida = false;
    
    /**
     * Datos adicionales en formato JSON (opcional)
     * Ejemplo: {"pedidoId": "123", "total": 5000}
     */
    @Column(columnDefinition = "TEXT")
    private String datos;
    
    /**
     * Fecha de creación
     */
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    /**
     * Fecha en que fue leída (null si no ha sido leída)
     */
    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;
    
    // Constructor de conveniencia
    public Notificacion(Usuario usuario, TipoNotificacion tipo, String titulo, String mensaje, String url) {
        this.usuario = usuario;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.url = url;
        this.leida = false;
    }
}