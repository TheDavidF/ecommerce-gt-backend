package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.ActualizarCantidadRequest;
import com.ecommercegt.backend.dto.request.ItemCarritoRequest;
import com.ecommercegt.backend.dto.response.CarritoResponse;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CarritoController {
    
    @Autowired
    private CarritoService carritoService;
    
    /**
     * Agregar item al carrito
     * POST /api/carrito/items
     */
    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> agregarItem(@Valid @RequestBody ItemCarritoRequest request) {
        try {
            CarritoResponse response = carritoService.agregarItem(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al agregar item al carrito: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener mi carrito
     * GET /api/carrito
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtenerMiCarrito() {
        try {
            CarritoResponse response = carritoService.obtenerMiCarrito();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Si no tiene carrito, devolver carrito vacío
            Map<String, Object> carritoVacio = new HashMap<>();
            carritoVacio.put("items", new Object[]{});
            carritoVacio.put("total", BigDecimal.ZERO);
            carritoVacio.put("cantidadTotalItems", 0);
            carritoVacio.put("vacio", true);
            return ResponseEntity.ok(carritoVacio);
        }
    }
    
    /**
     * Actualizar cantidad de un item
     * PUT /api/carrito/items/{id}
     */
    @PutMapping("/items/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> actualizarCantidadItem(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarCantidadRequest request) {
        try {
            CarritoResponse response = carritoService.actualizarCantidadItem(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar cantidad: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar item del carrito
     * DELETE /api/carrito/items/{id}
     */
    @DeleteMapping("/items/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> eliminarItem(@PathVariable Integer id) {
        try {
            CarritoResponse response = carritoService.eliminarItem(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar item: " + e.getMessage()));
        }
    }
    
    /**
     * Limpiar carrito (eliminar todos los items)
     * DELETE /api/carrito/limpiar
     */
    @DeleteMapping("/limpiar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> limpiarCarrito() {
        try {
            carritoService.limpiarCarrito();
            return ResponseEntity.ok(new MessageResponse("Carrito limpiado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al limpiar carrito: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener total del carrito
     * GET /api/carrito/total
     */
    @GetMapping("/total")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtenerTotal() {
        try {
            BigDecimal total = carritoService.calcularTotal();
            Map<String, Object> response = new HashMap<>();
            response.put("total", total);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("total", BigDecimal.ZERO);
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Obtener cantidad de items en el carrito
     * GET /api/carrito/cantidad
     */
    @GetMapping("/cantidad")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtenerCantidadItems() {
        try {
            Integer cantidad = carritoService.contarItems();
            Map<String, Object> response = new HashMap<>();
            response.put("cantidad", cantidad);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("cantidad", 0);
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Verificar disponibilidad de stock
     * GET /api/carrito/verificar-stock
     */
    @GetMapping("/verificar-stock")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> verificarStock() {
        try {
            boolean stockDisponible = carritoService.verificarStockDisponible();
            Map<String, Object> response = new HashMap<>();
            response.put("stockDisponible", stockDisponible);
            response.put("mensaje", stockDisponible ? 
                    "Todos los productos tienen stock disponible" : 
                    "Algunos productos no tienen stock suficiente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al verificar stock: " + e.getMessage()));
        }
    }
}