package com.ecommercegt.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para mostrar producto en tarjetas (listados, búsqueda)
 * Información resumida optimizada para cards
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCardResponse {
    
    private UUID id;
    private String nombre;
    private String descripcionCorta; // Primeros 100 caracteres
    private BigDecimal precio;
    private BigDecimal precioDescuento;
    private BigDecimal precioFinal;
    private Integer porcentajeDescuento;
    private String marca;
    private String imagenPrincipal; // URL de la primera imagen
    private Double calificacionPromedio;
    private Integer cantidadReviews;
    private Integer stock;
    private Boolean disponible;
    private Boolean destacado;
    
    // Info de categoría
    private Integer categoriaId;
    private String categoriaNombre;
    
    // Info de vendedor
    private UUID vendedorId;
    private String vendedorNombre;
    
    /**
     * Constructor desde entidad Producto
     */
    public static ProductoCardResponse fromProducto(com.ecommercegt.backend.models.entidades.Producto producto) {
        ProductoCardResponse response = new ProductoCardResponse();
        
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        
        // Descripción corta (primeros 100 caracteres)
        if (producto.getDescripcion() != null) {
            String desc = producto.getDescripcion();
            response.setDescripcionCorta(desc.length() > 100 ? desc.substring(0, 100) + "..." : desc);
        }
        
        response.setPrecio(producto.getPrecio());
        response.setPrecioDescuento(producto.getPrecioDescuento());
        response.setPrecioFinal(producto.getPrecioFinal());
        
        // Calcular porcentaje de descuento
        if (producto.getPrecioDescuento() != null && producto.getPrecio() != null) {
            BigDecimal descuento = producto.getPrecio().subtract(producto.getPrecioDescuento());
            BigDecimal porcentaje = descuento.divide(producto.getPrecio(), 2, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setPorcentajeDescuento(porcentaje.intValue());
        } else {
            response.setPorcentajeDescuento(0);
        }
        
        response.setMarca(producto.getMarca());
        
        // Imagen principal (primera imagen)
        if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
            response.setImagenPrincipal(producto.getImagenes().get(0).getUrlImagen());
        }
        
        response.setCalificacionPromedio(producto.getCalificacionPromedio());
        response.setCantidadReviews(producto.getCantidadReviews());
        response.setStock(producto.getStock());
        response.setDisponible(producto.tieneStock());
        response.setDestacado(producto.getDestacado());
        
        // Categoría
        if (producto.getCategoria() != null) {
            response.setCategoriaId(producto.getCategoria().getId());
            response.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        
        // Vendedor
        if (producto.getVendedor() != null) {
            response.setVendedorId(producto.getVendedor().getId());
            response.setVendedorNombre(producto.getVendedor().getNombreCompleto());
        }
        
        return response;
    }
}