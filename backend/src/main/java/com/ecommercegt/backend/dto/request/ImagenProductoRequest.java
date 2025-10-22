package com.ecommercegt.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenProductoRequest {
    
    @NotBlank(message = "La URL de la imagen es obligatoria")
    private String urlImagen;
    
    private Boolean esPrincipal = false;
    
    private Integer orden = 0;
}