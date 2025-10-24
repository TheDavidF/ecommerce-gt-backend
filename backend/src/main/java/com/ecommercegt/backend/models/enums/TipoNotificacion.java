package com.ecommercegt.backend.models.enums;

/**
 * Tipos de notificaciones del sistema
 */
public enum TipoNotificacion {
    /**
     * Pedido creado exitosamente
     */
    PEDIDO_CREADO,
    
    /**
     * Estado del pedido cambió (confirmado, enviado, entregado, cancelado)
     */
    PEDIDO_ESTADO_CAMBIADO,
    
    /**
     * Review fue aprobada por moderador
     */
    REVIEW_APROBADA,
    
    /**
     * Review fue rechazada por moderador
     */
    REVIEW_RECHAZADA,
    
    /**
     * Producto con stock bajo (para vendedores)
     */
    PRODUCTO_STOCK_BAJO,
    
    /**
     * Nueva venta realizada (para vendedores)
     */
    NUEVA_VENTA,
    
    /**
     * Producto aprobado (para vendedores)
     */
    PRODUCTO_APROBADO,
    
    /**
     * Producto rechazado (para vendedores)
     */
    PRODUCTO_RECHAZADO
}