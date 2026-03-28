package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.dto.categoria.CategoriaRequestDTO;
import com.felipeboos.gestao_produtos.dto.categoria.CategoriaResponseDTO;
import com.felipeboos.gestao_produtos.dto.categoria.CategoriaUpdateDTO;
import com.felipeboos.gestao_produtos.entity.Categoria;
import com.felipeboos.gestao_produtos.exception.RecursoDuplicadoException;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.exception.RegraDeNegocioException;
import com.felipeboos.gestao_produtos.repository.CategoriaRepository;
import com.felipeboos.gestao_produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaRequestDTO categoriaRequestDTO;
    private CategoriaUpdateDTO categoriaUpdateDTO;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nome("Eletrônicos")
                .descricao("Categoria de produtos eletrônicos")
                .build();

        categoriaRequestDTO = new CategoriaRequestDTO();
        categoriaRequestDTO.setNome("Eletrônicos");
        categoriaRequestDTO.setDescricao("Categoria de produtos eletrônicos");

        categoriaUpdateDTO = new CategoriaUpdateDTO();
        categoriaUpdateDTO.setNome("Informática");
        categoriaUpdateDTO.setDescricao("Categoria atualizada");
    }

    @Test
    @DisplayName("T1 - CategoriaServiceTest - Deve salvar categoria com sucesso")
    void t1_deveSalvarCategoriaComSucesso() {
        when(repository.existsByNome("Eletrônicos")).thenReturn(false);
        when(repository.saveAndFlush(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponseDTO response = categoriaService.salvarCategoria(categoriaRequestDTO);

        assertNotNull(response);
        assertEquals("Eletrônicos", response.getNome());
        assertEquals("Categoria de produtos eletrônicos", response.getDescricao());

        verify(repository, times(1)).saveAndFlush(any(Categoria.class));
    }

    @Test
    @DisplayName("T2 - CategoriaServiceTest - Deve lançar exceção ao tentar salvar categoria com nome já cadastrado")
    void t2_deveLancarExcecaoAoTentarSalvarCategoriaComNomeJaCadastrado() {
        when(repository.existsByNome("Eletrônicos")).thenReturn(true);

        RecursoDuplicadoException exception = assertThrows(RecursoDuplicadoException.class,
                () -> categoriaService.salvarCategoria(categoriaRequestDTO));

        assertEquals("Já existe uma categoria cadastrada com esse nome", exception.getMessage());

        verify(repository, never()).saveAndFlush(any(Categoria.class));
    }

    @Test
    @DisplayName("T2 - CategoriaServiceTest - Deve buscar categoria por id com sucesso")
    void t2_deveBuscarCategoriaPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponseDTO response = categoriaService.buscarCategoriaPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Eletrônicos", response.getNome());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("T3 - CategoriaServiceTest - Deve lançar exceção ao buscar categoria por id inexistente")
    void t3_deveLancarExcecaoAoBuscarCategoriaPorIdInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> categoriaService.buscarCategoriaPorId(1L));

        assertEquals("Nenhuma categoria encontrada para o id informado", exception.getMessage());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("T4 - CategoriaServiceTest - Deve buscar categoria por nome com sucesso")
    void t4_deveBuscarCategoriaPorNomeComSucesso() {
        when(repository.findByNome("Eletrônicos")).thenReturn(Optional.of(categoria));

        List<CategoriaResponseDTO> response = categoriaService.buscarCategoriaPorNome("Eletrônicos");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Eletrônicos", response.get(0).getNome());

        verify(repository, times(1)).findByNome("Eletrônicos");
    }

    @Test
    @DisplayName("T5 - CategoriaServiceTest - Deve lançar exceção ao buscar categoria por nome inexistente")
    void t5_deveLancarExcecaoAoBuscarPorNomeInexistente() {
        when(repository.findByNome("Inexistente")).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> categoriaService.buscarCategoriaPorNome("Inexistente"));

        assertEquals("Nenhuma categoria encontrada para o nome informado", exception.getMessage());
        verify(repository, times(1)).findByNome("Inexistente");
    }

    @Test
    @DisplayName("T6 - CategoriaServiceTest - Deve listar todas as categorias com sucesso")
    void t6_deveListarTodasAsCategoriasComSucesso() {
        Categoria categoria2 = Categoria.builder()
                .id(2L)
                .nome("Móveis")
                .descricao("Categoria de móveis")
                .build();

        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(categoria, categoria2));

        List<CategoriaResponseDTO> response = categoriaService.listarTodasAsCategorias();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Eletrônicos", response.get(0).getNome());
        assertEquals("Móveis", response.get(1).getNome());

        verify(repository, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    @DisplayName("T7 - CategoriaServiceTest - Listar categorias deve retornar lista vazia quando não houver registros")
    void t7_deveRetornarListaVaziaAoListarCategorias() {
        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of());

        List<CategoriaResponseDTO> response = categoriaService.listarTodasAsCategorias();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(repository, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    @DisplayName("T8 - CategoriaServiceTest - Deve deletar categoria por id quando não houver produtos vinculados")
    void t8_deveDeletarCategoriaPorIdQuandoNaoHouverProdutosVinculados() {
        when(repository.existsById(1L)).thenReturn(true);
        when(produtoRepository.existsByCategoriaId(1L)).thenReturn(false);
        doNothing().when(repository).deleteById(1L);

        categoriaService.deletarCategoriaPorId(1L);

        verify(repository, times(1)).existsById(1L);
        verify(produtoRepository, times(1)).existsByCategoriaId(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("T9 - CategoriaServiceTest - Deve lançar exceção ao deletar categoria com produtos vinculados")
    void t9_deveLancarExcecaoAoDeletarCategoriaComProdutosVinculados() {
        when(repository.existsById(1L)).thenReturn(true);
        when(produtoRepository.existsByCategoriaId(1L)).thenReturn(true);

        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class,
                () -> categoriaService.deletarCategoriaPorId(1L));

        assertEquals("Nao eh possivel excluir uma categoria se ja existe um produto vinculado a ela",
                exception.getMessage());
        verify(produtoRepository, times(1)).existsByCategoriaId(1L);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("T10 - CategoriaServiceTest - Deve lançar exceção ao tentar deletar categoria com id não cadastrado")
    void t10_deveLancarExcecaoAoTentarDeletarCategoriaComIdNaoCadastrado() {
        when(repository.existsById(1L)).thenReturn(false);

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> categoriaService.deletarCategoriaPorId(1L));

        assertEquals("Nenhuma categoria encontrada para o id informado", exception.getMessage());
        verify(produtoRepository, never()).existsByCategoriaId(anyLong());
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("T11 - CategoriaServiceTest - Deve atualizar categoria por id com sucesso")
    void t11_deveAtualizarCategoriaPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        when(repository.existsByNome("Informática")).thenReturn(false);
        when(repository.saveAndFlush(any(Categoria.class))).thenReturn(categoria);

        categoriaService.atualizarCategoriaPorId(1L, categoriaUpdateDTO);

        assertEquals("Informática", categoria.getNome());
        assertEquals("Categoria atualizada", categoria.getDescricao());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByNome("Informática");
        verify(repository, times(1)).saveAndFlush(categoria);
    }

    @Test
    @DisplayName("Tx - CategoriaServiceTest - Deve lançar exceção ao atualizar categoria com nome já cadastrado")
    void tx_deveLancarExcecaoAoAtualizarCategoriaComNomeJaCadastrado() {
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        when(repository.existsByNome("Informática")).thenReturn(true);

        RecursoDuplicadoException exception = assertThrows(RecursoDuplicadoException.class,
                () -> categoriaService.atualizarCategoriaPorId(1L, categoriaUpdateDTO));

        assertEquals("Já existe uma categoria cadastrada com o nome informado", exception.getMessage());

        verify(repository, never()).saveAndFlush(any(Categoria.class));
    }

    @Test
    @DisplayName("T12 - CategoriaServiceTest - Deve atualizar apenas o nome quando descrição for nula")
    void t12_deveAtualizarApenasONomeQuandoDescricaoForNula() {
        when(repository.existsByNome("Novo nome")).thenReturn(false);
        CategoriaUpdateDTO updateParcial = new CategoriaUpdateDTO();
        updateParcial.setNome("Novo nome");
        updateParcial.setDescricao(null);

        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        when(repository.saveAndFlush(any(Categoria.class))).thenReturn(categoria);

        categoriaService.atualizarCategoriaPorId(1L, updateParcial);

        assertEquals("Novo nome", categoria.getNome());
        assertEquals("Categoria de produtos eletrônicos", categoria.getDescricao());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).saveAndFlush(categoria);
    }

    @Test
    @DisplayName("T13 - CategoriaServiceTest - Deve lançar exceção ao atualizar categoria com id inexistente")
    void t13_deveLancarExcecaoAoAtualizarCategoriaComIdInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class,
                () -> categoriaService.atualizarCategoriaPorId(1L, categoriaUpdateDTO));

        assertEquals("Nenhuma categoria encontrada para o Id informado", exception.getMessage());

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).saveAndFlush(any(Categoria.class));
    }
}