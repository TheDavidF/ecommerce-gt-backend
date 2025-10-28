package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.dto.response.ProductoModeracionResponse;
import com.ecommercegt.backend.models.enums.EstadoSolicitudModeracion;
import com.ecommercegt.backend.service.ModeradorService;
import com.ecommercegt.backend.service.SancionService;
import com.ecommercegt.backend.models.entidades.Sancion;

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
    private final SancionService sancionService;
    private final com.ecommercegt.backend.service.NotificacionService notificacionService;

    // ==================== SOLICITUDES ====================

    /**
     * Listar solicitudes pendientes
     * GET /api/moderador/solicitudes/pendientes
     */
    @GetMapping("/solicitudes/pendientes")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<Page<ProductoModeracionResponse>> listarSolicitudesPendientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaSolicitud").descending());
        Page<ProductoModeracionResponse> solicitudes = moderadorService.listarSolicitudes("PENDIENTE", pageable);
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Listar todas las solicitudes
     * GET /api/moderador/solicitudes
     */
    @GetMapping("/solicitudes")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<Page<ProductoModeracionResponse>> listarTodasLasSolicitudes(
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
            ProductoModeracionResponse solicitud = moderadorService.obtenerSolicitudPorId(id);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== SANCIONES ====================

    /**
     * Crear sanción
     * POST /api/moderador/sanciones
     */
    @PostMapping("/sanciones")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> crearSancion(@RequestBody com.ecommercegt.backend.dto.request.SancionRequest request) {
        try {
            UUID moderadorId = request.getModeradorId();
            UUID usuarioId = request.getUsuarioId();
            java.time.LocalDateTime fechaFin = null;
            if (request.getFechaFin() != null && !request.getFechaFin().isEmpty()) {
                fechaFin = java.time.LocalDateTime.parse(request.getFechaFin());
            }
            Sancion sancion = sancionService.crearSancion(
                usuarioId,
                moderadorId,
                request.getRazon(),
                fechaFin
            );
            if (sancion != null && sancion.getUsuario() != null) {
                notificacionService.notificarUsuarioSancionado(sancion.getUsuario().getId(), sancion.getRazon());
            }
            return ResponseEntity.ok(new MessageResponse("Sanción creada exitosamente"));
        } catch (Exception e) {
            e.printStackTrace(); // Mostrar el error real en los logs
            return ResponseEntity.badRequest().body(new MessageResponse("Error al crear sanción: " + e.getMessage()));
        }
    }

    /**
     * Desactivar sanción
     * PUT /api/moderador/sanciones/{id}/desactivar
     */
    @PutMapping("/sanciones/{id}/desactivar")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> desactivarSancion(@PathVariable Integer id) {
        try {
            sancionService.desactivarSancion(id);
            return ResponseEntity.ok(new MessageResponse("Sanción desactivada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error al desactivar sanción: " + e.getMessage()));
        }
    }

    // ==================== USUARIOS ====================

    /**
     * Listar usuarios para sancionar
     * GET /api/moderador/usuarios
     */
    @GetMapping("/usuarios")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> listarUsuariosParaSancion() {
        try {
            return ResponseEntity.ok(moderadorService.listarUsuariosParaSancion());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error al listar usuarios: " + e.getMessage()));
        }
    }

    /**
     * Listar sanciones por moderador (DTO para evitar error de serialización)
     */
    @GetMapping("/sanciones/moderador/{id}")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> listarSancionesPorModerador(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaInicio").descending());
        Page<Sancion> sanciones = sancionService.listarSancionesPorModerador(UUID.fromString(id), pageable);
        // Mapear a DTO simple para evitar error de serialización
        Page<Map<String, Object>> dtoPage = sanciones.map(s -> {
            Map<String, Object> dto = new HashMap<>();
            // Forzar inicialización de usuario y moderador
            if (s.getUsuario() != null) {
                dto.put("usuarioId", s.getUsuario().getId());
                String nombreUsuario = s.getUsuario().getNombreCompleto();
                dto.put("usuarioNombre", nombreUsuario != null ? nombreUsuario : "");
            } else {
                dto.put("usuarioId", null);
                dto.put("usuarioNombre", "");
            }
            if (s.getModerador() != null) {
                String nombreModerador = s.getModerador().getNombreCompleto();
                dto.put("moderadorNombre", nombreModerador != null ? nombreModerador : "");
            } else {
                dto.put("moderadorId", null);
                dto.put("moderadorNombre", "");
            }
            dto.put("id", s.getId());
            dto.put("razon", s.getRazon());
            dto.put("fechaInicio", s.getFechaInicio());
            dto.put("fechaFin", s.getFechaFin());
            dto.put("activa", s.isActiva());
            dto.put("fechaCreacion", s.getFechaCreacion());
            return dto;
        });
        return ResponseEntity.ok(dtoPage);
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
}