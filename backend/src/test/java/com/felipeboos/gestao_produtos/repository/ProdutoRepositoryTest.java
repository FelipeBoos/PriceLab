package com.felipeboos.gestao_produtos.repository;

import com.felipeboos.gestao_produtos.entity.Categoria;
import com.felipeboos.gestao_produtos.entity.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("T1 - ProdutoRepositoryTest - Deve encontrar produto por nome com sucesso")
    void t1_deveEncontrarProdutoPorNomeComSucesso() {
        Categoria categoria = salvarCategoria("Eletronicos");
        Produto produto = criarProduto("Notebook", categoria);
        repository.save(produto);

        Optional<Produto> resultado = repository.findByNome("Notebook");

        assertTrue(resultado.isPresent());
        assertNotNull(resultado.get().getId());
        assertEquals("Notebook", resultado.get().getNome());
        assertNotNull(resultado.get().getCategoria());
        assertEquals("Eletronicos", resultado.get().getCategoria().getNome());
    }

    @Test
    @DisplayName("T2 - ProdutoRepositoryTest - Deve retornar Optional vazio ao buscar produto por nome inexistente")
    void t2_deveRetornarOptionalVazioAoBuscarProdutoPorNomeInexistente() {
        Optional<Produto> resultado = repository.findByNome("Produto inexistente");

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("T3 - ProdutoRepositoryTest - Deve retornar true quando existir produto com o nome informado")
    void t3_deveRetornarTrueQuandoExistirProdutoComONomeInformado() {
        Categoria categoria = salvarCategoria("Perifericos");
        Produto produto = criarProduto("Mouse Gamer", categoria);
        repository.save(produto);

        boolean resultado = repository.existsByNome("Mouse Gamer");

        assertTrue(resultado);
    }

    @Test
    @DisplayName("T4 - ProdutoRepositoryTest - Deve retornar false quando nao existir produto com o nome informado")
    void t4_deveRetornarFalseQuandoNaoExistirProdutoComONomeInformado() {
        boolean resultado = repository.existsByNome("Produto inexistente");

        assertFalse(resultado);
    }

    @Test
    @DisplayName("T5 - ProdutoRepositoryTest - Deve retornar true quando existir produto para a categoria informada")
    void t5_deveRetornarTrueQuandoExistirProdutoParaCategoriaInformada() {
        Categoria categoria = salvarCategoria("Eletronicos");
        Produto produto = criarProduto("Notebook", categoria);
        repository.save(produto);

        boolean resultado = repository.existsByCategoriaId(categoria.getId());

        assertTrue(resultado);
    }

    @Test
    @DisplayName("T6 - ProdutoRepositoryTest - Deve retornar false quando nao existir produto para a categoria informada")
    void t6_deveRetornarFalseQuandoNaoExistirProdutoParaCategoriaInformada() {
        Categoria categoria = salvarCategoria("Moveis");

        boolean resultado = repository.existsByCategoriaId(categoria.getId());

        assertFalse(resultado);
    }

    @Test
    @DisplayName("T7 - ProdutoRepositoryTest - Deve listar todos os produtos ordenados por id asc")
    void t7_deveListarTodosOsProdutosOrdenadosPorIdAsc() {
        Categoria categoria = salvarCategoria("Eletronicos");

        Produto produto1 = criarProduto("Notebook", categoria);
        Produto produto2 = criarProduto("Mouse", categoria);
        Produto produto3 = criarProduto("Teclado", categoria);

        repository.save(produto1);
        repository.save(produto2);
        repository.save(produto3);

        List<Produto> resultado = repository.findAllByOrderByIdAsc();

        assertEquals(3, resultado.size());
        assertEquals("Notebook", resultado.get(0).getNome());
        assertEquals("Mouse", resultado.get(1).getNome());
        assertEquals("Teclado", resultado.get(2).getNome());

        assertTrue(resultado.get(0).getId() < resultado.get(1).getId());
        assertTrue(resultado.get(1).getId() < resultado.get(2).getId());
    }

    @Test
    @DisplayName("T8 - ProdutoRepositoryTest - Deve retornar lista vazia quando nao houver produtos cadastrados")
    void t8_deveRetornarListaVaziaQuandoNaoHouverProdutosCadastrados() {
        List<Produto> resultado = repository.findAllByOrderByIdAsc();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    private Categoria salvarCategoria(String nome) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        return categoriaRepository.save(categoria);
    }

    private Produto criarProduto(String nome, Categoria categoria) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao("Descricao teste");
        produto.setCategoria(categoria);
        produto.setPrecoCusto(BigDecimal.valueOf(100.00));
        produto.setPrecoVenda(BigDecimal.valueOf(150.00));
        produto.setQuantidadeEstoque(10);
        produto.setDemandaBase(50);
        produto.setFatorElasticidade(BigDecimal.valueOf(0.10));
        return produto;
    }
}