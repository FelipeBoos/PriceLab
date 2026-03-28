package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoRequestDTO;
import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoResponseDTO;
import com.felipeboos.gestao_produtos.entity.Categoria;
import com.felipeboos.gestao_produtos.entity.EstrategiaPreco;
import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.exception.RecursoDuplicadoException;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.repository.EstrategiaPrecoRepository;
import com.felipeboos.gestao_produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstrategiaPrecoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EstrategiaPrecoRepository repository;

    @InjectMocks
    private EstrategiaPrecoService service;

    private Produto produto;
    private Categoria categoria;
    private EstrategiaPrecoRequestDTO requestDTO;
    private EstrategiaPreco estrategiaPreco;

    private final BigDecimal precoSugeridoEsperado = BigDecimal.valueOf(594);
    private final BigDecimal lucroUnitarioEsperado = BigDecimal.valueOf(90);
    private final int demandaEstimadaEsperada = 70;
    private final BigDecimal lucroTotalEsperado = BigDecimal.valueOf(6300);

    @BeforeEach
    void setup() {
        categoria = Categoria.builder()
                .id(1L)
                .nome("Eletrônicos")
                .descricao("Categoria teste")
                .build();

        produto = Produto.builder()
                .id(1L)
                .nome("Produto teste")
                .categoria(categoria)
                .precoCusto(BigDecimal.valueOf(450))
                .demandaBase(100)
                .fatorElasticidade(BigDecimal.valueOf(0.05))
                .build();

        requestDTO = new EstrategiaPrecoRequestDTO();
        requestDTO.setProdutoId(1L);
        requestDTO.setMargemLucro(BigDecimal.valueOf(20));
        requestDTO.setPercentualImposto(BigDecimal.valueOf(10));

        estrategiaPreco = EstrategiaPreco.builder()
                .id(1L)
                .produto(produto)
                .margemLucro(BigDecimal.valueOf(20))
                .percentualImposto(BigDecimal.valueOf(10))
                .precoSugerido(BigDecimal.valueOf(594))
                .lucroUnitario(BigDecimal.valueOf(90))
                .demandaEstimada(70)
                .lucroTotalEstimado(BigDecimal.valueOf(6300))
                .dataSimulacao(Instant.now())
                .build();
    }

    @Test
    @DisplayName("T1 - EstrategiaPrecoServiceTest - Deve simular estrategia de preco com sucesso")
    void t1_deveSimularEstrategiaDePrecoComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        EstrategiaPrecoResponseDTO responseDTO = service.simularPreco(requestDTO);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getProdutoId());
        assertEquals("Produto teste", responseDTO.getProdutoNome());
        assertEquals(BigDecimal.valueOf(20), responseDTO.getMargemLucro());
        assertEquals(BigDecimal.valueOf(10), responseDTO.getPercentualImposto());

        // Validando campos calculados:
        assertEquals(0, responseDTO.getPrecoSugerido().compareTo(precoSugeridoEsperado));
        assertEquals(0, responseDTO.getLucroUnitario().compareTo(lucroUnitarioEsperado));
        assertEquals(demandaEstimadaEsperada, responseDTO.getDemandaEstimada());
        assertEquals(0, responseDTO.getLucroTotalEstimado().compareTo(lucroTotalEsperado));

        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("T2 - EstrategiaPrecoServiceTest - Deve lançar exceção ao simular com produto inexistente")
    void t2_deveLancarExcecaoAoSimularComProdutoInexistente() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());


        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.simularPreco(requestDTO));

        assertEquals("Produto nao encontrado para o id informado", exception.getMessage());
        verify(produtoRepository, times(1)).findById(1L);
        verify(repository, never()).saveAndFlush(any(EstrategiaPreco.class));
    }

    @Test
    @DisplayName("T3 - EstrategiaPrecoServiceTest - Deve criar e salvar estrategia de preço com sucesso")
    void t3_deveCriarESalvarEstrategiaDePrecoComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.saveAndFlush(any(EstrategiaPreco.class))).thenReturn(estrategiaPreco);

        EstrategiaPrecoResponseDTO responseDTO = service.criarEstrategiaPreco(requestDTO);

        assertNotNull(responseDTO);
        assertEquals("Produto teste", responseDTO.getProdutoNome());
        assertEquals(BigDecimal.valueOf(20), responseDTO.getMargemLucro());
        assertEquals(BigDecimal.valueOf(10), responseDTO.getPercentualImposto());
        assertEquals(BigDecimal.valueOf(594), responseDTO.getPrecoSugerido());
        assertEquals(BigDecimal.valueOf(90), responseDTO.getLucroUnitario());
        assertEquals(70, responseDTO.getDemandaEstimada());
        assertEquals(BigDecimal.valueOf(6300), responseDTO.getLucroTotalEstimado());
        verify(produtoRepository, times(1)).findById(1L);
        verify(repository, times(1)).saveAndFlush(any(EstrategiaPreco.class));
    }

    @Test
    @DisplayName("T4 - EstrategiaPrecoServiceTest - Deve salvar a entidade com os valores calculados corretamente")
    void t4_deveSalvarAEntidadeComOsValoresCalculadosCorretamente() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.saveAndFlush(any(EstrategiaPreco.class))).thenReturn(estrategiaPreco);

        service.criarEstrategiaPreco(requestDTO);

        ArgumentCaptor<EstrategiaPreco> estrategiaCaptor = ArgumentCaptor.forClass(EstrategiaPreco.class);

        verify(repository, times(1)).saveAndFlush(estrategiaCaptor.capture());

        EstrategiaPreco estrategiaCapturada = estrategiaCaptor.getValue();

        assertEquals(BigDecimal.valueOf(20), estrategiaCapturada.getMargemLucro());
        assertEquals(BigDecimal.valueOf(10), estrategiaCapturada.getPercentualImposto());

        // Validando campos calculados:
        assertEquals(0, estrategiaCapturada.getPrecoSugerido().compareTo(precoSugeridoEsperado));
        assertEquals(0, estrategiaCapturada.getLucroUnitario().compareTo(lucroUnitarioEsperado));
        assertEquals(demandaEstimadaEsperada, estrategiaCapturada.getDemandaEstimada());
        assertEquals(0, estrategiaCapturada.getLucroTotalEstimado().compareTo(lucroTotalEsperado));

        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("T5 - EstrategiaPrecoServiceTest - Deve lançar exceção ao criar estratégia com produto inexistente")
    void t5_deveLancarExcecaoAoCriarEstrategiaComProdutoInexistente() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.criarEstrategiaPreco(requestDTO));

        assertEquals("Produto nao encontrado para o id informado", exception.getMessage());
        verify(produtoRepository, times(1)).findById(1L);
        verify(repository, never()).saveAndFlush(any(EstrategiaPreco.class));
    }

    @Test
    @DisplayName("T6 - EstrategiaPrecoServiceTest - Deve listar todas as estrategias com sucesso")
    void t6_deveListarTodasAsEstrategiasComSucesso() {

        Produto produto2 = Produto.builder()
                .id(2L)
                .nome("Produto teste 2")
                .categoria(categoria)
                .precoCusto(BigDecimal.valueOf(730))
                .demandaBase(175)
                .fatorElasticidade(BigDecimal.valueOf(0.07))
                .build();

        EstrategiaPreco estrategiaPreco2 = EstrategiaPreco.builder()
                .id(2L)
                .produto(produto2)
                .margemLucro(BigDecimal.valueOf(15))
                .percentualImposto(BigDecimal.valueOf(12))
                .precoSugerido(BigDecimal.valueOf(940.24))
                .lucroUnitario(BigDecimal.valueOf(109.5))
                .demandaEstimada(109)
                .lucroTotalEstimado(BigDecimal.valueOf(11935.5))
                .dataSimulacao(Instant.now())
                .build();

        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(estrategiaPreco, estrategiaPreco2));

        List<EstrategiaPrecoResponseDTO> response = service.listarTodasAsEstrategiasDePreco();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getId());
        assertEquals(1L, response.get(0).getProdutoId());
        assertEquals("Produto teste", response.get(0).getProdutoNome());
        assertEquals(0, response.get(0).getMargemLucro().compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, response.get(0).getLucroTotalEstimado().compareTo(lucroTotalEsperado));

        assertEquals(2L, response.get(1).getId());
        assertEquals(2L, response.get(1).getProdutoId());
        assertEquals("Produto teste 2", response.get(1).getProdutoNome());
        assertEquals(0, response.get(1).getMargemLucro().compareTo(BigDecimal.valueOf(15)));
        assertEquals(0, response.get(1).getLucroTotalEstimado().compareTo(BigDecimal.valueOf(11935.5)));

        verify(repository, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    @DisplayName("T7 - EstrategiaPrecoServiceTest - Deve retornar lista vazia ao listar estrategias")
    void t7_deveRetornarListaVaziaAoListarEstrategias() {
        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of());

        List<EstrategiaPrecoResponseDTO> response = service.listarTodasAsEstrategiasDePreco();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(repository, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    @DisplayName("T8 - EstrategiaPrecoServiceTest - Deve buscar estratégia por id com sucesso")
    void t8_deveBuscarEstrategiaPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(estrategiaPreco));

        EstrategiaPrecoResponseDTO response = service.buscarEstrategiaPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getProdutoId());
        assertEquals("Produto teste", response.getProdutoNome());
        assertEquals(0, response.getMargemLucro().compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, response.getPercentualImposto().compareTo(BigDecimal.valueOf(10)));
        assertEquals(0, response.getLucroUnitario().compareTo(BigDecimal.valueOf(90)));
        assertEquals(0, response.getPrecoSugerido().compareTo(BigDecimal.valueOf(594)));

        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("T9 - EstrategiaPrecoServiceTest - Deve lançar exceção ao buscar estratégia por id inexistente")
    void t9_deveLancarExcecaoAoBuscarEstrategiaPorIdInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarEstrategiaPorId(1L));

        assertEquals("Estrategia de preco nao encontrada para o id informado", exception.getMessage());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("T10 - EstrategiaPrecoServiceTest - Deve buscar estrategias por produto id com sucesso")
    void t10_deveBuscarEstrategiasPorProdutoIdComSucesso() {
        EstrategiaPreco estrategiaPreco2 = EstrategiaPreco.builder()
                .id(2L)
                .produto(produto)
                .margemLucro(BigDecimal.valueOf(12))
                .percentualImposto(BigDecimal.valueOf(8))
                .precoSugerido(BigDecimal.valueOf(544.32))
                .lucroUnitario(BigDecimal.valueOf(54))
                .demandaEstimada(73)
                .lucroTotalEstimado(BigDecimal.valueOf(3942))
                .dataSimulacao(Instant.now())
                .build();

        when(repository.findByProduto_Id(1L)).thenReturn(List.of(estrategiaPreco, estrategiaPreco2));

        List<EstrategiaPrecoResponseDTO> response = service.buscarEstrategiaPorProdutoId(1L);

        assertNotNull(response);

        assertEquals(1L, response.get(0).getId());
        assertEquals(1L, response.get(0).getProdutoId());
        assertEquals("Produto teste", response.get(0).getProdutoNome());
        assertEquals("Eletrônicos", response.get(0).getCategoriaNome());
        assertEquals(0, response.get(0).getLucroTotalEstimado().compareTo(BigDecimal.valueOf(6300)));

        assertEquals(2L, response.get(1).getId());
        assertEquals(1L, response.get(1).getProdutoId());
        assertEquals("Produto teste", response.get(1).getProdutoNome());
        assertEquals("Eletrônicos", response.get(1).getCategoriaNome());
        assertEquals(0, response.get(1).getLucroTotalEstimado().compareTo(BigDecimal.valueOf(3942)));

        verify(repository, times(1)).findByProduto_Id(1L);
    }

    @Test
    @DisplayName("T11 - EstrategiaPrecoServiceTest - Deve retornar lista vazia ao buscar estratégias por produto id")
    void t11_deveRetornarListaVaziaAoBuscarEstrategiasPorProdutoId() {
        when(repository.findByProduto_Id(1L)).thenReturn(List.of());

        List<EstrategiaPrecoResponseDTO> response = service.buscarEstrategiaPorProdutoId(1L);

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(repository, times(1)).findByProduto_Id(1L);
    }

    @Test
    @DisplayName("T12 - EstrategiaPrecoServiceTest - Deve deletar estratégia por id com sucesso")
    void t12_deveDeletarEstrategiaPorIdComSucesso() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        service.deletarEstrategiaPorId(1L);

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("T13 - EstrategiaPrecoServiceTest - Deve lançar exceção ao deletar estrategia por id inexistente")
    void t13_deveLancarExcecaoAoDeletarEstrategiaPorIdInexistente() {
        when(repository.existsById(1L)).thenReturn(false);

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.deletarEstrategiaPorId(1L));

        assertEquals("Nenhuma estrategia de preco encontrada para o id informado", exception.getMessage());

        verify(repository, times(1)).existsById(1L);
        verify(repository, never()).deleteById(anyLong());
    }
}
