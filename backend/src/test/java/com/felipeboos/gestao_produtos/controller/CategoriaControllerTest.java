package com.felipeboos.gestao_produtos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipeboos.gestao_produtos.dto.categoria.CategoriaRequestDTO;
import com.felipeboos.gestao_produtos.dto.categoria.CategoriaResponseDTO;
import com.felipeboos.gestao_produtos.dto.categoria.CategoriaUpdateDTO;
import com.felipeboos.gestao_produtos.exception.RecursoDuplicadoException;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.exception.RegraDeNegocioException;
import com.felipeboos.gestao_produtos.handler.GlobalExceptionHandler;
import com.felipeboos.gestao_produtos.service.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
@Import(GlobalExceptionHandler.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CategoriaService service;

    @Test
    @DisplayName("T1 - CategoriaControllerTest - Deve buscar categoria por id com sucesso")
    void t1_deveBuscarCategoriaPorIdComSucesso() throws Exception {
        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Eletrônicos");
        responseDTO.setDescricao("Categoria teste 1");

        when(service.buscarCategoriaPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/categorias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Eletrônicos"))
                .andExpect(jsonPath("$.descricao").value("Categoria teste 1"));

        verify(service, times(1)).buscarCategoriaPorId(1L);
    }

    @Test
    @DisplayName("T2 - CategoriaControllerTest - Deve retornar NotFound ao buscar categoria por id inexistente")
    void t2_deveRetornarNotFoundAoBuscarCategoriaPorIdInexistente() throws Exception {
        when(service.buscarCategoriaPorId(1L))
                .thenThrow(new RecursoNaoEncontradoException("Nenhuma categoria encontrada para o id informado"));

        mockMvc.perform(get("/categorias/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value(
                        "Nenhuma categoria encontrada para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).buscarCategoriaPorId(1L);
    }

    @Test
    @DisplayName("T3 - CategoriaControllerTest - Deve listar todas as categorias com sucesso")
    void t3_deveListarTodasAsCategoriasComSucesso() throws Exception {
        CategoriaResponseDTO categoria1 = new CategoriaResponseDTO();
        categoria1.setId(1L);
        categoria1.setNome("Eletrônicos");
        categoria1.setDescricao("Categoria teste 1");

        CategoriaResponseDTO categoria2 = new CategoriaResponseDTO();
        categoria2.setId(2L);
        categoria2.setNome("Alimentos");
        categoria2.setDescricao("Categoria teste 2");

        when(service.listarTodasAsCategorias())
                .thenReturn(List.of(categoria1, categoria2));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Eletrônicos"))
                .andExpect(jsonPath("$[0].descricao").value("Categoria teste 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nome").value("Alimentos"))
                .andExpect(jsonPath("$[1].descricao").value("Categoria teste 2"));

        verify(service, times(1)).listarTodasAsCategorias();
    }

    @Test
    @DisplayName("T4 - CategoriaControllerTest - Deve buscar categorias por nome com sucesso")
    void t4_deveBuscarCategoriaPorNomeComSucesso() throws Exception {
        CategoriaResponseDTO categoria1 = new CategoriaResponseDTO();
        categoria1.setId(1L);
        categoria1.setNome("Eletrônicos");
        categoria1.setDescricao("Categoria teste 1");

        when(service.buscarCategoriaPorNome("Eletrônicos")).thenReturn(List.of(categoria1));

        mockMvc.perform(get("/categorias").param("nome", "Eletrônicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Eletrônicos"))
                .andExpect(jsonPath("$[0].descricao").value("Categoria teste 1"));

        verify(service, times(1)).buscarCategoriaPorNome("Eletrônicos");
    }

    @Test
    @DisplayName("T5 - CategoriaControllerTest - Deve retornar NotFound ao buscar categoria por nome inexistente")
    void t5_deveRetornarNotFoundAoBuscarCategoriaPorNomeInexistente() throws Exception {
        when(service.buscarCategoriaPorNome("Inexistente"))
                .thenThrow(new RecursoNaoEncontradoException("Nenhuma categoria encontrada para o nome informado"));

        mockMvc.perform(get("/categorias").param("nome", "Inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem")
                        .value("Nenhuma categoria encontrada para o nome informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).buscarCategoriaPorNome("Inexistente");
    }

    @Test
    @DisplayName("T6 - CategoriaControllerTest - Deve salvar categoria com sucesso")
    void t6_deveSalvarCategoriaComSucesso() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNome("Eletrônicos");
        requestDTO.setDescricao("Categoria teste 1");

        CategoriaResponseDTO responseDTO = new CategoriaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("Eletrônicos");
        responseDTO.setDescricao("Categoria teste 1");

        when(service.salvarCategoria(any(CategoriaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Eletrônicos"))
                .andExpect(jsonPath("$.descricao").value("Categoria teste 1"));

        verify(service, times(1)).salvarCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("T7 - CategoriaControllerTest - Deve retornar BadRequest ao salvar categoria sem nome")
    void t7_deveRetornarBadRequestAoSalvarCategoriaSemNome() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNome("");
        requestDTO.setDescricao("Categoria teste");

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("nome: O nome da categoria eh obrigatorio"));

        verify(service, never()).salvarCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("T8 - CategoriaControllerTest - Deve retornar BadRequest ao salvar categoria com nome maior que 50 caracteres")
    void t8_deveRetornarBadRequestAoSalvarCategoriaComNomeMaiorQue50Caracteres() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNome("Categoria com nome muito grande para ultrapassar o limite de cinquenta caracteres");
        requestDTO.setDescricao("Categoria de teste");

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]")
                        .value("nome: O nome da categoria deve ter no maximo 50 caracteres"));

        verify(service, never()).salvarCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("T9 - CategoriaControllerTest - Deve retornar BadRequest ao salvar categoria com descrição maior que 255 caracteres")
    void t9_deveRetornarBadRequestAoSalvarCategoriaComDescricaoMaiorQue255Caracteres() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNome("Eletrônicos");
        requestDTO.setDescricao("a".repeat(256));

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]")
                        .value("descricao: A descricao deve ter no maximo 255 caracteres"));

        verify(service, never()).salvarCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("T10 - CategoriaControllerTest - Deve retornar Conflict ao salvar categoria com nome duplicado")
    void t10_deveRetornarConflictAoSalvarCategoriaComNomeDuplicado() throws Exception {

        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO();
        requestDTO.setNome("Eletrônicos");
        requestDTO.setDescricao("Categoria de eletrônicos");

        when(service.salvarCategoria(any(CategoriaRequestDTO.class)))
                .thenThrow(new RecursoDuplicadoException("Já existe uma categoria cadastrada com esse nome"));

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem")
                        .value("Já existe uma categoria cadastrada com esse nome"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).salvarCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("T11 - CategoriaControllerTest - Deve atualizar categoria por id com sucesso")
    void t11_deveAtualizarCategoriaPorIdComSucesso() throws Exception {
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO();
        updateDTO.setNome("Nome atualizado");
        updateDTO.setDescricao("Descrição atualizada");

        doNothing().when(service).atualizarCategoriaPorId(eq(1L), any(CategoriaUpdateDTO.class));

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).atualizarCategoriaPorId(eq(1L), any(CategoriaUpdateDTO.class));
    }

    @Test
    @DisplayName("T12 - CategoriaControllerTest - Deve retornar BadRequest ao atualizar categoria com nome maior que 50 caracteres")
    void t12_deveRetornarBadRequestAoAtualizarCategoriaComNomeMaiorQue50Caracteres() throws Exception {
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO();
        updateDTO.setNome("Categoria com nome muito grande para ultrapassar o limite de cinquenta caracteres");
        updateDTO.setDescricao("Descrição válida");

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]")
                        .value("nome: size must be between 0 and 50"));

        verify(service, never()).atualizarCategoriaPorId(anyLong(), any(CategoriaUpdateDTO.class));
    }

    @Test
    @DisplayName("T13 - CategoriaControllerTest - Deve retornar BadRequest ao atualizar categoria com descrição maior que 255 caracteres")
    void t13_deveRetornarBadRequestAoAtualizarCategoriaComDescricaoMaiorQue255Caracteres() throws Exception {
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO();
        updateDTO.setNome("Eletrônicos");
        updateDTO.setDescricao("a".repeat(256));

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]")
                        .value("descricao: size must be between 0 and 255"));

        verify(service, never()).atualizarCategoriaPorId(anyLong(), any(CategoriaUpdateDTO.class));
    }

    @Test
    @DisplayName("T14 - CategoriaControllerTest - Deve retornar NotFound ao atualizar categoria por id inexistente")
    void t14_deveRetornarNotFoundAoAtualizarCategoriaPorIdInexistente() throws Exception {
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO();
        updateDTO.setNome("Nome atualizado");
        updateDTO.setDescricao("Descrição atualizada");

        doThrow(new RecursoNaoEncontradoException("Nenhuma categoria encontrada para o Id informado"))
                .when(service).atualizarCategoriaPorId(eq(1L), any(CategoriaUpdateDTO.class));

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem")
                        .value("Nenhuma categoria encontrada para o Id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).atualizarCategoriaPorId(eq(1L), any(CategoriaUpdateDTO.class));
    }

    @Test
    @DisplayName("T15 - CategoriaControllerTest - Deve retornar Conflict ao atualizar categoria com nome duplicado")
    void t15_deveRetornarConflictAoAtualizarCategoriaComNomeDuplicado() throws Exception {
        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO();
        updateDTO.setNome("Eletrônicos");
        updateDTO.setDescricao("Descrição atualizada");

        doThrow(new RecursoDuplicadoException("Já existe uma categoria cadastrada com o nome informado"))
                .when(service).atualizarCategoriaPorId(eq(1L), any(CategoriaUpdateDTO.class));

        mockMvc.perform(put("/categorias/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem")
                        .value("Já existe uma categoria cadastrada com o nome informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).atualizarCategoriaPorId(eq(1L), any(CategoriaUpdateDTO.class));
    }

    @Test
    @DisplayName("T16 - CategoriaControllerTest - Deve deletar categoria por id com sucesso")
    void t16_deveDeletarCategoriaPorIdComSucesso() throws Exception {
        doNothing().when(service).deletarCategoriaPorId(1L);

        mockMvc.perform(delete("/categorias/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deletarCategoriaPorId(1L);
    }

    @Test
    @DisplayName("T17 - CategoriaControllerTest - Deve retornar NotFound ao deletar categoria por id inexistente")
    void t17_deveRetornarNotFoundAoDeletarCategoriaPorIdInexistente() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Nenhuma categoria encontrada para o id informado"))
                .when(service).deletarCategoriaPorId(1L);

        mockMvc.perform(delete("/categorias/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem")
                        .value("Nenhuma categoria encontrada para o id informado"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).deletarCategoriaPorId(1L);
    }

    @Test
    @DisplayName("T18 - CategoriaControllerTest - Deve retornar BadRequest ao deletar categoria com produto vinculado")
    void t18_deveRetornarBadRequestAoDeletarCategoriaComProdutoVinculado() throws Exception {
        doThrow(new RegraDeNegocioException(
                "Nao eh possivel excluir uma categoria se ja existe um produto vinculado a ela"))
                .when(service).deletarCategoriaPorId(1L);

        mockMvc.perform(delete("/categorias/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem")
                        .value("Nao eh possivel excluir uma categoria se ja existe um produto vinculado a ela"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(service, times(1)).deletarCategoriaPorId(1L);
    }
}