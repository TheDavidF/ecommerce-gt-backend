package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.response.ProductoModeracionResponse;
import com.ecommercegt.backend.models.enums.EstadoProducto;
import com.ecommercegt.backend.models.entidades.Producto;
import com.ecommercegt.backend.repositorios.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModeradorService {
    
    private final ProductoRepository productoRepository;
    
    public Page<ProductoModeracionResponse> listarProductosPendientes(Pageable pageable) {
        Page<Producto> productos = productoRepository.findByEstado(
            EstadoProducto.PENDIENTE_REVISION, 
            pageable
        );
        return productos.map(this::convertirAResponse);
    }
    
    public Page<ProductoModeracionResponse> listarProductos(String estado, Pageable pageable) {
        if (estado == null || estado.isEmpty()) {
            return productoRepository.findAll(pageable).map(this::convertirAResponse);
        }
        
        try {
            EstadoProducto estadoEnum = EstadoProducto.valueOf(estado.toUpperCase());
            return productoRepository.findByEstado(estadoEnum, pageable)
                    .map(this::convertirAResponse);
        } catch (IllegalArgumentException e) {
            return productoRepository.findAll(pageable).map(this::convertirAResponse);
        }
    }
    
    public ProductoModeracionResponse obtenerProductoPorId(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertirAResponse(producto);
    }
    
    @Transactional
    public ProductoModeracionResponse aprobarProducto(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (producto.getEstado() == EstadoProducto.APROBADO) {
            throw new RuntimeException("El producto ya está aprobado");
        }
        
        producto.setEstado(EstadoProducto.APROBADO);
        // Nota: motivoRechazo no existe en la entidad Producto actual
        producto.setFechaActualizacion(LocalDateTime.now());
        
        Producto productoActualizado = productoRepository.save(producto);
        
        return convertirAResponse(productoActualizado);
    }
    
    @Transactional
    public ProductoModeracionResponse rechazarProducto(UUID id, String motivo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (producto.getEstado() == EstadoProducto.RECHAZADO) {
            throw new RuntimeException("El producto ya está rechazado");
        }
        
        producto.setEstado(EstadoProducto.RECHAZADO);
        // Nota: motivoRechazo no existe en la entidad Producto actual
        // Podrías necesitar agregar este campo a la entidad o usar otra estrategia
        producto.setFechaActualizacion(LocalDateTime.now());
        
        Producto productoActualizado = productoRepository.save(producto);
        
        return convertirAResponse(productoActualizado);
    }
    
    private ProductoModeracionResponse convertirAResponse(Producto producto) {
        ProductoModeracionResponse response = new ProductoModeracionResponse();
        
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setStock(producto.getStock());
        
        // Obtener la primera imagen si existe
        if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
            response.setImagenUrl(producto.getImagenes().get(0).getUrlImagen());
        }
        
        response.setEstado(producto.getEstado());
        // motivoRechazo no existe en la entidad actual - se deja null por ahora
        response.setMotivoRechazo(null);
        response.setFechaCreacion(producto.getFechaCreacion());
        response.setFechaActualizacion(producto.getFechaActualizacion());
        
        // Categoría - Categoria tiene ID Integer, pero el DTO espera UUID
        if (producto.getCategoria() != null) {
            // Conversión de Integer a UUID - esto puede ser problemático
            // Opción 1: Cambiar el DTO para usar Integer
            // Opción 2: Cambiar Categoria para usar UUID
            // Por ahora comentamos esta línea
            // response.setCategoriaId(producto.getCategoria().getId());
            response.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        
        // Vendedor - Usar los campos que realmente existen en Usuario
        if (producto.getVendedor() != null) {
            response.setVendedorId(producto.getVendedor().getId());
            response.setVendedorNombre(producto.getVendedor().getNombreCompleto());
            response.setVendedorEmail(producto.getVendedor().getCorreo());
        }
        
        return response;
    }
}