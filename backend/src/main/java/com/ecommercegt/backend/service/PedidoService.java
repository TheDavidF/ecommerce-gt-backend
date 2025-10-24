package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.ActualizarEstadoRequest;
import com.ecommercegt.backend.dto.request.CrearPedidoRequest;
import com.ecommercegt.backend.dto.response.ItemPedidoResponse;
import com.ecommercegt.backend.dto.response.PedidoResponse;
import com.ecommercegt.backend.dto.response.ResumenPedidosResponse;
import com.ecommercegt.backend.models.entidades.*;
import com.ecommercegt.backend.models.enums.EstadoPedido;
import com.ecommercegt.backend.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de Pedidos
 * Gestiona el ciclo completo de pedidos:
 * - Crear desde carrito
 * - Actualizar estados
 * - Cancelar con devolución de stock
 * - Consultar historial
 */
@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    /**
     * Crear pedido desde el carrito del usuario autenticado
     * 
     * Proceso:
     * 1. Obtener carrito del usuario
     * 2. Validar que el carrito no esté vacío
     * 3. Verificar stock de todos los productos
     * 4. Crear pedido con snapshot de datos
     * 5. Reducir stock de productos
     * 6. Limpiar carrito
     * 7. Retornar pedido creado
     */
    @Transactional
    public PedidoResponse crearPedidoDesdeCarrito(CrearPedidoRequest request) {
        // 1. Obtener usuario autenticado
        Usuario usuario = obtenerUsuarioAutenticado();
        
        // 2. Obtener carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("No se encontró el carrito del usuario"));
        
        // 3. Validar que el carrito no esté vacío
        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío. Agrega productos antes de hacer el pedido.");
        }
        
        // 4. Verificar stock de todos los productos
        for (ItemCarrito itemCarrito : carrito.getItems()) {
            Producto producto = productoRepository.findById(itemCarrito.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemCarrito.getProducto().getNombre()));
            
            if (producto.getStock() < itemCarrito.getCantidad()) {
                throw new RuntimeException(
                    String.format("Stock insuficiente para %s. Disponible: %d, Solicitado: %d",
                        producto.getNombre(), producto.getStock(), itemCarrito.getCantidad())
                );
            }
        }
        
        // 5. Crear pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setNumeroOrden(generarNumeroOrden());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setDireccionEnvio(request.getDireccionEnvio());
        pedido.setTelefonoContacto(request.getTelefonoContacto());
        pedido.setMetodoPago(request.getMetodoPago());
        pedido.setNotas(request.getNotas());
        
        // 6. Crear items del pedido (snapshot de datos)
        for (ItemCarrito itemCarrito : carrito.getItems()) {
            Producto producto = itemCarrito.getProducto();
            
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProductoId(producto.getId());
            itemPedido.setProductoNombre(producto.getNombre());
            
            // Obtener imagen del producto
            if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
                itemPedido.setProductoImagen(producto.getImagenes().get(0).getUrlImagen());
            }
            
            itemPedido.setVendedorNombre(producto.getVendedor().getNombreUsuario());
            itemPedido.setCantidad(itemCarrito.getCantidad());
            itemPedido.setPrecioUnitario(itemCarrito.getPrecioUnitario());
            itemPedido.calcularSubtotal();
            
            pedido.agregarItem(itemPedido);
        }
        
        // 7. Calcular total
        pedido.calcularTotal();
        
        // 8. Guardar pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        // 9. Reducir stock de productos
        for (ItemCarrito itemCarrito : carrito.getItems()) {
            Producto producto = itemCarrito.getProducto();
            producto.setStock(producto.getStock() - itemCarrito.getCantidad());
            productoRepository.save(producto);
        }
        
        // 10. Limpiar carrito
        carrito.getItems().clear();
        carritoRepository.save(carrito);
        
        // 11. Retornar respuesta
        return convertirAResponse(pedidoGuardado);
    }
    
    /**
     * Obtener mis pedidos (usuario autenticado)
     */
    @Transactional(readOnly = true)
    public Page<PedidoResponse> obtenerMisPedidos(Pageable pageable) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Page<Pedido> pedidos = pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuario.getId(), pageable);
        return pedidos.map(this::convertirAResponse);
    }
    
    /**
     * Obtener detalle de un pedido
     * Valida que el pedido pertenezca al usuario autenticado
     */
    @Transactional(readOnly = true)
    public PedidoResponse obtenerDetallePedido(UUID pedidoId) {
        Usuario usuario = obtenerUsuarioAutenticado();
        
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        
        // Validar que el pedido pertenezca al usuario
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para ver este pedido");
        }
        
        return convertirAResponse(pedido);
    }
    
    /**
     * Actualizar estado de un pedido
     * Solo VENDEDOR, MODERADOR o ADMIN pueden actualizar estados
     */
    @Transactional
    public PedidoResponse actualizarEstado(UUID pedidoId, ActualizarEstadoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        
        // Validar transición de estado
        if (!pedido.getEstado().puedeTransicionarA(request.getNuevoEstado())) {
            throw new RuntimeException(
                String.format("No se puede cambiar el estado de %s a %s", 
                    pedido.getEstado(), request.getNuevoEstado())
            );
        }
        
        // Cambiar estado
        pedido.cambiarEstado(request.getNuevoEstado());
        
        // Si hay notas, agregarlas
        if (request.getNotas() != null && !request.getNotas().isEmpty()) {
            String notasActuales = pedido.getNotas() != null ? pedido.getNotas() : "";
            pedido.setNotas(notasActuales + "\n[" + LocalDateTime.now() + "] " + request.getNotas());
        }
        
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        return convertirAResponse(pedidoActualizado);
    }
    
    /**
     * Cancelar pedido
     * Devuelve el stock a los productos
     * Solo se puede cancelar si está en estado PENDIENTE o CONFIRMADO
     */
    @Transactional
    public PedidoResponse cancelarPedido(UUID pedidoId, String motivo) {
        Usuario usuario = obtenerUsuarioAutenticado();
        
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        
        // Validar que el pedido pertenezca al usuario
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para cancelar este pedido");
        }
        
        // Validar que se pueda cancelar
        if (!pedido.puedeSerCancelado()) {
            throw new RuntimeException(
                "El pedido no puede ser cancelado en su estado actual: " + pedido.getEstado()
            );
        }
        
        // Devolver stock a productos
        for (ItemPedido item : pedido.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElse(null);
            
            if (producto != null) {
                producto.setStock(producto.getStock() + item.getCantidad());
                productoRepository.save(producto);
            }
        }
        
        // Cancelar pedido
        pedido.cancelar(motivo);
        Pedido pedidoCancelado = pedidoRepository.save(pedido);
        
        return convertirAResponse(pedidoCancelado);
    }
    
    /**
     * Obtener pedidos de un vendedor
     * Retorna pedidos que contengan productos del vendedor
     */
    @Transactional(readOnly = true)
    public Page<PedidoResponse> obtenerPedidosVendedor(Pageable pageable) {
        Usuario vendedor = obtenerUsuarioAutenticado();
        Page<Pedido> pedidos = pedidoRepository.findPedidosConProductosDeVendedor(vendedor.getId(), pageable);
        return pedidos.map(this::convertirAResponse);
    }
    
    /**
     * Obtener todos los pedidos (solo ADMIN)
     */
    @Transactional(readOnly = true)
    public Page<PedidoResponse> obtenerTodosPedidos(Pageable pageable) {
        Page<Pedido> pedidos = pedidoRepository.findAll(pageable);
        return pedidos.map(this::convertirAResponse);
    }
    
    /**
     * Obtener resumen de pedidos del usuario
     */
    @Transactional(readOnly = true)
    public ResumenPedidosResponse obtenerResumenPedidos() {
        Usuario usuario = obtenerUsuarioAutenticado();
        
        ResumenPedidosResponse resumen = new ResumenPedidosResponse();
        resumen.setPedidosPendientes(pedidoRepository.countByEstado(EstadoPedido.PENDIENTE));
        resumen.setPedidosConfirmados(pedidoRepository.countByEstado(EstadoPedido.CONFIRMADO));
        resumen.setPedidosEnPreparacion(pedidoRepository.countByEstado(EstadoPedido.EN_PREPARACION));
        resumen.setPedidosEnviados(pedidoRepository.countByEstado(EstadoPedido.ENVIADO));
        resumen.setPedidosEntregados(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO));
        resumen.setPedidosCancelados(pedidoRepository.countByEstado(EstadoPedido.CANCELADO));
        resumen.setTotalPedidos(pedidoRepository.countByUsuarioId(usuario.getId()));
        
        Double totalCompras = pedidoRepository.calcularTotalComprasUsuario(usuario.getId());
        resumen.setTotalCompras(totalCompras != null ? java.math.BigDecimal.valueOf(totalCompras) : java.math.BigDecimal.ZERO);
        
        return resumen;
    }
    
    // ==================== MÉTODOS DE UTILIDAD ====================
    
    /**
     * Generar número de orden único
     * Formato: PED-YYYYMMDD-####
     * Ejemplo: PED-20251024-0001
     */
    private String generarNumeroOrden() {
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefijo = "PED-" + fechaHoy + "-";
        
        // Buscar último número de orden del día
        List<String> numerosOrden = pedidoRepository.findUltimoNumeroOrdenDelDia(prefijo + "%");
        
        int siguienteNumero = 1;
        if (!numerosOrden.isEmpty()) {
            String ultimoNumero = numerosOrden.get(0);
            String numeroStr = ultimoNumero.substring(ultimoNumero.lastIndexOf('-') + 1);
            siguienteNumero = Integer.parseInt(numeroStr) + 1;
        }
        
        return String.format("%s%04d", prefijo, siguienteNumero);
    }
    
    /**
     * Obtener usuario autenticado
     */
    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nombreUsuario = authentication.getName();
        
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + nombreUsuario));
    }
    
    /**
     * Convertir entidad Pedido a PedidoResponse
     */
    private PedidoResponse convertirAResponse(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setNumeroOrden(pedido.getNumeroOrden());
        response.setUsuarioId(pedido.getUsuario().getId());
        response.setUsuarioNombre(pedido.getUsuario().getNombreCompleto());
        response.setUsuarioEmail(pedido.getUsuario().getCorreo());
        
        // Convertir items
        List<ItemPedidoResponse> itemsResponse = pedido.getItems().stream()
                .map(this::convertirItemAResponse)
                .collect(Collectors.toList());
        response.setItems(itemsResponse);
        
        response.setCantidadTotalItems(pedido.getCantidadTotalItems());
        response.setTotal(pedido.getTotal());
        response.setEstado(pedido.getEstado());
        response.setDireccionEnvio(pedido.getDireccionEnvio());
        response.setTelefonoContacto(pedido.getTelefonoContacto());
        response.setMetodoPago(pedido.getMetodoPago());
        response.setNotas(pedido.getNotas());
        response.setFechaPedido(pedido.getFechaPedido());
        response.setFechaActualizacion(pedido.getFechaActualizacion());
        response.setFechaCancelacion(pedido.getFechaCancelacion());
        response.setMotivoCancelacion(pedido.getMotivoCancelacion());
        response.setFechaEntrega(pedido.getFechaEntrega());
        response.setPuedeSerCancelado(pedido.puedeSerCancelado());
        response.setEsFinal(pedido.getEstado().esFinal());
        
        return response;
    }
    
    /**
     * Convertir entidad ItemPedido a ItemPedidoResponse
     */
    private ItemPedidoResponse convertirItemAResponse(ItemPedido item) {
        ItemPedidoResponse response = new ItemPedidoResponse();
        response.setId(item.getId());
        response.setProductoId(item.getProductoId());
        response.setProductoNombre(item.getProductoNombre());
        response.setProductoImagen(item.getProductoImagen());
        response.setVendedorNombre(item.getVendedorNombre());
        response.setCantidad(item.getCantidad());
        response.setPrecioUnitario(item.getPrecioUnitario());
        response.setSubtotal(item.getSubtotal());
        return response;
    }
}