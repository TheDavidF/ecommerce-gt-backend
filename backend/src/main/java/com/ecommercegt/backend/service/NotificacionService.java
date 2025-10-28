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
import org.springframework.data.domain.Sort;
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
    private CorreoService correoService;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Notificar usuario sancionado
     */
    @Transactional
    public void notificarUsuarioSancionado(UUID usuarioId, String razon) {
        crearNotificacion(
            usuarioId,
            TipoNotificacion.USUARIO_SANCIONADO,
            "Has sido sancionado",
            "Has recibido una sanción. Motivo: " + razon,
            "/mis-sanciones"
        );
    }

    /**
     * Notificar sanción desactivada
     */
    @Transactional
    public void notificarSancionDesactivada(UUID usuarioId, String razon) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(TipoNotificacion.SANCION_DESACTIVADA);
        notificacion.setTitulo("Sanción desactivada");
        notificacion.setMensaje("Tu sanción ha sido desactivada. Motivo original: " + razon);
        notificacion.setUrl("/mis-sanciones");
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());

        notificacionRepository.save(notificacion);
    }

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
        notificacion = notificacionRepository.save(notificacion);
        // Enviar correo solo para eventos relevantes
        if (tipo == TipoNotificacion.PEDIDO_ESTADO_CAMBIADO || tipo == TipoNotificacion.PEDIDO_CREADO
            || tipo == TipoNotificacion.REVIEW_APROBADA || tipo == TipoNotificacion.REVIEW_RECHAZADA
            || tipo == TipoNotificacion.USUARIO_SANCIONADO) {
            correoService.enviarCorreo(
                usuario.getCorreo(),
                titulo,
                mensaje + "\n\nPuedes ver el detalle en: " + url
            );
        }
        return notificacion;
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
        notificacion = notificacionRepository.save(notificacion);
        return notificacion;
    }

    /**
     * Obtener notificaciones de un usuario (paginadas)
     */
    @Transactional(readOnly = true)
    public Page<NotificacionResponse> obtenerNotificaciones(UUID usuarioId, Pageable pageable) {
        Page<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioIdOrderByFechaCreacionDesc(usuarioId, pageable);

        return notificaciones.map(NotificacionResponse::fromNotificacion);
    }

    /**
     * Obtener todas las notificaciones del sistema (para admin) - paginadas
     */
    @Transactional(readOnly = true)
    public Page<NotificacionResponse> obtenerTodasLasNotificaciones(Pageable pageable) {
        Page<Notificacion> notificaciones = notificacionRepository.findAll(pageable);
        return notificaciones.map(NotificacionResponse::fromNotificacion);
    }

    /**
     * Compatibilidad: obtenerMisNotificaciones(usuarioId, page, size)
     */
    @Transactional(readOnly = true)
    public Page<NotificacionResponse> obtenerMisNotificaciones(UUID usuarioId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        return obtenerNotificaciones(usuarioId, pageable);
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
     * Marcar notificación como leída por id (Long) y usuarioId
     */
    @Transactional
    public NotificacionResponse marcarComoLeida(Long notificacionId, UUID usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para marcar esta notificación");
        }
        notificacion.setLeida(true);
        notificacion.setFechaLectura(LocalDateTime.now());
        notificacion = notificacionRepository.save(notificacion);
        return NotificacionResponse.fromNotificacion(notificacion);
    }

    /**
     * Contar notificaciones no leídas
     */
    @Transactional(readOnly = true)
    public Long contarNoLeidas(UUID usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    /**
     * Notificar cambio de estado de pedido (firma compatible con controladores)
     */
    @Transactional
    public void notificarCambioEstadoPedido(UUID usuarioId, String numeroOrden, String nuevoEstado) {
        crearNotificacion(
            usuarioId,
            TipoNotificacion.PEDIDO_ESTADO_CAMBIADO,
            "Estado de pedido actualizado",
            "El estado de tu pedido #" + numeroOrden + " ha cambiado a: " + nuevoEstado,
            "/mis-pedidos/" + numeroOrden
        );
    }

    /**
     * Notificar pedido creado (firma compatible)
     */
    @Transactional
    public void notificarPedidoCreado(UUID usuarioId, String numeroOrden, String total) {
        crearNotificacion(
            usuarioId,
            TipoNotificacion.PEDIDO_CREADO,
            "Pedido creado exitosamente",
            "Tu pedido #" + numeroOrden + " ha sido creado y está siendo procesado. Total: " + total,
            "/mis-pedidos/" + numeroOrden
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
            "Review aprobada",
            "Tu review del producto '" + productoNombre + "' ha sido aprobada y es visible.",
            "/mis-reviews/" + reviewId
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
            "Review rechazada",
            "Tu review del producto '" + productoNombre + "' no fue aprobada. " +
            (motivo != null ? "Motivo: " + motivo : "Por favor revisa el contenido."),
            "/mis-reviews/" + reviewId
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

    /**
     * Notificar producto con stock bajo
     */
    @Transactional
    public void notificarProductoStockBajo(UUID vendedorId, UUID productoId, String productoNombre, int stockActual) {
        crearNotificacion(
            vendedorId,
            TipoNotificacion.PRODUCTO_STOCK_BAJO,
            "Producto con stock bajo",
            "El producto \"" + productoNombre + "\" tiene solo " + stockActual + " unidades en stock.",
            "/mis-productos/" + productoId
        );
    }

    // Métodos utilitarios agregados para compatibilidad con el controlador

    /**
     * Obtener las últimas 5 notificaciones de un usuario
     */
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerUltimas5(UUID usuarioId) {
        List<Notificacion> notificaciones = notificacionRepository.findTop5ByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
        return notificaciones.stream().map(NotificacionResponse::fromNotificacion).collect(Collectors.toList());
    }

    /**
     * Marcar todas las notificaciones como leídas para un usuario
     */
    @Transactional
    public int marcarTodasComoLeidas(UUID usuarioId) {
        return notificacionRepository.marcarTodasComoLeidas(usuarioId);
    }

    /**
     * Eliminar una notificación por id (Long) y usuarioId
     */
    @Transactional
    public void eliminarNotificacion(Long id, UUID usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta notificación");
        }
        notificacionRepository.deleteById(id);
    }

    /**
     * Eliminar todas las notificaciones leídas de un usuario
     */
    @Transactional
    public void eliminarLeidas(UUID usuarioId) {
        List<Notificacion> leidas = notificacionRepository.findByUsuarioIdAndLeidaTrue(usuarioId);
        notificacionRepository.deleteAll(leidas);
    }

    /**
     * Notificar nueva venta a vendedor
     */
    @Transactional
    public void notificarNuevaVenta(UUID vendedorId, String numeroOrden, String productoNombre, String cantidad) {
        crearNotificacion(
            vendedorId,
            TipoNotificacion.PEDIDO_CREADO,
            "Nueva venta realizada",
            "Se ha vendido " + cantidad + " unidad(es) del producto '" + productoNombre + "' en el pedido #" + numeroOrden,
            "/mis-ventas/" + numeroOrden
        );
    }
}
