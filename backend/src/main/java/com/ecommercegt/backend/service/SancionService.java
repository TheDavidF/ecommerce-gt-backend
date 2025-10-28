package com.ecommercegt.backend.service;

import com.ecommercegt.backend.models.entidades.Sancion;
import com.ecommercegt.backend.models.entidades.Usuario;
import com.ecommercegt.backend.repositorios.SancionRepository;
import com.ecommercegt.backend.repositorios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SancionService {
    private final SancionRepository sancionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Sancion crearSancion(UUID usuarioId, UUID moderadorId, String razon, LocalDateTime fechaFin) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Usuario moderador = usuarioRepository.findById(moderadorId).orElseThrow();
        Sancion sancion = new Sancion();
        sancion.setUsuario(usuario);
        sancion.setModerador(moderador);
        sancion.setRazon(razon);
        sancion.setFechaInicio(LocalDateTime.now());
        sancion.setFechaFin(fechaFin);
        sancion.setActiva(true);
        sancion.setFechaCreacion(LocalDateTime.now());
        return sancionRepository.save(sancion);
    }

    @Transactional(readOnly = true)
    public Page<Sancion> listarSancionesPorUsuario(UUID usuarioId, Pageable pageable) {
        return sancionRepository.findByUsuarioId(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Sancion> listarSancionesPorModerador(UUID moderadorId, Pageable pageable) {
        return sancionRepository.findByModeradorId(moderadorId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Sancion> listarSancionesActivas(Pageable pageable) {
        return sancionRepository.findByActivaTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Sancion> listarTodasLasSanciones(Pageable pageable) {
        return sancionRepository.findAll(pageable);
    }

    @Transactional
    public void desactivarSancion(Integer id) {
        Optional<Sancion> sancionOpt = sancionRepository.findById(id);
        if (sancionOpt.isPresent()) {
            Sancion sancion = sancionOpt.get();
            sancion.setActiva(false);
            sancionRepository.save(sancion);
        }
    }
}
