package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.response.NotificacionResponse;
import com.ecommercegt.backend.models.entidades.Notificacion;
import com.ecommercegt.backend.models.entidades.Usuario;
import com.ecommercegt.backend.models.enums.TipoNotificacion;
import com.ecommercegt.backend.repositorios.NotificacionRepository;
import com.ecommercegt.backend.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de Notificaciones
 * Gestiona la creación y lectura de notificaciones de usuarios
 */
@Service
public class NotificacionService {
    
    @Autowired
    private NotificacionRepository notificacionRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Crear notificación para un usuario
     */
    @Transactional
    public Notificacion crearNotificacion(
            UUID usuarioId,
            TipoNotificacion tipo,
            String titulo,
            String mensaje,
            String url) {
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Notificacion notificacion = new Notificacion(usuario, tipo, titulo, mensaje, url);
        
        return notificacionRepository.save(notificacion);
    }
    
    /**
     * Crear notificación con datos adicionales (JSON)
     */
    @Transactional
    public Notificacion crearNotificacionConDatos(
            UUID usuarioId,
            TipoNotificacion tipo,
            String titulo,
            String mensaje,
            String url,
            String datos) {
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Notificacion notificacion = new Notificacion(usuario, tipo, titulo, mensaje, url);
        notificacion.setDatos(datos);
        
        return notificacionRepository.save(notificacion);
    }
    
    /**
     * Obtener notificaciones de un usuario (paginadas)
     */
    @Transactional(readOnly = true)
    public Page<NotificacionResponse> obtenerMisNotificaciones(UUID usuarioId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioIdOrderByFechaCreacionDesc(usuarioId, pageable);
        
        return notificaciones.map(NotificacionResponse::fromNotificacion);
    }
    
    /**
     * Obtener notificaciones no leídas
     */
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerNoLeidas(UUID usuarioId) {
        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuarioId);
        
        return notificaciones.stream()
                .map(NotificacionResponse::fromNotificacion)
                .collect(Collectors.toList());
    }
    
    /**
     * Contar notificaciones no leídas
     */
    @Transactional(readOnly = true)
    public Long contarNoLeidas(UUID usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }
    
    /**
     * Obtener últimas 5 notificaciones (para badge/vista rápida)
     */
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerUltimas5(UUID usuarioId) {
        List<Notificacion> notificaciones = notificacionRepository
                .findTop5ByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
        
        return notificaciones.stream()
                .map(NotificacionResponse::fromNotificacion)
                .collect(Collectors.toList());
    }
    
    /**
     * Marcar notificación como leída
     */
    @Transactional
    public NotificacionResponse marcarComoLeida(Long notificacionId, UUID usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        // Verificar que la notificación pertenece al usuario
        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para marcar esta notificación");
        }
        
        if (!notificacion.getLeida()) {
            notificacion.setLeida(true);
            notificacion.setFechaLectura(LocalDateTime.now());
            notificacion = notificacionRepository.save(notificacion);
        }
        
        return NotificacionResponse.fromNotificacion(notificacion);
    }
    
    /**
     * Marcar todas las notificaciones como leídas
     */
    @Transactional
    public int marcarTodasComoLeidas(UUID usuarioId) {
        return notificacionRepository.marcarTodasComoLeidas(usuarioId);
    }
    
    /**
     * Eliminar notificación
     */
    @Transactional
    public void eliminarNotificacion(Long notificacionId, UUID usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        // Verificar que la notificación pertenece al usuario
        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta notificación");
        }
        
        notificacionRepository.delete(notificacion);
    }
    
    /**
     * Eliminar todas las notificaciones leídas del usuario
     */
    @Transactional
    public void eliminarLeidas(UUID usuarioId) {
        List<Notificacion> leidas = notificacionRepository
                .findByUsuarioIdOrderByFechaCreacionDesc(usuarioId, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(Notificacion::getLeida)
                .collect(Collectors.toList());
        
        notificacionRepository.deleteAll(leidas);
    }
    
    // ========================================
    // MÉTODOS DE CONVENIENCIA PARA EVENTOS
    // ========================================
    
    /**
     * Notificar pedido creado
     */
    @Transactional
    public void notificarPedidoCreado(UUID usuarioId, String numeroPedido, String total) {
        crearNotificacion(
            usuarioId,
            TipoNotificacion.PEDIDO_CREADO,
            "Pedido creado exitosamente",
            "Tu pedido #" + numeroPedido + " por " + total + " ha sido creado. " +
            "Te notificaremos cuando cambie de estado.",
            "/mis-pedidos/" + numeroPedido
        );
    }
    
    /**
     * Notificar cambio de estado de pedido
     */
    @Transactional
    public void notificarCambioEstadoPedido(
            UUID usuarioId,
            String numeroPedido,
            String nuevoEstado) {
        
        String mensaje = switch (nuevoEstado.toUpperCase()) {
            case "CONFIRMADO" -> "Tu pedido ha sido confirmado y está siendo preparado.";
            case "ENVIADO" -> "Tu pedido ha sido enviado y está en camino.";
            case "ENTREGADO" -> "Tu pedido ha sido entregado. ¡Esperamos que lo disfrutes!";
            case "CANCELADO" -> "Tu pedido ha sido cancelado.";
            default -> "El estado de tu pedido ha cambiado a: " + nuevoEstado;
        };
        
        crearNotificacion(
            usuarioId,
            TipoNotificacion.PEDIDO_ESTADO_CAMBIADO,
            "Estado de pedido actualizado",
            mensaje,
            "/mis-pedidos/" + numeroPedido
        );
    }
    
    /**
     * Notificar review aprobada
     */
    @Transactional
    public void notificarReviewAprobada(UUID usuarioId, Long reviewId, String productoNombre) {
        crearNotificacion(
            usuarioId,
            TipoNotificacion.REVIEW_APROBADA,
            "Tu reseña fue aprobada",
            "Tu reseña del producto \"" + productoNombre + "\" ha sido aprobada y ya es visible para otros usuarios.",
            "/reviews/" + reviewId
        );
    }
    
    /**
     * Notificar review rechazada
     */
    @Transactional
    public void notificarReviewRechazada(UUID usuarioId, Long reviewId, String productoNombre, String motivo) {
        crearNotificacion(
            usuarioId,
            TipoNotificacion.REVIEW_RECHAZADA,
            "Tu reseña fue rechazada",
            "Tu reseña del producto \"" + productoNombre + "\" no fue aprobada. " +
            (motivo != null ? "Motivo: " + motivo : ""),
            "/reviews/" + reviewId
        );
    }
    
    /**
     * Notificar nueva venta a vendedor
     */
    @Transactional
    public void notificarNuevaVenta(UUID vendedorId, String numeroPedido, String productoNombre, String cantidad) {
        crearNotificacion(
            vendedorId,
            TipoNotificacion.NUEVA_VENTA,
            "¡Nueva venta realizada!",
            "Se vendieron " + cantidad + " unidades de \"" + productoNombre + "\" en el pedido #" + numeroPedido,
            "/mis-ventas/" + numeroPedido
        );
    }
    
    /**
     * Notificar stock bajo a vendedor
     */
    @Transactional
    public void notificarStockBajo(UUID vendedorId, UUID productoId, String productoNombre, Integer stockActual) {
        crearNotificacion(
            vendedorId,
            TipoNotificacion.PRODUCTO_STOCK_BAJO,
            "Stock bajo: " + productoNombre,
            "Tu producto \"" + productoNombre + "\" tiene solo " + stockActual + 
            " unidades disponibles. Considera reabastecer pronto.",
            "/mis-productos/" + productoId
        );
    }
    
    /**
     * Notificar producto aprobado a vendedor
     */
    @Transactional
    public void notificarProductoAprobado(UUID vendedorId, UUID productoId, String productoNombre) {
        crearNotificacion(
            vendedorId,
            TipoNotificacion.PRODUCTO_APROBADO,
            "Producto aprobado",
            "Tu producto \"" + productoNombre + "\" ha sido aprobado y ya está visible en la tienda.",
            "/mis-productos/" + productoId
        );
    }
    
    /**
     * Notificar producto rechazado a vendedor
     */
    @Transactional
    public void notificarProductoRechazado(UUID vendedorId, UUID productoId, String productoNombre, String motivo) {
        crearNotificacion(
            vendedorId,
            TipoNotificacion.PRODUCTO_RECHAZADO,
            "Producto rechazado",
            "Tu producto \"" + productoNombre + "\" no fue aprobado. " +
            (motivo != null ? "Motivo: " + motivo : "Por favor revisa la información."),
            "/mis-productos/" + productoId
        );
    }
}