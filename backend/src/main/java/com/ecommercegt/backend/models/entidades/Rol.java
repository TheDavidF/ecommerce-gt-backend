package com.ecommercegt.backend.models.entidades;

import com.ecommercegt.backend.models.enums.RolNombre;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", unique = true, nullable = false, length = 50)
    private RolNombre nombre;
    
    public Rol(RolNombre nombre) {
        this.nombre = nombre;
    }
}
