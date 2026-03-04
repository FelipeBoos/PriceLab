package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.entity.Categoria;
import com.felipeboos.gestao_produtos.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public Categoria salvarCategoria(Categoria categoria) {
        return repository.saveAndFlush(categoria);
    }

    public Categoria buscarCategoriaPorId(Long id) {

        return repository.findById(id).orElseThrow(
                () -> new RuntimeException("Id nao encontrado")
        );
    }

    public Categoria buscarCategoriaPorNome(String nome) {

        return repository.findByNome(nome).orElseThrow(
                () -> new RuntimeException("Nome nao encontrado")
        );
    }

    public void deletarCategoriaPorId(Long id) {
        repository.deleteById(id);
    }

    public void atualizarPorId(Long id, Categoria categoriaPatch) {
        Categoria categoriaEntity = buscarCategoriaPorId(id);

        aplicarAlteracoes(categoriaEntity, categoriaPatch);

        repository.saveAndFlush(categoriaEntity);
    }

    private void aplicarAlteracoes(Categoria catEntity, Categoria catPatch) {
        if (catPatch.getNome() != null) catEntity.setNome(catPatch.getNome());
        if (catPatch.getDescricao() != null) catEntity.setDescricao(catPatch.getDescricao());
    }
}
