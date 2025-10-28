package com.ecommercegt.backend.dto.request;

import com.ecommercegt.backend.models.enums.TipoNotificacion;
import lombok.Data;

// import java.util.Map;
import java.util.UUID;

@Data
public class NotificacionRequest {
    private UUID usuarioId;
    private TipoNotificacion tipo;
    private String titulo;
    private String mensaje;
    private String url;
    private String datos;
}
