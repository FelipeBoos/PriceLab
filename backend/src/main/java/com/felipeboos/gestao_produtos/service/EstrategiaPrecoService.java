package com.felipeboos.gestao_produtos.service;

import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoRequestDTO;
import com.felipeboos.gestao_produtos.dto.estrategiapreco.EstrategiaPrecoResponseDTO;
import com.felipeboos.gestao_produtos.entity.EstrategiaPreco;
import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.repository.EstrategiaPrecoRepository;
import com.felipeboos.gestao_produtos.repository.ProdutoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class EstrategiaPrecoService {

    private final ProdutoRepository produtoRepository;
    private final EstrategiaPrecoRepository estrategiaPrecoRepository;

    public EstrategiaPrecoService(
            ProdutoRepository produtoRepository, EstrategiaPrecoRepository estrategiaPrecoRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.estrategiaPrecoRepository = estrategiaPrecoRepository;
    }

    public EstrategiaPrecoResponseDTO simularPreco(EstrategiaPrecoRequestDTO request) {
        Produto produto = produtoRepository.findById(request.getProdutoId()).orElseThrow(
                () -> new RuntimeException("Produto nao encontrado")
        );

        EstrategiaPreco estrategia = calcularEstrategiaPreco(request, produto);

        return EstrategiaPrecoResponseDTO.fromEntity(estrategia);
    }

    public EstrategiaPrecoResponseDTO criarEstrategiaPreco(EstrategiaPrecoRequestDTO request) {
        Produto produto = produtoRepository.findById(request.getProdutoId()).orElseThrow(
                () -> new RuntimeException("Produto nao encontrado")
        );

        EstrategiaPreco estrategia = calcularEstrategiaPreco(request, produto);

        EstrategiaPreco estrategiaSalva = estrategiaPrecoRepository.saveAndFlush(estrategia);

        return EstrategiaPrecoResponseDTO.fromEntity(estrategiaSalva);
    }

    @Transactional(readOnly = true)
    public List<EstrategiaPrecoResponseDTO> listarTodasAsEstrategiasDePreco() {
        List<EstrategiaPreco> listaEstrategiasEntidade = estrategiaPrecoRepository.findAllByOrderByIdAsc();
        List<EstrategiaPrecoResponseDTO> listaEstrategiasResponse = new ArrayList<>();

        for (EstrategiaPreco entidade : listaEstrategiasEntidade) {
            listaEstrategiasResponse.add(EstrategiaPrecoResponseDTO.fromEntity(entidade));
        }

        return listaEstrategiasResponse;
    }

    @Transactional(readOnly = true)
    public EstrategiaPrecoResponseDTO buscarEstrategiaPorId(Long id) {
        EstrategiaPreco estrategiaPreco = estrategiaPrecoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Estrategia de preco nao encontrada")
        );

        return EstrategiaPrecoResponseDTO.fromEntity(estrategiaPreco);
    }

    @Transactional(readOnly = true)
    public List<EstrategiaPrecoResponseDTO> buscarEstrategiaPorProdutoId(Long id) {
        List<EstrategiaPreco> listaEstrategiasEntidade = estrategiaPrecoRepository.findByProduto_Id(id);
        List<EstrategiaPrecoResponseDTO> listaEstrategiasResponse = new ArrayList<>();

        for (EstrategiaPreco entidade : listaEstrategiasEntidade) {
            listaEstrategiasResponse.add(EstrategiaPrecoResponseDTO.fromEntity(entidade));
        }

        return listaEstrategiasResponse;
    }

    public void deletarEstrategiaPorId(Long id) { estrategiaPrecoRepository.deleteById(id); }

    private EstrategiaPreco calcularEstrategiaPreco(EstrategiaPrecoRequestDTO request, Produto produto) {

        BigDecimal margemLucroFracao = converterPercentualParaFracao(request.getMargemLucro());
        BigDecimal percentualImpostoFracao = converterPercentualParaFracao(request.getPercentualImposto());

        BigDecimal precoSugerido = calcularPrecoSugerido(
                produto.getPrecoCusto(), margemLucroFracao, percentualImpostoFracao);

        BigDecimal lucroUnitario = calcularLucroUnitario(
                produto.getPrecoCusto(), precoSugerido, percentualImpostoFracao);

        Integer demandaEstimada = calcularDemandaEstimada(
                produto.getDemandaBase(), precoSugerido, produto.getFatorElasticidade());

        BigDecimal lucroTotalEstimado = calcularLucroTotalEstimado(lucroUnitario, demandaEstimada);

        return toEntity(request, produto, precoSugerido, lucroUnitario,
                demandaEstimada, lucroTotalEstimado, Instant.now());
    }

    private BigDecimal calcularPrecoSugerido(
            BigDecimal precoCusto,
            BigDecimal margemLucro,
            BigDecimal percentualImposto
    ) {
        // precoSugerido = precoCusto * (1 + margemLucro) * (1 + percentualImposto)
        BigDecimal fatorMargemLucro = BigDecimal.ONE.add(margemLucro);
        BigDecimal fatorPercentualImposto = BigDecimal.ONE.add(percentualImposto);

        return precoCusto.multiply(fatorMargemLucro).multiply(fatorPercentualImposto);
    }

    private BigDecimal calcularLucroUnitario(
            BigDecimal precoCusto,
            BigDecimal precoSugerido,
            BigDecimal percentualImposto
    ) {
        // lucroUnitario = precoSugerido - precoCusto - valorImposto

        BigDecimal valorSemImposto = precoSugerido.divide(
                BigDecimal.ONE.add(percentualImposto),
                2,
                RoundingMode.HALF_UP
        );
        BigDecimal valorImposto = precoSugerido.subtract(valorSemImposto);

        return precoSugerido.subtract(precoCusto).subtract(valorImposto);
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

        return (demandaEstimada > 0) ? demandaEstimada : 0;
    }

    private BigDecimal calcularLucroTotalEstimado(
            BigDecimal lucroUnitario,
            Integer demandaEstimada
    ) {
        // lucroTotalEstimado = lucroUnitario * demandaEstimada
        return lucroUnitario.multiply(
                BigDecimal.valueOf(demandaEstimada)
        );
    }

    private EstrategiaPreco toEntity(
            EstrategiaPrecoRequestDTO dto, Produto produto, BigDecimal precoSugerido, BigDecimal lucroUnitario,
            Integer demandaEstimada, BigDecimal lucroTotalEstimado, Instant dataSimulacao
    ) {
        EstrategiaPreco estrategiaPreco = EstrategiaPreco.builder()
                .produto(produto)
                .margemLucro(dto.getMargemLucro())
                .percentualImposto(dto.getPercentualImposto())
                .precoSugerido(precoSugerido)
                .lucroUnitario(lucroUnitario)
                .demandaEstimada(demandaEstimada)
                .lucroTotalEstimado(lucroTotalEstimado)
                .dataSimulacao(dataSimulacao)
                .build();

        return estrategiaPreco;
    }

    private BigDecimal converterPercentualParaFracao(BigDecimal percentual) {
        return percentual.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }
}