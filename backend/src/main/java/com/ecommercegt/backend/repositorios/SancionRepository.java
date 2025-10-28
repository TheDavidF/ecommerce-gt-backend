package com.ecommercegt.backend.repositorios;


import com.ecommercegt.backend.models.entidades.Sancion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Integer> {
    Page<Sancion> findByUsuarioId(UUID usuarioId, Pageable pageable);

    Page<Sancion> findByModeradorId(UUID moderadorId, Pageable pageable);

    Page<Sancion> findByActivaTrue(Pageable pageable);
}
