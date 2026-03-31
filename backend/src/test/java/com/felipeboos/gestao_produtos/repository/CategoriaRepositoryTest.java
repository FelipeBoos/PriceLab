package com.felipeboos.gestao_produtos.repository;

import com.felipeboos.gestao_produtos.entity.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository repository;

    @Test
    @DisplayName("T1 - CategoriaRepositoryTest - Deve encontrar categoria por nome com sucesso")
    void t1_deveEncontrarCategoriaPorNomeComSucesso() {
        Categoria categoria = new Categoria();
        categoria.setNome("Eletrônicos");
        repository.save(categoria);

        Optional<Categoria> resultado = repository.findByNome("Eletrônicos");

        assertTrue(resultado.isPresent());
        assertNotNull(resultado.get().getId());
        assertEquals("Eletrônicos", resultado.get().getNome());
    }

    @Test
    @DisplayName("T2 - CategoriaRepositoryTest - Deve retornar Optional vazio ao buscar categoria por nome inexistente")
    void t2_deveRetornarOptionalVazioAoBuscarCategoriaPorNomeInexistente() {
        Optional<Categoria> resultado = repository.findByNome("Categoria inexistente");

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("T3 - CategoriaRepositoryTest - Deve retornar true quando existir categoria com o nome informado")
    void t3_deveRetornarTrueQuandoExistirCategoriaComONomeInformado() {
        Categoria categoria = new Categoria();
        categoria.setNome("Informática");
        repository.save(categoria);

        Boolean resultado = repository.existsByNome("Informática");

        assertTrue(resultado);
    }

    @Test
    @DisplayName("T4 - CategoriaRepositoryTest - Deve retornar false quando nao existir categoria com o nome informado")
    void t4_deveRetornarFalseQuandoNaoExistirCategoriaComONomeInformado() {
        Boolean resultado = repository.existsByNome("Moveis");

        assertFalse(resultado);
    }

    @Test
    @DisplayName("T5 - CategoriaRepositoryTest - Deve listar todas as categorias ordenadas por id asc")
    void t5_deveListarTodasAsCategoriasOrdenadasPorIdAsc() {
        Categoria categoria1 = new Categoria();
        categoria1.setNome("Eletrônicos");
        repository.save(categoria1);

        Categoria categoria2 = new Categoria();
        categoria2.setNome("Vestuário");
        repository.save(categoria2);

        Categoria categoria3 = new Categoria();
        categoria3.setNome("Alimentos");
        repository.save(categoria3);

        List<Categoria> resultado = repository.findAllByOrderByIdAsc();

        assertEquals(3, resultado.size());
        assertEquals("Eletrônicos", resultado.get(0).getNome());
        assertEquals("Vestuário", resultado.get(1).getNome());
        assertEquals("Alimentos", resultado.get(2).getNome());

        assertTrue(resultado.get(0).getId() < resultado.get(1).getId());
        assertTrue(resultado.get(1).getId() < resultado.get(2).getId());
    }

    @Test
    @DisplayName("T6 - CategoriaRepositoryTest - Deve retornar lista vazia quando nao houver categorias cadastradas")
    void t6_deveRetornarListaVaziaQuandoNaoHouverCategoriasCadastradas() {
        List<Categoria> resultado = repository.findAllByOrderByIdAsc();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}