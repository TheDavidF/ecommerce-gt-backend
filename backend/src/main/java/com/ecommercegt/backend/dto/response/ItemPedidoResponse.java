package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO Response para un item dentro de un pedido
 * Representa un producto con su cantidad y precio en el momento del pedido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoResponse {
    
    /**
     * ID del item
     */
    private Long id;
    
    /**
     * ID del producto (para consultas, no es FK)
     */
    private UUID productoId;
    
    /**
     * Nombre del producto (snapshot)
     */
    private String productoNombre;
    
    /**
     * URL de la imagen del producto (snapshot)
     */
    private String productoImagen;
    
    /**
     * Nombre del vendedor (snapshot)
     */
    private String vendedorNombre;
    
    /**
     * Cantidad comprada
     */
    private Integer cantidad;
    
    /**
     * Precio unitario en el momento del pedido
     */
    private BigDecimal precioUnitario;
    
    /**
     * Subtotal del item (cantidad × precio unitario)
     */
    private BigDecimal subtotal;
}