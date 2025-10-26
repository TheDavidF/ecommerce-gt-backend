package com.ecommercegt.backend.dto.response;

import com.ecommercegt.backend.models.enums.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO Response para un pedido completo
 * Incluye información del usuario, items y totales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    
    /**
     * ID del pedido
     */
    private UUID id;
    
    /**
     * Número de orden único
     * Formato: PED-YYYYMMDD-####
     */
    private String numeroOrden;
    
    /**
     * ID del usuario que realizó el pedido
     */
    private UUID usuarioId;
    
    /**
     * Nombre completo del usuario
     */
    private String usuarioNombre;
    
    /**
     * Email del usuario
     */
    private String usuarioEmail;
    
    /**
     * Items del pedido
     */
    private List<ItemPedidoResponse> items = new ArrayList<>();
    
    /**
     * Cantidad total de items (suma de cantidades)
     */
    private Integer cantidadTotalItems;
    
    /**
     * Total del pedido
     */
    private BigDecimal total;
    
    /**
     * Estado actual del pedido
     */
    private EstadoPedido estado;
    
    /**
     * Dirección de envío
     */
    private String direccionEnvio;
    
    /**
     * Teléfono de contacto
     */
    private String telefonoContacto;
    
    /**
     * Método de pago
     */
    private String metodoPago;
    
    /**
     * Notas del cliente
     */
    private String notas;
    
    /**
     * Fecha en que se creó el pedido
     */
    private LocalDateTime fechaPedido;
    
    /**
     * Última actualización
     */
    private LocalDateTime fechaActualizacion;
    
    /**
     * Fecha de cancelación (si aplica)
     */
    private LocalDateTime fechaCancelacion;
    
    /**
     * Motivo de cancelación (si aplica)
     */
    private String motivoCancelacion;
    
    /**
     * Fecha de entrega (si aplica)
     */
    private LocalDateTime fechaEntrega;

    private LocalDateTime fechaEntregaEstimada;
    
    /**
     * Indica si el pedido puede ser cancelado
     * (solo PENDIENTE y CONFIRMADO)
     */
    private Boolean puedeSerCancelado;
    
    /**
     * Indica si es un estado final (ENTREGADO o CANCELADO)
     */
    private Boolean esFinal;
}