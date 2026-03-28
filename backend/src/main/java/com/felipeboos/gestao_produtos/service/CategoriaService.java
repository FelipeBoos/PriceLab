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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final ProdutoRepository produtoRepository;

    public CategoriaService(CategoriaRepository repository, ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
    }

    public CategoriaResponseDTO salvarCategoria(CategoriaRequestDTO dto) {

        if (repository.existsByNome(dto.getNome())) {
            throw new RecursoDuplicadoException("Já existe uma categoria cadastrada com esse nome");
        }

        Categoria categoria = toEntity(dto);

        Categoria categoriaSalva = repository.saveAndFlush(categoria);

        return CategoriaResponseDTO.fromEntity(categoriaSalva);
    }

    public CategoriaResponseDTO buscarCategoriaPorId(Long id) {

        Categoria categoria = repository.findById(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Nenhuma categoria encontrada para o id informado")
        );

        return CategoriaResponseDTO.fromEntity(categoria);
    }

    public List<CategoriaResponseDTO> buscarCategoriaPorNome(String nome) {

        Categoria categoria = repository.findByNome(nome).orElseThrow(
                () -> new RecursoNaoEncontradoException("Nenhuma categoria encontrada para o nome informado")
        );

        return List.of(CategoriaResponseDTO.fromEntity(categoria));
    }

    public List<CategoriaResponseDTO> listarTodasAsCategorias() {
        List<Categoria> listaCategorias = repository.findAllByOrderByIdAsc();

        List<CategoriaResponseDTO> listaCategoriasResponse = new ArrayList<>();

        for (Categoria categoria : listaCategorias) {
            listaCategoriasResponse.add(CategoriaResponseDTO.fromEntity(categoria));
        }

        return listaCategoriasResponse;
    }

    public void deletarCategoriaPorId(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Nenhuma categoria encontrada para o id informado");
        }

        if (produtoRepository.existsByCategoriaId(id)) {
            throw new RegraDeNegocioException(
                    "Nao eh possivel excluir uma categoria se ja existe um produto vinculado a ela"
            );
        }

        repository.deleteById(id);
    }

    public void atualizarCategoriaPorId(Long id, CategoriaUpdateDTO categoriaPatch) {

        Categoria categoriaEntity = repository.findById(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Nenhuma categoria encontrada para o Id informado")
        );

        if (categoriaPatch.getNome() != null
                && !categoriaEntity.getNome().equals(categoriaPatch.getNome())
                && repository.existsByNome(categoriaPatch.getNome())) {
            throw new RecursoDuplicadoException("Já existe uma categoria cadastrada com o nome informado");
        }

        aplicarAlteracoes(categoriaEntity, categoriaPatch);

        repository.saveAndFlush(categoriaEntity);
    }

    private void aplicarAlteracoes(Categoria catEntity, CategoriaUpdateDTO catPatch) {
        if (catPatch.getNome() != null) catEntity.setNome(catPatch.getNome());
        if (catPatch.getDescricao() != null) catEntity.setDescricao(catPatch.getDescricao());
    }

    private Categoria toEntity(CategoriaRequestDTO dto) {
        return Categoria.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .build();
    }
}
