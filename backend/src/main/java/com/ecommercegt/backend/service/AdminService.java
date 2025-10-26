package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.CreateUserRequest;
import com.ecommercegt.backend.dto.request.UpdateUserRequest;
import com.ecommercegt.backend.dto.response.EstadisticasGeneralesResponse;
import com.ecommercegt.backend.dto.response.UserResponse;
import com.ecommercegt.backend.models.entidades.Pedido;
import com.ecommercegt.backend.models.entidades.Rol;
import com.ecommercegt.backend.models.entidades.Usuario;
import com.ecommercegt.backend.models.enums.EstadoPedido;
import com.ecommercegt.backend.models.enums.RolNombre;
import com.ecommercegt.backend.repositorios.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    // Repositories
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final ReviewRepository reviewRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    
    // Encoder
    private final PasswordEncoder passwordEncoder;
    
    // ==================== GESTIÓN DE USUARIOS ====================
    
    /**
     * Listar todos los usuarios
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> listarUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(this::convertirUsuarioAResponse);
    }
    
    /**
     * Listar usuarios por rol
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> listarUsuariosPorRol(String rol, Pageable pageable) {
        try {
            RolNombre rolNombre = RolNombre.valueOf(rol.toUpperCase());
            return usuarioRepository.findByRolesNombre(rolNombre, pageable)
                    .map(this::convertirUsuarioAResponse);
        } catch (IllegalArgumentException e) {
            return usuarioRepository.findAll(pageable)
                    .map(this::convertirUsuarioAResponse);
        }
    }
    
    /**
     * Obtener usuario por ID
     */
    @Transactional(readOnly = true)
    public UserResponse obtenerUsuarioPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertirUsuarioAResponse(usuario);
    }
    
    /**
     * Crear nuevo usuario (empleado)
     */
    @Transactional
    public UserResponse crearUsuario(CreateUserRequest request) {
        // Validar que el usuario no exista
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasenaHash(passwordEncoder.encode(request.getPassword())); // ← CORRECTO
        usuario.setNombreCompleto(request.getNombre() + " " + request.getApellido()); // ← CORRECTO
        usuario.setTelefono(request.getTelefono());
        usuario.setActivo(true);
        
        // Asignar rol
        Set<Rol> roles = new HashSet<>();
        RolNombre rolNombre;
        
        switch (request.getRol().toUpperCase()) {
            case "MODERADOR":
                rolNombre = RolNombre.MODERADOR;
                break;
            case "LOGISTICA":
                rolNombre = RolNombre.LOGISTICA;
                break;
            case "ADMIN":
                rolNombre = RolNombre.ADMIN;
                break;
            case "COMUN":
            default:
                rolNombre = RolNombre.COMUN;
                break;
        }
        
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));
        
        roles.add(rol);
        usuario.setRoles(roles);
        
        // Guardar
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        return convertirUsuarioAResponse(usuarioGuardado);
    }
    
    /**
     * Actualizar usuario
     */
    @Transactional
    public UserResponse actualizarUsuario(UUID id, UpdateUserRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Actualizar campos si vienen en el request
        if (request.getCorreo() != null) {
            if (usuarioRepository.existsByCorreo(request.getCorreo()) && 
                !usuario.getCorreo().equals(request.getCorreo())) {
                throw new RuntimeException("El correo ya está en uso");
            }
            usuario.setCorreo(request.getCorreo());
        }
        
        // Actualizar nombreCompleto si viene nombre o apellido
        if (request.getNombre() != null || request.getApellido() != null) {
            String[] partes = usuario.getNombreCompleto().split(" ", 2);
            String nombreActual = partes.length > 0 ? partes[0] : "";
            String apellidoActual = partes.length > 1 ? partes[1] : "";
            
            String nuevoNombre = request.getNombre() != null ? request.getNombre() : nombreActual;
            String nuevoApellido = request.getApellido() != null ? request.getApellido() : apellidoActual;
            
            usuario.setNombreCompleto(nuevoNombre + " " + nuevoApellido);
        }
        
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        
        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirUsuarioAResponse(usuarioActualizado);
    }
    
    /**
     * Desactivar usuario
     */
    @Transactional
    public void desactivarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
    
    /**
     * Activar usuario
     */
    @Transactional
    public void activarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
    
    // ==================== ESTADÍSTICAS ====================
    
    /**
     * Obtener estadísticas generales para el dashboard
     */
    @Transactional(readOnly = true)
    public EstadisticasGeneralesResponse obtenerEstadisticasGenerales() {
        EstadisticasGeneralesResponse stats = new EstadisticasGeneralesResponse();

        // Usuarios
        stats.setTotalUsuarios(usuarioRepository.count());
        
        Long usuariosConPedidos = pedidoRepository.findAll().stream()
                .map(p -> p.getUsuario().getId())
                .distinct()
                .count();
        stats.setUsuariosActivos(usuariosConPedidos);
        
        Long moderadores = usuarioRepository.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getNombre() == RolNombre.MODERADOR))
                .count();
        stats.setTotalVendedores(moderadores);

        // Productos
        stats.setTotalProductos(productoRepository.count());
        stats.setProductosAprobados(productoRepository.count());
        stats.setProductosPendientes(0L);
        stats.setProductosStockBajo(productoRepository.countByStockLessThan(10));

        // Pedidos
        stats.setTotalPedidos(pedidoRepository.count());
        stats.setPedidosPendientes(pedidoRepository.countByEstado(EstadoPedido.PENDIENTE));
        stats.setPedidosEnPreparacion(pedidoRepository.countByEstado(EstadoPedido.EN_PREPARACION));
        stats.setPedidosEnviados(pedidoRepository.countByEstado(EstadoPedido.ENVIADO));
        stats.setPedidosEntregados(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO));
        stats.setPedidosCancelados(pedidoRepository.countByEstado(EstadoPedido.CANCELADO));

        // Ventas
        List<Pedido> pedidosEntregados = pedidoRepository
                .findByUsuarioIdAndEstado(null, EstadoPedido.ENTREGADO);

        BigDecimal totalVentas = pedidosEntregados.stream()
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalVentas(totalVentas);

        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = LocalDate.now().plusDays(1).atStartOfDay();
        List<Pedido> pedidosMes = pedidoRepository.findByFechaPedidoBetween(inicioMes, finMes);
        BigDecimal ventasMes = pedidosMes.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO)
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setVentasMes(ventasMes);

        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().plusDays(1).atStartOfDay();
        List<Pedido> pedidosHoy = pedidoRepository.findByFechaPedidoBetween(inicioHoy, finHoy);
        BigDecimal ventasHoy = pedidosHoy.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO)
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setVentasHoy(ventasHoy);

        if (stats.getTotalPedidos() > 0) {
            stats.setTicketPromedio(
                    totalVentas.divide(
                            BigDecimal.valueOf(stats.getTotalPedidos()),
                            2,
                            RoundingMode.HALF_UP));
        } else {
            stats.setTicketPromedio(BigDecimal.ZERO);
        }

        // Reviews
        stats.setTotalReviews(reviewRepository.count());
        stats.setReviewsPendientes(reviewRepository.countByAprobadoFalse());
        
        Double calificacionPromedio = reviewRepository
                .findAll()
                .stream()
                .filter(r -> r.getAprobado())
                .mapToDouble(r -> r.getCalificacion())
                .average()
                .orElse(0.0);
        stats.setCalificacionPromedioGeneral(calificacionPromedio);

        // Productos más vendidos
        List<Object[]> productosMasVendidos = itemPedidoRepository.findProductosMasVendidos(
                PageRequest.of(0, 10)
        );

        List<EstadisticasGeneralesResponse.ProductoMasVendido> topProductos = productosMasVendidos.stream()
                .limit(5)
                .map(row -> {
                    String productoNombre = "Producto";
                    Long cantidadVendida = ((Number) row[1]).longValue();
                    BigDecimal ingresos = BigDecimal.ZERO;

                    return new EstadisticasGeneralesResponse.ProductoMasVendido(
                            productoNombre,
                            cantidadVendida,
                            ingresos);
                })
                .collect(Collectors.toList());

        stats.setProductosMasVendidos(topProductos);

        // Últimos pedidos
        List<Pedido> ultimosPedidos = pedidoRepository
                .findAll(PageRequest.of(0, 10))
                .getContent();

        List<EstadisticasGeneralesResponse.UltimoPedido> ultimos = ultimosPedidos.stream()
                .map(p -> new EstadisticasGeneralesResponse.UltimoPedido(
                        p.getNumeroOrden(),
                        p.getUsuario().getNombreCompleto(),
                        p.getTotal(),
                        p.getEstado().toString()))
                .collect(Collectors.toList());

        stats.setUltimosPedidos(ultimos);

        return stats;
    }
    
    // ==================== CONVERSORES ====================
    
    private UserResponse convertirUsuarioAResponse(Usuario usuario) {
        UserResponse response = new UserResponse();
        response.setId(usuario.getId());
        response.setNombreUsuario(usuario.getNombreUsuario());
        response.setCorreo(usuario.getCorreo());
        response.setNombreCompleto(usuario.getNombreCompleto());
        response.setTelefono(usuario.getTelefono());
        response.setActivo(usuario.getActivo());
        response.setFechaCreacion(usuario.getFechaCreacion());
        response.setFechaActualizacion(usuario.getFechaActualizacion());
        
        // Parsear nombreCompleto en nombre y apellido
        String[] partes = usuario.getNombreCompleto().split(" ", 2);
        if (partes.length == 2) {
            response.setNombre(partes[0]);
            response.setApellido(partes[1]);
        } else {
            response.setNombre(usuario.getNombreCompleto());
            response.setApellido("");
        }
        
        // Roles
        Set<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toSet());
        response.setRoles(roles);
        
        return response;
    }
}