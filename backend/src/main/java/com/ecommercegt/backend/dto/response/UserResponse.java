package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String nombreUsuario;
    private String correo;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String telefono;
    private Boolean activo;
    private Set<String> roles;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}