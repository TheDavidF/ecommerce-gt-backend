package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.ImagenProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, UUID> {
    
    // Buscar imágenes de un producto ordenadas
    List<ImagenProducto> findByProductoIdOrderByOrdenAsc(UUID productoId);
    
    // Buscar imagen principal de un producto
    Optional<ImagenProducto> findByProductoIdAndEsPrincipalTrue(UUID productoId);
    
    // Eliminar todas las imágenes de un producto
    void deleteByProductoId(UUID productoId);
}