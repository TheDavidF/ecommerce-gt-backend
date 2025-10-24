package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.CategoriaRequest;
import com.ecommercegt.backend.dto.response.CategoriaResponse;
import com.ecommercegt.backend.models.entidades.Categoria;
import com.ecommercegt.backend.repositorios.CategoriaRepository;
import com.ecommercegt.backend.repositorios.ProductoRepository;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;
    
    /**
     * Crear nueva categoría
     */
    @Transactional
    public CategoriaResponse crearCategoria(CategoriaRequest request) {
        // Validar que no exista categoría con el mismo nombre
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + request.getNombre());
        }
        
        // Crear entidad
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setImagenUrl(request.getImagenUrl());
        
        // Solo setear activo si existe el campo en la entidad
        if (request.getActivo() != null) {
            try {
                categoria.setActivo(request.getActivo());
            } catch (Exception e) {
                // Si no existe el campo, ignorar
            }
        }
        
        // Guardar
        Categoria guardada = categoriaRepository.save(categoria);
        
        return convertirAResponse(guardada);
    }
    
    /**
     * Listar todas las categorías
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar solo categorías activas (o todas si no hay campo activo)
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategoriasActivas() {
        // OPCIÓN 1: Todas las categorías (si no hay campo activo)
        return categoriaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
        
        // OPCIÓN 2: Solo categorías con productos (alternativa)
        // return categoriaRepository.findCategoriasConProductos().stream()
        //         .map(this::convertirAResponse)
        //         .collect(Collectors.toList());
    }
    
    /**
     * Obtener categoría por ID
     */
    @Transactional(readOnly = true)
    public CategoriaResponse obtenerCategoriaPorId(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        return convertirAResponse(categoria);
    }
    
    /**
     * Actualizar categoría
     */
    @Transactional
    public CategoriaResponse actualizarCategoria(Integer id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        
        // Validar nombre único (si cambió)
        if (!categoria.getNombre().equals(request.getNombre()) && 
            categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + request.getNombre());
        }
        
        // Actualizar campos
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setImagenUrl(request.getImagenUrl());
        
        // Solo actualizar activo si existe el campo
        if (request.getActivo() != null) {
            try {
                categoria.setActivo(request.getActivo());
            } catch (Exception e) {
                // Si no existe el campo, ignorar
            }
        }
        
        Categoria actualizada = categoriaRepository.save(categoria);
        return convertirAResponse(actualizada);
    }
    
    /**
     * Eliminar categoría (soft delete - desactivar si existe campo activo, sino eliminar)
     */
    @Transactional
    public void eliminarCategoria(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        
        // Intentar desactivar (soft delete)
        try {
            categoria.setActivo(false);
            categoriaRepository.save(categoria);
        } catch (Exception e) {
            // Si no existe campo activo, verificar si tiene productos
            Long cantidadProductos = productoRepository.countByCategoriaId(id);
            if (cantidadProductos > 0) {
                throw new RuntimeException("No se puede eliminar la categoría porque tiene " + 
                                         cantidadProductos + " productos asociados");
            }
            // Si no tiene productos, eliminar físicamente
            categoriaRepository.delete(categoria);
        }
    }
    
    /**
     * Buscar categorías por nombre
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> buscarCategorias(String query) {
        return categoriaRepository.findByNombreContainingIgnoreCase(query).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convertir entidad a DTO Response
     */
    private CategoriaResponse convertirAResponse(Categoria categoria) {
        CategoriaResponse response = new CategoriaResponse();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());
        response.setImagenUrl(categoria.getImagenUrl());
        
        // Solo setear activo si existe el campo
        try {
            response.setActivo(categoria.getActivo());
        } catch (Exception e) {
            response.setActivo(true);  // Default true si no existe
        }
        
        response.setFechaCreacion(categoria.getFechaCreacion());
        
        // Contar productos de la categoría
        try {
            Long count = productoRepository.countByCategoriaId(categoria.getId());
            response.setCantidadProductos(count != null ? count.intValue() : 0);
        } catch (Exception e) {
            response.setCantidadProductos(0);
        }
        
        return response;
    }
}