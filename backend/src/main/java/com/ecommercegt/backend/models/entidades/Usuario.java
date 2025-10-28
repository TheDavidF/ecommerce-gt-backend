package com.ecommercegt.backend.models.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;
    

    @Column(name = "nombre_usuario", unique = true, nullable = false, length = 100)
    private String nombreUsuario;
    
    @Column(name = "correo", unique = true, nullable = false, length = 255)
    private String correo;
    
    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasenaHash;
    
    @Column(name = "nombre_completo", length = 255)
    private String nombreCompleto;

    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    @Column(name = "telefono", length = 50)
    private String telefono;
    
    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}