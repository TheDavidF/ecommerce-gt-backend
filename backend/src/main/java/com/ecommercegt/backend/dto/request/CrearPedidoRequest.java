package com.ecommercegt.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Request para crear un pedido desde el carrito
 * 
 * El usuario proporciona información de envío y pago.
 * El sistema:
 * 1. Obtiene el carrito del usuario
 * 2. Valida stock
 * 3. Crea el pedido
 * 4. Reduce stock
 * 5. Limpia el carrito
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoRequest {
    
    /**
     * Dirección completa de envío
     * Ejemplo: "5ta Avenida 10-50 Zona 1, Ciudad de Guatemala, Guatemala"
     */
    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(min = 10, max = 500, message = "La dirección debe tener entre 10 y 500 caracteres")
    private String direccionEnvio;
    
    /**
     * Teléfono de contacto para la entrega
     * Ejemplo: "+502 5555-1234"
     */
    @NotBlank(message = "El teléfono de contacto es obligatorio")
    @Size(min = 8, max = 20, message = "El teléfono debe tener entre 8 y 20 caracteres")
    private String telefonoContacto;
    
    /**
     * Método de pago elegido por el cliente
     * Valores posibles: EFECTIVO, TRANSFERENCIA, TARJETA, WALLET
     */
    @NotBlank(message = "El método de pago es obligatorio")
    @Size(max = 50, message = "El método de pago no puede exceder 50 caracteres")
    private String metodoPago;
    
    /**
     * Notas adicionales del cliente (opcional)
     * Ejemplo: "Entregar en horario de oficina (9am-5pm)"
     */
    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notas;
}