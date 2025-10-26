package com.ecommercegt.backend.models.entidades;

import com.ecommercegt.backend.models.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad Pedido - Representa una orden de compra confirmada
 * 
 * Relaciones:
 * - N:1 con Usuario (un usuario puede tener muchos pedidos)
 * - 1:N con ItemPedido (un pedido tiene muchos items)
 * 
 * Ciclo de vida:
 * 1. Usuario agrega productos al carrito
 * 2. Usuario hace checkout → se crea Pedido
 * 3. Se hace snapshot de datos (precios, nombres)
 * 4. Se reduce stock de productos
 * 5. Se limpia el carrito
 * 6. Vendedor actualiza estados
 */
@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Usuario que realizó el pedido
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Items del pedido (productos con sus cantidades)
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();

    /**
     * Número de orden único (formato: PED-YYYYMMDD-####)
     * Ejemplo: PED-20251024-0001
     */
    @Column(name = "numero_orden", unique = true, nullable = false, length = 50)
    private String numeroOrden;

    /**
     * Total del pedido (suma de subtotales de items)
     * ✅ CORRECCIÓN: Campo montoTotal mapea a columna monto_total
     */
    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    /**
     * Estado actual del pedido
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPedido estado;

    /**
     * Dirección de envío completa
     */
    @Column(name = "direccion_envio", nullable = false, length = 500)
    private String direccionEnvio;

    /**
     * Teléfono de contacto para la entrega
     */
    @Column(name = "telefono_contacto", nullable = false, length = 20)
    private String telefonoContacto;

    /**
     * Método de pago elegido
     * Ejemplos: EFECTIVO, TRANSFERENCIA, TARJETA, WALLET
     */
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    /**
     * Notas adicionales del cliente
     */
    @Column(name = "notas", length = 1000)
    private String notas;

    /**
     * Fecha en que se creó el pedido
     */
    @CreationTimestamp
    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    private LocalDateTime fechaPedido;

    /**
     * Última actualización del pedido (cambio de estado)
     */
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    /**
     * Fecha de cancelación (si aplica)
     */
    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    /**
     * Motivo de cancelación (si aplica)
     */
    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    /**
     * Fecha de entrega (cuando se marca como ENTREGADO)
     */
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Agregar item al pedido
     */
    public void agregarItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
    }

    /**
     * Calcular total del pedido (suma de subtotales)
     * ✅ ACTUALIZADO: Usar montoTotal
     */
    public void calcularTotal() {
        this.montoTotal = items.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtener cantidad total de items
     */
    public int getCantidadTotalItems() {
        return items.stream()
                .mapToInt(ItemPedido::getCantidad)
                .sum();
    }

    /**
     * Verificar si el pedido puede ser cancelado
     */
    public boolean puedeSerCancelado() {
        return estado.puedeSerCancelado();
    }

    /**
     * Cancelar pedido
     */
    public void cancelar(String motivo) {
        if (!puedeSerCancelado()) {
            throw new RuntimeException("El pedido no puede ser cancelado en su estado actual: " + estado);
        }
        this.estado = EstadoPedido.CANCELADO;
        this.fechaCancelacion = LocalDateTime.now();
        this.motivoCancelacion = motivo;
    }

    /**
     * Marcar como entregado
     */
    public void marcarComoEntregado() {
        this.estado = EstadoPedido.ENTREGADO;
        this.fechaEntrega = LocalDateTime.now();
    }

    /**
     * Cambiar estado del pedido (con validación)
     */
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (!this.estado.puedeTransicionarA(nuevoEstado)) {
            throw new RuntimeException(
                    String.format("No se puede cambiar de %s a %s", this.estado, nuevoEstado));
        }
        this.estado = nuevoEstado;

        if (nuevoEstado == EstadoPedido.ENTREGADO) {
            this.fechaEntrega = LocalDateTime.now();
        }
    }

    // ==================== MÉTODOS DE COMPATIBILIDAD ====================

    /**
     * Alias para getMontoTotal() para compatibilidad
     * 
     * @deprecated Usar getMontoTotal() en su lugar
     */
    @Deprecated
    public BigDecimal getTotal() {
        return this.montoTotal;
    }

    /**
     * Alias para setMontoTotal() para compatibilidad
     * 
     * @deprecated Usar setMontoTotal() en su lugar
     */
    @Deprecated
    public void setTotal(BigDecimal total) {
        this.montoTotal = total;
    }
}