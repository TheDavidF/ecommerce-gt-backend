package com.ecommercegt.backend.models.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sanciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sancion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public String getUsuarioNombreCompleto() {
        return usuario != null ? usuario.getNombreCompleto() : null;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moderador_id")
    private Usuario moderador;

    public String getModeradorNombreCompleto() {
        return moderador != null ? moderador.getNombreCompleto() : null;
    }

    @Column(name = "razon", nullable = false)
    private String razon;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now();
        }
    }
}
