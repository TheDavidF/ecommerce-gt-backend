package com.ecommercegt.backend.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommercegt.backend.models.entidades.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UsuarioReporteRepository extends JpaRepository<Usuario, UUID> {
    
    /**
     * Top 5 clientes que más ganancias han generado por compras
     * Retorna: usuario_id, nombre_completo, total_gastado, cantidad_pedidos
     */
    @Query(value = "SELECT " +
            "u.id, " +
            "u.nombre_completo, " +
            "COALESCE(SUM(p.monto_total), 0) as total_gastado, " +
            "COUNT(p.id) as cantidad_pedidos " +
            "FROM usuarios u " +
            "LEFT JOIN pedidos p ON u.id = p.usuario_id " +
            "WHERE p.estado = 'ENTREGADO' " +
            "AND p.fecha_pedido BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY u.id, u.nombre_completo " +
            "ORDER BY total_gastado DESC " +
            "LIMIT 5", 
            nativeQuery = true)
    List<Object[]> findTop5ClientesPorGanancias(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    /**
     * Top 5 clientes que más productos han vendido
     * Retorna: usuario_id, nombre_completo, total_productos_vendidos, ingresos_generados
     */
    @Query(value = "SELECT " +
            "u.id, " +
            "u.nombre_completo, " +
            "COALESCE(SUM(ip.cantidad), 0) as total_productos_vendidos, " +
            "COALESCE(SUM(ip.subtotal), 0) as ingresos_generados " +
            "FROM usuarios u " +
            "INNER JOIN productos prod ON u.id = prod.vendedor_id " +
            "INNER JOIN items_pedido ip ON prod.id = ip.producto_id " +
            "INNER JOIN pedidos p ON ip.pedido_id = p.id " +
            "WHERE p.estado = 'ENTREGADO' " +
            "AND p.fecha_pedido BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY u.id, u.nombre_completo " +
            "ORDER BY total_productos_vendidos DESC " +
            "LIMIT 5", 
            nativeQuery = true)
    List<Object[]> findTop5ClientesPorVentas(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    /**
     * Top 10 clientes que más pedidos han realizado
     * Retorna: usuario_id, nombre_completo, cantidad_pedidos, total_gastado
     */
    @Query(value = "SELECT " +
            "u.id, " +
            "u.nombre_completo, " +
            "COUNT(p.id) as cantidad_pedidos, " +
            "COALESCE(SUM(p.monto_total), 0) as total_gastado " +
            "FROM usuarios u " +
            "INNER JOIN pedidos p ON u.id = p.usuario_id " +
            "WHERE p.fecha_pedido BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY u.id, u.nombre_completo " +
            "ORDER BY cantidad_pedidos DESC " +
            "LIMIT 10", 
            nativeQuery = true)
    List<Object[]> findTop10ClientesPorPedidos(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    /**
     * Top 10 clientes que más productos tienen a la venta
     * Retorna: usuario_id, nombre_completo, cantidad_productos, productos_aprobados
     */
    @Query(value = "SELECT " +
            "u.id, " +
            "u.nombre_completo, " +
            "COUNT(p.id) as cantidad_productos, " +
            "SUM(CASE WHEN p.estado = 'APROBADO' THEN 1 ELSE 0 END) as productos_aprobados " +
            "FROM usuarios u " +
            "INNER JOIN productos p ON u.id = p.vendedor_id " +
            "GROUP BY u.id, u.nombre_completo " +
            "ORDER BY cantidad_productos DESC " +
            "LIMIT 10", 
            nativeQuery = true)
    List<Object[]> findTop10ClientesPorProductosEnVenta();
}