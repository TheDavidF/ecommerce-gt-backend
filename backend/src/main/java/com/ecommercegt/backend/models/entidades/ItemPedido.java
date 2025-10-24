package com.ecommercegt.backend.models.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidad ItemPedido - Representa un producto dentro de un pedido
 * 
 * IMPORTANTE: Esta entidad NO tiene relación directa con Producto
 * porque guarda un SNAPSHOT de los datos en el momento del pedido.
 * 
 * ¿Por qué snapshot?
 * - Los precios de productos pueden cambiar después del pedido
 * - Los productos pueden ser eliminados después del pedido
 * - Los nombres de productos pueden cambiar
 * 
 * El pedido debe mantener los datos históricos tal como eran
 * en el momento de la compra.
 * 
 * Relaciones:
 * - N:1 con Pedido (muchos items pertenecen a un pedido)
 */
@Entity
@Table(name = "items_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Pedido al que pertenece este item
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    /**
     * ID del producto (referencia para consultas, pero no es FK)
     * Esto permite que el producto pueda ser eliminado sin afectar el pedido
     */
    @Column(name = "producto_id", nullable = false)
    private UUID productoId;
    
    /**
     * Nombre del producto (SNAPSHOT en el momento del pedido)
     */
    @Column(name = "producto_nombre", nullable = false, length = 200)
    private String productoNombre;
    
    /**
     * URL de la imagen principal del producto (SNAPSHOT)
     */
    @Column(name = "producto_imagen", length = 500)
    private String productoImagen;
    
    /**
     * Nombre del vendedor (SNAPSHOT)
     */
    @Column(name = "vendedor_nombre", length = 200)
    private String vendedorNombre;
    
    /**
     * Cantidad de unidades compradas
     */
    @Column(nullable = false)
    private Integer cantidad;
    
    /**
     * Precio unitario en el momento del pedido (SNAPSHOT)
     * Este precio puede ser diferente al precio actual del producto
     */
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    /**
     * Subtotal del item (cantidad × precio unitario)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    // ==================== MÉTODOS DE UTILIDAD ====================
    
    /**
     * Calcular subtotal (cantidad × precio unitario)
     */
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
    
    /**
     * Constructor de conveniencia para crear desde producto y cantidad
     */
    /**
 * Constructor de conveniencia para crear desde producto y cantidad
 */
    public ItemPedido(Producto producto, Integer cantidad) {
        this.productoId = producto.getId();
        this.productoNombre = producto.getNombre();
        
        // Obtener primera imagen si existe
        if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
            this.productoImagen = producto.getImagenes().get(0).getUrlImagen();
        } else {
            this.productoImagen = null;
        }
        
        this.vendedorNombre = producto.getVendedor().getNombreUsuario();
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioFinal();
        calcularSubtotal();
    }
}