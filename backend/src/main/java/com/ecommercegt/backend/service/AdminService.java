package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.CreateUserRequest;
import com.ecommercegt.backend.dto.request.UpdateUserRequest;
import com.ecommercegt.backend.dto.response.EstadisticasGeneralesResponse;
import com.ecommercegt.backend.dto.response.ProductoPopularResponse;
import com.ecommercegt.backend.dto.response.UsuariosPorRolResponse;
import com.ecommercegt.backend.dto.response.VentaDelDiaResponse;
import com.ecommercegt.backend.dto.response.UserResponse;
import com.ecommercegt.backend.models.entidades.Pedido;
import com.ecommercegt.backend.models.entidades.Producto;
import com.ecommercegt.backend.models.entidades.Rol;
import com.ecommercegt.backend.models.entidades.Usuario;
import com.ecommercegt.backend.models.enums.EstadoPedido;
import com.ecommercegt.backend.models.enums.EstadoProducto;
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
import java.util.ArrayList;
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
        EstadisticasGeneralesResponse estadisticas = new EstadisticasGeneralesResponse();

        try {
            // ==================== USUARIOS ====================
            long totalUsuarios = usuarioRepository.count();
            long usuariosActivos = usuarioRepository.countByActivo(true);

            estadisticas.setTotalUsuarios(totalUsuarios);
            estadisticas.setUsuariosActivos(usuariosActivos);

            // ==================== PRODUCTOS ====================
            long totalProductos = productoRepository.count();
            long productosPendientes = 0L;

            try {
                productosPendientes = productoRepository.countByEstado(EstadoProducto.PENDIENTE_REVISION);
            } catch (Exception e) {
                System.err.println("Error al contar productos pendientes: " + e.getMessage());
            }

            estadisticas.setTotalProductos(totalProductos);
            estadisticas.setProductosPendientes(productosPendientes);

            // ==================== PEDIDOS ====================
            long totalPedidos = pedidoRepository.count();

            // ✅ Contar pedidos pendientes (usando ENUM)
            long pedidosPendientes = 0L;
            try {
                List<EstadoPedido> estadosPendientes = List.of(
                        EstadoPedido.PENDIENTE,
                        EstadoPedido.CONFIRMADO,
                        EstadoPedido.EN_PREPARACION);
                pedidosPendientes = pedidoRepository.countByEstadoIn(estadosPendientes);
            } catch (Exception e) {
                System.err.println("Error al contar pedidos pendientes: " + e.getMessage());
            }

            estadisticas.setTotalPedidos(totalPedidos);
            estadisticas.setPedidosPendientes(pedidosPendientes);

            // ==================== VENTAS ====================
            BigDecimal totalVentas = pedidoRepository.sumMontoTotal();
            if (totalVentas == null) {
                totalVentas = BigDecimal.ZERO;
            }
            estadisticas.setTotalVentas(totalVentas);

            // Ventas del mes actual
            LocalDateTime inicioMes = LocalDateTime.now()
                    .withDayOfMonth(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);

            BigDecimal ventasMes = pedidoRepository.sumMontoTotalByFechaPedidoAfter(inicioMes);
            if (ventasMes == null) {
                ventasMes = BigDecimal.ZERO;
            }
            estadisticas.setVentasMes(ventasMes);

            // ==================== PRODUCTOS POPULARES ====================
            List<ProductoPopularResponse> productosPopulares = new ArrayList<>();

            try {
                List<Object[]> productosData = itemPedidoRepository
                        .findProductosMasVendidos(PageRequest.of(0, 10));

                for (Object[] row : productosData) {
                    // row[0] = UUID id
                    // row[1] = String nombre
                    // row[2] = Long total_vendido
                    // row[3] = BigDecimal precio
                    // row[4] = String imagen_url (puede ser null)

                    UUID productoId = (UUID) row[0];
                    String nombre = (String) row[1];
                    Long cantidadVendida = ((Number) row[2]).longValue();
                    BigDecimal precio = (BigDecimal) row[3];
                    String imagenUrl = row[4] != null ? row[4].toString() : null;

                    ProductoPopularResponse dto = new ProductoPopularResponse();
                    dto.setId(productoId);
                    dto.setNombre(nombre);
                    dto.setPrecio(precio);
                    dto.setCantidadVendida(cantidadVendida);
                    dto.setImagenUrl(imagenUrl);

                    productosPopulares.add(dto);
                }
            } catch (Exception e) {
                System.err.println("Error al obtener productos populares: " + e.getMessage());
                e.printStackTrace();
            }

            estadisticas.setProductosPopulares(productosPopulares);

            // ==================== USUARIOS POR ROL ====================
            List<UsuariosPorRolResponse> usuariosPorRol = new ArrayList<>();

            try {
                List<Object[]> rolesData = usuarioRepository.countUsuariosPorRol();

                for (Object[] row : rolesData) {
                    UsuariosPorRolResponse dto = new UsuariosPorRolResponse();
                    dto.setRol(row[0].toString()); 
                    dto.setCantidad(((Number) row[1]).longValue());

                    usuariosPorRol.add(dto);
                }
            } catch (Exception e) {
                System.err.println("Error al obtener usuarios por rol: " + e.getMessage());
                e.printStackTrace();
            }

            estadisticas.setUsuariosPorRol(usuariosPorRol);

            // ==================== VENTAS DEL MES ====================
            List<VentaDelDiaResponse> ventasDelMes = new ArrayList<>();

            try {
                List<Object[]> ventasData = pedidoRepository.findVentasPorDiaDelMes(inicioMes);

                for (Object[] row : ventasData) {
                    // Manejar fecha
                    String fecha = String.valueOf(row[0]);

                    // Manejar total
                    BigDecimal total;
                    if (row[1] instanceof BigDecimal) {
                        total = (BigDecimal) row[1];
                    } else if (row[1] instanceof Number) {
                        total = new BigDecimal(row[1].toString());
                    } else {
                        total = new BigDecimal(String.valueOf(row[1]));
                    }

                    VentaDelDiaResponse dto = new VentaDelDiaResponse();
                    dto.setFecha(fecha);
                    dto.setTotal(total);

                    ventasDelMes.add(dto);
                }
            } catch (Exception e) {
                System.err.println("Error al obtener ventas del mes: " + e.getMessage());
                e.printStackTrace();
            }

            estadisticas.setVentasDelMes(ventasDelMes);

            return estadisticas;

        } catch (Exception e) {
            System.err.println("Error general en obtenerEstadisticasGenerales: " + e.getMessage());
            e.printStackTrace();

            // Retornar estadísticas vacías
            return new EstadisticasGeneralesResponse();
        }
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