package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.ProductoRequest;
import com.ecommercegt.backend.dto.request.ProductoUpdateRequest;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.dto.response.ProductoResponse;
import com.ecommercegt.backend.models.enums.EstadoProducto;
import com.ecommercegt.backend.service.ProductoService;
import com.ecommercegt.backend.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;

    @Autowired
    private NotificacionService notificacionService;
    
    /**
     * Crear nuevo producto (usuarios autenticados)
     * POST /api/productos
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoRequest request) {
        try {
            ProductoResponse response = productoService.crearProducto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al crear producto: " + e.getMessage()));
        }
    }
    
    /**
     * Listar todos los productos (con paginación)
     * GET /api/productos?page=0&size=10&sort=nombre,asc
     */
    @GetMapping
    public ResponseEntity<Page<ProductoResponse>> listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ProductoResponse> productos = productoService.listarProductos(pageable);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Listar productos disponibles para tienda (aprobados con stock)
     * GET /api/productos/disponibles?page=0&size=10
     */
    @GetMapping("/disponibles")
    public ResponseEntity<Page<ProductoResponse>> listarProductosDisponibles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<ProductoResponse> productos = productoService.listarProductosDisponibles(pageable);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtener producto por ID
     * GET /api/productos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable UUID id) {
        try {
            ProductoResponse response = productoService.obtenerProductoPorId(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Actualizar producto
     * PUT /api/productos/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable UUID id,
            @Valid @RequestBody ProductoUpdateRequest request) {
        try {
            ProductoResponse response = productoService.actualizarProducto(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar producto: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar producto
     * DELETE /api/productos/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> eliminarProducto(@PathVariable UUID id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok(new MessageResponse("Producto eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar producto: " + e.getMessage()));
        }
    }
    
    /**
     * Buscar productos
     * GET /api/productos/buscar?q=laptop&page=0&size=10
     */
    @GetMapping("/buscar")
    public ResponseEntity<Page<ProductoResponse>> buscarProductos(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponse> productos = productoService.buscarProductos(q, pageable);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Listar productos por categoría
     * GET /api/productos/categoria/{categoriaId}?page=0&size=10
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<Page<ProductoResponse>> listarProductosPorCategoria(
            @PathVariable Integer categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponse> productos = productoService.listarProductosPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Listar productos por vendedor
     * GET /api/productos/vendedor/{vendedorId}?page=0&size=10
     */
    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<Page<ProductoResponse>> listarProductosPorVendedor(
            @PathVariable UUID vendedorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponse> productos = productoService.listarProductosPorVendedor(vendedorId, pageable);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtener mis productos (vendedor actual)
     * GET /api/productos/mis-productos?page=0&size=10
     */
    @GetMapping("/mis-productos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ProductoResponse>> obtenerMisProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<ProductoResponse> productos = productoService.obtenerMisProductos(pageable);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Filtrar productos por rango de precio
     * GET /api/productos/filtrar/precio?min=100&max=500&page=0&size=10
     */
    @GetMapping("/filtrar/precio")
    public ResponseEntity<Page<ProductoResponse>> filtrarPorPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponse> productos = productoService.filtrarPorPrecio(min, max, pageable);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtener productos destacados
     * GET /api/productos/destacados
     */
    @GetMapping("/destacados")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosDestacados() {
        List<ProductoResponse> productos = productoService.obtenerProductosDestacados();
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Listar productos por estado (solo moderadores)
     * GET /api/productos/estado/{estado}?page=0&size=10
     */
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<Page<ProductoResponse>> listarProductosPorEstado(
            @PathVariable EstadoProducto estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponse> productos = productoService.listarProductosPorEstado(estado, pageable);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Aprobar producto (solo moderadores)
     * POST /api/productos/{id}/aprobar
     */
    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> aprobarProducto(@PathVariable UUID id) {
        try {
            ProductoResponse response = productoService.aprobarProducto(id);
            // Notificar al vendedor
            if (response.getVendedorId() != null) {
                notificacionService.notificarProductoAprobado(response.getVendedorId(), response.getId(), response.getNombre());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al aprobar producto: " + e.getMessage()));
        }
    }
    
    /**
     * Rechazar producto (solo moderadores)
     * POST /api/productos/{id}/rechazar
     */
    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyAuthority('MODERADOR', 'ADMIN')")
    public ResponseEntity<?> rechazarProducto(@PathVariable UUID id) {
        try {
            ProductoResponse response = productoService.rechazarProducto(id);
            // Notificar al vendedor
            if (response.getVendedorId() != null) {
                notificacionService.notificarProductoRechazado(response.getVendedorId(), response.getId(), response.getNombre(), "Rechazado por moderador");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al rechazar producto: " + e.getMessage()));
        }
    }
    
    /**
     * Subir imagen para un producto
     * POST /api/productos/{id}/imagenes
     */
    @PostMapping("/{id}/imagenes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> subirImagenProducto(
            @PathVariable UUID id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "esPrincipal", defaultValue = "false") boolean esPrincipal
    ) {
        try {
            ProductoResponse response = productoService.subirImagen(id, file, esPrincipal);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al subir imagen: " + e.getMessage()));
        }
    }
}