package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.dto.produto.ProdutoRequestDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoResponseDTO;
import com.felipeboos.gestao_produtos.dto.produto.ProdutoUpdateDTO;
import com.felipeboos.gestao_produtos.entity.Categoria;
import com.felipeboos.gestao_produtos.entity.Moeda;
import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.exception.RecursoDuplicadoException;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.repository.CategoriaRepository;
import com.felipeboos.gestao_produtos.repository.ProdutoRepository;
import com.felipeboos.gestao_produtos.service.cambio.CambioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaRepository catRepository;
    private final CambioService cambioService;

    public ProdutoService(
            ProdutoRepository repository,
            CategoriaRepository catRepository,
            CambioService cambioService
    ) {
        this.repository = repository;
        this.catRepository = catRepository;
        this.cambioService = cambioService;
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
        Produto produto = repository.findByNome(nome).orElseThrow(
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
        recalcularCamposMonetarios(produtoEntity);

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
        if (produtoPatch.getMoeda() != null) {
            produtoEntity.setMoeda(produtoPatch.getMoeda());
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
        if (produtoPatch.getImportado() != null) {
            produtoEntity.setImportado(produtoPatch.getImportado());
        }
        if (produtoPatch.getRemessaConforme() != null) {
            produtoEntity.setRemessaConforme(produtoPatch.getRemessaConforme());
        }
        if (produtoPatch.getFreteInternacional() != null) {
            produtoEntity.setFreteInternacional(produtoPatch.getFreteInternacional());
        }
        if (produtoPatch.getSeguroInternacional() != null) {
            produtoEntity.setSeguroInternacional(produtoPatch.getSeguroInternacional());
        }
        if (produtoPatch.getAliquotaIcmsImportacao() != null) {
            produtoEntity.setAliquotaIcmsImportacao(produtoPatch.getAliquotaIcmsImportacao());
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
                .importado(dto.getImportado())
                .remessaConforme(dto.getRemessaConforme())
                .freteInternacional(dto.getFreteInternacional())
                .seguroInternacional(dto.getSeguroInternacional())
                .aliquotaIcmsImportacao(dto.getAliquotaIcmsImportacao())
                .build();

        setCategoria(produto, dto.getCategoriaId());

        Moeda moeda = dto.getMoeda() != null ? dto.getMoeda() : Moeda.BRL;
        produto.setMoeda(moeda);

        recalcularCamposMonetarios(produto);

        return produto;
    }

    private void recalcularCamposMonetarios(Produto produto) {
        BigDecimal cotacao = cambioService.obterCotacao(produto.getMoeda());
        BigDecimal precoCustoEmReais = produto.getPrecoCusto().multiply(cotacao);

        produto.setCotacaoMoeda(cotacao);
        produto.setPrecoCustoEmReais(precoCustoEmReais);

        BigDecimal frete = valorOuZero(produto.getFreteInternacional());
        BigDecimal seguro = valorOuZero(produto.getSeguroInternacional());

        produto.setImpostoImportacao(BigDecimal.ZERO);
        produto.setIcmsImportacao(BigDecimal.ZERO);

        BigDecimal custoFinalAquisicao = precoCustoEmReais
                .add(frete)
                .add(seguro);

        produto.setCustoFinalAquisicao(custoFinalAquisicao);
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}