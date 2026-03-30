package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.dto.produto.ProdutoRequestDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoResponseDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoUpdateDTO;
import com.felipeboos.gestao_produtos.entity.Categoria;
import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.exception.RecursoDuplicadoException;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.repository.CategoriaRepository;
import com.felipeboos.gestao_produtos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaRepository catRepository;

    public ProdutoService(ProdutoRepository repository, CategoriaRepository catRepository) {
        this.repository = repository;
        this.catRepository = catRepository;
    }

    @Transactional
    public ProdutoResponseDTO salvarProduto(ProdutoRequestDTO dto) {

        if (repository.existsByNome(dto.getNome())) {
            throw new RecursoDuplicadoException("Já existe um produto cadastrado com esse nome");
        }

        Produto produto = toEntity(dto);

        Produto produtoSalvo = repository.saveAndFlush(produto);

        return ProdutoResponseDTO.fromEntity(produtoSalvo);
    }

    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = repository.findById(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Produto nao encontrado para o id informado")
        );

        return ProdutoResponseDTO.fromEntity(produto);
    }

    public List<ProdutoResponseDTO> buscarProdutoPorNome(String nome) {

        Produto produto =  repository.findByNome(nome).orElseThrow(
                () -> new RecursoNaoEncontradoException("Produto nao encontrado para o nome informado")
        );

        return List.of(ProdutoResponseDTO.fromEntity(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodosOsProdutos() {
        List<Produto> listaProdutos = repository.findAllByOrderByIdAsc();

        List<ProdutoResponseDTO> listaProdutosResponse = new ArrayList<>();

        for (Produto produto : listaProdutos) {
            listaProdutosResponse.add(ProdutoResponseDTO.fromEntity(produto));
        }

        return listaProdutosResponse;
    }

    public void deletarProdutoPorId(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto nao encontrado para o id informado");
        }

        repository.deleteById(id);
    }

    public void atualizarProdutoPorId(Long id, ProdutoUpdateDTO produtoPatch) {

        Produto produtoEntity = repository.findById(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Produto nao encontrado para o id informado")
        );

        if (produtoPatch.getNome() != null
                && !produtoPatch.getNome().equals(produtoEntity.getNome())
                && repository.existsByNome(produtoPatch.getNome())) {
            throw new RecursoDuplicadoException("Já existe um produto cadastrado com esse nome");
        }

        aplicarAlteracoes(produtoEntity, produtoPatch);

        repository.saveAndFlush(produtoEntity);
    }

    private void aplicarAlteracoes(Produto produtoEntity, ProdutoUpdateDTO produtoPatch) {
        if (produtoPatch.getNome() != null) {
            produtoEntity.setNome(produtoPatch.getNome());
        }
        if (produtoPatch.getDescricao() != null) {
            produtoEntity.setDescricao(produtoPatch.getDescricao());
        }
        if (produtoPatch.getCategoriaId() != null) {
            setCategoria(produtoEntity, produtoPatch.getCategoriaId());
        }
        if (produtoPatch.getPrecoCusto() != null) {
            produtoEntity.setPrecoCusto(produtoPatch.getPrecoCusto());
        }
        if (produtoPatch.getPrecoVenda() != null) {
            produtoEntity.setPrecoVenda(produtoPatch.getPrecoVenda());
        }
        if (produtoPatch.getQuantidadeEstoque() != null) {
            produtoEntity.setQuantidadeEstoque(produtoPatch.getQuantidadeEstoque());
        }

        if (produtoPatch.getDemandaBase() != null) {
            produtoEntity.setDemandaBase(produtoPatch.getDemandaBase());
        }

        if (produtoPatch.getFatorElasticidade() != null) {
            produtoEntity.setFatorElasticidade(produtoPatch.getFatorElasticidade());
        }
    }

    private void setCategoria(Produto produtoEntity, Long categoriaId) {
        Categoria categoria = catRepository.findById(categoriaId).orElseThrow(
                () -> new RecursoNaoEncontradoException("Categoria nao encontrada para o id informado")
        );

        produtoEntity.setCategoria(categoria);
    }

    private Produto toEntity(ProdutoRequestDTO dto) {
        Produto produto = Produto.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .precoCusto(dto.getPrecoCusto())
                .precoVenda(dto.getPrecoVenda())
                .quantidadeEstoque(dto.getQuantidadeEstoque())
                .demandaBase(dto.getDemandaBase())
                .fatorElasticidade(dto.getFatorElasticidade())
                .build();

        setCategoria(produto, dto.getCategoriaId());

        return produto;
    }
}
