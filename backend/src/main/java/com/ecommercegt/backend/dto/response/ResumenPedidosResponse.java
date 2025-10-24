package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO Response para resumen de pedidos
 * Útil para dashboards y estadísticas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPedidosResponse {
    
    /**
     * Total de pedidos pendientes
     */
    private Long pedidosPendientes;
    
    /**
     * Total de pedidos confirmados
     */
    private Long pedidosConfirmados;
    
    /**
     * Total de pedidos en preparación
     */
    private Long pedidosEnPreparacion;
    
    /**
     * Total de pedidos enviados
     */
    private Long pedidosEnviados;
    
    /**
     * Total de pedidos entregados
     */
    private Long pedidosEntregados;
    
    /**
     * Total de pedidos cancelados
     */
    private Long pedidosCancelados;
    
    /**
     * Total de todas las compras realizadas
     */
    private BigDecimal totalCompras;
    
    /**
     * Total de pedidos realizados
     */
    private Long totalPedidos;
}