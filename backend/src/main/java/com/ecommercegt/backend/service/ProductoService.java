package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.CrearProductoRequest;
import com.ecommercegt.backend.dto.request.ProductoRequest;
import com.ecommercegt.backend.dto.request.ProductoUpdateRequest;
import com.ecommercegt.backend.dto.response.ProductoResponse;
import com.ecommercegt.backend.models.entidades.Categoria;
import com.ecommercegt.backend.models.entidades.Producto;
import com.ecommercegt.backend.models.entidades.Usuario;
import com.ecommercegt.backend.models.enums.EstadoProducto;
import com.ecommercegt.backend.repositorios.CategoriaRepository;
import com.ecommercegt.backend.repositorios.ProductoRepository;
import com.ecommercegt.backend.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommercegt.backend.dto.request.CrearProductoRequest;
import com.ecommercegt.backend.dto.request.ActualizarProductoRequest;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crear nuevo producto
     */
    @Transactional
    public ProductoResponse crearProducto(ProductoRequest request) {
        // Obtener usuario autenticado (vendedor)
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario vendedor = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que la categoría existe
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + request.getCategoriaId()));

        // Crear producto
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setPrecioDescuento(request.getPrecioDescuento());
        producto.setStock(request.getStock());
        producto.setMarca(request.getMarca());
        producto.setModelo(request.getModelo());
        producto.setCategoria(categoria);
        producto.setVendedor(vendedor);
        producto.setEstado(EstadoProducto.PENDIENTE_REVISION); // Siempre empieza pendiente
        producto.setDestacado(request.getDestacado() != null ? request.getDestacado() : false);

        // Guardar
        Producto guardado = productoRepository.save(producto);

        return convertirAResponse(guardado);
    }

    /**
     * Listar todos los productos (paginado)
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarProductos(Pageable pageable) {
        return productoRepository.findAll(pageable)
                .map(this::convertirAResponse);
    }

    /**
     * Listar productos aprobados con stock (para tienda pública)
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarProductosDisponibles(Pageable pageable) {
        return productoRepository.findByEstadoAndStockGreaterThan(EstadoProducto.APROBADO, 0, pageable)
                .map(this::convertirAResponse);
    }

    /**
     * Actualizar producto
     */
    @Transactional
    public ProductoResponse actualizarProducto(UUID id, ProductoUpdateRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Verificar que el usuario actual es el vendedor o es moderador/admin
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esVendedor = producto.getVendedor().getId().equals(usuario.getId());
        boolean esModerador = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().name().equals("MODERADOR") ||
                        rol.getNombre().name().equals("ADMIN"));

        if (!esVendedor && !esModerador) {
            throw new RuntimeException("No tienes permiso para editar este producto");
        }

        // Actualizar campos
        if (request.getNombre() != null) {
            producto.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            producto.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecio() != null) {
            producto.setPrecio(request.getPrecio());
        }
        if (request.getPrecioDescuento() != null) {
            producto.setPrecioDescuento(request.getPrecioDescuento());
        }
        if (request.getStock() != null) {
            producto.setStock(request.getStock());
            // Actualizar estado si se agota
            if (request.getStock() == 0) {
                producto.setEstado(EstadoProducto.AGOTADO);
            }
        }
        if (request.getMarca() != null) {
            producto.setMarca(request.getMarca());
        }
        if (request.getModelo() != null) {
            producto.setModelo(request.getModelo());
        }
        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }

        // Solo moderadores pueden cambiar el estado
        if (request.getEstado() != null && esModerador) {
            producto.setEstado(request.getEstado());
        }

        if (request.getDestacado() != null && esModerador) {
            producto.setDestacado(request.getDestacado());
        }

        Producto actualizado = productoRepository.save(producto);
        return convertirAResponse(actualizado);
    }

    /**
     * Eliminar producto
     */
    @Transactional
    public void eliminarProducto(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Verificar permisos
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esVendedor = producto.getVendedor().getId().equals(usuario.getId());
        boolean esAdmin = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().name().equals("ADMIN"));

        if (!esVendedor && !esAdmin) {
            throw new RuntimeException("No tienes permiso para eliminar este producto");
        }

        productoRepository.delete(producto);
    }

    /**
     * Buscar productos por término
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponse> buscarProductos(String query, Pageable pageable) {
        return productoRepository.buscarProductos(query, pageable)
                .map(this::convertirAResponse);
    }

    /**
     * Listar productos por categoría
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarProductosPorCategoria(Integer categoriaId, Pageable pageable) {
        return productoRepository.findByCategoriaId(categoriaId, pageable)
                .map(this::convertirAResponse);
    }

    /**
     * Listar productos por vendedor
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarProductosPorVendedor(UUID vendedorId, Pageable pageable) {
        return productoRepository.findByVendedorId(vendedorId, pageable)
                .map(this::convertirAResponse);
    }

    /**
     * Listar productos por estado (para moderadores)
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarProductosPorEstado(EstadoProducto estado, Pageable pageable) {
        return productoRepository.findByEstado(estado, pageable)
                .map(this::convertirAResponse);
    }

    /**
     * Filtrar productos por rango de precio
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponse> filtrarPorPrecio(BigDecimal precioMin, BigDecimal precioMax, Pageable pageable) {
        return productoRepository.findByPrecioBetween(precioMin, precioMax, pageable)
                .map(this::convertirAResponse);
    }

    /**
     * Obtener productos destacados
     */
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosDestacados() {
        return productoRepository.findByDestacadoTrueAndEstado(EstadoProducto.APROBADO).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /**
     * Aprobar producto (solo moderadores)
     */
    @Transactional
    public ProductoResponse aprobarProducto(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setEstado(EstadoProducto.APROBADO);
        Producto actualizado = productoRepository.save(producto);

        return convertirAResponse(actualizado);
    }

    /**
     * Rechazar producto (solo moderadores)
     */
    @Transactional
    public ProductoResponse rechazarProducto(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setEstado(EstadoProducto.RECHAZADO);
        Producto actualizado = productoRepository.save(producto);

        return convertirAResponse(actualizado);
    }

    /**
     * Obtener mis productos (vendedor actual)
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponse> obtenerMisProductos(Pageable pageable) {
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario vendedor = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return productoRepository.findByVendedorId(vendedor.getId(), pageable)
                .map(this::convertirAResponse);
    }

    /**
     * Convertir entidad a DTO Response
     */
    private ProductoResponse convertirAResponse(Producto producto) {
        ProductoResponse response = new ProductoResponse();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setPrecioDescuento(producto.getPrecioDescuento());
        response.setPrecioFinal(producto.getPrecioFinal());
        response.setStock(producto.getStock());
        response.setMarca(producto.getMarca());
        response.setModelo(producto.getModelo());
        response.setEstado(producto.getEstado());
        response.setDestacado(producto.getDestacado());

        // Categoría
        response.setCategoriaId(producto.getCategoria().getId());
        response.setCategoriaNombre(producto.getCategoria().getNombre());

        // Vendedor
        response.setVendedorId(producto.getVendedor().getId());
        response.setVendedorNombre(producto.getVendedor().getNombreCompleto());

        // Imágenes
        response.setImagenes(producto.getImagenes().stream()
                .map(img -> img.getUrlImagen())
                .collect(Collectors.toList()));

        producto.getImagenes().stream()
                .filter(img -> img.getEsPrincipal())
                .findFirst()
                .ifPresent(img -> response.setImagenPrincipal(img.getUrlImagen()));

        // Fechas
        response.setFechaCreacion(producto.getFechaCreacion());
        response.setFechaActualizacion(producto.getFechaActualizacion());

        return response;
    }

    /**
     * Crear producto con CrearProductoRequest
     */
    @Transactional
    public Producto crearProducto(CrearProductoRequest request, Usuario vendedor) {

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + request.getCategoriaId()));

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setPrecioDescuento(request.getPrecioDescuento());
        producto.setStock(request.getStock());
        producto.setMarca(request.getMarca());
        producto.setModelo(request.getModelo());
        producto.setCategoria(categoria);
        producto.setVendedor(vendedor);
        producto.setEstado(EstadoProducto.PENDIENTE_REVISION); // Siempre empieza pendiente
        producto.setDestacado(request.getDestacado() != null ? request.getDestacado() : false);

        return productoRepository.save(producto);
    }

    /**
     * Actualizar producto con ActualizarProductoRequest y verificación de vendedor
     */
    @Transactional
    public Producto actualizarProducto(UUID id, ActualizarProductoRequest request, Usuario vendedor) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Verificar que el usuario actual es el vendedor del producto
        if (!producto.getVendedor().getId().equals(vendedor.getId())) {
            throw new RuntimeException("No tienes permiso para editar este producto");
        }

        // Actualizar campos
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setPrecioDescuento(request.getPrecioDescuento());
        producto.setStock(request.getStock());
        producto.setMarca(request.getMarca());
        producto.setModelo(request.getModelo());

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }

        if (request.getDestacado() != null) {
            producto.setDestacado(request.getDestacado());
        }

        // Al actualizar, vuelve a PENDIENTE_REVISION
        producto.setEstado(EstadoProducto.PENDIENTE_REVISION);

        return productoRepository.save(producto);
    }

    /**
     * Listar productos por vendedor (retorna List en lugar de Page)
     */
    @Transactional(readOnly = true)
    public List<Producto> listarProductosPorVendedor(UUID vendedorId) {
        return productoRepository.findByVendedorId(vendedorId);
    }

    /**
     * Listar productos por estado (retorna List en lugar de Page)
     */
    @Transactional(readOnly = true)
    public List<Producto> listarProductosPorEstado(EstadoProducto estado) {
        return productoRepository.findByEstado(estado);
    }

    /**
     * Obtener producto por ID (retorna ProductoResponse)
     */
    @Transactional(readOnly = true)
    public ProductoResponse obtenerProductoPorId(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return convertirAResponse(producto);
    }

    /**
     * Pausar/reanudar producto
     */
    @Transactional
    public Producto pausarProducto(UUID id, Usuario vendedor) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!producto.getVendedor().getId().equals(vendedor.getId())) {
            throw new RuntimeException("No tienes permiso para modificar este producto");
        }

        // Toggle entre APROBADO y PAUSADO
        if (producto.getEstado() == EstadoProducto.APROBADO) {
            producto.setEstado(EstadoProducto.PAUSADO);
        } else if (producto.getEstado() == EstadoProducto.PAUSADO) {
            producto.setEstado(EstadoProducto.APROBADO);
        } else {
            throw new RuntimeException("Solo se pueden pausar productos aprobados");
        }

        return productoRepository.save(producto);
    }

    /**
     * Eliminar producto con verificación de vendedor
     */
    @Transactional
    public void eliminarProducto(UUID id, Usuario vendedor) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!producto.getVendedor().getId().equals(vendedor.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este producto");
        }

        productoRepository.delete(producto);
    }
}