package com.ecommercegt.backend.controllers;  // ← Correcto

import com.ecommercegt.backend.dto.response.EstadisticasGeneralesResponse;
import com.ecommercegt.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController                                    // ← DEBE ESTAR
@RequestMapping("/api/admin")                      // ← DEBE ESTAR
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerEstadisticas() {
        EstadisticasGeneralesResponse estadisticas = adminService.obtenerEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }
}