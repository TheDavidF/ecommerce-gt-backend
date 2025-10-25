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

    /**
     * Contar usuarios registrados hoy (SQL nativo)
     */
    @Query(value = "SELECT COUNT(*) FROM usuarios WHERE DATE(fecha_creacion) = CURRENT_DATE", nativeQuery = true)
    Long countUsuariosRegistradosHoy();

    /**
     * Contar usuarios registrados esta semana (SQL nativo)
     */
    @Query(value = "SELECT COUNT(*) FROM usuarios " +
            "WHERE fecha_creacion >= CURRENT_DATE - INTERVAL '7 days'", nativeQuery = true)
    Long countUsuariosRegistradosEstaSemana();

    /**
     * Contar vendedores (SQL nativo)
     */
    @Query(value = "SELECT COUNT(DISTINCT u.id) FROM usuarios u " +
            "INNER JOIN usuario_roles ur ON u.id = ur.usuario_id " +
            "INNER JOIN roles r ON ur.rol_id = r.id " +
            "WHERE r.nombre = 'VENDEDOR'", nativeQuery = true)
    Long countVendedores();
}