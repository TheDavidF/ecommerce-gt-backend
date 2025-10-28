package com.ecommercegt.backend.config;

import com.ecommercegt.backend.security.jwt.AuthEntryPointJwt;
import com.ecommercegt.backend.security.jwt.AuthTokenFilter;
import com.ecommercegt.backend.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Para @PreAuthorize
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Value("${cors.allowed-origins:http://localhost:5173,https://*.ngrok-free.dev,https://*.netlify.app}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.allowed-headers:Authorization,Content-Type,X-Requested-With}")
    private String allowedHeaders;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // ============================================
    // CONFIGURACIÓN DE CORS
    // ============================================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

    // Patrones de origen permitidos desde variable de entorno (permite subdominios dinámicos)
    configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));

        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos (incluye ngrok-skip-browser-warning)
        List<String> headers = Arrays.asList(allowedHeaders.split(","));
        if (!headers.contains("ngrok-skip-browser-warning")) {
            headers = new java.util.ArrayList<>(headers);
            headers.add("ngrok-skip-browser-warning");
        }
        configuration.setAllowedHeaders(headers);

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Headers expuestos
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ============================================
                // HABILITAR CORS
                // ============================================
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ============================================
                        // ENDPOINTS PÚBLICOS (NO REQUIEREN TOKEN)
                        // ============================================

                        // Autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/public").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Endpoint de prueba moderador (temporal)
                        .requestMatchers("/api/moderador/test").permitAll()
                        .requestMatchers("/api/moderador/test-service").permitAll()

                        // Categorías - TODOS los GET son públicos
                        .requestMatchers(HttpMethod.GET, "/api/categorias").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()

                        // Productos - TODOS los GET son públicos
                        .requestMatchers(HttpMethod.GET, "/api/productos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

                        // Búsqueda - PÚBLICO
                        .requestMatchers(HttpMethod.GET, "/api/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/search/**").permitAll()

                        // Reviews públicas - Solo GET de reviews y estadísticas
                        .requestMatchers(HttpMethod.GET, "/api/reviews/producto/**").permitAll()

                        // ============================================
                        // ENDPOINTS DE MODERADOR (TEMPORALMENTE PÚBLICOS PARA TESTING)
                        // ============================================
                        .requestMatchers("/api/moderador/**").permitAll()

                        // ============================================
                        // ENDPOINTS DE ADMIN (TEMPORALMENTE PÚBLICOS PARA TESTING)
                        // ============================================
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/api/reportes/**").permitAll()

                        // ============================================
                        // ENDPOINTS DE LOGISTICA (TEMPORALMENTE PÚBLICOS PARA TESTING)
                        // ============================================
                        .requestMatchers("/api/pedidos/en-curso").permitAll()
                        .requestMatchers("/api/pedidos/proximos-vencer").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/*/fecha-entrega").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/*/marcar-entregado").permitAll()

                        // ============================================
                        // TODO LO DEMAS REQUIERE AUTENTICACION
                        // ============================================
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}