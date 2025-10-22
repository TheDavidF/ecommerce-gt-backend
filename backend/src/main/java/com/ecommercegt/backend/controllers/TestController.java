package com.ecommercegt.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Este endpoint es PÚBLICO - No requiere autenticación";
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasAuthority('COMUN')")
    public String userEndpoint() {
        return "Este endpoint requiere estar autenticado con rol COMUN";
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminEndpoint() {
        return "Este endpoint requiere rol ADMIN";
    }
}