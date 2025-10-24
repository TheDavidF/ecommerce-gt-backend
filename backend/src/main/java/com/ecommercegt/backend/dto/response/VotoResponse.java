package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Response para el resultado de un voto en una review
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotoResponse {
    
    /**
     * Mensaje de confirmación
     */
    private String mensaje;
    
    /**
     * ID de la review votada
     */
    private Long reviewId;
    
    /**
     * Cantidad actualizada de votos útiles
     */
    private Long votosUtiles;
    
    /**
     * Cantidad actualizada de votos no útiles
     */
    private Long votosNoUtiles;
    
    /**
     * Balance de votos (útiles - no útiles)
     */
    private Long balanceVotos;
}