package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de búsqueda con metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    
    /**
     * Lista de productos encontrados
     */
    private List<ProductoCardResponse> productos;
    
    /**
     * Total de resultados encontrados
     */
    private Long totalResultados;
    
    /**
     * Total de páginas
     */
    private Integer totalPaginas;
    
    /**
     * Página actual
     */
    private Integer paginaActual;
    
    /**
     * Tamaño de página
     */
    private Integer tamanioPagina;
    
    /**
     * ¿Tiene página siguiente?
     */
    private Boolean tieneSiguiente;
    
    /**
     * ¿Tiene página anterior?
     */
    private Boolean tieneAnterior;
    
    /**
     * Filtros aplicados (para mostrar en UI)
     */
    private FiltrosAplicados filtros;
    
    /**
     * Tiempo de búsqueda en ms (para debugging)
     */
    private Long tiempoBusqueda;
}