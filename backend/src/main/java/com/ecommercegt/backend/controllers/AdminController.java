package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.CreateUserRequest;
import com.ecommercegt.backend.dto.request.UpdateUserRequest;
import com.ecommercegt.backend.dto.response.EstadisticasGeneralesResponse;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.dto.response.UserResponse;
import com.ecommercegt.backend.service.AdminService;
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
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    // ==================== ESTADÍSTICAS ====================
    
    /**
     * Obtener estadísticas generales
     * GET /api/admin/estadisticas
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerEstadisticas() {
        EstadisticasGeneralesResponse estadisticas = adminService.obtenerEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }
    
    // ==================== GESTIÓN DE USUARIOS ====================
    
    /**
     * Listar todos los usuarios
     * GET /api/admin/usuarios
     */
    @GetMapping("/usuarios")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> listarUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserResponse> usuarios;
        if (rol != null && !rol.isEmpty()) {
            usuarios = adminService.listarUsuariosPorRol(rol, pageable);
        } else {
            usuarios = adminService.listarUsuarios(pageable);
        }
        
        return ResponseEntity.ok(usuarios);
    }
    
    /**
     * Obtener usuario por ID
     * GET /api/admin/usuarios/{id}
     */
    @GetMapping("/usuarios/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> obtenerUsuario(@PathVariable UUID id) {
        try {
            UserResponse usuario = adminService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Crear nuevo usuario (empleado)
     * POST /api/admin/usuarios
     */
    @PostMapping("/usuarios")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserResponse usuario = adminService.crearUsuario(request);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al crear usuario: " + e.getMessage()));
        }
    }
    
    /**
     * Actualizar usuario
     * PUT /api/admin/usuarios/{id}
     */
    @PutMapping("/usuarios/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        try {
            UserResponse usuario = adminService.actualizarUsuario(id, request);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar usuario: " + e.getMessage()));
        }
    }
    
    /**
     * Desactivar usuario
     * PUT /api/admin/usuarios/{id}/desactivar
     */
    @PutMapping("/usuarios/{id}/desactivar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> desactivarUsuario(@PathVariable UUID id) {
        try {
            adminService.desactivarUsuario(id);
            return ResponseEntity.ok(new MessageResponse("Usuario desactivado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al desactivar usuario: " + e.getMessage()));
        }
    }
    
    /**
     * Activar usuario
     * PUT /api/admin/usuarios/{id}/activar
     */
    @PutMapping("/usuarios/{id}/activar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> activarUsuario(@PathVariable UUID id) {
        try {
            adminService.activarUsuario(id);
            return ResponseEntity.ok(new MessageResponse("Usuario activado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al activar usuario: " + e.getMessage()));
        }
    }
}