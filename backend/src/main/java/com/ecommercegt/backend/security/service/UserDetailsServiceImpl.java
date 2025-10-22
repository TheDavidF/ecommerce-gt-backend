package com.ecommercegt.backend.security.service;

import com.ecommercegt.backend.models.entidades.Usuario;
import com.ecommercegt.backend.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Carga un usuario por nombre de usuario
     * Usado por Spring Security para autenticación
     * @param username - Nombre de usuario
     * @return UserDetails con información del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con nombre: " + username));
        
        return UserDetailsImpl.build(usuario);
    }
}