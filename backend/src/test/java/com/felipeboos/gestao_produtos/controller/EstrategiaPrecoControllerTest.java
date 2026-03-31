package com.felipeboos.gestao_produtos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoRequestDTO;
import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoResponseDTO;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.handler.GlobalExceptionHandler;
import com.felipeboos.gestao_produtos.service.EstrategiaPrecoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstrategiaPrecoController.class)
@Import(GlobalExceptionHandler.class)
class EstrategiaPrecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private EstrategiaPrecoService service;

    @Test
    @DisplayName("T1 - EstrategiaPrecoControllerTest - Deve buscar estrategia por id com sucesso")
    void t1_deveBuscarEstrategiaPorIdComSucesso() throws Exception {
        EstrategiaPrecoResponseDTO responseDTO = new EstrategiaPrecoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setProdutoId(1L);
        responseDTO.setProdutoNome("Notebook");
        responseDTO.setCategoriaNome("Eletrônicos");
        responseDTO.setPrecoUnidade(BigDecimal.valueOf(3000));
        responseDTO.setDemandaBase(100);
        responseDTO.setMargemLucro(BigDecimal.valueOf(20));
        responseDTO.setPercentualImposto(BigDecimal.valueOf(10));
        responseDTO.setPrecoSugerido(BigDecimal.valueOf(3960));
        responseDTO.setDemandaEstimada(80);
        responseDTO.setImpostoUnitario(BigDecimal.valueOf(360));
        responseDTO.setImpostoTotal(BigDecimal.valueOf(28800));
        responseDTO.setLucroUnitario(BigDecimal.valueOf(600));
        responseDTO.setLucroTotalEstimado(BigDecimal.valueOf(48000));
        responseDTO.setDataSimulacao(Instant.parse("2025-01-01T10:00:00Z"));

        when(service.buscarEstrategiaPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/estrategias-preco/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.produtoId").value(1L))
                .andExpect(jsonPath("$.produtoNome").value("Notebook"))
                .andExpect(jsonPath("$.categoriaNome").value("Eletrônicos"))
                .andExpect(jsonPath("$.precoUnidade").value(3000))
                .andExpect(jsonPath("$.demandaBase").value(100))
                .andExpect(jsonPath("$.margemLucro").value(20))
                .andExpect(jsonPath("$.percentualImposto").value(10))
                .andExpect(jsonPath("$.precoSugerido").value(3960))
                .andExpect(jsonPath("$.demandaEstimada").value(80))
                .andExpect(jsonPath("$.impostoUnitario").value(360))
                .andExpect(jsonPath("$.impostoTotal").value(28800))
                .andExpect(jsonPath("$.lucroUnitario").value(600))
                .andExpect(jsonPath("$.lucroTotalEstimado").value(48000))
                .andExpect(jsonPath("$.dataSimulacao").value("2025-01-01T10:00:00Z"));

        verify(service, times(1)).buscarEstrategiaPorId(1L);
    }

    @Test
    @DisplayName("T2 - EstrategiaPrecoControllerTest - Deve retornar NotFound ao buscar estrategia por id inexistente")
    void t2_deveRetornarNotFoundAoBuscarEstrategiaPorIdInexistente() throws Exception {
        when(service.buscarEstrategiaPorId(1L))
                .thenThrow(new RecursoNaoEncontradoException("Estrategia de preco nao encontrada para o id informado"));

        mockMvc.perform(get("/estrategias-preco/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Estrategia de preco nao encontrada para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).buscarEstrategiaPorId(1L);
    }

    @Test
    @DisplayName("T3 - EstrategiaPrecoControllerTest - Deve buscar estrategias por produto id com sucesso")
    void t3_deveBuscarEstrategiasPorProdutoIdComSucesso() throws Exception {
        EstrategiaPrecoResponseDTO estrategia1 = new EstrategiaPrecoResponseDTO();
        estrategia1.setId(1L);
        estrategia1.setProdutoId(1L);
        estrategia1.setProdutoNome("Notebook");
        estrategia1.setCategoriaNome("Eletrônicos");
        estrategia1.setPrecoSugerido(BigDecimal.valueOf(3960));
        estrategia1.setDemandaEstimada(80);

        EstrategiaPrecoResponseDTO estrategia2 = new EstrategiaPrecoResponseDTO();
        estrategia2.setId(2L);
        estrategia2.setProdutoId(1L);
        estrategia2.setProdutoNome("Notebook");
        estrategia2.setCategoriaNome("Eletrônicos");
        estrategia2.setPrecoSugerido(BigDecimal.valueOf(4200));
        estrategia2.setDemandaEstimada(75);

        when(service.buscarEstrategiaPorProdutoId(1L)).thenReturn(List.of(estrategia1, estrategia2));

        mockMvc.perform(get("/estrategias-preco/produto/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].produtoId").value(1L))
                .andExpect(jsonPath("$[0].produtoNome").value("Notebook"))
                .andExpect(jsonPath("$[0].precoSugerido").value(3960))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].produtoId").value(1L))
                .andExpect(jsonPath("$[1].produtoNome").value("Notebook"))
                .andExpect(jsonPath("$[1].precoSugerido").value(4200));

        verify(service, times(1)).buscarEstrategiaPorProdutoId(1L);
    }

    @Test
    @DisplayName("T4 - EstrategiaPrecoControllerTest - Deve listar todas as estrategias com sucesso")
    void t4_deveListarTodasAsEstrategiasComSucesso() throws Exception {
        EstrategiaPrecoResponseDTO estrategia1 = new EstrategiaPrecoResponseDTO();
        estrategia1.setId(1L);
        estrategia1.setProdutoId(1L);
        estrategia1.setProdutoNome("Notebook");
        estrategia1.setCategoriaNome("Eletrônicos");
        estrategia1.setPrecoSugerido(BigDecimal.valueOf(3960));

        EstrategiaPrecoResponseDTO estrategia2 = new EstrategiaPrecoResponseDTO();
        estrategia2.setId(2L);
        estrategia2.setProdutoId(2L);
        estrategia2.setProdutoNome("Mouse");
        estrategia2.setCategoriaNome("Periféricos");
        estrategia2.setPrecoSugerido(BigDecimal.valueOf(120));

        when(service.listarTodasAsEstrategiasDePreco()).thenReturn(List.of(estrategia1, estrategia2));

        mockMvc.perform(get("/estrategias-preco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].produtoNome").value("Notebook"))
                .andExpect(jsonPath("$[0].categoriaNome").value("Eletrônicos"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].produtoNome").value("Mouse"))
                .andExpect(jsonPath("$[1].categoriaNome").value("Periféricos"));

        verify(service, times(1)).listarTodasAsEstrategiasDePreco();
    }

    @Test
    @DisplayName("T5 - EstrategiaPrecoControllerTest - Deve simular estrategia de preco com sucesso")
    void t5_deveSimularEstrategiaPrecoComSucesso() throws Exception {
        EstrategiaPrecoRequestDTO requestDTO = new EstrategiaPrecoRequestDTO();
        requestDTO.setProdutoId(1L);
        requestDTO.setMargemLucro(BigDecimal.valueOf(20));
        requestDTO.setPercentualImposto(BigDecimal.valueOf(10));

        EstrategiaPrecoResponseDTO responseDTO = new EstrategiaPrecoResponseDTO();
        responseDTO.setProdutoId(1L);
        responseDTO.setProdutoNome("Notebook");
        responseDTO.setCategoriaNome("Eletrônicos");
        responseDTO.setMargemLucro(BigDecimal.valueOf(20));
        responseDTO.setPercentualImposto(BigDecimal.valueOf(10));
        responseDTO.setPrecoSugerido(BigDecimal.valueOf(3960));
        responseDTO.setDemandaEstimada(80);
        responseDTO.setImpostoUnitario(BigDecimal.valueOf(360));
        responseDTO.setImpostoTotal(BigDecimal.valueOf(28800));
        responseDTO.setLucroUnitario(BigDecimal.valueOf(600));
        responseDTO.setLucroTotalEstimado(BigDecimal.valueOf(48000));

        when(service.simularPreco(any(EstrategiaPrecoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/estrategias-preco/simular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtoId").value(1L))
                .andExpect(jsonPath("$.produtoNome").value("Notebook"))
                .andExpect(jsonPath("$.categoriaNome").value("Eletrônicos"))
                .andExpect(jsonPath("$.margemLucro").value(20))
                .andExpect(jsonPath("$.percentualImposto").value(10))
                .andExpect(jsonPath("$.precoSugerido").value(3960))
                .andExpect(jsonPath("$.demandaEstimada").value(80))
                .andExpect(jsonPath("$.impostoUnitario").value(360))
                .andExpect(jsonPath("$.impostoTotal").value(28800))
                .andExpect(jsonPath("$.lucroUnitario").value(600))
                .andExpect(jsonPath("$.lucroTotalEstimado").value(48000));

        verify(service, times(1)).simularPreco(any(EstrategiaPrecoRequestDTO.class));
    }

    @Test
    @DisplayName("T6 - EstrategiaPrecoControllerTest - Deve retornar BadRequest ao simular estrategia sem produtoId")
    void t6_deveRetornarBadRequestAoSimularEstrategiaSemProdutoId() throws Exception {
        EstrategiaPrecoRequestDTO requestDTO = new EstrategiaPrecoRequestDTO();
        requestDTO.setMargemLucro(BigDecimal.valueOf(20));
        requestDTO.setPercentualImposto(BigDecimal.valueOf(10));

        mockMvc.perform(post("/estrategias-preco/simular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("produtoId: O produto eh obrigatorio"));

        verify(service, never()).simularPreco(any(EstrategiaPrecoRequestDTO.class));
    }

    @Test
    @DisplayName("T7 - EstrategiaPrecoControllerTest - Deve retornar BadRequest ao simular estrategia com margem de lucro negativa")
    void t7_deveRetornarBadRequestAoSimularEstrategiaComMargemLucroNegativa() throws Exception {
        EstrategiaPrecoRequestDTO requestDTO = new EstrategiaPrecoRequestDTO();
        requestDTO.setProdutoId(1L);
        requestDTO.setMargemLucro(BigDecimal.valueOf(-1));
        requestDTO.setPercentualImposto(BigDecimal.valueOf(10));

        mockMvc.perform(post("/estrategias-preco/simular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("margemLucro: A margem de lucro nao pode ser negativa"));

        verify(service, never()).simularPreco(any(EstrategiaPrecoRequestDTO.class));
    }

    @Test
    @DisplayName("T8 - EstrategiaPrecoControllerTest - Deve retornar NotFound ao simular estrategia para produto inexistente")
    void t8_deveRetornarNotFoundAoSimularEstrategiaParaProdutoInexistente() throws Exception {
        EstrategiaPrecoRequestDTO requestDTO = new EstrategiaPrecoRequestDTO();
        requestDTO.setProdutoId(1L);
        requestDTO.setMargemLucro(BigDecimal.valueOf(20));
        requestDTO.setPercentualImposto(BigDecimal.valueOf(10));

        when(service.simularPreco(any(EstrategiaPrecoRequestDTO.class)))
                .thenThrow(new RecursoNaoEncontradoException("Produto nao encontrado para o id informado"));

        mockMvc.perform(post("/estrategias-preco/simular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Produto nao encontrado para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).simularPreco(any(EstrategiaPrecoRequestDTO.class));
    }

    @Test
    @DisplayName("T9 - EstrategiaPrecoControllerTest - Deve salvar estrategia de preco com sucesso")
    void t9_deveSalvarEstrategiaPrecoComSucesso() throws Exception {
        EstrategiaPrecoRequestDTO requestDTO = new EstrategiaPrecoRequestDTO();
        requestDTO.setProdutoId(1L);
        requestDTO.setMargemLucro(BigDecimal.valueOf(20));
        requestDTO.setPercentualImposto(BigDecimal.valueOf(10));

        EstrategiaPrecoResponseDTO responseDTO = new EstrategiaPrecoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setProdutoId(1L);
        responseDTO.setProdutoNome("Notebook");
        responseDTO.setCategoriaNome("Eletrônicos");
        responseDTO.setMargemLucro(BigDecimal.valueOf(20));
        responseDTO.setPercentualImposto(BigDecimal.valueOf(10));
        responseDTO.setPrecoSugerido(BigDecimal.valueOf(3960));
        responseDTO.setDemandaEstimada(80);
        responseDTO.setImpostoUnitario(BigDecimal.valueOf(360));
        responseDTO.setImpostoTotal(BigDecimal.valueOf(28800));
        responseDTO.setLucroUnitario(BigDecimal.valueOf(600));
        responseDTO.setLucroTotalEstimado(BigDecimal.valueOf(48000));

        when(service.criarEstrategiaPreco(any(EstrategiaPrecoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/estrategias-preco")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.produtoId").value(1L))
                .andExpect(jsonPath("$.produtoNome").value("Notebook"))
                .andExpect(jsonPath("$.categoriaNome").value("Eletrônicos"))
                .andExpect(jsonPath("$.precoSugerido").value(3960));

        verify(service, times(1)).criarEstrategiaPreco(any(EstrategiaPrecoRequestDTO.class));
    }

    @Test
    @DisplayName("T10 - EstrategiaPrecoControllerTest - Deve retornar BadRequest ao salvar estrategia sem percentual de imposto")
    void t10_deveRetornarBadRequestAoSalvarEstrategiaSemPercentualDeImposto() throws Exception {
        EstrategiaPrecoRequestDTO requestDTO = new EstrategiaPrecoRequestDTO();
        requestDTO.setProdutoId(1L);
        requestDTO.setMargemLucro(BigDecimal.valueOf(20));

        mockMvc.perform(post("/estrategias-preco")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("percentualImposto: O percentual de imposto eh obrigatorio"));

        verify(service, never()).criarEstrategiaPreco(any(EstrategiaPrecoRequestDTO.class));
    }

    @Test
    @DisplayName("T11 - EstrategiaPrecoControllerTest - Deve retornar BadRequest ao salvar estrategia com percentual de imposto negativo")
    void t11_deveRetornarBadRequestAoSalvarEstrategiaComPercentualDeImpostoNegativo() throws Exception {
        EstrategiaPrecoRequestDTO requestDTO = new EstrategiaPrecoRequestDTO();
        requestDTO.setProdutoId(1L);
        requestDTO.setMargemLucro(BigDecimal.valueOf(20));
        requestDTO.setPercentualImposto(BigDecimal.valueOf(-5));

        mockMvc.perform(post("/estrategias-preco")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("percentualImposto: O percentual de imposto nao pode ser negativo"));

        verify(service, never()).criarEstrategiaPreco(any(EstrategiaPrecoRequestDTO.class));
    }

    @Test
    @DisplayName("T12 - EstrategiaPrecoControllerTest - Deve retornar NotFound ao salvar estrategia para produto inexistente")
    void t12_deveRetornarNotFoundAoSalvarEstrategiaParaProdutoInexistente() throws Exception {
        EstrategiaPrecoRequestDTO requestDTO = new EstrategiaPrecoRequestDTO();
        requestDTO.setProdutoId(1L);
        requestDTO.setMargemLucro(BigDecimal.valueOf(20));
        requestDTO.setPercentualImposto(BigDecimal.valueOf(10));

        when(service.criarEstrategiaPreco(any(EstrategiaPrecoRequestDTO.class)))
                .thenThrow(new RecursoNaoEncontradoException("Produto nao encontrado para o id informado"));

        mockMvc.perform(post("/estrategias-preco")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Produto nao encontrado para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).criarEstrategiaPreco(any(EstrategiaPrecoRequestDTO.class));
    }

    @Test
    @DisplayName("T13 - EstrategiaPrecoControllerTest - Deve deletar estrategia por id com sucesso")
    void t13_deveDeletarEstrategiaPorIdComSucesso() throws Exception {
        doNothing().when(service).deletarEstrategiaPorId(1L);

        mockMvc.perform(delete("/estrategias-preco/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletarEstrategiaPorId(1L);
    }

    @Test
    @DisplayName("T14 - EstrategiaPrecoControllerTest - Deve retornar NotFound ao deletar estrategia por id inexistente")
    void t14_deveRetornarNotFoundAoDeletarEstrategiaPorIdInexistente() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Nenhuma estrategia de preco encontrada para o id informado"))
                .when(service).deletarEstrategiaPorId(1L);

        mockMvc.perform(delete("/estrategias-preco/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Nenhuma estrategia de preco encontrada para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).deletarEstrategiaPorId(1L);
    }
}