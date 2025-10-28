package com.ecommercegt.backend.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class SancionRequest {
    private UUID usuarioId;
    private UUID moderadorId; // opcional, si lo usas en el request
    private String razon;
    private String fechaFin; // ISO 8601 string, puede ajustarse a Date si lo prefieres
}
