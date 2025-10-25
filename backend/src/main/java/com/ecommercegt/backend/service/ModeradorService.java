package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.response.ProductoModeracionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ModeradorService {
    
    // Listar productos pendientes de revisión
    Page<ProductoModeracionResponse> listarProductosPendientes(Pageable pageable);
    
    // Listar todos los productos (con filtro por estado)
    Page<ProductoModeracionResponse> listarProductos(String estado, Pageable pageable);
    
    // Obtener detalle de un producto para moderación
    ProductoModeracionResponse obtenerProductoPorId(UUID id);
    
    // Aprobar producto
    ProductoModeracionResponse aprobarProducto(UUID id);
    
    // Rechazar producto
    ProductoModeracionResponse rechazarProducto(UUID id, String motivo);
}