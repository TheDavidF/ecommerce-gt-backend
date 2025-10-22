package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.LoginRequest;
import com.ecommercegt.backend.dto.request.RegisterRequest;
import com.ecommercegt.backend.dto.response.JwtResponse;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Endpoint para login de usuarios
     * POST /api/auth/login
     * @param loginRequest - Credenciales (nombreUsuario, contrasena)
     * @return JwtResponse con token y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Credenciales inválidas - " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint para registro de nuevos usuarios
     * POST /api/auth/register
     * @param registerRequest - Datos del nuevo usuario
     * @return MessageResponse con resultado del registro
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            MessageResponse response = authService.register(registerRequest);
            
            if (response.getMessage().startsWith("Error")) {
                return ResponseEntity.badRequest().body(response);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al registrar usuario: " + e.getMessage()));
        }
    }
}