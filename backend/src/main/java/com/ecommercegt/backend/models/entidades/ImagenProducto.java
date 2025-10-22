package com.ecommercegt.backend.models.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "imagenes_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenProducto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "url_imagen", nullable = false, length = 500)
    private String urlImagen;
    
    @Column(name = "es_principal")
    private Boolean esPrincipal = false;
    
    @Column(name = "orden")
    private Integer orden = 0;
    
    // Relación: Muchas imágenes pertenecen a un producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @CreationTimestamp
    @Column(name = "fecha_subida", nullable = false, updatable = false)
    private LocalDateTime fechaSubida;
}