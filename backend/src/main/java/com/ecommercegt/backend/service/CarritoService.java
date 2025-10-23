package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.ActualizarCantidadRequest;
import com.ecommercegt.backend.dto.request.ItemCarritoRequest;
import com.ecommercegt.backend.dto.response.CarritoResponse;
import com.ecommercegt.backend.dto.response.ItemCarritoResponse;
import com.ecommercegt.backend.models.entidades.Carrito;
import com.ecommercegt.backend.models.entidades.ItemCarrito;
import com.ecommercegt.backend.models.entidades.Producto;
import com.ecommercegt.backend.models.entidades.Usuario;
import com.ecommercegt.backend.repositorios.CarritoRepository;
import com.ecommercegt.backend.repositorios.ItemCarritoRepository;
import com.ecommercegt.backend.repositorios.ProductoRepository;
import com.ecommercegt.backend.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CarritoService {
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private ItemCarritoRepository itemCarritoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Obtener o crear carrito del usuario actual
     */
    @Transactional
    public Carrito obtenerOCrearCarrito() {
        Usuario usuario = obtenerUsuarioActual();
        
        return carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    return carritoRepository.save(nuevoCarrito);
                });
    }
    
    /**
     * Agregar item al carrito
     */
    @Transactional
    public CarritoResponse agregarItem(ItemCarritoRequest request) {
        // Obtener o crear carrito
        Carrito carrito = obtenerOCrearCarrito();
        
        // Validar que el producto existe y está disponible
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + request.getProductoId()));
        
        // Validar que el producto está aprobado
        if (!producto.getEstado().name().equals("APROBADO")) {
            throw new RuntimeException("El producto no está disponible para compra");
        }
        
        // Validar stock disponible
        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }
        
        // Verificar si el producto ya está en el carrito
        ItemCarrito itemExistente = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), producto.getId())
                .orElse(null);
        
        if (itemExistente != null) {
            // Si ya existe, aumentar cantidad
            int nuevaCantidad = itemExistente.getCantidad() + request.getCantidad();
            
            // Validar stock para nueva cantidad
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock() + 
                        ", ya tienes " + itemExistente.getCantidad() + " en el carrito");
            }
            
            itemExistente.setCantidad(nuevaCantidad);
            itemCarritoRepository.save(itemExistente);
        } else {
            // Crear nuevo item
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(request.getCantidad());
            nuevoItem.setPrecioUnitario(producto.getPrecioFinal());
            
            itemCarritoRepository.save(nuevoItem);
            carrito.agregarItem(nuevoItem);
        }
        
        // Actualizar carrito
        carrito = carritoRepository.save(carrito);
        
        return convertirAResponse(carrito);
    }
    
    /**
     * Obtener carrito del usuario actual
     */
    @Transactional(readOnly = true)
    public CarritoResponse obtenerMiCarrito() {
        Usuario usuario = obtenerUsuarioActual();
        
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElseThrow(() -> new RuntimeException("No tienes un carrito activo"));
        
        return convertirAResponse(carrito);
    }
    
    /**
     * Actualizar cantidad de un item
     */
    @Transactional
    public CarritoResponse actualizarCantidadItem(Integer itemId, ActualizarCantidadRequest request) {
        Usuario usuario = obtenerUsuarioActual();
        
        // Buscar item
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado con ID: " + itemId));
        
        // Verificar que el item pertenece al carrito del usuario
        if (!item.getCarrito().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para modificar este item");
        }
        
        // Validar stock disponible
        if (item.getProducto().getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + item.getProducto().getStock());
        }
        
        // Actualizar cantidad
        item.setCantidad(request.getCantidad());
        itemCarritoRepository.save(item);
        
        // Obtener carrito actualizado
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        return convertirAResponse(carrito);
    }
    
    /**
     * Eliminar item del carrito
     */
    @Transactional
    public CarritoResponse eliminarItem(Integer itemId) {
        Usuario usuario = obtenerUsuarioActual();
        
        // Buscar item
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado con ID: " + itemId));
        
        // Verificar que el item pertenece al carrito del usuario
        if (!item.getCarrito().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este item");
        }
        
        // Eliminar item
        itemCarritoRepository.delete(item);
        
        // Obtener carrito actualizado
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        return convertirAResponse(carrito);
    }
    
    /**
     * Limpiar carrito (eliminar todos los items)
     */
    @Transactional
    public void limpiarCarrito() {
        Usuario usuario = obtenerUsuarioActual();
        
        Carrito carrito = carritoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("No tienes un carrito activo"));
        
        // Eliminar todos los items
        itemCarritoRepository.deleteByCarritoId(carrito.getId());
        
        // Limpiar lista de items en el carrito
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }
    
    /**
     * Calcular total del carrito
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotal() {
        Usuario usuario = obtenerUsuarioActual();
        
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElseThrow(() -> new RuntimeException("No tienes un carrito activo"));
        
        return carrito.calcularTotal();
    }
    
    /**
     * Verificar disponibilidad de stock para todos los items
     */
    @Transactional(readOnly = true)
    public boolean verificarStockDisponible() {
        Usuario usuario = obtenerUsuarioActual();
        
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElse(null);
        
        if (carrito == null || carrito.estaVacio()) {
            return true;
        }
        
        return carrito.getItems().stream()
                .allMatch(item -> item.getProducto().getStock() >= item.getCantidad());
    }
    
    /**
     * Obtener cantidad de items en el carrito
     */
    @Transactional(readOnly = true)
    public Integer contarItems() {
        Usuario usuario = obtenerUsuarioActual();
        
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElse(null);
        
        if (carrito == null) {
            return 0;
        }
        
        return carrito.getCantidadTotalItems();
    }
    
    /**
     * Obtener usuario actual del contexto de seguridad
     */
    private Usuario obtenerUsuarioActual() {
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    /**
     * Convertir entidad Carrito a DTO Response
     */
    private CarritoResponse convertirAResponse(Carrito carrito) {
        CarritoResponse response = new CarritoResponse();
        response.setId(carrito.getId());
        response.setUsuarioId(carrito.getUsuario().getId());
        response.setUsuarioNombre(carrito.getUsuario().getNombreCompleto());
        response.setFechaCreacion(carrito.getFechaCreacion());
        response.setFechaActualizacion(carrito.getFechaActualizacion());
        response.setVacio(carrito.estaVacio());
        response.setCantidadTotalItems(carrito.getCantidadTotalItems());
        response.setTotal(carrito.calcularTotal());
        
        // Convertir items
        List<ItemCarritoResponse> itemsResponse = carrito.getItems().stream()
                .map(this::convertirItemAResponse)
                .collect(Collectors.toList());
        
        response.setItems(itemsResponse);
        
        return response;
    }
    
    /**
     * Convertir entidad ItemCarrito a DTO Response
     */
    private ItemCarritoResponse convertirItemAResponse(ItemCarrito item) {
        ItemCarritoResponse response = new ItemCarritoResponse();
        response.setId(item.getId());
        response.setProductoId(item.getProducto().getId());
        response.setProductoNombre(item.getProducto().getNombre());
        response.setProductoStock(item.getProducto().getStock());
        
        // Obtener imagen principal del producto
        item.getProducto().getImagenes().stream()
                .filter(img -> img.getEsPrincipal())
                .findFirst()
                .ifPresent(img -> response.setProductoImagen(img.getUrlImagen()));
        
        // Si no hay imagen principal, tomar la primera
        if (response.getProductoImagen() == null && !item.getProducto().getImagenes().isEmpty()) {
            response.setProductoImagen(item.getProducto().getImagenes().get(0).getUrlImagen());
        }
        
        response.setCantidad(item.getCantidad());
        response.setPrecioUnitario(item.getPrecioUnitario());
        response.setSubtotal(item.calcularSubtotal());
        response.setFechaAgregado(item.getFechaAgregado());
        
        return response;
    }
}