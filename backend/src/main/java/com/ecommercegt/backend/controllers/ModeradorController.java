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
     * Listar productos pendientes de revisión
     * GET /api/moderador/productos/pendientes
     */
    @GetMapping("/productos/pendientes")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<ProductoModeracionResponse>> listarProductosPendientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductoModeracionResponse> productos = moderadorService.listarProductosPendientes(pageable);
        
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Listar todos los productos con filtro por estado
     * GET /api/moderador/productos?estado=APROBADO
     */
    @GetMapping("/productos")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<ProductoModeracionResponse>> listarProductos(
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductoModeracionResponse> productos = moderadorService.listarProductos(estado, pageable);
        
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtener detalle de un producto
     * GET /api/moderador/productos/{id}
     */
    @GetMapping("/productos/{id}")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<ProductoModeracionResponse> obtenerProducto(@PathVariable UUID id) {
        ProductoModeracionResponse producto = moderadorService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }
    
    /**
     * Aprobar producto
     * PUT /api/moderador/productos/{id}/aprobar
     */
    @PutMapping("/productos/{id}/aprobar")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<?> aprobarProducto(@PathVariable UUID id) {
        try {
            ProductoModeracionResponse producto = moderadorService.aprobarProducto(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al aprobar producto: " + e.getMessage()));
        }
    }
    
    /**
     * Rechazar producto
     * PUT /api/moderador/productos/{id}/rechazar
     */
    @PutMapping("/productos/{id}/rechazar")
    @PreAuthorize("hasRole('MODERADOR') or hasRole('ADMIN')")
    public ResponseEntity<?> rechazarProducto(
            @PathVariable UUID id,
            @Valid @RequestBody ModeracionRequest request
    ) {
        try {
            ProductoModeracionResponse producto = moderadorService.rechazarProducto(
                id, 
                request.getMotivo()
            );
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al rechazar producto: " + e.getMessage()));
        }
    }
}