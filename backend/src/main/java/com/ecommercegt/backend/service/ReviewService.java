package com.ecommercegt.backend.service;

import com.ecommercegt.backend.dto.request.ActualizarReviewRequest;
import com.ecommercegt.backend.dto.request.CrearReviewRequest;
import com.ecommercegt.backend.dto.request.VotarReviewRequest;
import com.ecommercegt.backend.dto.response.EstadisticasReviewsResponse;
import com.ecommercegt.backend.dto.response.ReviewResponse;
import com.ecommercegt.backend.dto.response.VotoResponse;
import com.ecommercegt.backend.models.entidades.*;
import com.ecommercegt.backend.models.enums.EstadoPedido;
import com.ecommercegt.backend.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de Reviews
 * Gestiona reseñas, votos y estadísticas
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private VotoUtilRepository votoUtilRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    /**
     * Crear una nueva review
     * 
     * Validaciones:
     * 1. El producto debe existir
     * 2. El usuario debe haber comprado el producto (pedido ENTREGADO)
     * 3. El usuario no debe tener review previa para ese producto
     * 4. La calificación debe estar entre 1 y 5
     */
    @Transactional
    public ReviewResponse crearReview(CrearReviewRequest request) {
        Usuario usuario = obtenerUsuarioAutenticado();

        // 1. Validar que el producto existe
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + request.getProductoId()));

        // 2. Validar que no tenga review previa
        if (reviewRepository.existsByProductoIdAndUsuarioId(request.getProductoId(), usuario.getId())) {
            throw new RuntimeException("Ya has dejado una reseña para este producto");
        }

        // 3. Verificar si el usuario compró el producto
        boolean compro = usuarioComproProducto(usuario.getId(), request.getProductoId());

        // 4. Crear review
        Review review = new Review();
        review.setProducto(producto);
        review.setUsuario(usuario);
        review.setCalificacion(request.getCalificacion());
        review.setTitulo(request.getTitulo());
        review.setComentario(request.getComentario());
        review.setVerificado(compro);
        review.setAprobado(false); // Requiere aprobación de moderador

        // Validar calificación
        review.validarCalificacion();

        // 5. Guardar review
        Review reviewGuardada = reviewRepository.save(review);

        // 6. Recalcular promedio del producto (solo si está aprobada, pero lo hacemos
        // preventivo)
        recalcularPromedioProducto(request.getProductoId());

        // 7. Retornar respuesta
        return convertirAResponse(reviewGuardada, usuario.getId());
    }

    /**
     * Actualizar una review existente
     * Solo el autor puede actualizar
     * La review debe pasar por moderación de nuevo
     */
    @Transactional
    public ReviewResponse actualizarReview(Long reviewId, ActualizarReviewRequest request) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada con ID: " + reviewId));

        // Validar que sea el autor
        if (!review.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Solo el autor puede actualizar esta review");
        }

        // Actualizar campos si se proporcionan
        if (request.getCalificacion() != null) {
            review.setCalificacion(request.getCalificacion());
            review.validarCalificacion();
        }

        if (request.getTitulo() != null && !request.getTitulo().isEmpty()) {
            review.setTitulo(request.getTitulo());
        }

        if (request.getComentario() != null && !request.getComentario().isEmpty()) {
            review.setComentario(request.getComentario());
        }

        // Requiere aprobación de nuevo
        review.setAprobado(false);
        review.setFechaAprobacion(null);
        review.setModerador(null);

        Review reviewActualizada = reviewRepository.save(review);

        // Recalcular promedio
        recalcularPromedioProducto(review.getProducto().getId());

        return convertirAResponse(reviewActualizada, usuario.getId());
    }

    /**
     * Eliminar una review
     * Solo el autor o ADMIN pueden eliminar
     */
    @Transactional
    public void eliminarReview(Long reviewId) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada con ID: " + reviewId));

        // Validar permisos (autor o ADMIN)
        boolean esAutor = review.getUsuario().getId().equals(usuario.getId());
        boolean esAdmin = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN"));

        if (!esAutor && !esAdmin) {
            throw new RuntimeException("No tienes permiso para eliminar esta review");
        }

        UUID productoId = review.getProducto().getId();

        // Eliminar votos asociados
        votoUtilRepository.deleteByReviewId(reviewId);

        // Eliminar review
        reviewRepository.delete(review);

        // Recalcular promedio
        recalcularPromedioProducto(productoId);
    }

    /**
     * Votar en una review
     * Un usuario puede votar una sola vez
     * Puede cambiar su voto
     * No puede votar su propia review
     */
    @Transactional
    public VotoResponse votarReview(Long reviewId, VotarReviewRequest request) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada con ID: " + reviewId));

        // Validar que no vote su propia review
        if (review.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No puedes votar tu propia reseña");
        }

        // Buscar voto existente
        Optional<VotoUtil> votoExistente = votoUtilRepository
                .findByReviewIdAndUsuarioId(reviewId, usuario.getId());

        if (votoExistente.isPresent()) {
            // Actualizar voto
            VotoUtil voto = votoExistente.get();
            voto.setEsUtil(request.getEsUtil());
            votoUtilRepository.save(voto);
        } else {
            // Crear nuevo voto
            VotoUtil nuevoVoto = new VotoUtil();
            nuevoVoto.setReview(review);
            nuevoVoto.setUsuario(usuario);
            nuevoVoto.setEsUtil(request.getEsUtil());
            votoUtilRepository.save(nuevoVoto);
        }

        // Calcular estadísticas actualizadas
        Long votosUtiles = votoUtilRepository.countVotosUtiles(reviewId);
        Long votosNoUtiles = votoUtilRepository.countVotosNoUtiles(reviewId);
        Long balance = votosUtiles - votosNoUtiles;

        return new VotoResponse(
                "Voto registrado exitosamente",
                reviewId,
                votosUtiles,
                votosNoUtiles,
                balance);
    }

    /**
     * Aprobar una review (MODERADOR/ADMIN)
     */
    @Transactional
    public ReviewResponse aprobarReview(Long reviewId) {
        Usuario moderador = obtenerUsuarioAutenticado();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada con ID: " + reviewId));

        review.aprobar(moderador);
        Review reviewAprobada = reviewRepository.save(review);

        // Recalcular promedio (ahora incluye esta review)
        recalcularPromedioProducto(review.getProducto().getId());

        return convertirAResponse(reviewAprobada, moderador.getId());
    }

    /**
     * Rechazar una review (MODERADOR/ADMIN)
     */
    @Transactional
    public ReviewResponse rechazarReview(Long reviewId) {
        Usuario moderador = obtenerUsuarioAutenticado();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada con ID: " + reviewId));

        review.rechazar();
        Review reviewRechazada = reviewRepository.save(review);

        // Recalcular promedio (excluye esta review)
        recalcularPromedioProducto(review.getProducto().getId());

        return convertirAResponse(reviewRechazada, moderador.getId());
    }

    /**
     * Obtener reviews de un producto (solo aprobadas)
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> obtenerReviewsProducto(UUID productoId, Pageable pageable) {
        Usuario usuarioActual = obtenerUsuarioAutenticadoOpcional();
        UUID usuarioId = usuarioActual != null ? usuarioActual.getId() : null;

        Page<Review> reviews = reviewRepository
                .findByProductoIdAndAprobadoTrueOrderByVotosUtiles(productoId, pageable);

        return reviews.map(review -> convertirAResponse(review, usuarioId));
    }

    /**
     * Obtener mis reviews
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> obtenerMisReviews(Pageable pageable) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Page<Review> reviews = reviewRepository
                .findByUsuarioIdOrderByFechaCreacionDesc(usuario.getId(), pageable);

        return reviews.map(review -> convertirAResponse(review, usuario.getId()));
    }

    /**
     * Obtener reviews pendientes de aprobación (MODERADOR/ADMIN)
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> obtenerReviewsPendientes(Pageable pageable) {
        Usuario moderador = obtenerUsuarioAutenticado();

        Page<Review> reviews = reviewRepository
                .findByAprobadoFalseOrderByFechaCreacionDesc(pageable);

        return reviews.map(review -> convertirAResponse(review, moderador.getId()));
    }

    /**
     * Obtener estadísticas de reviews de un producto
     */
    @Transactional(readOnly = true)
    public EstadisticasReviewsResponse obtenerEstadisticas(UUID productoId) {
        EstadisticasReviewsResponse estadisticas = new EstadisticasReviewsResponse();

        // Total de reviews
        Long totalReviews = reviewRepository.countByProductoIdAndAprobadoTrue(productoId);
        estadisticas.setTotalReviews(totalReviews);

        // Promedio de calificación
        Double promedio = reviewRepository.calcularPromedioCalificacion(productoId);
        estadisticas.setPromedioCalificacion(promedio != null ? promedio : 0.0);

        // Inicializar distribución
        estadisticas.inicializarDistribucion();

        // Distribución por calificación
        List<Object[]> distribucion = reviewRepository.contarPorCalificacion(productoId);
        for (Object[] fila : distribucion) {
            Integer calificacion = (Integer) fila[0];
            Long cantidad = (Long) fila[1];
            estadisticas.getDistribucionCalificaciones().put(calificacion, cantidad);
        }

        // Reviews verificadas
        Long verificadas = reviewRepository
                .countByProductoIdAndAprobadoTrueAndVerificadoTrue(productoId);
        estadisticas.setReviewsVerificadas(verificadas);

        // Calcular porcentajes
        estadisticas.calcularPorcentajeRecomendacion();
        estadisticas.calcularPorcentajeVerificadas();

        return estadisticas;
    }

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Verificar si el usuario compró el producto
     */
    private boolean usuarioComproProducto(UUID usuarioId, UUID productoId) {
        // Buscar en pedidos ENTREGADOS que contengan el producto
        List<Pedido> pedidosEntregados = pedidoRepository
                .findByUsuarioIdAndEstado(usuarioId, EstadoPedido.ENTREGADO);

        return pedidosEntregados.stream()
                .flatMap(pedido -> pedido.getItems().stream())
                .anyMatch(item -> item.getProductoId().equals(productoId)); // ✅ CORRECTO
    }

    /**
     * Recalcular promedio de calificación del producto
     */
    private void recalcularPromedioProducto(UUID productoId) {
        Double promedio = reviewRepository.calcularPromedioCalificacion(productoId);
        Long totalReviews = reviewRepository.countByProductoIdAndAprobadoTrue(productoId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setCalificacionPromedio(promedio != null ? promedio : 0.0);
        producto.setCantidadReviews(totalReviews != null ? totalReviews.intValue() : 0);

        productoRepository.save(producto);
    }

    /**
     * Obtener usuario autenticado
     */
    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nombreUsuario = authentication.getName();

        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + nombreUsuario));
    }

    /**
     * Obtener usuario autenticado opcional (para endpoints públicos)
     */
    private Usuario obtenerUsuarioAutenticadoOpcional() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getName().equals("anonymousUser")) {
                String nombreUsuario = authentication.getName();
                return usuarioRepository.findByNombreUsuario(nombreUsuario).orElse(null);
            }
        } catch (Exception e) {
            // Usuario no autenticado
        }
        return null;
    }

    /**
     * Convertir Review a ReviewResponse
     */
    private ReviewResponse convertirAResponse(Review review, UUID usuarioActualId) {
        ReviewResponse response = new ReviewResponse();

        response.setId(review.getId());
        response.setProductoId(review.getProducto().getId());
        response.setProductoNombre(review.getProducto().getNombre());
        response.setUsuarioId(review.getUsuario().getId());
        response.setUsuarioNombre(review.getUsuario().getNombreCompleto());
        response.setCalificacion(review.getCalificacion());
        response.setTitulo(review.getTitulo());
        response.setComentario(review.getComentario());

        // Contar votos
        Long votosUtiles = votoUtilRepository.countVotosUtiles(review.getId());
        Long votosNoUtiles = votoUtilRepository.countVotosNoUtiles(review.getId());
        response.setVotosUtiles(votosUtiles);
        response.setVotosNoUtiles(votosNoUtiles);
        response.setBalanceVotos(votosUtiles - votosNoUtiles);

        response.setVerificado(review.getVerificado());
        response.setAprobado(review.getAprobado());
        response.setFechaCreacion(review.getFechaCreacion());
        response.setFechaActualizacion(review.getFechaActualizacion());
        response.setFechaAprobacion(review.getFechaAprobacion());

        if (review.getModerador() != null) {
            response.setModeradorNombre(review.getModerador().getNombreCompleto());
        }

        // Permisos
        if (usuarioActualId != null) {
            response.setPuedeEditar(review.getUsuario().getId().equals(usuarioActualId));
            response.setPuedeEliminar(review.getUsuario().getId().equals(usuarioActualId));

            // Obtener voto del usuario actual
            Optional<VotoUtil> miVoto = votoUtilRepository
                    .findByReviewIdAndUsuarioId(review.getId(), usuarioActualId);
            response.setMiVoto(miVoto.map(VotoUtil::getEsUtil).orElse(null));
        } else {
            response.setPuedeEditar(false);
            response.setPuedeEliminar(false);
            response.setMiVoto(null);
        }

        return response;
    }
}