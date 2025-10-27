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
        Page<SolicitudModeracion> solicitudes = solicitudRepository.findByEstado(
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
     * Obtener detalle de una solicitud por su propio ID
     */
    @Transactional(readOnly = true)
    public ProductoModeracionResponse obtenerSolicitudPorId(UUID solicitudId) {
        SolicitudModeracion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
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
        
        if (solicitud.getEstado() == EstadoSolicitudModeracion.APROBADO) {
            throw new RuntimeException("La solicitud ya está aprobada");
        }
        
        // Obtener el moderador actual
        Usuario moderador = obtenerUsuarioActual();
        
        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitudModeracion.APROBADO);
        solicitud.setModerador(moderador);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setComentarioModerador(null);
        
        // Actualizar producto
        Producto producto = solicitud.getProducto();
        producto.setEstado(EstadoProducto.APROBADO);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        // Guardar cambios
        solicitudRepository.save(solicitud);
        productoRepository.save(producto);
        
        return convertirSolicitudAResponse(solicitud);
    }
    
    /**
     * Rechazar solicitud de moderación
     */
    @Transactional
    public ProductoModeracionResponse rechazarSolicitud(UUID productoId, String motivo) {
        // Validar motivo
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new RuntimeException("Debe proporcionar un motivo para rechazar el producto");
        }
        
        // Buscar la solicitud
        SolicitudModeracion solicitud = solicitudRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        if (solicitud.getEstado() == EstadoSolicitudModeracion.RECHAZADO) {
            throw new RuntimeException("La solicitud ya está rechazada");
        }
        
        // Obtener el moderador actual
        Usuario moderador = obtenerUsuarioActual();
        
        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitudModeracion.RECHAZADO);
        solicitud.setModerador(moderador);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setComentarioModerador(motivo);
        
        // Actualizar producto
        Producto producto = solicitud.getProducto();
        producto.setEstado(EstadoProducto.RECHAZADO);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        // Guardar cambios
        solicitudRepository.save(solicitud);
        productoRepository.save(producto);
        
        return convertirSolicitudAResponse(solicitud);
    }
    
    /**
     * Contar solicitudes por estado
     */
    public Long contarSolicitudesPorEstado(EstadoSolicitudModeracion estado) {
        try {
            return solicitudRepository.countByEstado(estado);
        } catch (Exception e) {
            return 0L;
        }
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
        
        // ==================== DATOS DE LA SOLICITUD ====================
        response.setId(solicitud.getId());
        response.setEstado(solicitud.getEstado().name());
        response.setFechaSolicitud(solicitud.getFechaSolicitud());
        response.setFechaRevision(solicitud.getFechaRevision());
        response.setComentarioModerador(solicitud.getComentarioModerador());
        
        // ==================== DATOS DEL PRODUCTO ====================
        response.setProductoId(producto.getId());
        
        // Obtener imagen principal
        String imagenUrl = null;
        if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
            imagenUrl = producto.getImagenes().stream()
                    .filter(img -> img.getEsPrincipal())
                    .findFirst()
                    .map(img -> img.getUrlImagen())
                    .orElse(producto.getImagenes().get(0).getUrlImagen());
        }
        
        // Obtener nombre de categoría
        String categoriaNombre = producto.getCategoria() != null 
                ? producto.getCategoria().getNombre() 
                : null;
        
        // Obtener estado del producto como String
        String estadoProducto = producto.getEstado() != null 
                ? producto.getEstado().name() 
                : null;
        
        // Crear objeto ProductoInfo con TODOS los parámetros
        ProductoModeracionResponse.ProductoInfo productoInfo = 
                new ProductoModeracionResponse.ProductoInfo(
                    producto.getId(),           // UUID id
                    producto.getNombre(),       // String nombre
                    producto.getDescripcion(),  // String descripcion
                    producto.getPrecio(),       // BigDecimal precio
                    producto.getStock(),        // Integer stock
                    imagenUrl,                  // String imagenUrl
                    categoriaNombre,            // String categoriaNombre
                    estadoProducto              // String estado
                );
        
        response.setProducto(productoInfo);
        
        // ==================== DATOS DEL SOLICITANTE ====================
        if (solicitud.getSolicitante() != null) {
            response.setSolicitanteId(solicitud.getSolicitante().getId());
            response.setSolicitanteNombre(solicitud.getSolicitante().getNombreCompleto());
        }
        
        // ==================== DATOS DEL MODERADOR ====================
        if (solicitud.getModerador() != null) {
            response.setModeradorId(solicitud.getModerador().getId());
            response.setModeradorNombre(solicitud.getModerador().getNombreCompleto());
        }
        
        return response;
    }

        /**
     * Aprobar solicitud por ID de solicitud (no de producto)
     */
    @Transactional
    public ProductoModeracionResponse aprobarSolicitudPorId(UUID solicitudId) {
        // Buscar la solicitud por ID
        SolicitudModeracion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        if (solicitud.getEstado() == EstadoSolicitudModeracion.APROBADO) {
            throw new RuntimeException("La solicitud ya está aprobada");
        }
        
        // Obtener el moderador actual
        Usuario moderador = obtenerUsuarioActual();
        
        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitudModeracion.APROBADO);
        solicitud.setModerador(moderador);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setComentarioModerador(null);
        
        // Actualizar producto
        Producto producto = solicitud.getProducto();
        producto.setEstado(EstadoProducto.APROBADO);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        // Guardar cambios
        solicitudRepository.save(solicitud);
        productoRepository.save(producto);
        
        return convertirSolicitudAResponse(solicitud);
    }
    
    /**
     * Rechazar solicitud por ID de solicitud (no de producto)
     */
    @Transactional
    public ProductoModeracionResponse rechazarSolicitudPorId(UUID solicitudId, String motivo) {
        // Validar motivo
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new RuntimeException("Debe proporcionar un motivo para rechazar el producto");
        }
        
        // Buscar la solicitud por ID
        SolicitudModeracion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        if (solicitud.getEstado() == EstadoSolicitudModeracion.RECHAZADO) {
            throw new RuntimeException("La solicitud ya está rechazada");
        }
        
        // Obtener el moderador actual
        Usuario moderador = obtenerUsuarioActual();
        
        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitudModeracion.RECHAZADO);
        solicitud.setModerador(moderador);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setComentarioModerador(motivo);
        
        // Actualizar producto
        Producto producto = solicitud.getProducto();
        producto.setEstado(EstadoProducto.RECHAZADO);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        // Guardar cambios
        solicitudRepository.save(solicitud);
        productoRepository.save(producto);
        
        return convertirSolicitudAResponse(solicitud);
    }
    
    /**
     * Solicitar cambios por ID de solicitud (no de producto)
     */
    @Transactional
    public ProductoModeracionResponse solicitarCambiosPorId(UUID solicitudId, String comentario) {
        // Validar comentario
        if (comentario == null || comentario.trim().isEmpty()) {
            throw new RuntimeException("Debe proporcionar un comentario");
        }
        
        // Buscar la solicitud por ID
        SolicitudModeracion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        // Obtener el moderador actual
        Usuario moderador = obtenerUsuarioActual();
        
        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitudModeracion.CAMBIOS_SOLICITADOS);
        solicitud.setModerador(moderador);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setComentarioModerador(comentario);
        
        // Actualizar producto (mantener en PENDIENTE_REVISION)
        Producto producto = solicitud.getProducto();
        producto.setEstado(EstadoProducto.PENDIENTE_REVISION);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        // Guardar cambios
        solicitudRepository.save(solicitud);
        productoRepository.save(producto);
        
        return convertirSolicitudAResponse(solicitud);
    }
}