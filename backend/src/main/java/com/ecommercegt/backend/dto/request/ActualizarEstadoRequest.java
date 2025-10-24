package com.ecommercegt.backend.dto.request;

import com.ecommercegt.backend.models.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Request para actualizar el estado de un pedido
 * 
 * Solo usuarios con permisos (VENDEDOR, MODERADOR, ADMIN) pueden actualizar estados.
 * 
 * Transiciones válidas:
 * PENDIENTE → CONFIRMADO
 * CONFIRMADO → EN_PREPARACION
 * EN_PREPARACION → ENVIADO
 * ENVIADO → ENTREGADO
 * 
 * O cancelar desde PENDIENTE/CONFIRMADO → CANCELADO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoRequest {
    
    /**
     * Nuevo estado del pedido
     */
    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoPedido nuevoEstado;
    
    /**
     * Notas sobre el cambio de estado (opcional)
     * Ejemplo: "Paquete enviado con guía #12345"
     */
    private String notas;
}