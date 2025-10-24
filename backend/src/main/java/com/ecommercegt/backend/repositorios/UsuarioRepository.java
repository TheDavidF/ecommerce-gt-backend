package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByCorreo(String correo);

    Boolean existsByNombreUsuario(String nombreUsuario);

    Boolean existsByCorreo(String correo);

    /**
     * Contar usuarios por rol
     */
    @Query("SELECT COUNT(u) FROM Usuario u JOIN u.roles r WHERE r.nombre = :rolNombre")
    Long countUsuariosByRolNombre(@Param("rolNombre") String rolNombre);
}