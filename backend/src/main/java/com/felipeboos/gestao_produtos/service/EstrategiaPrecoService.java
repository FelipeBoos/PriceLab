package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoRequestDTO;
import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoResponseDTO;
import com.felipeboos.gestao_produtos.entity.EstrategiaPreco;
import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.exception.RecursoNaoEncontradoException;
import com.felipeboos.gestao_produtos.repository.EstrategiaPrecoRepository;
import com.felipeboos.gestao_produtos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class EstrategiaPrecoService {

    private static final int SCALE_MONETARIO = 2;
    private static final int SCALE_PERCENTUAL = 4;

    private final ProdutoRepository produtoRepository;
    private final EstrategiaPrecoRepository estrategiaPrecoRepository;

    public EstrategiaPrecoService(
            ProdutoRepository produtoRepository,
            EstrategiaPrecoRepository estrategiaPrecoRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.estrategiaPrecoRepository = estrategiaPrecoRepository;
    }

    @Transactional(readOnly = true)
    public EstrategiaPrecoResponseDTO simularPreco(EstrategiaPrecoRequestDTO request) {
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado para o id informado"));

        EstrategiaPreco estrategia = calcularEstrategiaPreco(request, produto);

        return toResponseDTO(estrategia);
    }

    @Transactional
    public EstrategiaPrecoResponseDTO criarEstrategiaPreco(EstrategiaPrecoRequestDTO request) {
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado para o id informado"));

        EstrategiaPreco estrategia = calcularEstrategiaPreco(request, produto);
        EstrategiaPreco estrategiaSalva = estrategiaPrecoRepository.saveAndFlush(estrategia);

        return toResponseDTO(estrategiaSalva);
    }

    @Transactional(readOnly = true)
    public List<EstrategiaPrecoResponseDTO> listarTodasAsEstrategiasDePreco() {
        List<EstrategiaPreco> listaEstrategiasEntidade = estrategiaPrecoRepository.findAllByOrderByIdAsc();
        List<EstrategiaPrecoResponseDTO> listaEstrategiasResponse = new ArrayList<>();

        for (EstrategiaPreco entidade : listaEstrategiasEntidade) {
            listaEstrategiasResponse.add(toResponseDTO(entidade));
        }

        return listaEstrategiasResponse;
    }

    @Transactional(readOnly = true)
    public EstrategiaPrecoResponseDTO buscarEstrategiaPorId(Long id) {
        EstrategiaPreco estrategiaPreco = estrategiaPrecoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estrategia de preco nao encontrada para o id informado"));

        return toResponseDTO(estrategiaPreco);
    }

    @Transactional(readOnly = true)
    public List<EstrategiaPrecoResponseDTO> buscarEstrategiaPorProdutoId(Long id) {
        List<EstrategiaPreco> listaEstrategiasEntidade = estrategiaPrecoRepository.findByProduto_Id(id);
        List<EstrategiaPrecoResponseDTO> listaEstrategiasResponse = new ArrayList<>();

        for (EstrategiaPreco entidade : listaEstrategiasEntidade) {
            listaEstrategiasResponse.add(toResponseDTO(entidade));
        }

        return listaEstrategiasResponse;
    }

    @Transactional
    public void deletarEstrategiaPorId(Long id) {
        if (!estrategiaPrecoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Nenhuma estrategia de preco encontrada para o id informado");
        }

        estrategiaPrecoRepository.deleteById(id);
    }

    private EstrategiaPreco calcularEstrategiaPreco(EstrategiaPrecoRequestDTO request, Produto produto) {
        BigDecimal margemLucroFracao = converterPercentualParaFracao(request.getMargemLucro());
        BigDecimal percentualImpostoFracao = converterPercentualParaFracao(request.getPercentualImposto());

        BigDecimal precoSugerido = calcularPrecoSugerido(
                produto.getPrecoCusto(),
                margemLucroFracao,
                percentualImpostoFracao
        );

        BigDecimal impostoUnitario = calcularImpostoUnitario(
                precoSugerido,
                percentualImpostoFracao
        );

        BigDecimal lucroUnitario = calcularLucroUnitario(
                produto.getPrecoCusto(),
                precoSugerido,
                impostoUnitario
        );

        Integer demandaEstimada = calcularDemandaEstimada(
                produto.getDemandaBase(),
                precoSugerido,
                produto.getFatorElasticidade()
        );

        BigDecimal lucroTotalEstimado = calcularLucroTotalEstimado(lucroUnitario, demandaEstimada);

        return toEntity(
                request,
                produto,
                precoSugerido,
                lucroUnitario,
                demandaEstimada,
                lucroTotalEstimado,
                Instant.now()
        );
    }

    private EstrategiaPrecoResponseDTO toResponseDTO(EstrategiaPreco estrategiaPreco) {
        EstrategiaPrecoResponseDTO response = EstrategiaPrecoResponseDTO.fromEntity(estrategiaPreco);

        BigDecimal percentualImpostoFracao = converterPercentualParaFracao(estrategiaPreco.getPercentualImposto());

        BigDecimal impostoUnitario = calcularImpostoUnitario(
                estrategiaPreco.getPrecoSugerido(),
                percentualImpostoFracao
        );

        BigDecimal impostoTotal = calcularImpostoTotal(
                impostoUnitario,
                estrategiaPreco.getDemandaEstimada()
        );

        response.setImpostoUnitario(impostoUnitario);
        response.setImpostoTotal(impostoTotal);

        return response;
    }

    private BigDecimal calcularPrecoSugerido(
            BigDecimal precoCusto,
            BigDecimal margemLucroFracao,
            BigDecimal percentualImpostoFracao
    ) {
        // precoSugerido = precoCusto * (1 + margemLucro) * (1 + percentualImposto)
        BigDecimal fatorMargemLucro = BigDecimal.ONE.add(margemLucroFracao);
        BigDecimal fatorPercentualImposto = BigDecimal.ONE.add(percentualImpostoFracao);

        return precoCusto
                .multiply(fatorMargemLucro)
                .multiply(fatorPercentualImposto)
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularImpostoUnitario(
            BigDecimal precoSugerido,
            BigDecimal percentualImpostoFracao
    ) {
        // impostoUnitario = parte do imposto embutida no precoSugerido
        BigDecimal precoSemImposto = precoSugerido.divide(
                BigDecimal.ONE.add(percentualImpostoFracao),
                SCALE_MONETARIO,
                RoundingMode.HALF_UP
        );

        return precoSugerido.subtract(precoSemImposto)
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularLucroUnitario(
            BigDecimal precoCusto,
            BigDecimal precoSugerido,
            BigDecimal impostoUnitario
    ) {
        // lucroUnitario = precoSugerido - precoCusto - impostoUnitario
        return precoSugerido.subtract(precoCusto)
                .subtract(impostoUnitario)
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private Integer calcularDemandaEstimada(
            Integer demandaBase,
            BigDecimal precoSugerido,
            BigDecimal fatorElasticidade
    ) {
        // demandaEstimada = demandaBase - (precoSugerido * fatorElasticidade)
        Integer quedaEstimadaVendas = precoSugerido.multiply(fatorElasticidade)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        Integer demandaEstimada = demandaBase - quedaEstimadaVendas;

        return Math.max(demandaEstimada, 0);
    }

    private BigDecimal calcularImpostoTotal(
            BigDecimal impostoUnitario,
            Integer demandaEstimada
    ) {
        return impostoUnitario.multiply(BigDecimal.valueOf(demandaEstimada))
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularLucroTotalEstimado(
            BigDecimal lucroUnitario,
            Integer demandaEstimada
    ) {
        // lucroTotalEstimado = lucroUnitario * demandaEstimada
        return lucroUnitario.multiply(BigDecimal.valueOf(demandaEstimada))
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private EstrategiaPreco toEntity(
            EstrategiaPrecoRequestDTO dto,
            Produto produto,
            BigDecimal precoSugerido,
            BigDecimal lucroUnitario,
            Integer demandaEstimada,
            BigDecimal lucroTotalEstimado,
            Instant dataSimulacao
    ) {
        return EstrategiaPreco.builder()
                .produto(produto)
                .margemLucro(dto.getMargemLucro())
                .percentualImposto(dto.getPercentualImposto())
                .precoSugerido(precoSugerido)
                .lucroUnitario(lucroUnitario)
                .demandaEstimada(demandaEstimada)
                .lucroTotalEstimado(lucroTotalEstimado)
                .dataSimulacao(dataSimulacao)
                .build();
    }

    private BigDecimal converterPercentualParaFracao(BigDecimal percentual) {
        return percentual.divide(BigDecimal.valueOf(100), SCALE_PERCENTUAL, RoundingMode.HALF_UP);
    }
}