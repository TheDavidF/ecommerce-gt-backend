package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.CategoriaRequest;
import com.ecommercegt.backend.dto.response.CategoriaResponse;
import com.ecommercegt.backend.models.entidades.Categoria;
import com.ecommercegt.backend.repositorios.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
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
        categoria.setActivo(request.getActivo() != null ? request.getActivo() : true);
        
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
     * Listar solo categorías activas
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategoriasActivas() {
        return categoriaRepository.findByActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
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
        if (request.getActivo() != null) {
            categoria.setActivo(request.getActivo());
        }
        
        Categoria actualizada = categoriaRepository.save(categoria);
        return convertirAResponse(actualizada);
    }
    
    /**
     * Eliminar categoría (soft delete - desactivar)
     */
    @Transactional
    public void eliminarCategoria(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        
        // Desactivar en lugar de eliminar
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
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
        response.setActivo(categoria.getActivo());
        response.setFechaCreacion(categoria.getFechaCreacion());
        response.setCantidadProductos(categoria.getProductos().size());
        return response;
    }
}