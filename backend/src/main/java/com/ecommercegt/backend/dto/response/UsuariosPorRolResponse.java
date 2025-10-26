package com.ecommercegt.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuariosPorRolResponse {
    private String rol;
    private Long cantidad;
}