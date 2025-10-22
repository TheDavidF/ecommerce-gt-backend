package com.ecommercegt.backend.models.entidades;

import com.ecommercegt.backend.models.enums.EstadoProducto;
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
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(name = "precio_descuento", precision = 10, scale = 2)
    private BigDecimal precioDescuento;
    
    @Column(nullable = false)
    private Integer stock = 0;
    
    @Column(length = 100)
    private String marca;
    
    @Column(length = 100)
    private String modelo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoProducto estado = EstadoProducto.PENDIENTE_REVISION;
    
    @Column(name = "destacado")
    private Boolean destacado = false;
    
    // Relación: Muchos productos pertenecen a una categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    // Relación: Muchos productos pertenecen a un vendedor (usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;
    
    // Relación: Un producto tiene muchas imágenes
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenProducto> imagenes = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Método helper para agregar imágenes
    public void agregarImagen(ImagenProducto imagen) {
        imagenes.add(imagen);
        imagen.setProducto(this);
    }
    
    // Método helper para verificar si tiene stock
    public boolean tieneStock() {
        return stock != null && stock > 0;
    }
    
    // Método helper para calcular precio final
    public BigDecimal getPrecioFinal() {
        return precioDescuento != null ? precioDescuento : precio;
    }
}