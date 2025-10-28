package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.models.entidades.Notificacion;
import com.ecommercegt.backend.dto.request.NotificacionRequest;

import com.ecommercegt.backend.dto.response.NotificacionResponse;
import com.ecommercegt.backend.security.service.UserDetailsImpl;
import com.ecommercegt.backend.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador de Notificaciones
 * Gestiona las notificaciones de los usuarios
 */
@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {
    /**
     * Crear notificación desde el frontend
     * POST /api/notificaciones
     */
    @PostMapping
    public ResponseEntity<NotificacionResponse> crearNotificacion(@RequestBody NotificacionRequest request) {
        Notificacion notificacion = notificacionService.crearNotificacionConDatos(
            request.getUsuarioId(),
            request.getTipo(),
            request.getTitulo(),
            request.getMensaje(),
            request.getUrl(),
            request.getDatos()
        );
        return ResponseEntity.ok(NotificacionResponse.fromNotificacion(notificacion));
    }
    
    @Autowired
    private NotificacionService notificacionService;
    
    /**
     * Obtener mis notificaciones (paginadas)
     * GET /api/notificaciones?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<NotificacionResponse>> obtenerMisNotificaciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID usuarioId = userDetails.getId();
        
        Page<NotificacionResponse> notificaciones = notificacionService
                .obtenerMisNotificaciones(usuarioId, page, size);
        
        return ResponseEntity.ok(notificaciones);
    }
    
    /**
     * Obtener notificaciones no leídas
     * GET /api/notificaciones/no-leidas
     */
    @GetMapping("/no-leidas")
    public ResponseEntity<List<NotificacionResponse>> obtenerNoLeidas(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID usuarioId = userDetails.getId();
        
        List<NotificacionResponse> notificaciones = notificacionService.obtenerNoLeidas(usuarioId);
        
        return ResponseEntity.ok(notificaciones);
    }
    
    /**
     * Contar notificaciones no leídas (para badge)
     * GET /api/notificaciones/no-leidas/count
     */
    @GetMapping("/no-leidas/count")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID usuarioId = userDetails.getId();
        
        Long count = notificacionService.contarNoLeidas(usuarioId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener últimas 5 notificaciones (vista rápida)
     * GET /api/notificaciones/recientes
     */
    @GetMapping("/recientes")
    public ResponseEntity<List<NotificacionResponse>> obtenerRecientes(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID usuarioId = userDetails.getId();
        
        List<NotificacionResponse> notificaciones = notificacionService.obtenerUltimas5(usuarioId);
        
        return ResponseEntity.ok(notificaciones);
    }
    
    /**
     * Marcar notificación como leída
     * PUT /api/notificaciones/{id}/leer
     */
    @PutMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponse> marcarComoLeida(
            @PathVariable Long id,
            Authentication authentication) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID usuarioId = userDetails.getId();
        
        NotificacionResponse notificacion = notificacionService.marcarComoLeida(id, usuarioId);
        
        return ResponseEntity.ok(notificacion);
    }
    
    /**
     * Marcar todas las notificaciones como leídas
     * PUT /api/notificaciones/marcar-todas-leidas
     */
    @PutMapping("/marcar-todas-leidas")
    public ResponseEntity<Map<String, Object>> marcarTodasComoLeidas(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID usuarioId = userDetails.getId();
        
        int actualizadas = notificacionService.marcarTodasComoLeidas(usuarioId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Notificaciones marcadas como leídas");
        response.put("actualizadas", actualizadas);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Eliminar notificación
     * DELETE /api/notificaciones/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarNotificacion(
            @PathVariable Long id,
            Authentication authentication) {
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID usuarioId = userDetails.getId();
        
        notificacionService.eliminarNotificacion(id, usuarioId);
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Notificación eliminada exitosamente");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Eliminar todas las notificaciones leídas
     * DELETE /api/notificaciones/leidas
     */
    @DeleteMapping("/leidas")
    public ResponseEntity<Map<String, String>> eliminarLeidas(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID usuarioId = userDetails.getId();
        
        notificacionService.eliminarLeidas(usuarioId);
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Notificaciones leídas eliminadas exitosamente");
        
        return ResponseEntity.ok(response);
    }
}