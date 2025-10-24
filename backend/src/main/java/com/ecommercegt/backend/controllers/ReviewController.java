package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.ActualizarReviewRequest;
import com.ecommercegt.backend.dto.request.CrearReviewRequest;
import com.ecommercegt.backend.dto.request.VotarReviewRequest;
import com.ecommercegt.backend.dto.response.EstadisticasReviewsResponse;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.dto.response.ReviewResponse;
import com.ecommercegt.backend.dto.response.VotoResponse;
import com.ecommercegt.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller de Reviews
 * Gestiona reseñas, votos y estadísticas de productos
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    /**
     * Crear una nueva review
     * POST /api/reviews
     * 
     * Validaciones:
     * - Usuario debe haber comprado el producto
     * - Usuario no debe tener review previa
     * - Calificación 1-5
     * 
     * Requiere autenticación
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> crearReview(@Valid @RequestBody CrearReviewRequest request) {
        try {
            ReviewResponse review = reviewService.crearReview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al crear review: " + e.getMessage()));
        }
    }
    
    /**
     * Actualizar una review existente
     * PUT /api/reviews/{id}
     * 
     * Solo el autor puede actualizar
     * La review debe pasar por moderación de nuevo
     * 
     * Requiere autenticación
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> actualizarReview(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarReviewRequest request) {
        try {
            ReviewResponse review = reviewService.actualizarReview(id, request);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar review: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar una review
     * DELETE /api/reviews/{id}
     * 
     * Solo el autor o ADMIN pueden eliminar
     * 
     * Requiere autenticación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> eliminarReview(@PathVariable Long id) {
        try {
            reviewService.eliminarReview(id);
            return ResponseEntity.ok(new MessageResponse("Review eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar review: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener reviews de un producto (solo aprobadas)
     * GET /api/reviews/producto/{productoId}
     * 
     * Ordenadas por votos útiles (más populares primero)
     * 
     * Público (no requiere autenticación)
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Page<ReviewResponse>> obtenerReviewsProducto(
            @PathVariable UUID productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.obtenerReviewsProducto(productoId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Obtener mis reviews
     * GET /api/reviews/mis-reviews
     * 
     * Reviews del usuario autenticado
     * 
     * Requiere autenticación
     */
    @GetMapping("/mis-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ReviewResponse>> obtenerMisReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.obtenerMisReviews(pageable);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Votar en una review
     * POST /api/reviews/{id}/votar
     * 
     * Un usuario puede votar una sola vez
     * Puede cambiar su voto
     * No puede votar su propia review
     * 
     * Requiere autenticación
     */
    @PostMapping("/{id}/votar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> votarReview(
            @PathVariable Long id,
            @Valid @RequestBody VotarReviewRequest request) {
        try {
            VotoResponse voto = reviewService.votarReview(id, request);
            return ResponseEntity.ok(voto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al votar: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener estadísticas de reviews de un producto
     * GET /api/reviews/producto/{productoId}/estadisticas
     * 
     * Incluye:
     * - Total reviews
     * - Promedio calificación
     * - Distribución por estrellas
     * - Porcentaje recomendación
     * - Reviews verificadas
     * 
     * Público
     */
    @GetMapping("/producto/{productoId}/estadisticas")
    public ResponseEntity<EstadisticasReviewsResponse> obtenerEstadisticas(
            @PathVariable UUID productoId) {
        EstadisticasReviewsResponse estadisticas = reviewService.obtenerEstadisticas(productoId);
        return ResponseEntity.ok(estadisticas);
    }
    
    /**
     * Aprobar una review (MODERADOR/ADMIN)
     * PUT /api/reviews/{id}/aprobar
     * 
     * Requiere rol MODERADOR o ADMIN
     */
    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> aprobarReview(@PathVariable Long id) {
        try {
            ReviewResponse review = reviewService.aprobarReview(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al aprobar review: " + e.getMessage()));
        }
    }
    
    /**
     * Rechazar una review (MODERADOR/ADMIN)
     * PUT /api/reviews/{id}/rechazar
     * 
     * Requiere rol MODERADOR o ADMIN
     */
    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> rechazarReview(@PathVariable Long id) {
        try {
            ReviewResponse review = reviewService.rechazarReview(id);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al rechazar review: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener reviews pendientes de aprobación (MODERADOR/ADMIN)
     * GET /api/reviews/pendientes
     * 
     * Requiere rol MODERADOR o ADMIN
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<Page<ReviewResponse>> obtenerReviewsPendientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.obtenerReviewsPendientes(pageable);
        return ResponseEntity.ok(reviews);
    }
}