package com.ecommercegt.backend.models.entidades;

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

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Relación: Un carrito pertenece a un usuario (1:1)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    
    // Relación: Un carrito tiene muchos items (1:N)
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // ========================================
    // MÉTODOS HELPER
    // ========================================
    
    /**
     * Agregar item al carrito
     */
    public void agregarItem(ItemCarrito item) {
        items.add(item);
        item.setCarrito(this);
    }
    
    /**
     * Eliminar item del carrito
     */
    public void eliminarItem(ItemCarrito item) {
        items.remove(item);
        item.setCarrito(null);
    }
    
    /**
     * Calcular total del carrito
     */
    public BigDecimal calcularTotal() {
        return items.stream()
                .map(ItemCarrito::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Obtener cantidad total de items
     */
    public int getCantidadTotalItems() {
        return items.stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }
    
    /**
     * Limpiar todos los items del carrito
     */
    public void limpiar() {
        items.clear();
    }
    
    /**
     * Verificar si el carrito está vacío
     */
    public boolean estaVacio() {
        return items.isEmpty();
    }
}