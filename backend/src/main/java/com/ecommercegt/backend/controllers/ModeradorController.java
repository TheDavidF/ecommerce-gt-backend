package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.ModeracionRequest;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.dto.response.ProductoModeracionResponse;
import com.ecommercegt.backend.models.enums.EstadoSolicitudModeracion;
import com.ecommercegt.backend.service.ModeradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/moderador")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class ModeradorController {
    
    private final ModeradorService moderadorService;
    
    // ==================== LISTAR SOLICITUDES ====================
    
    /**
     * Listar solicitudes pendientes de revisión
     * GET /api/moderador/solicitudes/pendientes
     */
    @GetMapping("/solicitudes/pendientes")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<Page<ProductoModeracionResponse>> listarSolicitudesPendientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaSolicitud").descending());
        Page<ProductoModeracionResponse> solicitudes = moderadorService.listarSolicitudesPendientes(pageable);
        return ResponseEntity.ok(solicitudes);
    }
    
    /**
     * Listar todas las solicitudes
     * GET /api/moderador/solicitudes
     */
    @GetMapping("/solicitudes")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<Page<ProductoModeracionResponse>> listarTodasSolicitudes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaSolicitud").descending());
        Page<ProductoModeracionResponse> solicitudes = moderadorService.listarSolicitudes(null, pageable);
        return ResponseEntity.ok(solicitudes);
    }
    
    /**
     * Listar solicitudes por estado
     * GET /api/moderador/solicitudes/estado/{estado}
     */
    @GetMapping("/solicitudes/estado/{estado}")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<Page<ProductoModeracionResponse>> listarSolicitudesPorEstado(
            @PathVariable String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaSolicitud").descending());
        Page<ProductoModeracionResponse> solicitudes = moderadorService.listarSolicitudes(estado, pageable);
        return ResponseEntity.ok(solicitudes);
    }
    
    /**
     * Obtener detalle de una solicitud por ID
     * GET /api/moderador/solicitudes/{id}
     */
    @GetMapping("/solicitudes/{id}")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<ProductoModeracionResponse> obtenerSolicitud(@PathVariable UUID id) {
        try {
            ProductoModeracionResponse solicitud = moderadorService.obtenerSolicitudPorProductoId(id);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ==================== ACCIONES DE MODERACIÓN ====================
    
    /**
     * Aprobar solicitud de producto
     * PUT /api/moderador/solicitudes/{id}/aprobar
     */
    @PutMapping("/solicitudes/{id}/aprobar")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> aprobarSolicitud(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body
    ) {
        try {
            String comentario = body != null ? body.get("comentario") : null;
            
            
            ProductoModeracionResponse solicitud = moderadorService.aprobarSolicitudPorId(id);
            
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al aprobar solicitud: " + e.getMessage()));
        }
    }
    
     /**
     * Rechazar solicitud de producto
     * PUT /api/moderador/solicitudes/{id}/rechazar
     */
    @PutMapping("/solicitudes/{id}/rechazar")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> rechazarSolicitud(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String motivo = body.get("motivo");
            if (motivo == null || motivo.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El motivo del rechazo es obligatorio"));
            }
            
            
            ProductoModeracionResponse solicitud = moderadorService.rechazarSolicitudPorId(id, motivo);
            
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al rechazar solicitud: " + e.getMessage()));
        }
    }
    
    /**
     * Solicitar cambios en el producto
     * PUT /api/moderador/solicitudes/{id}/solicitar-cambios
     */
    @PutMapping("/solicitudes/{id}/solicitar-cambios")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> solicitarCambios(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String comentario = body.get("comentario");
            if (comentario == null || comentario.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El comentario es obligatorio"));
            }
            
            
            ProductoModeracionResponse solicitud = moderadorService.solicitarCambiosPorId(id, comentario);
            
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al solicitar cambios: " + e.getMessage()));
        }
    }
    
    // ==================== ESTADÍSTICAS ====================
    
    /**
     * Obtener estadísticas de moderación
     * GET /api/moderador/estadisticas
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticas() {
        Map<String, Long> estadisticas = new HashMap<>();
        
        try {
            estadisticas.put("pendientes", moderadorService.contarSolicitudesPorEstado(EstadoSolicitudModeracion.PENDIENTE));
            estadisticas.put("aprobadas", moderadorService.contarSolicitudesPorEstado(EstadoSolicitudModeracion.APROBADO));
            estadisticas.put("rechazadas", moderadorService.contarSolicitudesPorEstado(EstadoSolicitudModeracion.RECHAZADO));
            estadisticas.put("cambiosSolicitados", moderadorService.contarSolicitudesPorEstado(EstadoSolicitudModeracion.CAMBIOS_SOLICITADOS));
        } catch (Exception e) {
            // Si hay error, devolver ceros
            estadisticas.put("pendientes", 0L);
            estadisticas.put("aprobadas", 0L);
            estadisticas.put("rechazadas", 0L);
            estadisticas.put("cambiosSolicitados", 0L);
        }
        
        return ResponseEntity.ok(estadisticas);
    }
    
    // ==================== ENDPOINTS LEGACY (por compatibilidad) ====================
    
    /**
     * Obtener detalle por ID del producto (legacy)
     * GET /api/moderador/productos/{productoId}
     */
    @GetMapping("/productos/{productoId}")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<ProductoModeracionResponse> obtenerSolicitudPorProducto(@PathVariable UUID productoId) {
        try {
            ProductoModeracionResponse solicitud = moderadorService.obtenerSolicitudPorProductoId(productoId);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}