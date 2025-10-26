package com.ecommercegt.backend.models.entidades;

import com.ecommercegt.backend.models.enums.EstadoSolicitudModeracion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitudes_moderacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudModeracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;
    
    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderador_id")
    private Usuario moderador;
    
    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSolicitudModeracion estado;
    
    @Column(name = "razon", columnDefinition = "TEXT")
    private String razon;
    
    @PrePersist
    protected void onCreate() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoSolicitudModeracion.PENDIENTE;
        }
    }
}