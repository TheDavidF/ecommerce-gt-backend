package com.ecommercegt.backend.models.enums;

/**
 * Estados posibles de un pedido
 * 
 * Flujo normal:
 * PENDIENTE → CONFIRMADO → EN_PREPARACION → ENVIADO → ENTREGADO
 * 
 * Flujo alternativo:
 * PENDIENTE/CONFIRMADO → CANCELADO (devuelve stock)
 */
public enum EstadoPedido {
    
    /**
     * Pedido recién creado, esperando confirmación del vendedor
     * Puede ser cancelado por el cliente
     */
    PENDIENTE,
    
    /**
     * Pedido confirmado por el vendedor
     * Puede ser cancelado por el cliente
     */
    CONFIRMADO,
    
    /**
     * Vendedor está preparando el pedido
     * Ya NO puede ser cancelado
     */
    EN_PREPARACION,
    
    /**
     * Pedido enviado al cliente
     * Ya NO puede ser cancelado
     */
    ENVIADO,
    
    /**
     * Pedido entregado exitosamente
     * Estado final
     */
    ENTREGADO,
    
    /**
     * Pedido cancelado (por cliente o vendedor)
     * Stock devuelto a productos
     * Estado final
     */
    CANCELADO;
    
    /**
     * Verifica si el pedido puede ser cancelado
     * Solo PENDIENTE y CONFIRMADO pueden cancelarse
     */
    public boolean puedeSerCancelado() {
        return this == PENDIENTE || this == CONFIRMADO;
    }
    
    /**
     * Verifica si es un estado final (no puede cambiar)
     */
    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }
    
    /**
     * Obtiene el siguiente estado válido en el flujo normal
     */
    public EstadoPedido siguienteEstado() {
        return switch (this) {
            case PENDIENTE -> CONFIRMADO;
            case CONFIRMADO -> EN_PREPARACION;
            case EN_PREPARACION -> ENVIADO;
            case ENVIADO -> ENTREGADO;
            case ENTREGADO, CANCELADO -> this; // Estados finales no cambian
        };
    }
    
    /**
     * Verifica si la transición al nuevo estado es válida
     */
    public boolean puedeTransicionarA(EstadoPedido nuevoEstado) {
        // No se puede cambiar desde estados finales
        if (this.esFinal()) {
            return false;
        }
        
        // Transiciones válidas
        return switch (this) {
            case PENDIENTE -> nuevoEstado == CONFIRMADO || nuevoEstado == CANCELADO;
            case CONFIRMADO -> nuevoEstado == EN_PREPARACION || nuevoEstado == CANCELADO;
            case EN_PREPARACION -> nuevoEstado == ENVIADO;
            case ENVIADO -> nuevoEstado == ENTREGADO;
            default -> false;
        };
    }
}