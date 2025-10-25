package com.ecommercegt.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeracionRequest {
    
    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;
}