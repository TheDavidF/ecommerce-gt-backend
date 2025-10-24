package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    /**
     * Buscar categoría por nombre
     */
    Optional<Categoria> findByNombre(String nombre);
    
    /**
     * Buscar por nombre (case insensitive)
     */
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Verificar si existe por nombre
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Obtener categorías con productos aprobados
     */
    @Query("SELECT DISTINCT c FROM Categoria c JOIN c.productos p WHERE p.estado = 'APROBADO'")
    List<Categoria> findCategoriasConProductos();
    
    /**
     * Contar categorías
     */
    @Query("SELECT COUNT(c) FROM Categoria c")
    Long contarTodas();
}