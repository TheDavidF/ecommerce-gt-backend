package com.ecommercegt.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Request para votar en una review
 * 
 * Permite indicar si una review fue útil o no
 * 
 * Reglas:
 * - Un usuario puede votar UNA SOLA VEZ por review
 * - Puede cambiar su voto posteriormente
 * - No puede votar su propia review
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotarReviewRequest {
    
    /**
     * Indica si la review fue útil
     * true = útil (upvote / thumbs up)
     * false = no útil (downvote / thumbs down)
     */
    @NotNull(message = "Debe indicar si la review fue útil o no")
    private Boolean esUtil;
}