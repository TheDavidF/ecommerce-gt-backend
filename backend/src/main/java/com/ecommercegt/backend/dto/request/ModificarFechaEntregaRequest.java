package com.ecommercegt.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModificarFechaEntregaRequest {
    
    @NotNull(message = "La fecha de entrega estimada es requerida")
    private LocalDateTime fechaEntregaEstimada;
}