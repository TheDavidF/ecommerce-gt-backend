package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.CategoriaRequest;
import com.ecommercegt.backend.dto.response.CategoriaResponse;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    /**
     * Crear nueva categoría (solo moderadores y admins)
     * POST /api/categorias
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> crearCategoria(@Valid @RequestBody CategoriaRequest request) {
        try {
            CategoriaResponse response = categoriaService.crearCategoria(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al crear categoría: " + e.getMessage()));
        }
    }
    
    /**
     * Listar todas las categorías (público)
     * GET /api/categorias
     */
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        List<CategoriaResponse> categorias = categoriaService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }
    
    /**
     * Listar solo categorías activas (público)
     * GET /api/categorias/activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<CategoriaResponse>> listarCategoriasActivas() {
        List<CategoriaResponse> categorias = categoriaService.listarCategoriasActivas();
        return ResponseEntity.ok(categorias);
    }
    
    /**
     * Obtener categoría por ID (público)
     * GET /api/categorias/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCategoriaPorId(@PathVariable Integer id) {
        try {
            CategoriaResponse response = categoriaService.obtenerCategoriaPorId(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Actualizar categoría (solo moderadores y admins)
     * PUT /api/categorias/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> actualizarCategoria(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaRequest request) {
        try {
            CategoriaResponse response = categoriaService.actualizarCategoria(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar categoría: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar categoría (solo admins)
     * DELETE /api/categorias/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.ok(new MessageResponse("Categoría eliminada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar categoría: " + e.getMessage()));
        }
    }
    
    /**
     * Buscar categorías por nombre (público)
     * GET /api/categorias/buscar?q=texto
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<CategoriaResponse>> buscarCategorias(@RequestParam String q) {
        List<CategoriaResponse> categorias = categoriaService.buscarCategorias(q);
        return ResponseEntity.ok(categorias);
    }
}