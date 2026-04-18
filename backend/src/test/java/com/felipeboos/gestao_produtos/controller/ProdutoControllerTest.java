package com.felipeboos.gestao_produtos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoRequestDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoResponseDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoUpdateDTO;
import com.felipeboos.gestao_produtos.entity.Moeda;
import com.felipeboos.gestao_produtos.exception.RecursoDuplicadoException;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.handler.GlobalExceptionHandler;
import com.felipeboos.gestao_produtos.service.ProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
@Import(GlobalExceptionHandler.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService service;

    @Test
    @DisplayName("T1 - ProdutoControllerTest - Deve buscar produto por id com sucesso")
    void t1_deveBuscarProdutoPorIdComSucesso() throws Exception {
        ProdutoResponseDTO responseDTO = new ProdutoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Produto teste 1");
        responseDTO.setDescricao("Descricao teste");
        responseDTO.setCategoriaId(1L);
        responseDTO.setCategoriaNome("Eletrônicos");
        responseDTO.setPrecoCusto(BigDecimal.valueOf(450));
        responseDTO.setMoeda(Moeda.BRL);
        responseDTO.setCotacaoMoeda(BigDecimal.ONE);
        responseDTO.setPrecoCustoEmReais(BigDecimal.valueOf(450));
        responseDTO.setImportado(false);
        responseDTO.setRemessaConforme(false);
        responseDTO.setFreteInternacional(BigDecimal.ZERO);
        responseDTO.setSeguroInternacional(BigDecimal.ZERO);
        responseDTO.setAliquotaIcmsImportacao(new BigDecimal("17.00"));
        responseDTO.setImpostoImportacao(BigDecimal.ZERO);
        responseDTO.setIcmsImportacao(BigDecimal.ZERO);
        responseDTO.setCustoFinalAquisicao(BigDecimal.valueOf(450));
        responseDTO.setPrecoVenda(BigDecimal.valueOf(600));
        responseDTO.setQuantidadeEstoque(10);
        responseDTO.setDemandaBase(100);
        responseDTO.setFatorElasticidade(BigDecimal.valueOf(0.05));

        when(service.buscarProdutoPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/produtos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Produto teste 1"))
                .andExpect(jsonPath("$.descricao").value("Descricao teste"))
                .andExpect(jsonPath("$.categoriaId").value(1L))
                .andExpect(jsonPath("$.categoriaNome").value("Eletrônicos"))
                .andExpect(jsonPath("$.precoCusto").value(450))
                .andExpect(jsonPath("$.precoVenda").value(600))
                .andExpect(jsonPath("$.quantidadeEstoque").value(10))
                .andExpect(jsonPath("$.demandaBase").value(100))
                .andExpect(jsonPath("$.fatorElasticidade").value(0.05));

        verify(service, times(1)).buscarProdutoPorId(1L);
    }

    @Test
    @DisplayName("T2 - ProdutoControllerTest - Deve retornar NotFound ao buscar produto por id inexistente")
    void t2_deveRetornarNotFoundAoBuscarProdutoPorIdInexistente() throws Exception {
        when(service.buscarProdutoPorId(1L))
                .thenThrow(new RecursoNaoEncontradoException("Produto nao encontrado para o id informado"));

        mockMvc.perform(get("/produtos/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Produto nao encontrado para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).buscarProdutoPorId(1L);
    }

    @Test
    @DisplayName("T3 - ProdutoControllerTest - Deve listar todos os produtos com sucesso")
    void t3_deveListarTodosOsProdutosComSucesso() throws Exception {
        ProdutoResponseDTO produto1 = new ProdutoResponseDTO();
        produto1.setId(1L);
        produto1.setNome("Produto teste 1");
        produto1.setCategoriaId(1L);
        produto1.setCategoriaNome("Eletrônicos");
        produto1.setPrecoCusto(BigDecimal.valueOf(450));
        produto1.setMoeda(Moeda.BRL);
        produto1.setCotacaoMoeda(BigDecimal.ONE);
        produto1.setPrecoCustoEmReais(BigDecimal.valueOf(450));
        produto1.setImportado(false);
        produto1.setRemessaConforme(false);
        produto1.setFreteInternacional(BigDecimal.ZERO);
        produto1.setSeguroInternacional(BigDecimal.ZERO);
        produto1.setAliquotaIcmsImportacao(new BigDecimal("17.00"));
        produto1.setImpostoImportacao(BigDecimal.ZERO);
        produto1.setIcmsImportacao(BigDecimal.ZERO);
        produto1.setCustoFinalAquisicao(BigDecimal.valueOf(450));
        produto1.setPrecoVenda(BigDecimal.valueOf(600));
        produto1.setQuantidadeEstoque(10);
        produto1.setDemandaBase(100);
        produto1.setFatorElasticidade(BigDecimal.valueOf(0.05));

        ProdutoResponseDTO produto2 = new ProdutoResponseDTO();
        produto2.setId(2L);
        produto2.setNome("Produto teste 2");
        produto2.setCategoriaId(1L);
        produto2.setCategoriaNome("Eletrônicos");
        produto2.setPrecoCusto(BigDecimal.valueOf(370));
        produto2.setMoeda(Moeda.BRL);
        produto2.setCotacaoMoeda(BigDecimal.ONE);
        produto2.setPrecoCustoEmReais(BigDecimal.valueOf(370));
        produto2.setImportado(false);
        produto2.setRemessaConforme(false);
        produto2.setFreteInternacional(BigDecimal.ZERO);
        produto2.setSeguroInternacional(BigDecimal.ZERO);
        produto2.setAliquotaIcmsImportacao(new BigDecimal("17.00"));
        produto2.setImpostoImportacao(BigDecimal.ZERO);
        produto2.setIcmsImportacao(BigDecimal.ZERO);
        produto2.setCustoFinalAquisicao(BigDecimal.valueOf(370));
        produto2.setPrecoVenda(BigDecimal.valueOf(430));
        produto2.setQuantidadeEstoque(25);
        produto2.setDemandaBase(80);
        produto2.setFatorElasticidade(BigDecimal.valueOf(0.07));

        when(service.listarTodosOsProdutos()).thenReturn(List.of(produto1, produto2));

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Produto teste 1"))
                .andExpect(jsonPath("$[0].categoriaNome").value("Eletrônicos"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nome").value("Produto teste 2"))
                .andExpect(jsonPath("$[1].categoriaNome").value("Eletrônicos"));

        verify(service, times(1)).listarTodosOsProdutos();
    }

    @Test
    @DisplayName("T4 - ProdutoControllerTest - Deve buscar produto por nome com sucesso")
    void t4_deveBuscarProdutoPorNomeComSucesso() throws Exception {
        ProdutoResponseDTO produto = new ProdutoResponseDTO();
        produto.setId(1L);
        produto.setNome("Produto teste 1");
        produto.setCategoriaId(1L);
        produto.setCategoriaNome("Eletrônicos");
        produto.setPrecoCusto(BigDecimal.valueOf(450));
        produto.setMoeda(Moeda.BRL);
        produto.setCotacaoMoeda(BigDecimal.ONE);
        produto.setPrecoCustoEmReais(BigDecimal.valueOf(450));
        produto.setImportado(false);
        produto.setRemessaConforme(false);
        produto.setFreteInternacional(BigDecimal.ZERO);
        produto.setSeguroInternacional(BigDecimal.ZERO);
        produto.setAliquotaIcmsImportacao(new BigDecimal("17.00"));
        produto.setImpostoImportacao(BigDecimal.ZERO);
        produto.setIcmsImportacao(BigDecimal.ZERO);
        produto.setCustoFinalAquisicao(BigDecimal.valueOf(450));
        produto.setPrecoVenda(BigDecimal.valueOf(600));
        produto.setQuantidadeEstoque(10);
        produto.setDemandaBase(100);
        produto.setFatorElasticidade(BigDecimal.valueOf(0.05));

        when(service.buscarProdutoPorNome("Produto teste 1")).thenReturn(List.of(produto));

        mockMvc.perform(get("/produtos").param("nome", "Produto teste 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Produto teste 1"))
                .andExpect(jsonPath("$[0].categoriaNome").value("Eletrônicos"));

        verify(service, times(1)).buscarProdutoPorNome("Produto teste 1");
    }

    @Test
    @DisplayName("T5 - ProdutoControllerTest - Deve retornar NotFound ao buscar produto por nome inexistente")
    void t5_deveRetornarNotFoundAoBuscarProdutoPorNomeInexistente() throws Exception {
        when(service.buscarProdutoPorNome("Inexistente"))
                .thenThrow(new RecursoNaoEncontradoException("Produto nao encontrado para o nome informado"));

        mockMvc.perform(get("/produtos").param("nome", "Inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Produto nao encontrado para o nome informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).buscarProdutoPorNome("Inexistente");
    }

    @Test
    @DisplayName("T6 - ProdutoControllerTest - Deve salvar produto com sucesso")
    void t6_deveSalvarProdutoComSucesso() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto teste 1");
        requestDTO.setDescricao("Descricao teste");
        requestDTO.setCategoriaId(1L);
        requestDTO.setPrecoCusto(BigDecimal.valueOf(450));
        requestDTO.setMoeda(Moeda.BRL);
        requestDTO.setPrecoVenda(BigDecimal.valueOf(600));
        requestDTO.setQuantidadeEstoque(10);
        requestDTO.setDemandaBase(100);
        requestDTO.setFatorElasticidade(BigDecimal.valueOf(0.05));
        requestDTO.setImportado(false);
        requestDTO.setRemessaConforme(false);
        requestDTO.setFreteInternacional(BigDecimal.ZERO);
        requestDTO.setSeguroInternacional(BigDecimal.ZERO);
        requestDTO.setAliquotaIcmsImportacao(new BigDecimal("17.00"));

        ProdutoResponseDTO responseDTO = new ProdutoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Produto teste 1");
        responseDTO.setDescricao("Descricao teste");
        responseDTO.setCategoriaId(1L);
        responseDTO.setCategoriaNome("Eletrônicos");
        responseDTO.setPrecoCusto(BigDecimal.valueOf(450));
        responseDTO.setMoeda(Moeda.BRL);
        responseDTO.setCotacaoMoeda(BigDecimal.ONE);
        responseDTO.setPrecoCustoEmReais(BigDecimal.valueOf(450));
        responseDTO.setImportado(false);
        responseDTO.setRemessaConforme(false);
        responseDTO.setFreteInternacional(BigDecimal.ZERO);
        responseDTO.setSeguroInternacional(BigDecimal.ZERO);
        responseDTO.setAliquotaIcmsImportacao(new BigDecimal("17.00"));
        responseDTO.setImpostoImportacao(BigDecimal.ZERO);
        responseDTO.setIcmsImportacao(BigDecimal.ZERO);
        responseDTO.setCustoFinalAquisicao(BigDecimal.valueOf(450));
        responseDTO.setPrecoVenda(BigDecimal.valueOf(600));
        responseDTO.setQuantidadeEstoque(10);
        responseDTO.setDemandaBase(100);
        responseDTO.setFatorElasticidade(BigDecimal.valueOf(0.05));

        when(service.salvarProduto(any(ProdutoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Produto teste 1"))
                .andExpect(jsonPath("$.categoriaId").value(1L))
                .andExpect(jsonPath("$.categoriaNome").value("Eletrônicos"));

        verify(service, times(1)).salvarProduto(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("T7 - ProdutoControllerTest - Deve retornar BadRequest ao salvar produto sem nome")
    void t7_deveRetornarBadRequestAoSalvarProdutoSemNome() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("");
        requestDTO.setCategoriaId(1L);
        requestDTO.setPrecoCusto(BigDecimal.valueOf(450));
        requestDTO.setMoeda(Moeda.BRL);
        requestDTO.setPrecoVenda(BigDecimal.valueOf(600));
        requestDTO.setQuantidadeEstoque(10);
        requestDTO.setImportado(false);
        requestDTO.setRemessaConforme(false);
        requestDTO.setFreteInternacional(BigDecimal.ZERO);
        requestDTO.setSeguroInternacional(BigDecimal.ZERO);
        requestDTO.setAliquotaIcmsImportacao(new BigDecimal("17.00"));

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("nome: O nome do produto eh obrigatorio"));

        verify(service, never()).salvarProduto(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("T8 - ProdutoControllerTest - Deve retornar BadRequest ao salvar produto sem categoria")
    void t8_deveRetornarBadRequestAoSalvarProdutoSemCategoria() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto teste 1");
        requestDTO.setPrecoCusto(BigDecimal.valueOf(450));
        requestDTO.setMoeda(Moeda.BRL);
        requestDTO.setPrecoVenda(BigDecimal.valueOf(600));
        requestDTO.setQuantidadeEstoque(10);
        requestDTO.setImportado(false);
        requestDTO.setRemessaConforme(false);
        requestDTO.setFreteInternacional(BigDecimal.ZERO);
        requestDTO.setSeguroInternacional(BigDecimal.ZERO);
        requestDTO.setAliquotaIcmsImportacao(new BigDecimal("17.00"));

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("categoriaId: A categoria do produto eh obrigatória"));

        verify(service, never()).salvarProduto(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("T9 - ProdutoControllerTest - Deve retornar BadRequest ao salvar produto com preco de custo negativo")
    void t9_deveRetornarBadRequestAoSalvarProdutoComPrecoDeCustoNegativo() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto teste 1");
        requestDTO.setCategoriaId(1L);
        requestDTO.setPrecoCusto(BigDecimal.valueOf(-1));
        requestDTO.setMoeda(Moeda.BRL);
        requestDTO.setPrecoVenda(BigDecimal.valueOf(600));
        requestDTO.setQuantidadeEstoque(10);
        requestDTO.setImportado(false);
        requestDTO.setRemessaConforme(false);
        requestDTO.setFreteInternacional(BigDecimal.ZERO);
        requestDTO.setSeguroInternacional(BigDecimal.ZERO);
        requestDTO.setAliquotaIcmsImportacao(new BigDecimal("17.00"));

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("precoCusto: O preco de custo nao pode ser negativo"));

        verify(service, never()).salvarProduto(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("T10 - ProdutoControllerTest - Deve retornar Conflict ao salvar produto com nome duplicado")
    void t10_deveRetornarConflictAoSalvarProdutoComNomeDuplicado() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto teste 1");
        requestDTO.setCategoriaId(1L);
        requestDTO.setPrecoCusto(BigDecimal.valueOf(450));
        requestDTO.setMoeda(Moeda.BRL);
        requestDTO.setPrecoVenda(BigDecimal.valueOf(600));
        requestDTO.setQuantidadeEstoque(10);
        requestDTO.setImportado(false);
        requestDTO.setRemessaConforme(false);
        requestDTO.setFreteInternacional(BigDecimal.ZERO);
        requestDTO.setSeguroInternacional(BigDecimal.ZERO);
        requestDTO.setAliquotaIcmsImportacao(new BigDecimal("17.00"));

        when(service.salvarProduto(any(ProdutoRequestDTO.class)))
                .thenThrow(new RecursoDuplicadoException("Já existe um produto cadastrado com esse nome"));

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value("Já existe um produto cadastrado com esse nome"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).salvarProduto(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("T11 - ProdutoControllerTest - Deve retornar NotFound ao salvar produto com categoria inexistente")
    void t11_deveRetornarNotFoundAoSalvarProdutoComCategoriaInexistente() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto teste 1");
        requestDTO.setCategoriaId(1L);
        requestDTO.setPrecoCusto(BigDecimal.valueOf(450));
        requestDTO.setMoeda(Moeda.BRL);
        requestDTO.setPrecoVenda(BigDecimal.valueOf(600));
        requestDTO.setQuantidadeEstoque(10);
        requestDTO.setImportado(false);
        requestDTO.setRemessaConforme(false);
        requestDTO.setFreteInternacional(BigDecimal.ZERO);
        requestDTO.setSeguroInternacional(BigDecimal.ZERO);
        requestDTO.setAliquotaIcmsImportacao(new BigDecimal("17.00"));

        when(service.salvarProduto(any(ProdutoRequestDTO.class)))
                .thenThrow(new RecursoNaoEncontradoException("Categoria nao encontrada para o id informado"));

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Categoria nao encontrada para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).salvarProduto(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("T12 - ProdutoControllerTest - Deve atualizar produto por id com sucesso")
    void t12_deveAtualizarProdutoPorIdComSucesso() throws Exception {
        ProdutoUpdateDTO updateDTO = new ProdutoUpdateDTO();
        updateDTO.setNome("Nome atualizado");
        updateDTO.setPrecoCusto(BigDecimal.valueOf(470));
        updateDTO.setPrecoVenda(BigDecimal.valueOf(580));
        updateDTO.setQuantidadeEstoque(8);

        doNothing().when(service).atualizarProdutoPorId(eq(1L), any(ProdutoUpdateDTO.class));

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).atualizarProdutoPorId(eq(1L), any(ProdutoUpdateDTO.class));
    }

    @Test
    @DisplayName("T13 - ProdutoControllerTest - Deve retornar BadRequest ao atualizar produto com nome maior que 50 caracteres")
    void t13_deveRetornarBadRequestAoAtualizarProdutoComNomeMaiorQue50Caracteres() throws Exception {
        ProdutoUpdateDTO updateDTO = new ProdutoUpdateDTO();
        updateDTO.setNome("Produto com nome muito grande para ultrapassar o limite de cinquenta caracteres");

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("nome: O nome nao pode ter mais que 50 caracteres"));

        verify(service, never()).atualizarProdutoPorId(anyLong(), any(ProdutoUpdateDTO.class));
    }

    @Test
    @DisplayName("T14 - ProdutoControllerTest - Deve retornar BadRequest ao atualizar produto com categoriaId nao positivo")
    void t14_deveRetornarBadRequestAoAtualizarProdutoComCategoriaIdNaoPositivo() throws Exception {
        ProdutoUpdateDTO updateDTO = new ProdutoUpdateDTO();
        updateDTO.setCategoriaId(0L);

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("categoriaId: O id da categoria deve ser positivo"));

        verify(service, never()).atualizarProdutoPorId(anyLong(), any(ProdutoUpdateDTO.class));
    }

    @Test
    @DisplayName("T15 - ProdutoControllerTest - Deve retornar NotFound ao atualizar produto por id inexistente")
    void t15_deveRetornarNotFoundAoAtualizarProdutoPorIdInexistente() throws Exception {
        ProdutoUpdateDTO updateDTO = new ProdutoUpdateDTO();
        updateDTO.setNome("Nome atualizado");

        doThrow(new RecursoNaoEncontradoException("Produto nao encontrado para o id informado"))
                .when(service).atualizarProdutoPorId(eq(1L), any(ProdutoUpdateDTO.class));

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Produto nao encontrado para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).atualizarProdutoPorId(eq(1L), any(ProdutoUpdateDTO.class));
    }

    @Test
    @DisplayName("T16 - ProdutoControllerTest - Deve retornar Conflict ao atualizar produto com nome duplicado")
    void t16_deveRetornarConflictAoAtualizarProdutoComNomeDuplicado() throws Exception {
        ProdutoUpdateDTO updateDTO = new ProdutoUpdateDTO();
        updateDTO.setNome("Nome duplicado");

        doThrow(new RecursoDuplicadoException("Já existe um produto cadastrado com esse nome"))
                .when(service).atualizarProdutoPorId(eq(1L), any(ProdutoUpdateDTO.class));

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value("Já existe um produto cadastrado com esse nome"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).atualizarProdutoPorId(eq(1L), any(ProdutoUpdateDTO.class));
    }

    @Test
    @DisplayName("T17 - ProdutoControllerTest - Deve retornar NotFound ao atualizar produto com categoria inexistente")
    void t17_deveRetornarNotFoundAoAtualizarProdutoComCategoriaInexistente() throws Exception {
        ProdutoUpdateDTO updateDTO = new ProdutoUpdateDTO();
        updateDTO.setCategoriaId(2L);

        doThrow(new RecursoNaoEncontradoException("Categoria nao encontrada para o id informado"))
                .when(service).atualizarProdutoPorId(eq(1L), any(ProdutoUpdateDTO.class));

        mockMvc.perform(put("/produtos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Categoria nao encontrada para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).atualizarProdutoPorId(eq(1L), any(ProdutoUpdateDTO.class));
    }

    @Test
    @DisplayName("T18 - ProdutoControllerTest - Deve deletar produto por id com sucesso")
    void t18_deveDeletarProdutoPorIdComSucesso() throws Exception {
        doNothing().when(service).deletarProdutoPorId(1L);

        mockMvc.perform(delete("/produtos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletarProdutoPorId(1L);
    }

    @Test
    @DisplayName("T19 - ProdutoControllerTest - Deve retornar NotFound ao deletar produto por id inexistente")
    void t19_deveRetornarNotFoundAoDeletarProdutoPorIdInexistente() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Produto nao encontrado para o id informado"))
                .when(service).deletarProdutoPorId(1L);

        mockMvc.perform(delete("/produtos/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Produto nao encontrado para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).deletarProdutoPorId(1L);
    }
}