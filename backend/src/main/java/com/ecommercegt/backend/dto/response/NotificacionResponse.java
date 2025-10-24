package com.ecommercegt.backend.dto.response;

import com.ecommercegt.backend.models.enums.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de notificación
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponse {
    
    private Long id;
    private TipoNotificacion tipo;
    private String titulo;
    private String mensaje;
    private String url;
    private Boolean leida;
    private String datos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLectura;
    
    /**
     * Tiempo relativo (hace 5 minutos, hace 2 horas, etc.)
     */
    private String tiempoRelativo;
    
    /**
     * Constructor desde entidad
     */
    public static NotificacionResponse fromNotificacion(com.ecommercegt.backend.models.entidades.Notificacion notificacion) {
        NotificacionResponse response = new NotificacionResponse();
        response.setId(notificacion.getId());
        response.setTipo(notificacion.getTipo());
        response.setTitulo(notificacion.getTitulo());
        response.setMensaje(notificacion.getMensaje());
        response.setUrl(notificacion.getUrl());
        response.setLeida(notificacion.getLeida());
        response.setDatos(notificacion.getDatos());
        response.setFechaCreacion(notificacion.getFechaCreacion());
        response.setFechaLectura(notificacion.getFechaLectura());
        response.setTiempoRelativo(calcularTiempoRelativo(notificacion.getFechaCreacion()));
        return response;
    }
    
    /**
     * Calcular tiempo relativo (hace X minutos/horas/días)
     */
    private static String calcularTiempoRelativo(LocalDateTime fecha) {
        if (fecha == null) return "";
        
        LocalDateTime ahora = LocalDateTime.now();
        long segundos = java.time.Duration.between(fecha, ahora).getSeconds();
        
        if (segundos < 60) {
            return "Hace " + segundos + " segundos";
        } else if (segundos < 3600) {
            long minutos = segundos / 60;
            return "Hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
        } else if (segundos < 86400) {
            long horas = segundos / 3600;
            return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        } else {
            long dias = segundos / 86400;
            return "Hace " + dias + (dias == 1 ? " día" : " días");
        }
    }
}