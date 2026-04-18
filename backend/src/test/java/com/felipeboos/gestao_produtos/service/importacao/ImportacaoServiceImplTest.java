package com.felipeboos.gestao_produtos.service.importacao;

import com.felipeboos.gestao_produtos.entity.Moeda;
import com.felipeboos.gestao_produtos.entity.Produto;
import com.felipeboos.gestao_produtos.service.cambio.CambioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportacaoServiceImplTest {

    @Mock
    private CambioService cambioService;

    @InjectMocks
    private ImportacaoServiceImpl service;

    private Produto produto;

    @BeforeEach
    void setup() {
        produto = Produto.builder()
                .nome("Produto teste")
                .precoCusto(BigDecimal.ZERO)
                .moeda(Moeda.BRL)
                .importado(false)
                .remessaConforme(false)
                .freteInternacional(BigDecimal.ZERO)
                .seguroInternacional(BigDecimal.ZERO)
                .aliquotaIcmsImportacao(BigDecimal.ZERO)
                .build();
    }

    @Test
    @DisplayName("T1 - ImportacaoServiceImplTest - Deve calcular custo de produto nacional em BRL sem impostos de importacao")
    void t1_deveCalcularProdutoNacionalEmBrlSemImpostosDeImportacao() {
        produto.setPrecoCusto(new BigDecimal("80.00"));
        produto.setMoeda(Moeda.BRL);
        produto.setImportado(false);
        produto.setFreteInternacional(BigDecimal.ZERO);
        produto.setSeguroInternacional(BigDecimal.ZERO);
        produto.setAliquotaIcmsImportacao(BigDecimal.ZERO);

        when(cambioService.obterCotacao(Moeda.BRL)).thenReturn(new BigDecimal("1.000000"));

        ResultadoCalculoImportacao resultado = service.calcular(produto);

        assertBigDecimalEquals("1.000000", resultado.getCotacaoMoeda());
        assertBigDecimalEquals("80.0000", resultado.getPrecoCustoEmReais());
        assertBigDecimalEquals("0.0000", resultado.getImpostoImportacao());
        assertBigDecimalEquals("0.0000", resultado.getIcmsImportacao());
        assertBigDecimalEquals("80.0000", resultado.getCustoFinalAquisicao());

        verify(cambioService, times(1)).obterCotacao(Moeda.BRL);
    }

    @Test
    @DisplayName("T2 - ImportacaoServiceImplTest - Deve calcular produto importado em moeda estrangeira com imposto de importacao e ICMS")
    void t2_deveCalcularProdutoImportadoEmMoedaEstrangeiraComImpostos() {
        produto.setPrecoCusto(new BigDecimal("90.00"));
        produto.setMoeda(Moeda.USD);
        produto.setImportado(true);
        produto.setRemessaConforme(true);
        produto.setFreteInternacional(new BigDecimal("40.00"));
        produto.setSeguroInternacional(new BigDecimal("20.00"));
        produto.setAliquotaIcmsImportacao(new BigDecimal("17.00"));

        when(cambioService.obterCotacao(Moeda.USD)).thenReturn(new BigDecimal("4.998100"));

        ResultadoCalculoImportacao resultado = service.calcular(produto);

        assertBigDecimalEquals("4.998100", resultado.getCotacaoMoeda());
        assertBigDecimalEquals("449.8290", resultado.getPrecoCustoEmReais());
        assertBigDecimalEquals("305.8974", resultado.getImpostoImportacao());
        assertBigDecimalEquals("138.6735", resultado.getIcmsImportacao());
        assertBigDecimalEquals("954.3999", resultado.getCustoFinalAquisicao());

        verify(cambioService, times(1)).obterCotacao(Moeda.USD);
    }

    @Test
    @DisplayName("T3 - ImportacaoServiceImplTest - Deve calcular produto estrangeiro nao importado convertendo para reais sem tributos de importacao")
    void t3_deveCalcularProdutoEstrangeiroNaoImportadoSemTributosDeImportacao() {
        produto.setPrecoCusto(new BigDecimal("70.00"));
        produto.setMoeda(Moeda.USD);
        produto.setImportado(false);
        produto.setFreteInternacional(new BigDecimal("30.00"));
        produto.setSeguroInternacional(new BigDecimal("15.00"));
        produto.setAliquotaIcmsImportacao(new BigDecimal("18.00"));

        when(cambioService.obterCotacao(Moeda.USD)).thenReturn(new BigDecimal("4.998100"));

        ResultadoCalculoImportacao resultado = service.calcular(produto);

        assertBigDecimalEquals("4.998100", resultado.getCotacaoMoeda());
        assertBigDecimalEquals("349.8670", resultado.getPrecoCustoEmReais());
        assertBigDecimalEquals("0.0000", resultado.getImpostoImportacao());
        assertBigDecimalEquals("0.0000", resultado.getIcmsImportacao());
        assertBigDecimalEquals("349.8670", resultado.getCustoFinalAquisicao());

        verify(cambioService, times(1)).obterCotacao(Moeda.USD);
    }

    @Test
    @DisplayName("T4 - ImportacaoServiceImplTest - Deve considerar frete e seguro na base de calculo do produto importado")
    void t4_deveConsiderarFreteESeguroNaBaseDeCalculoDoProdutoImportado() {
        produto.setPrecoCusto(new BigDecimal("60.00"));
        produto.setMoeda(Moeda.USD);
        produto.setImportado(true);
        produto.setRemessaConforme(false);
        produto.setFreteInternacional(new BigDecimal("20.00"));
        produto.setSeguroInternacional(new BigDecimal("10.00"));
        produto.setAliquotaIcmsImportacao(new BigDecimal("17.00"));

        when(cambioService.obterCotacao(Moeda.USD)).thenReturn(new BigDecimal("4.998100"));

        ResultadoCalculoImportacao resultado = service.calcular(produto);

        assertBigDecimalEquals("299.8860", resultado.getPrecoCustoEmReais());
        assertBigDecimalEquals("197.9316", resultado.getImpostoImportacao());
        assertBigDecimalEquals("89.7290", resultado.getIcmsImportacao());
        assertBigDecimalEquals("617.5466", resultado.getCustoFinalAquisicao());

        verify(cambioService, times(1)).obterCotacao(Moeda.USD);
    }

    @Test
    @DisplayName("T5 - ImportacaoServiceImplTest - Deve zerar frete e seguro quando importado for falso no resultado final")
    void t5_deveIgnorarFreteESeguroQuandoProdutoNaoForImportado() {
        produto.setPrecoCusto(new BigDecimal("1200.00"));
        produto.setMoeda(Moeda.USD);
        produto.setImportado(false);
        produto.setFreteInternacional(new BigDecimal("300.00"));
        produto.setSeguroInternacional(new BigDecimal("150.00"));
        produto.setAliquotaIcmsImportacao(new BigDecimal("18.00"));

        when(cambioService.obterCotacao(Moeda.USD)).thenReturn(new BigDecimal("4.998100"));

        ResultadoCalculoImportacao resultado = service.calcular(produto);

        assertBigDecimalEquals("5997.7200", resultado.getPrecoCustoEmReais());
        assertBigDecimalEquals("0.0000", resultado.getImpostoImportacao());
        assertBigDecimalEquals("0.0000", resultado.getIcmsImportacao());
        assertBigDecimalEquals("5997.7200", resultado.getCustoFinalAquisicao());

        verify(cambioService, times(1)).obterCotacao(Moeda.USD);
    }

    @Test
    @DisplayName("T6 - ImportacaoServiceImplTest - Deve aplicar corretamente aliquota de ICMS informada em percentual")
    void t6_deveAplicarCorretamenteAliquotaDeIcmsInformadaEmPercentual() {
        produto.setPrecoCusto(new BigDecimal("800.00"));
        produto.setMoeda(Moeda.USD);
        produto.setImportado(true);
        produto.setFreteInternacional(new BigDecimal("200.00"));
        produto.setSeguroInternacional(new BigDecimal("80.00"));
        produto.setAliquotaIcmsImportacao(new BigDecimal("18.00"));

        when(cambioService.obterCotacao(Moeda.USD)).thenReturn(new BigDecimal("4.998100"));

        ResultadoCalculoImportacao resultado = service.calcular(produto);

        assertBigDecimalEquals("3998.4800", resultado.getPrecoCustoEmReais());
        assertBigDecimalEquals("2567.0880", resultado.getImpostoImportacao());
        assertBigDecimalEquals("1232.2022", resultado.getIcmsImportacao());
        assertBigDecimalEquals("8077.7702", resultado.getCustoFinalAquisicao());

        verify(cambioService, times(1)).obterCotacao(Moeda.USD);
    }

    @Test
    @DisplayName("T7 - ImportacaoServiceImplTest - Deve manter resultado igual para remessa conforme verdadeiro ou falso quando regra ainda nao interfere no calculo")
    void t7_deveManterResultadoIgualParaRemessaConformeVerdadeiroOuFalso() {
        produto.setPrecoCusto(new BigDecimal("70.00"));
        produto.setMoeda(Moeda.USD);
        produto.setImportado(true);
        produto.setFreteInternacional(new BigDecimal("30.00"));
        produto.setSeguroInternacional(new BigDecimal("15.00"));
        produto.setAliquotaIcmsImportacao(new BigDecimal("18.00"));

        when(cambioService.obterCotacao(Moeda.USD)).thenReturn(new BigDecimal("4.998100"));

        produto.setRemessaConforme(false);
        ResultadoCalculoImportacao resultadoSemRemessa = service.calcular(produto);

        produto.setRemessaConforme(true);
        ResultadoCalculoImportacao resultadoComRemessa = service.calcular(produto);

        assertBigDecimalEquals(
                resultadoSemRemessa.getCotacaoMoeda().toPlainString(),
                resultadoComRemessa.getCotacaoMoeda()
        );
        assertBigDecimalEquals(
                resultadoSemRemessa.getPrecoCustoEmReais().toPlainString(),
                resultadoComRemessa.getPrecoCustoEmReais()
        );
        assertBigDecimalEquals(
                resultadoSemRemessa.getImpostoImportacao().toPlainString(),
                resultadoComRemessa.getImpostoImportacao()
        );
        assertBigDecimalEquals(
                resultadoSemRemessa.getIcmsImportacao().toPlainString(),
                resultadoComRemessa.getIcmsImportacao()
        );
        assertBigDecimalEquals(
                resultadoSemRemessa.getCustoFinalAquisicao().toPlainString(),
                resultadoComRemessa.getCustoFinalAquisicao()
        );

        verify(cambioService, times(2)).obterCotacao(Moeda.USD);
    }

    private void assertBigDecimalEquals(String esperado, BigDecimal atual) {
        assertEquals(0, new BigDecimal(esperado).compareTo(atual));
    }

    private void assertBigDecimalEquals(String esperado, String atual) {
        assertEquals(0, new BigDecimal(esperado).compareTo(new BigDecimal(atual)));
    }
}