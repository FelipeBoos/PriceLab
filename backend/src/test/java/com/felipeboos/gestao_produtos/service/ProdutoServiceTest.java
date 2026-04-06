package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.dto.produto.ProdutoRequestDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoResponseDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoUpdateDTO;
import com.felipeboos.gestao_produtos.entity.Categoria;
import com.felipeboos.gestao_produtos.entity.Moeda;
import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.exception.RecursoDuplicadoException;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.repository.CategoriaRepository;
import com.felipeboos.gestao_produtos.repository.ProdutoRepository;
import com.felipeboos.gestao_produtos.service.cambio.CambioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CambioService cambioService;

    @InjectMocks
    private ProdutoService service;

    private Categoria categoria;
    private Categoria categoria2;

    private Produto produto;
    private ProdutoRequestDTO produtoRequestDTO;
    private ProdutoUpdateDTO produtoUpdateDTOCompleto;
    private ProdutoUpdateDTO produtoUpdateDTOParcial;
    private ProdutoUpdateDTO produtoUpdateDTOSomenteCategoria;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nome("Eletrônicos")
                .descricao("Categoria teste 1")
                .build();

        categoria2 = Categoria.builder()
                .id(2L)
                .nome("Alimentos")
                .descricao("Categoria teste 2")
                .build();

        produto = Produto.builder()
                .id(1L)
                .nome("Produto teste 1")
                .categoria(categoria)
                .precoCusto(BigDecimal.valueOf(450))
                .moeda(Moeda.BRL)
                .cotacaoMoeda(BigDecimal.ONE)
                .precoCustoEmReais(BigDecimal.valueOf(450))
                .precoVenda(BigDecimal.valueOf(600))
                .quantidadeEstoque(10)
                .demandaBase(100)
                .fatorElasticidade(BigDecimal.valueOf(0.05))
                .build();

        produtoRequestDTO = new ProdutoRequestDTO();
        produtoRequestDTO.setNome("Produto teste 1");
        produtoRequestDTO.setCategoriaId(1L);
        produtoRequestDTO.setPrecoCusto(BigDecimal.valueOf(450));
        produtoRequestDTO.setMoeda(Moeda.BRL);
        produtoRequestDTO.setPrecoVenda(BigDecimal.valueOf(600));
        produtoRequestDTO.setQuantidadeEstoque(10);
        produtoRequestDTO.setDemandaBase(100);
        produtoRequestDTO.setFatorElasticidade(BigDecimal.valueOf(0.05));

        produtoUpdateDTOCompleto = new ProdutoUpdateDTO();
        produtoUpdateDTOCompleto.setNome("Nome teste alteração");
        produtoUpdateDTOCompleto.setCategoriaId(2L);
        produtoUpdateDTOCompleto.setPrecoCusto(BigDecimal.valueOf(470));
        produtoUpdateDTOCompleto.setMoeda(Moeda.BRL);
        produtoUpdateDTOCompleto.setPrecoVenda(BigDecimal.valueOf(580));
        produtoUpdateDTOCompleto.setQuantidadeEstoque(8);
        produtoUpdateDTOCompleto.setDemandaBase(90);
        produtoUpdateDTOCompleto.setFatorElasticidade(BigDecimal.valueOf(0.06));

        produtoUpdateDTOParcial = new ProdutoUpdateDTO();
        produtoUpdateDTOParcial.setNome("Nome teste alteração parcial");
        produtoUpdateDTOParcial.setPrecoCusto(BigDecimal.valueOf(480));

        produtoUpdateDTOSomenteCategoria = new ProdutoUpdateDTO();
        produtoUpdateDTOSomenteCategoria.setCategoriaId(2L);
    }

    @Test
    @DisplayName("T1 - ProdutoServiceTest - Deve salvar produto com sucesso")
    void t1_deveSalvarProdutoComSucesso() {
        when(repository.existsByNome("Produto teste 1")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(cambioService.obterCotacao(Moeda.BRL)).thenReturn(BigDecimal.ONE);
        when(repository.saveAndFlush(any(Produto.class))).thenReturn(produto);

        ProdutoResponseDTO response = service.salvarProduto(produtoRequestDTO);

        assertNotNull(response);
        assertEquals("Produto teste 1", response.getNome());
        assertEquals(1L, response.getCategoriaId());
        assertEquals("Eletrônicos", response.getCategoriaNome());
        assertEquals(0, response.getPrecoCusto().compareTo(BigDecimal.valueOf(450)));
        assertEquals(100, response.getDemandaBase());

        verify(repository, times(1)).saveAndFlush(any(Produto.class));
        verify(cambioService, times(1)).obterCotacao(Moeda.BRL);
    }

    @Test
    @DisplayName("T2 - ProdutoServiceTest - Deve lançar exceção ao tentar salvar produto com nome já cadastrado")
    void t2_deveLancarExcecaoAoTentarSalvarProdutoComNomeJaCadastrado() {
        when(repository.existsByNome("Produto teste 1")).thenReturn(true);

        RecursoDuplicadoException exception = assertThrows(RecursoDuplicadoException.class,
                () -> service.salvarProduto(produtoRequestDTO));

        assertEquals("Já existe um produto cadastrado com esse nome", exception.getMessage());

        verify(categoriaRepository, never()).findById(anyLong());
        verify(repository, never()).saveAndFlush(any(Produto.class));
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T3 - ProdutoServiceTest - Deve lançar exceção ao salvar produto com categoria inexistente")
    void t3_deveLancarExcecaoAoSalvarProdutoComCategoriaInexistente() {
        when(repository.existsByNome("Produto teste 1")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.salvarProduto(produtoRequestDTO));

        assertEquals("Categoria nao encontrada para o id informado", exception.getMessage());

        verify(repository, times(1)).existsByNome("Produto teste 1");
        verify(categoriaRepository, times(1)).findById(1L);
        verifyNoInteractions(cambioService);
        verify(repository, never()).saveAndFlush(any(Produto.class));
    }

    @Test
    @DisplayName("T4 - ProdutoServiceTest - Deve buscar produto por id com sucesso")
    void t4_deveBuscarProdutoPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoResponseDTO response = service.buscarProdutoPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Produto teste 1", response.getNome());
        assertEquals(1L, response.getCategoriaId());
        assertEquals("Eletrônicos", response.getCategoriaNome());
        assertEquals(0, response.getPrecoCusto().compareTo(BigDecimal.valueOf(450)));

        verifyNoInteractions(categoriaRepository);
        verify(repository, times(1)).findById(1L);
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T5 - ProdutoServiceTest - Deve lançar exceção ao buscar produto por id inexistente")
    void t5_deveLancarExcecaoAoBuscarProdutoPorIdInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarProdutoPorId(1L));

        assertEquals("Produto nao encontrado para o id informado", exception.getMessage());

        verifyNoInteractions(categoriaRepository);
        verify(repository, times(1)).findById(1L);
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T6 - ProdutoServiceTest - Deve buscar produto por nome com sucesso")
    void t6_deveBuscarProdutoPorNomeComSucesso() {
        when(repository.findByNome("Produto teste 1")).thenReturn(Optional.of(produto));

        List<ProdutoResponseDTO> response = service.buscarProdutoPorNome("Produto teste 1");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals("Produto teste 1", response.get(0).getNome());
        assertEquals(1L, response.get(0).getCategoriaId());
        assertEquals("Eletrônicos", response.get(0).getCategoriaNome());
        assertEquals(0, response.get(0).getPrecoCusto().compareTo(BigDecimal.valueOf(450)));

        verifyNoInteractions(categoriaRepository);
        verify(repository, times(1)).findByNome("Produto teste 1");
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T7 - ProdutoServiceTest - Deve lançar exceção ao buscar produto por nome inexistente")
    void t7_deveLancarExcecaoAoBuscarProdutoPorNomeInexistente() {
        when(repository.findByNome("Produto teste 1")).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarProdutoPorNome("Produto teste 1"));

        assertEquals("Produto nao encontrado para o nome informado", exception.getMessage());

        verifyNoInteractions(categoriaRepository);
        verify(repository, times(1)).findByNome("Produto teste 1");
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T8 - ProdutoServiceTest - Deve listar todos os produtos com sucesso")
    void t8_deveListarTodosOsProdutosComSucesso() {
        Produto produto2 = Produto.builder()
                .id(2L)
                .nome("Produto teste 2")
                .categoria(categoria)
                .precoCusto(BigDecimal.valueOf(370))
                .moeda(Moeda.BRL)
                .cotacaoMoeda(BigDecimal.ONE)
                .precoCustoEmReais(BigDecimal.valueOf(370))
                .precoVenda(BigDecimal.valueOf(430))
                .quantidadeEstoque(25)
                .demandaBase(80)
                .fatorElasticidade(BigDecimal.valueOf(0.07))
                .build();

        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(produto, produto2));

        List<ProdutoResponseDTO> response = service.listarTodosOsProdutos();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getId());
        assertEquals("Produto teste 1", response.get(0).getNome());
        assertEquals(0, response.get(0).getPrecoCusto().compareTo(BigDecimal.valueOf(450)));
        assertEquals(100, response.get(0).getDemandaBase());

        assertEquals(2L, response.get(1).getId());
        assertEquals("Produto teste 2", response.get(1).getNome());
        assertEquals(0, response.get(1).getPrecoCusto().compareTo(BigDecimal.valueOf(370)));
        assertEquals(80, response.get(1).getDemandaBase());

        verifyNoInteractions(categoriaRepository);
        verify(repository, times(1)).findAllByOrderByIdAsc();
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T9 - ProdutoServiceTest - Deve retornar lista vazia ao listar produtos")
    void t9_deveRetornarListaVaziaAoListarProdutos() {
        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of());

        List<ProdutoResponseDTO> response = service.listarTodosOsProdutos();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verifyNoInteractions(categoriaRepository);
        verify(repository, times(1)).findAllByOrderByIdAsc();
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T10 - ProdutoServiceTest - Deve deletar produto por id com sucesso")
    void t10_deveDeletarProdutoPorIdComSucesso() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        service.deletarProdutoPorId(1L);

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
        verifyNoInteractions(categoriaRepository);
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T11 - ProdutoServiceTest - Deve lançar exceção ao deletar produto com id inexistente")
    void t11_deveLancarExcecaoAoDeletarProdutoComIdInexistente() {
        when(repository.existsById(1L)).thenReturn(false);

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.deletarProdutoPorId(1L));

        assertEquals("Produto nao encontrado para o id informado", exception.getMessage());

        verify(repository, times(1)).existsById(1L);
        verify(repository, never()).deleteById(anyLong());
        verifyNoInteractions(categoriaRepository);
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T12 - ProdutoServiceTest - Deve atualizar produto por id com sucesso")
    void t12_deveAtualizarProdutoPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.existsByNome("Nome teste alteração")).thenReturn(false);
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoria2));
        when(cambioService.obterCotacao(Moeda.BRL)).thenReturn(BigDecimal.ONE);

        service.atualizarProdutoPorId(1L, produtoUpdateDTOCompleto);

        assertEquals(1L, produto.getId());
        assertEquals("Nome teste alteração", produto.getNome());
        assertEquals(2L, produto.getCategoria().getId());
        assertEquals(0, produto.getPrecoCusto().compareTo(BigDecimal.valueOf(470)));
        assertEquals(0, produto.getPrecoVenda().compareTo(BigDecimal.valueOf(580)));
        assertEquals(8, produto.getQuantidadeEstoque());
        assertEquals(90, produto.getDemandaBase());
        assertEquals(0, produto.getFatorElasticidade().compareTo(BigDecimal.valueOf(0.06)));
        assertEquals(Moeda.BRL, produto.getMoeda());
        assertEquals(0, produto.getCotacaoMoeda().compareTo(BigDecimal.ONE));
        assertEquals(0, produto.getPrecoCustoEmReais().compareTo(BigDecimal.valueOf(470)));

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByNome("Nome teste alteração");
        verify(categoriaRepository, times(1)).findById(2L);
        verify(repository, times(1)).saveAndFlush(produto);
        verify(cambioService, times(1)).obterCotacao(Moeda.BRL);
    }

    @Test
    @DisplayName("T13 - ProdutoServiceTest - Deve lançar exceção ao atualizar produto com id inexistente")
    void t13_deveLancarExcecaoAoAtualizarProdutoComIdInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.atualizarProdutoPorId(1L, produtoUpdateDTOCompleto));

        assertEquals("Produto nao encontrado para o id informado", exception.getMessage());

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).existsByNome(anyString());
        verify(repository, never()).saveAndFlush(any(Produto.class));
        verifyNoInteractions(categoriaRepository);
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T14 - ProdutoServiceTest - Deve lançar exceção ao atualizar produto com nome já cadastrado")
    void t14_deveLancarExcecaoAoAtualizarProdutoComNomeJaCadastrado() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.existsByNome("Nome teste alteração")).thenReturn(true);

        RecursoDuplicadoException exception = assertThrows(RecursoDuplicadoException.class,
                () -> service.atualizarProdutoPorId(1L, produtoUpdateDTOCompleto));

        assertEquals("Já existe um produto cadastrado com esse nome", exception.getMessage());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByNome(anyString());
        verify(repository, never()).saveAndFlush(any(Produto.class));
        verifyNoInteractions(categoriaRepository);
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T15 - ProdutoServiceTest - Deve atualizar apenas campos informados")
    void t15_deveAtualizarApenasCamposInformados() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.existsByNome("Nome teste alteração parcial")).thenReturn(false);
        when(cambioService.obterCotacao(Moeda.BRL)).thenReturn(BigDecimal.ONE);

        service.atualizarProdutoPorId(1L, produtoUpdateDTOParcial);

        assertEquals("Nome teste alteração parcial", produto.getNome());
        assertEquals(0, produto.getPrecoCusto().compareTo(BigDecimal.valueOf(480)));

        assertEquals(1L, produto.getId());
        assertEquals(1L, produto.getCategoria().getId());
        assertEquals(0, produto.getPrecoVenda().compareTo(BigDecimal.valueOf(600)));
        assertEquals(10, produto.getQuantidadeEstoque());
        assertEquals(100, produto.getDemandaBase());
        assertEquals(0, produto.getFatorElasticidade().compareTo(BigDecimal.valueOf(0.05)));
        assertEquals(Moeda.BRL, produto.getMoeda());
        assertEquals(0, produto.getCotacaoMoeda().compareTo(BigDecimal.ONE));
        assertEquals(0, produto.getPrecoCustoEmReais().compareTo(BigDecimal.valueOf(480)));

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByNome("Nome teste alteração parcial");
        verifyNoInteractions(categoriaRepository);
        verify(repository, times(1)).saveAndFlush(produto);
        verify(cambioService, times(1)).obterCotacao(Moeda.BRL);
    }

    @Test
    @DisplayName("T16 - ProdutoServiceTest - Deve atualizar categoria com sucesso")
    void t16_deveAtualizarCategoriaComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoria2));
        when(cambioService.obterCotacao(Moeda.BRL)).thenReturn(BigDecimal.ONE);

        service.atualizarProdutoPorId(1L, produtoUpdateDTOSomenteCategoria);

        assertEquals(2L, produto.getCategoria().getId());
        assertEquals("Alimentos", produto.getCategoria().getNome());

        assertEquals(1L, produto.getId());
        assertEquals("Produto teste 1", produto.getNome());
        assertEquals(0, produto.getPrecoCusto().compareTo(BigDecimal.valueOf(450)));
        assertEquals(0, produto.getPrecoVenda().compareTo(BigDecimal.valueOf(600)));
        assertEquals(Moeda.BRL, produto.getMoeda());
        assertEquals(0, produto.getCotacaoMoeda().compareTo(BigDecimal.ONE));
        assertEquals(0, produto.getPrecoCustoEmReais().compareTo(BigDecimal.valueOf(450)));

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).existsByNome(anyString());
        verify(categoriaRepository, times(1)).findById(2L);
        verify(repository, times(1)).saveAndFlush(produto);
        verify(cambioService, times(1)).obterCotacao(Moeda.BRL);
    }

    @Test
    @DisplayName("T17 - ProdutoServiceTest - Deve lançar exceção ao atualizar produto com categoria inexistente")
    void t17_deveLancarExcecaoAoAtualizarProdutoComCategoriaInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(2L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.atualizarProdutoPorId(1L, produtoUpdateDTOSomenteCategoria));

        assertEquals("Categoria nao encontrada para o id informado", exception.getMessage());

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).existsByNome(anyString());
        verify(categoriaRepository, times(1)).findById(2L);
        verify(repository, never()).saveAndFlush(any(Produto.class));
        verifyNoInteractions(cambioService);
    }

    @Test
    @DisplayName("T18 - ProdutoServiceTest - Deve salvar produto em USD com cotação convertida")
    void t18_deveSalvarProdutoEmUsdComCotacaoConvertida() {
        ProdutoRequestDTO requestUsd = new ProdutoRequestDTO();
        requestUsd.setNome("Produto em dólar");
        requestUsd.setCategoriaId(1L);
        requestUsd.setPrecoCusto(BigDecimal.valueOf(100));
        requestUsd.setMoeda(Moeda.USD);
        requestUsd.setPrecoVenda(BigDecimal.valueOf(700));
        requestUsd.setQuantidadeEstoque(5);
        requestUsd.setDemandaBase(20);
        requestUsd.setFatorElasticidade(BigDecimal.valueOf(0.03));

        Produto produtoUsd = Produto.builder()
                .id(2L)
                .nome("Produto em dólar")
                .categoria(categoria)
                .precoCusto(BigDecimal.valueOf(100))
                .moeda(Moeda.USD)
                .cotacaoMoeda(BigDecimal.valueOf(5.25))
                .precoCustoEmReais(BigDecimal.valueOf(525.00))
                .precoVenda(BigDecimal.valueOf(700))
                .quantidadeEstoque(5)
                .demandaBase(20)
                .fatorElasticidade(BigDecimal.valueOf(0.03))
                .build();

        when(repository.existsByNome("Produto em dólar")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(cambioService.obterCotacao(Moeda.USD)).thenReturn(BigDecimal.valueOf(5.25));
        when(repository.saveAndFlush(any(Produto.class))).thenReturn(produtoUsd);

        ProdutoResponseDTO response = service.salvarProduto(requestUsd);

        assertNotNull(response);
        assertEquals("Produto em dólar", response.getNome());
        assertEquals(0, response.getPrecoCusto().compareTo(BigDecimal.valueOf(100)));

        verify(repository, times(1)).saveAndFlush(any(Produto.class));
        verify(cambioService, times(1)).obterCotacao(Moeda.USD);
    }
}