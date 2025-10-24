package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO Response para estadísticas de reviews de un producto
 * 
 * Incluye:
 * - Total de reviews
 * - Promedio de calificación
 * - Distribución por estrellas (5★, 4★, 3★, 2★, 1★)
 * - Porcentaje de recomendación
 * - Reviews verificadas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasReviewsResponse {
    
    /**
     * Total de reviews del producto
     */
    private Long totalReviews;
    
    /**
     * Promedio de calificación (0.0 - 5.0)
     */
    private Double promedioCalificacion;
    
    /**
     * Distribución de calificaciones
     * Key: calificación (1-5)
     * Value: cantidad de reviews con esa calificación
     */
    private Map<Integer, Long> distribucionCalificaciones = new HashMap<>();
    
    /**
     * Porcentaje de usuarios que recomiendan el producto
     * (reviews 4★ y 5★ / total reviews) × 100
     */
    private Double porcentajeRecomendacion;
    
    /**
     * Cantidad de reviews verificadas (compra confirmada)
     */
    private Long reviewsVerificadas;
    
    /**
     * Porcentaje de reviews verificadas
     */
    private Double porcentajeVerificadas;
    
    // ==================== MÉTODOS DE UTILIDAD ====================
    
    /**
     * Inicializar distribución con ceros
     */
    public void inicializarDistribucion() {
        for (int i = 1; i <= 5; i++) {
            distribucionCalificaciones.putIfAbsent(i, 0L);
        }
    }
    
    /**
     * Calcular porcentaje de recomendación
     */
    public void calcularPorcentajeRecomendacion() {
        if (totalReviews == null || totalReviews == 0) {
            this.porcentajeRecomendacion = 0.0;
            return;
        }
        
        Long reviews4y5 = distribucionCalificaciones.getOrDefault(4, 0L) 
                        + distribucionCalificaciones.getOrDefault(5, 0L);
        
        this.porcentajeRecomendacion = (reviews4y5.doubleValue() / totalReviews) * 100;
    }
    
    /**
     * Calcular porcentaje de verificadas
     */
    public void calcularPorcentajeVerificadas() {
        if (totalReviews == null || totalReviews == 0) {
            this.porcentajeVerificadas = 0.0;
            return;
        }
        
        this.porcentajeVerificadas = (reviewsVerificadas.doubleValue() / totalReviews) * 100;
    }
}