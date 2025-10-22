package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.LoginRequest;
import com.ecommercegt.backend.dto.request.RegisterRequest;
import com.ecommercegt.backend.dto.response.JwtResponse;
import com.ecommercegt.backend.dto.response.MessageResponse;
import com.ecommercegt.backend.models.entidades.Rol;
import com.ecommercegt.backend.models.entidades.Usuario;
import com.ecommercegt.backend.models.enums.RolNombre;
import com.ecommercegt.backend.repositorios.RolRepository;
import com.ecommercegt.backend.repositorios.UsuarioRepository;
import com.ecommercegt.backend.security.jwt.JwtUtils;
import com.ecommercegt.backend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * Autentica un usuario y genera un token JWT
     * @param loginRequest - Credenciales del usuario
     * @return JwtResponse con token y datos del usuario
     */
    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest loginRequest) {
        // Autenticar con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getNombreUsuario(),
                        loginRequest.getContrasena()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generar JWT
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // Obtener detalles del usuario autenticado
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        
        return new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );
    }
    
    /**
     * Registra un nuevo usuario en el sistema
     * @param registerRequest - Datos del nuevo usuario
     * @return MessageResponse con mensaje de éxito o error
     */
    @Transactional
    public MessageResponse register(RegisterRequest registerRequest) {
        // Validar que el nombre de usuario no exista
        if (usuarioRepository.existsByNombreUsuario(registerRequest.getNombreUsuario())) {
            return new MessageResponse("Error: El nombre de usuario ya está en uso");
        }
        
        // Validar que el correo no exista
        if (usuarioRepository.existsByCorreo(registerRequest.getCorreo())) {
            return new MessageResponse("Error: El correo electrónico ya está registrado");
        }
        
        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(registerRequest.getNombreUsuario());
        usuario.setCorreo(registerRequest.getCorreo());
        usuario.setContrasenaHash(passwordEncoder.encode(registerRequest.getContrasena()));
        usuario.setNombreCompleto(registerRequest.getNombreCompleto());
        usuario.setTelefono(registerRequest.getTelefono());
        usuario.setDireccion(registerRequest.getDireccion());
        usuario.setActivo(true);
        
        // Asignar roles
        Set<String> strRoles = registerRequest.getRoles();
        Set<Rol> roles = new HashSet<>();
        
        if (strRoles == null || strRoles.isEmpty()) {
            // Si no se especifican roles, asignar rol COMUN por defecto
            Rol userRole = rolRepository.findByNombre(RolNombre.COMUN)
                    .orElseThrow(() -> new RuntimeException("Error: Rol COMUN no encontrado en la base de datos"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                        Rol adminRole = rolRepository.findByNombre(RolNombre.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado"));
                        roles.add(adminRole);
                        break;
                    case "MODERADOR":
                        Rol modRole = rolRepository.findByNombre(RolNombre.MODERADOR)
                                .orElseThrow(() -> new RuntimeException("Error: Rol MODERADOR no encontrado"));
                        roles.add(modRole);
                        break;
                    case "LOGISTICA":
                        Rol logRole = rolRepository.findByNombre(RolNombre.LOGISTICA)
                                .orElseThrow(() -> new RuntimeException("Error: Rol LOGISTICA no encontrado"));
                        roles.add(logRole);
                        break;
                    default:
                        Rol userRole = rolRepository.findByNombre(RolNombre.COMUN)
                                .orElseThrow(() -> new RuntimeException("Error: Rol COMUN no encontrado"));
                        roles.add(userRole);
                }
            });
        }
        
        usuario.setRoles(roles);
        
        // Guardar usuario en la base de datos
        usuarioRepository.save(usuario);
        
        return new MessageResponse("Usuario registrado exitosamente");
    }
}