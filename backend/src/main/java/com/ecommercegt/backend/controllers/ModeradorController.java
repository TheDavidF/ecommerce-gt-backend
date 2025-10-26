package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.ModeracionRequest;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.dto.response.ProductoModeracionResponse;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/moderador")
@RequiredArgsConstructor
public class ModeradorController {
    
    private final ModeradorService moderadorService;
    
    /**
     * Listar solicitudes pendientes de revisión
     * GET /api/moderador/solicitudes/pendientes
     */
    @GetMapping("/solicitudes/pendientes")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<ProductoModeracionResponse>> listarSolicitudesPendientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaSolicitud") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductoModeracionResponse> solicitudes = moderadorService.listarSolicitudesPendientes(pageable);
        
        return ResponseEntity.ok(solicitudes);
    }
    
    /**
     * Listar todas las solicitudes con filtro por estado
     * GET /api/moderador/solicitudes?estado=APROBADA
     */
    @GetMapping("/solicitudes")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<ProductoModeracionResponse>> listarSolicitudes(
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaSolicitud") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductoModeracionResponse> solicitudes = moderadorService.listarSolicitudes(estado, pageable);
        
        return ResponseEntity.ok(solicitudes);
    }
    
    /**
     * Obtener detalle de una solicitud por ID del producto
     * GET /api/moderador/productos/{productoId}
     */
    @GetMapping("/productos/{productoId}")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<ProductoModeracionResponse> obtenerSolicitud(@PathVariable UUID productoId) {
        try {
            ProductoModeracionResponse solicitud = moderadorService.obtenerSolicitudPorProductoId(productoId);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Aprobar solicitud de producto
     * PUT /api/moderador/productos/{productoId}/aprobar
     */
    @PutMapping("/productos/{productoId}/aprobar")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable UUID productoId) {
        try {
            ProductoModeracionResponse solicitud = moderadorService.aprobarSolicitud(productoId);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al aprobar solicitud: " + e.getMessage()));
        }
    }
    
    /**
     * Rechazar solicitud de producto
     * PUT /api/moderador/productos/{productoId}/rechazar
     */
    @PutMapping("/productos/{productoId}/rechazar")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<?> rechazarSolicitud(
            @PathVariable UUID productoId,
            @Valid @RequestBody ModeracionRequest request
    ) {
        try {
            ProductoModeracionResponse solicitud = moderadorService.rechazarSolicitud(
                productoId, 
                request.getMotivo()
            );
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al rechazar solicitud: " + e.getMessage()));
        }
    }
}