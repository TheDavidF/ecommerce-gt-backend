package com.ecommercegt.backend.repositorios;

import com.ecommercegt.backend.models.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    // Buscar categoría por nombre
    Optional<Categoria> findByNombre(String nombre);
    
    // Verificar si existe categoría por nombre
    Boolean existsByNombre(String nombre);
    
    // Listar categorías activas
    List<Categoria> findByActivoTrue();
    
    // Buscar categorías que contengan texto en el nombre
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}