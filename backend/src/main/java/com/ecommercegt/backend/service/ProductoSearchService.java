package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.SearchRequest;
import com.ecommercegt.backend.dto.response.FiltrosAplicados;
import com.ecommercegt.backend.dto.response.ProductoCardResponse;
import com.ecommercegt.backend.dto.response.SearchResponse;
import com.ecommercegt.backend.models.entidades.Producto;
import com.ecommercegt.backend.models.enums.EstadoProducto;
import com.ecommercegt.backend.repositorios.CategoriaRepository;
import com.ecommercegt.backend.repositorios.ProductoRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Búsqueda de Productos
 * Maneja búsqueda, filtros y ordenamiento
 */
@Service
public class ProductoSearchService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Búsqueda principal de productos con filtros
     */
    @Transactional(readOnly = true)
    public SearchResponse buscarProductos(SearchRequest request) {
        long startTime = System.currentTimeMillis();

        // Construir paginación y ordenamiento
        Pageable pageable = construirPageable(request);

        // Construir Specification
        Specification<Producto> spec = construirSpecification(request);

        // Ejecutar búsqueda
        Page<Producto> pageProductos = productoRepository.findAll(spec, pageable);

        // Convertir a DTOs
        List<ProductoCardResponse> productos = pageProductos.getContent().stream()
                .map(ProductoCardResponse::fromProducto)
                .collect(Collectors.toList());

        // Construir respuesta
        SearchResponse response = new SearchResponse();
        response.setProductos(productos);
        response.setTotalResultados(pageProductos.getTotalElements());
        response.setTotalPaginas(pageProductos.getTotalPages());
        response.setPaginaActual(pageProductos.getNumber());
        response.setTamanioPagina(pageProductos.getSize());
        response.setTieneSiguiente(pageProductos.hasNext());
        response.setTieneAnterior(pageProductos.hasPrevious());

        // Filtros aplicados
        response.setFiltros(construirFiltrosAplicados(request));

        // Tiempo de búsqueda
        long endTime = System.currentTimeMillis();
        response.setTiempoBusqueda(endTime - startTime);

        return response;
    }

    /**
     * Construir Specification con todos los filtros
     */
    /**
     * Construir Specification con todos los filtros
     */
    private Specification<Producto> construirSpecification(SearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Siempre filtrar por estado APROBADO
            predicates.add(cb.equal(root.get("estado"), EstadoProducto.APROBADO));

            // Búsqueda por término
            if (request.getQ() != null && !request.getQ().trim().isEmpty()) {
                String termino = request.getQ().toLowerCase().trim();
                Predicate busquedaTexto = cb.or(
                        cb.like(cb.lower(root.get("nombre")), "%" + termino + "%"),
                        cb.like(cb.lower(root.get("descripcion")), "%" + termino + "%"),
                        cb.like(cb.lower(root.get("marca")), "%" + termino + "%"),
                        cb.like(cb.lower(root.get("modelo")), "%" + termino + "%"));
                predicates.add(busquedaTexto);
            }

            // Filtro por categoría (COMENTADO por ahora - UUID vs Integer)
            // if (request.getCategoriaId() != null) {
            // predicates.add(cb.equal(root.get("categoria").get("id"),
            // request.getCategoriaId()));
            // }

            // Filtro precio mínimo
            if (request.getPrecioMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("precio"), request.getPrecioMin()));
            }

            // Filtro precio máximo
            if (request.getPrecioMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("precio"), request.getPrecioMax()));
            }

            // Filtro calificación mínima
            if (request.getCalificacionMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("calificacionPromedio"),
                        request.getCalificacionMin().doubleValue()));
            }

            // Filtro solo en stock
            if (request.getEnStock() != null && request.getEnStock()) {
                predicates.add(cb.greaterThan(root.get("stock"), 0));
            }

            // Filtro por vendedor
            if (request.getVendedorId() != null) {
                predicates.add(cb.equal(root.get("vendedor").get("id"), request.getVendedorId()));
            }

            // Filtro solo destacados
            if (request.getDestacados() != null && request.getDestacados()) {
                predicates.add(cb.isTrue(root.get("destacado")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Construir Pageable con ordenamiento
     */
    private Pageable construirPageable(SearchRequest request) {
        Sort sort = construirOrdenamiento(request.getOrdenar());
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    /**
     * Construir ordenamiento según parámetro
     */
    private Sort construirOrdenamiento(String ordenar) {
        if (ordenar == null)
            ordenar = "relevancia";

        return switch (ordenar.toLowerCase()) {
            case "precio_asc" -> Sort.by("precio").ascending();
            case "precio_desc" -> Sort.by("precio").descending();
            case "calificacion_desc" -> Sort.by("calificacionPromedio").descending();
            case "recientes" -> Sort.by("fechaCreacion").descending();
            case "nombre_asc" -> Sort.by("nombre").ascending();
            case "nombre_desc" -> Sort.by("nombre").descending();
            default -> Sort.by("destacado").descending()
                    .and(Sort.by("calificacionPromedio").descending());
        };
    }

    /**
     * Construir filtros aplicados para respuesta
     */
    private FiltrosAplicados construirFiltrosAplicados(SearchRequest request) {
        FiltrosAplicados filtros = new FiltrosAplicados();

        filtros.setTermino(request.getQ());
        filtros.setCategoriaId(request.getCategoriaId());

        if (request.getCategoriaId() != null) {
            categoriaRepository.findById(request.getCategoriaId())
                    .ifPresent(cat -> filtros.setCategoriaNombre(cat.getNombre()));
        }

        filtros.setPrecioMin(request.getPrecioMin());
        filtros.setPrecioMax(request.getPrecioMax());
        filtros.setCalificacionMin(request.getCalificacionMin());
        filtros.setEnStock(request.getEnStock());
        filtros.setOrdenamiento(request.getOrdenar());

        return filtros;
    }
}