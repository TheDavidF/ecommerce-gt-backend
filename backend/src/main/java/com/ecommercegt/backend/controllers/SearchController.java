package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.SearchRequest;
import com.ecommercegt.backend.dto.response.SearchResponse;
import com.ecommercegt.backend.service.ProductoSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Controlador de Búsqueda de Productos
 * Endpoint público para búsqueda con filtros
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    
    @Autowired
    private ProductoSearchService searchService;
    
    /**
     * Búsqueda de productos con filtros
     * GET /api/search?q=laptop&precioMax=5000&calificacionMin=4&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<SearchResponse> buscarProductos(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer calificacionMin,
            @RequestParam(required = false) Boolean enStock,
            @RequestParam(required = false) UUID vendedorId,
            @RequestParam(required = false) Boolean destacados,
            @RequestParam(required = false, defaultValue = "relevancia") String ordenar,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        
        // Construir request
        SearchRequest request = new SearchRequest();
        request.setQ(q);
        request.setCategoriaId(categoriaId);
        request.setPrecioMin(precioMin);
        request.setPrecioMax(precioMax);
        request.setCalificacionMin(calificacionMin);
        request.setEnStock(enStock);
        request.setVendedorId(vendedorId);
        request.setDestacados(destacados);
        request.setOrdenar(ordenar);
        request.setPage(page);
        request.setSize(size);
        
        // Ejecutar búsqueda
        SearchResponse response = searchService.buscarProductos(request);
        
        return ResponseEntity.ok(response);
    }
}