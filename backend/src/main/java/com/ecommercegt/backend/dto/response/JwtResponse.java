package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String nombreUsuario;
    private String correo;
    private List<String> roles;
    
    public JwtResponse(String token, UUID id, String nombreUsuario, String correo, List<String> roles) {
        this.token = token;
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.roles = roles;
    }
}