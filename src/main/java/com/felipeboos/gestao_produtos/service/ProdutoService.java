package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto salvarProduto(Produto produto) {
        return repository.saveAndFlush(produto);
    }

    public Produto buscarProdutoPorId(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException("Id não encontrado")
        );
    }

    public Produto buscarProdutoPorNome(String nome) {

        return repository.findByNome(nome).orElseThrow(
                () -> new RuntimeException("Nome não encontrado")
        );
    }

    public void deletarProdutoPorId(Long id) {
        repository.deleteById(id);
    }

    public void atualizarProdutoPorId(Long id, Produto produtoPatch) {
        Produto produtoEntity = buscarProdutoPorId(id);

        aplicarAlteracoes(produtoEntity, produtoPatch);

        repository.saveAndFlush(produtoEntity);
    }

    public void atualizarProdutoPorNome(String nome, Produto produtoPatch) {
        Produto produtoEntity = buscarProdutoPorNome(nome);

        aplicarAlteracoes(produtoEntity, produtoPatch);

        repository.saveAndFlush(produtoEntity);
    }

    private void aplicarAlteracoes(Produto produtoEntity, Produto produtoPatch) {
        if (produtoPatch.getNome() != null) produtoEntity.setNome(produtoPatch.getNome());
        if (produtoPatch.getDescricao() != null) produtoEntity.setDescricao(produtoPatch.getDescricao());
        if (produtoPatch.getPrecoCusto() != null) produtoEntity.setPrecoCusto(produtoPatch.getPrecoCusto());
        if (produtoPatch.getPrecoVenda() != null) produtoEntity.setPrecoVenda(produtoPatch.getPrecoVenda());
        if (produtoPatch.getQuantidadeEstoque() != null) {
            produtoEntity.setQuantidadeEstoque(produtoPatch.getQuantidadeEstoque());
        }
    }
}
