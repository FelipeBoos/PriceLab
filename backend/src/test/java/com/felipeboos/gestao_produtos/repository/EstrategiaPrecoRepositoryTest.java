package com.felipeboos.gestao_produtos.repository;

import com.felipeboos.gestao_produtos.entity.Categoria;
import com.felipeboos.gestao_produtos.entity.EstrategiaPreco;
import com.felipeboos.gestao_produtos.entity.Produto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EstrategiaPrecoRepositoryTest {
    @Autowired
    private EstrategiaPrecoRepository repository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    @DisplayName("T1 - EstrategiaPrecoRepositoryTest - Deve listar todas as estrategias ordenadas por id asc")
    void t1_deveListarTodasAsEstrategiasOrdenadasPorIdAsc() {
        Categoria categoria = salvarCategoria("Eletronicos");
        Produto produto = salvarProduto("Notebook", categoria);

        EstrategiaPreco estrategia1 = criarEstrategia(produto, BigDecimal.valueOf(20), BigDecimal.valueOf(10));
        EstrategiaPreco estrategia2 = criarEstrategia(produto, BigDecimal.valueOf(25), BigDecimal.valueOf(12));
        EstrategiaPreco estrategia3 = criarEstrategia(produto, BigDecimal.valueOf(30), BigDecimal.valueOf(15));

        repository.save(estrategia1);
        repository.save(estrategia2);
        repository.save(estrategia3);

        List<EstrategiaPreco> resultado = repository.findAllByOrderByIdAsc();

        assertEquals(3, resultado.size());
        assertEquals(BigDecimal.valueOf(20), resultado.get(0).getMargemLucro());
        assertEquals(BigDecimal.valueOf(25), resultado.get(1).getMargemLucro());
        assertEquals(BigDecimal.valueOf(30), resultado.get(2).getMargemLucro());

        assertTrue(resultado.get(0).getId() < resultado.get(1).getId());
        assertTrue(resultado.get(1).getId() < resultado.get(2).getId());
    }

    @Test
    @DisplayName("T2 - EstrategiaPrecoRepositoryTest - Deve retornar lista vazia quando nao houver estrategias cadastradas")
    void t2_deveRetornarListaVaziaQuandoNaoHouverEstrategiasCadastradas() {
        List<EstrategiaPreco> resultado = repository.findAllByOrderByIdAsc();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("T3 - EstrategiaPrecoRepositoryTest - Deve buscar estrategias por id do produto com sucesso")
    void t3_deveBuscarEstrategiasPorIdDoProdutoComSucesso() {
        Categoria categoria = salvarCategoria("Eletronicos");
        Produto produto = salvarProduto("Notebook", categoria);

        EstrategiaPreco estrategia1 = criarEstrategia(produto, BigDecimal.valueOf(20), BigDecimal.valueOf(10));
        EstrategiaPreco estrategia2 = criarEstrategia(produto, BigDecimal.valueOf(25), BigDecimal.valueOf(12));

        repository.save(estrategia1);
        repository.save(estrategia2);

        List<EstrategiaPreco> resultado = repository.findByProduto_Id(produto.getId());

        assertEquals(2, resultado.size());
        assertEquals(produto.getId(), resultado.get(0).getProduto().getId());
        assertEquals(produto.getId(), resultado.get(1).getProduto().getId());
    }

    @Test
    @DisplayName("T4 - EstrategiaPrecoRepositoryTest - Deve retornar lista vazia ao buscar estrategias por id de produto inexistente")
    void t4_deveRetornarListaVaziaAoBuscarEstrategiasPorIdDeProdutoInexistente() {
        List<EstrategiaPreco> resultado = repository.findByProduto_Id(999L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("T5 - EstrategiaPrecoRepositoryTest - Deve buscar apenas estrategias do produto informado")
    void t5_deveBuscarApenasEstrategiasDoProdutoInformado() {
        Categoria categoria = salvarCategoria("Eletronicos");

        Produto produto1 = salvarProduto("Notebook", categoria);
        Produto produto2 = salvarProduto("Mouse", categoria);

        EstrategiaPreco estrategia1 = criarEstrategia(produto1, BigDecimal.valueOf(20), BigDecimal.valueOf(10));
        EstrategiaPreco estrategia2 = criarEstrategia(produto1, BigDecimal.valueOf(25), BigDecimal.valueOf(12));
        EstrategiaPreco estrategia3 = criarEstrategia(produto2, BigDecimal.valueOf(30), BigDecimal.valueOf(15));

        repository.save(estrategia1);
        repository.save(estrategia2);
        repository.save(estrategia3);

        List<EstrategiaPreco> resultado = repository.findByProduto_Id(produto1.getId());

        assertEquals(2, resultado.size());
        assertEquals(produto1.getId(), resultado.get(0).getProduto().getId());
        assertEquals(produto1.getId(), resultado.get(1).getProduto().getId());
    }

    private Categoria salvarCategoria(String nome) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        return categoriaRepository.save(categoria);
    }

    private Produto salvarProduto(String nome, Categoria categoria) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao("Descricao teste");
        produto.setCategoria(categoria);
        produto.setPrecoCusto(BigDecimal.valueOf(100.00));
        produto.setPrecoVenda(BigDecimal.valueOf(150.00));
        produto.setQuantidadeEstoque(10);
        produto.setDemandaBase(50);
        produto.setFatorElasticidade(BigDecimal.valueOf(0.10));
        return produtoRepository.save(produto);
    }

    private EstrategiaPreco criarEstrategia(Produto produto, BigDecimal margemLucro, BigDecimal percentualImposto) {
        EstrategiaPreco estrategia = new EstrategiaPreco();
        estrategia.setProduto(produto);
        estrategia.setMargemLucro(margemLucro);
        estrategia.setPercentualImposto(percentualImposto);
        estrategia.setPrecoSugerido(BigDecimal.valueOf(200.00));
        estrategia.setDemandaEstimada(40);
        estrategia.setLucroUnitario(BigDecimal.valueOf(50.00));
        estrategia.setLucroTotalEstimado(BigDecimal.valueOf(2000.00));
        estrategia.setDataSimulacao(Instant.now());
        return estrategia;
    }
}