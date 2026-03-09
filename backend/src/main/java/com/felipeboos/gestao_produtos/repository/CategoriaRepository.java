package com.felipeboos.gestao_produtos.repository;

import com.felipeboos.gestao_produtos.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNome(String nome);

    List<Categoria> findAllByOrderByIdAsc();
}
