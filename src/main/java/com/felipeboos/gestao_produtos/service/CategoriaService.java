package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.dto.categoria.CategoriaRequestDTO;
import com.felipeboos.gestao_produtos.dto.categoria.CategoriaResponseDTO;
import com.felipeboos.gestao_produtos.dto.categoria.CategoriaUpdateDTO;
import com.felipeboos.gestao_produtos.entity.Categoria;
import com.felipeboos.gestao_produtos.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public CategoriaResponseDTO salvarCategoria(CategoriaRequestDTO dto) {

        Categoria categoria = toEntity(dto);

        Categoria categoriaSalva = repository.saveAndFlush(categoria);

        return CategoriaResponseDTO.fromEntity(categoriaSalva);
    }

    public CategoriaResponseDTO buscarCategoriaPorId(Long id) {

        Categoria categoria = repository.findById(id).orElseThrow(
                () -> new RuntimeException("Id nao encontrado")
        );

        return CategoriaResponseDTO.fromEntity(categoria);
    }

    public CategoriaResponseDTO buscarCategoriaPorNome(String nome) {

        Categoria categoria = repository.findByNome(nome).orElseThrow(
                () -> new RuntimeException("Nome nao encontrado")
        );

        return CategoriaResponseDTO.fromEntity(categoria);
    }

    public void deletarCategoriaPorId(Long id) {
        repository.deleteById(id);
    }

    public void atualizarCategoriaPorId(Long id, CategoriaUpdateDTO categoriaPatch) {

        Categoria categoriaEntity = repository.findById(id).orElseThrow(
                () -> new RuntimeException("Nenhuma categoria encontrada para o Id informado")
        );

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
