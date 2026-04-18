package com.felipeboos.gestao_produtos.service.importacao;

import com.felipeboos.gestao_produtos.entity.Moeda;
import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.exception.DadoInvalidoException;
import com.felipeboos.gestao_produtos.service.cambio.CambioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ImportacaoServiceImpl implements ImportacaoService {

    private static final BigDecimal ALIQUOTA_IMPOSTO_IMPORTACAO_PADRAO = new BigDecimal("0.60");
    private static final BigDecimal CEM = new BigDecimal("100");
    private static final int SCALE_MOEDA = 4;
    private static final int SCALE_COTACAO = 6;

    private final CambioService cambioService;

    @Override
    public ResultadoCalculoImportacao calcular(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("O produto nao pode ser nulo");
        }

        Moeda moeda = produto.getMoeda() != null ? produto.getMoeda() : Moeda.BRL;

        BigDecimal precoCusto = valorOuZero(produto.getPrecoCusto());
        BigDecimal freteInternacional = valorOuZero(produto.getFreteInternacional());
        BigDecimal seguroInternacional = valorOuZero(produto.getSeguroInternacional());

        BigDecimal cotacaoMoeda = valorOuUm(cambioService.obterCotacao(moeda))
                .setScale(SCALE_COTACAO, RoundingMode.HALF_UP);

        BigDecimal precoCustoEmReais = precoCusto
                .multiply(cotacaoMoeda)
                .setScale(SCALE_MOEDA, RoundingMode.HALF_UP);

        if (!Boolean.TRUE.equals(produto.getImportado())) {
            return ResultadoCalculoImportacao.builder()
                    .cotacaoMoeda(cotacaoMoeda)
                    .precoCustoEmReais(precoCustoEmReais)
                    .impostoImportacao(BigDecimal.ZERO.setScale(SCALE_MOEDA, RoundingMode.HALF_UP))
                    .icmsImportacao(BigDecimal.ZERO.setScale(SCALE_MOEDA, RoundingMode.HALF_UP))
                    .custoFinalAquisicao(precoCustoEmReais)
                    .build();
        }

        BigDecimal baseCalculo = precoCustoEmReais
                .add(freteInternacional)
                .add(seguroInternacional)
                .setScale(SCALE_MOEDA, RoundingMode.HALF_UP);

        BigDecimal impostoImportacao = baseCalculo
                .multiply(ALIQUOTA_IMPOSTO_IMPORTACAO_PADRAO)
                .setScale(SCALE_MOEDA, RoundingMode.HALF_UP);

        BigDecimal aliquotaIcmsFracao = converterPercentualParaFracao(produto.getAliquotaIcmsImportacao());

        BigDecimal icmsImportacao = baseCalculo
                .add(impostoImportacao)
                .multiply(aliquotaIcmsFracao)
                .setScale(SCALE_MOEDA, RoundingMode.HALF_UP);

        BigDecimal custoFinalAquisicao = baseCalculo
                .add(impostoImportacao)
                .add(icmsImportacao)
                .setScale(SCALE_MOEDA, RoundingMode.HALF_UP);

        return ResultadoCalculoImportacao.builder()
                .cotacaoMoeda(cotacaoMoeda)
                .precoCustoEmReais(precoCustoEmReais)
                .impostoImportacao(impostoImportacao)
                .icmsImportacao(icmsImportacao)
                .custoFinalAquisicao(custoFinalAquisicao)
                .build();
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private BigDecimal valorOuUm(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ONE;
    }

    private BigDecimal converterPercentualParaFracao(BigDecimal percentual) {
        return valorOuZero(percentual)
                .divide(CEM, SCALE_COTACAO, RoundingMode.HALF_UP);
    }
}