package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.ActualizarEstadoRequest;
import com.ecommercegt.backend.dto.request.CrearPedidoRequest;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.dto.response.PedidoResponse;
import com.ecommercegt.backend.dto.response.ResumenPedidosResponse;
import com.ecommercegt.backend.service.PedidoService;
import com.ecommercegt.backend.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ecommercegt.backend.dto.request.ModificarFechaEntregaRequest;
import java.util.List;
import java.util.UUID;

/**
 * Controller de Pedidos
 * Gestiona endpoints para crear, consultar y actualizar pedidos
 */
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Crear pedido desde el carrito del usuario autenticado
     * POST /api/pedidos/crear-desde-carrito
     * 
     * Proceso:
     * 1. Valida stock de todos los productos
     * 2. Crea el pedido con snapshot de datos
     * 3. Reduce stock automáticamente
     * 4. Limpia el carrito
     * 
     * Requiere autenticación (cualquier usuario)
     */
    @PostMapping("/crear-desde-carrito")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> crearPedidoDesdeCarrito(@Valid @RequestBody CrearPedidoRequest request) {
        try {
            PedidoResponse pedido = pedidoService.crearPedidoDesdeCarrito(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al crear pedido: " + e.getMessage()));
        }
    }

    /**
     * Obtener mis pedidos (usuario autenticado)
     * GET /api/pedidos
     * 
     * Paginación:
     * - page: número de página (default 0)
     * - size: tamaño de página (default 10)
     * 
     * Requiere autenticación
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PedidoResponse>> obtenerMisPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoResponse> pedidos = pedidoService.obtenerMisPedidos(pageable);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtener detalle de un pedido
     * GET /api/pedidos/{id}
     * 
     * Valida que el pedido pertenezca al usuario autenticado
     * 
     * Requiere autenticación
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtenerDetallePedido(@PathVariable UUID id) {
        try {
            PedidoResponse pedido = pedidoService.obtenerDetallePedido(id);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Actualizar estado de un pedido
     * PUT /api/pedidos/{id}/estado
     * 
     * Solo VENDEDOR, MODERADOR o ADMIN pueden actualizar estados
     * 
     * Transiciones válidas:
     * PENDIENTE → CONFIRMADO
     * CONFIRMADO → EN_PREPARACION
     * EN_PREPARACION → ENVIADO
     * ENVIADO → ENTREGADO
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyAuthority('VENDEDOR', 'MODERADOR', 'ADMIN')")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        try {
            PedidoResponse pedido = pedidoService.actualizarEstado(id, request);
            // Notificar al usuario si el pedido existe y tiene usuario
            if (pedido.getUsuarioId() != null && pedido.getNumeroOrden() != null && pedido.getEstado() != null) {
                notificacionService.notificarCambioEstadoPedido(pedido.getUsuarioId(), pedido.getNumeroOrden(), pedido.getEstado().name());
            }
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar estado: " + e.getMessage()));
        }
    }

    /**
     * Cancelar un pedido
     * DELETE /api/pedidos/{id}/cancelar
     * 
     * Solo se puede cancelar si está en estado PENDIENTE o CONFIRMADO
     * Devuelve el stock a los productos
     * 
     * Requiere autenticación (el dueño del pedido)
     */
    @DeleteMapping("/{id}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelarPedido(
            @PathVariable UUID id,
            @RequestBody(required = false) String motivo) {
        try {
            String motivoCancelacion = motivo != null ? motivo : "Cancelado por el cliente";
            PedidoResponse pedido = pedidoService.cancelarPedido(id, motivoCancelacion);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al cancelar pedido: " + e.getMessage()));
        }
    }

    /**
     * Obtener pedidos que contengan mis productos (como vendedor)
     * GET /api/pedidos/vendedor
     * 
     * Retorna pedidos donde aparecen productos del vendedor autenticado
     * 
     * Requiere rol VENDEDOR
     */
    @GetMapping("/vendedor")
    @PreAuthorize("hasAuthority('VENDEDOR')")
    public ResponseEntity<Page<PedidoResponse>> obtenerPedidosVendedor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoResponse> pedidos = pedidoService.obtenerPedidosVendedor(pageable);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtener todos los pedidos (solo ADMIN)
     * GET /api/pedidos/admin/todos
     * 
     * Requiere rol ADMIN
     */
    @GetMapping("/admin/todos")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<PedidoResponse>> obtenerTodosPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoResponse> pedidos = pedidoService.obtenerTodosPedidos(pageable);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtener resumen de pedidos del usuario
     * GET /api/pedidos/resumen
     * 
     * Estadísticas:
     * - Total pedidos por estado
     * - Total compras realizadas
     * 
     * Requiere autenticación
     */
    @GetMapping("/resumen")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResumenPedidosResponse> obtenerResumenPedidos() {
        ResumenPedidosResponse resumen = pedidoService.obtenerResumenPedidos();
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/en-curso")
    @PreAuthorize("hasAnyAuthority('LOGISTICA', 'ADMIN')")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosEnCurso() {
        List<PedidoResponse> pedidos = pedidoService.obtenerPedidosEnCurso();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/proximos-vencer")
    @PreAuthorize("hasAnyAuthority('LOGISTICA', 'ADMIN')")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosProximosVencer() {
        List<PedidoResponse> pedidos = pedidoService.obtenerPedidosProximosVencer();
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}/fecha-entrega")
    @PreAuthorize("hasAnyAuthority('LOGISTICA', 'ADMIN')")
    public ResponseEntity<?> modificarFechaEntrega(
            @PathVariable UUID id,
            @Valid @RequestBody ModificarFechaEntregaRequest request) {
        try {
            PedidoResponse pedido = pedidoService.modificarFechaEntrega(
                    id,
                    request.getFechaEntregaEstimada());
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al modificar fecha: " + e.getMessage()));
        }
    }

    /**
     * Marcar pedido como entregado
     * PUT /api/pedidos/{id}/marcar-entregado
     * 
     * Cambia el estado a ENTREGADO y registra la fecha actual como fecha de entrega
     * Solo puede aplicarse a pedidos en estado EN_CAMINO
     * 
     * Requiere rol LOGISTICA o ADMIN
     */
    @PutMapping("/{id}/marcar-entregado")
    @PreAuthorize("hasAnyAuthority('LOGISTICA', 'ADMIN')")
    public ResponseEntity<?> marcarComoEntregado(@PathVariable UUID id) {
        try {
            PedidoResponse pedido = pedidoService.marcarComoEntregado(id);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al marcar como entregado: " + e.getMessage()));
        }
    }
}