package com.ecommercegt.backend.service.ModeradorServiceImpl;

import com.ecommercegt.backend.dto.response.ProductoModeracionResponse;
import com.ecommercegt.backend.models.enums.EstadoProducto;
import com.ecommercegt.backend.models.entidades.Producto;
import com.ecommercegt.backend.repositorios.ProductoRepository;
import com.ecommercegt.backend.service.ModeradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModeradorServiceImpl implements ModeradorService {
    
    private final ProductoRepository productoRepository;
    
    @Override
    public Page<ProductoModeracionResponse> listarProductosPendientes(Pageable pageable) {
        Page<Producto> productos = productoRepository.findByEstado(
            EstadoProducto.PENDIENTE_REVISION, 
            pageable
        );
        return productos.map(this::convertirAResponse);
    }
    
    @Override
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
    
    @Override
    public ProductoModeracionResponse obtenerProductoPorId(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertirAResponse(producto);
    }
    
    @Override
    @Transactional
    public ProductoModeracionResponse aprobarProducto(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (producto.getEstado() == EstadoProducto.APROBADO) {
            throw new RuntimeException("El producto ya está aprobado");
        }
        
        producto.setEstado(EstadoProducto.APROBADO);
        producto.setMotivoRechazo(null);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        Producto productoActualizado = productoRepository.save(producto);
        
        // TODO: Enviar notificación al vendedor
        
        return convertirAResponse(productoActualizado);
    }
    
    @Override
    @Transactional
    public ProductoModeracionResponse rechazarProducto(UUID id, String motivo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (producto.getEstado() == EstadoProducto.RECHAZADO) {
            throw new RuntimeException("El producto ya está rechazado");
        }
        
        producto.setEstado(EstadoProducto.RECHAZADO);
        producto.setMotivoRechazo(motivo);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        Producto productoActualizado = productoRepository.save(producto);
        
        // TODO: Enviar notificación al vendedor
        
        return convertirAResponse(productoActualizado);
    }
    
    private ProductoModeracionResponse convertirAResponse(Producto producto) {
        ProductoModeracionResponse response = new ProductoModeracionResponse();
        
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setStock(producto.getStock());
        response.setImagenUrl(producto.getImagenUrl());
        response.setEstado(producto.getEstado());
        response.setMotivoRechazo(producto.getMotivoRechazo());
        response.setFechaCreacion(producto.getFechaCreacion());
        response.setFechaActualizacion(producto.getFechaActualizacion());
        
        // Categoría
        if (producto.getCategoria() != null) {
            response.setCategoriaId(producto.getCategoria().getId());
            response.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        
        // Vendedor
        if (producto.getVendedor() != null) {
            response.setVendedorId(producto.getVendedor().getId());
            response.setVendedorNombre(
                producto.getVendedor().getNombre() + " " + 
                producto.getVendedor().getApellido()
            );
            response.setVendedorEmail(producto.getVendedor().getEmail());
        }
        
        return response;
    }
}