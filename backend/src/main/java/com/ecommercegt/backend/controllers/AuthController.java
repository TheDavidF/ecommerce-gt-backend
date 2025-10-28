package com.ecommercegt.backend.controllers;

import com.ecommercegt.backend.dto.request.LoginRequest;
import com.ecommercegt.backend.dto.request.RegisterRequest;
import com.ecommercegt.backend.dto.response.JwtResponse;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.service.AuthService;
import com.ecommercegt.backend.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

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
     * Endpoint para obtener el usuario actualmente autenticado
     * GET /api/auth/me
     * @return Datos del usuario autenticado
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new JwtResponse(
                null, // No devolver token en este endpoint
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList())
        ));
    }
}