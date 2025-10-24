package com.ecommercegt.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para recibir parámetros de búsqueda y filtros
 * Todos los campos son opcionales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    
    /**
     * Término de búsqueda (busca en nombre, descripción, marca, modelo)
     */
    private String q;
    
    /**
     * Filtrar por categoría
     */
    private Integer categoriaId;
    
    /**
     * Precio mínimo
     */
    private BigDecimal precioMin;
    
    /**
     * Precio máximo
     */
    private BigDecimal precioMax;
    
    /**
     * Calificación mínima (1-5)
     */
    private Integer calificacionMin;
    
    /**
     * Solo productos en stock
     */
    private Boolean enStock;
    
    /**
     * Filtrar por vendedor
     */
    private UUID vendedorId;
    
    /**
     * Solo productos destacados
     */
    private Boolean destacados;
    
    /**
     * Ordenamiento:
     * - relevancia (default)
     * - precio_asc
     * - precio_desc
     * - calificacion_desc
     * - ventas_desc
     * - recientes
     */
    private String ordenar = "relevancia";
    
    /**
     * Número de página (0-indexed)
     */
    private Integer page = 0;
    
    /**
     * Tamaño de página
     */
    private Integer size = 20;
}