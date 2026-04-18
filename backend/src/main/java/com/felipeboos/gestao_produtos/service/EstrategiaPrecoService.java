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

        BigDecimal basePreco = resolverBasePreco(produto);

        BigDecimal precoSugerido = calcularPrecoSugerido(
                basePreco,
                margemLucroFracao,
                percentualImpostoFracao
        );

        BigDecimal impostoUnitario = calcularImpostoUnitario(
                precoSugerido,
                percentualImpostoFracao
        );

        BigDecimal lucroUnitario = calcularLucroUnitario(
                basePreco,
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
        response.setAvisos(gerarAvisosEstrategia(estrategiaPreco));

        return response;
    }

    private BigDecimal calcularPrecoSugerido(
            BigDecimal basePreco,
            BigDecimal margemLucroFracao,
            BigDecimal percentualImpostoFracao
    ) {
        BigDecimal fatorMargemLucro = BigDecimal.ONE.add(margemLucroFracao);
        BigDecimal fatorPercentualImposto = BigDecimal.ONE.add(percentualImpostoFracao);

        return basePreco
                .multiply(fatorMargemLucro)
                .multiply(fatorPercentualImposto)
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularImpostoUnitario(
            BigDecimal precoSugerido,
            BigDecimal percentualImpostoFracao
    ) {
        BigDecimal precoSemImposto = precoSugerido.divide(
                BigDecimal.ONE.add(percentualImpostoFracao),
                SCALE_MONETARIO,
                RoundingMode.HALF_UP
        );

        return precoSugerido.subtract(precoSemImposto)
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularLucroUnitario(
            BigDecimal basePreco,
            BigDecimal precoSugerido,
            BigDecimal impostoUnitario
    ) {
        return precoSugerido.subtract(basePreco)
                .subtract(impostoUnitario)
                .setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private Integer calcularDemandaEstimada(
            Integer demandaBase,
            BigDecimal precoSugerido,
            BigDecimal fatorElasticidade
    ) {
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

    private BigDecimal resolverBasePreco(Produto produto) {
        if (produto.getCustoFinalAquisicao() != null) {
            return produto.getCustoFinalAquisicao();
        }

        if (produto.getPrecoCustoEmReais() != null) {
            return produto.getPrecoCustoEmReais();
        }

        if (produto.getPrecoCusto() != null) {
            return produto.getPrecoCusto();
        }

        return BigDecimal.ZERO.setScale(SCALE_MONETARIO, RoundingMode.HALF_UP);
    }

    private List<String> gerarAvisosEstrategia(EstrategiaPreco estrategiaPreco) {
        List<String> avisos = new ArrayList<>();

        if (estrategiaPreco == null || estrategiaPreco.getProduto() == null) {
            return avisos;
        }

        if (estrategiaPreco.getDemandaEstimada() != null && estrategiaPreco.getDemandaEstimada() <= 0) {
            BigDecimal fatorElasticidade = estrategiaPreco.getProduto().getFatorElasticidade();
            String avisoTexto = "⚠️ Demanda estimada zerada: O fator de elasticidade do produto ("
                    + String.format("%.2f", fatorElasticidade) + ") resultou em uma demanda estimada nula para a margem de lucro informada ("
                    + String.format("%.2f", estrategiaPreco.getMargemLucro()) + "%). "
                    + "Isso significa que o preço sugerido é tão alto que o produto não teria vendas estimadas. "
                    + "Considere aumentar o fator de elasticidade do produto, aumentar a demanda base ou reduzir a margem de lucro.";
            avisos.add(avisoTexto);
        }

        if (estrategiaPreco.getLucroTotalEstimado() != null
                && estrategiaPreco.getLucroTotalEstimado().compareTo(BigDecimal.ZERO) <= 0) {
            avisos.add("⚠️ Lucro total zerado: A estratégia resultou em lucro total estimado igual a zero. "
                    + "Não há viabilidade econômica nesta configuração.");
        }

        Integer demandaBase = estrategiaPreco.getProduto().getDemandaBase();
        if (demandaBase != null
                && demandaBase > 0
                && estrategiaPreco.getDemandaEstimada() != null
                && estrategiaPreco.getDemandaEstimada() < demandaBase * 0.1) {
            double percentualDemanda = (estrategiaPreco.getDemandaEstimada().doubleValue()
                    / demandaBase.doubleValue()) * 100;
            String avisoTexto = "ℹ️ Demanda crítica: A demanda estimada ("
                    + String.format("%.1f", percentualDemanda) + "% da demanda base) é muito baixa. "
                    + "Revise a margem de lucro ou o fator de elasticidade do produto.";
            avisos.add(avisoTexto);
        }

        return avisos;
    }
}