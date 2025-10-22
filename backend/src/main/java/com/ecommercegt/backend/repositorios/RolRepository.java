package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Rol;
import com.ecommercegt.backend.models.enums.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    Optional<Rol> findByNombre(RolNombre nombre);
}