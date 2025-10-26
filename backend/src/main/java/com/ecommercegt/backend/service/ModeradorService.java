package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.response.ProductoModeracionResponse;
import com.ecommercegt.backend.models.entidades.Producto;
import com.ecommercegt.backend.models.entidades.SolicitudModeracion;
import com.ecommercegt.backend.models.entidades.Usuario;
import com.ecommercegt.backend.models.enums.EstadoProducto;
import com.ecommercegt.backend.models.enums.EstadoSolicitudModeracion;
import com.ecommercegt.backend.repositorios.ProductoRepository;
import com.ecommercegt.backend.repositorios.SolicitudModeracionRepository;
import com.ecommercegt.backend.repositorios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModeradorService {
    
    private final ProductoRepository productoRepository;
    private final SolicitudModeracionRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Listar solicitudes pendientes de moderación
     */
    @Transactional(readOnly = true)
    public Page<ProductoModeracionResponse> listarSolicitudesPendientes(Pageable pageable) {
        Page<SolicitudModeracion> solicitudes = solicitudRepository.findByEstadoWithDetails(
            EstadoSolicitudModeracion.PENDIENTE, 
            pageable
        );
        return solicitudes.map(this::convertirSolicitudAResponse);
    }
    
    /**
     * Listar todas las solicitudes con filtro por estado
     */
    @Transactional(readOnly = true)
    public Page<ProductoModeracionResponse> listarSolicitudes(String estado, Pageable pageable) {
        if (estado == null || estado.isEmpty()) {
            return solicitudRepository.findAll(pageable)
                    .map(this::convertirSolicitudAResponse);
        }
        
        try {
            EstadoSolicitudModeracion estadoEnum = EstadoSolicitudModeracion.valueOf(estado.toUpperCase());
            return solicitudRepository.findByEstado(estadoEnum, pageable)
                    .map(this::convertirSolicitudAResponse);
        } catch (IllegalArgumentException e) {
            return solicitudRepository.findAll(pageable)
                    .map(this::convertirSolicitudAResponse);
        }
    }
    
    /**
     * Obtener detalle de una solicitud por ID del producto
     */
    @Transactional(readOnly = true)
    public ProductoModeracionResponse obtenerSolicitudPorProductoId(UUID productoId) {
        SolicitudModeracion solicitud = solicitudRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada para el producto"));
        return convertirSolicitudAResponse(solicitud);
    }
    
    /**
     * Aprobar solicitud de moderación
     */
    @Transactional
    public ProductoModeracionResponse aprobarSolicitud(UUID productoId) {
        // Buscar la solicitud
        SolicitudModeracion solicitud = solicitudRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        if (solicitud.getEstado() == EstadoSolicitudModeracion.APROBADA) {
            throw new RuntimeException("La solicitud ya está aprobada");
        }
        
        // Obtener el moderador actual
        Usuario moderador = obtenerUsuarioActual();
        
        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitudModeracion.APROBADA);
        solicitud.setModerador(moderador);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setRazon(null); // Limpiar razón de rechazo si había
        
        // Actualizar producto
        Producto producto = solicitud.getProducto();
        producto.setEstado(EstadoProducto.APROBADO);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        // Guardar cambios
        solicitudRepository.save(solicitud);
        productoRepository.save(producto);
        
        // TODO: Enviar notificación al vendedor
        
        return convertirSolicitudAResponse(solicitud);
    }
    
    /**
     * Rechazar solicitud de moderación
     */
    @Transactional
    public ProductoModeracionResponse rechazarSolicitud(UUID productoId, String razon) {
        // Validar razón
        if (razon == null || razon.trim().isEmpty()) {
            throw new RuntimeException("Debe proporcionar una razón para rechazar el producto");
        }
        
        // Buscar la solicitud
        SolicitudModeracion solicitud = solicitudRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        if (solicitud.getEstado() == EstadoSolicitudModeracion.RECHAZADA) {
            throw new RuntimeException("La solicitud ya está rechazada");
        }
        
        // Obtener el moderador actual
        Usuario moderador = obtenerUsuarioActual();
        
        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitudModeracion.RECHAZADA);
        solicitud.setModerador(moderador);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setRazon(razon);
        
        // Actualizar producto
        Producto producto = solicitud.getProducto();
        producto.setEstado(EstadoProducto.RECHAZADO);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        // Guardar cambios
        solicitudRepository.save(solicitud);
        productoRepository.save(producto);
        
        // TODO: Enviar notificación al vendedor
        
        return convertirSolicitudAResponse(solicitud);
    }
    
    /**
     * Obtener usuario actual del contexto de seguridad
     */
    private Usuario obtenerUsuarioActual() {
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    /**
     * Convertir SolicitudModeracion a ProductoModeracionResponse
     */
    private ProductoModeracionResponse convertirSolicitudAResponse(SolicitudModeracion solicitud) {
        Producto producto = solicitud.getProducto();
        ProductoModeracionResponse response = new ProductoModeracionResponse();
        
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setStock(producto.getStock());
        
        // Imagen principal
        if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
            producto.getImagenes().stream()
                    .filter(img -> img.getEsPrincipal())
                    .findFirst()
                    .ifPresent(img -> response.setImagenUrl(img.getUrlImagen()));
            
            // Si no hay principal, tomar la primera
            if (response.getImagenUrl() == null) {
                response.setImagenUrl(producto.getImagenes().get(0).getUrlImagen());
            }
        }
        
        response.setEstado(producto.getEstado());
        response.setMotivoRechazo(solicitud.getRazon());
        response.setFechaCreacion(producto.getFechaCreacion());
        response.setFechaActualizacion(producto.getFechaActualizacion());
        
        // Categoría
        if (producto.getCategoria() != null) {
            response.setCategoriaId(producto.getCategoria().getId());
            response.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        
        // Vendedor (solicitante)
        if (solicitud.getSolicitante() != null) {
            response.setVendedorId(solicitud.getSolicitante().getId());
            response.setVendedorNombre(solicitud.getSolicitante().getNombreCompleto());
            response.setVendedorEmail(solicitud.getSolicitante().getCorreo());
        }
        
        return response;
    }
}