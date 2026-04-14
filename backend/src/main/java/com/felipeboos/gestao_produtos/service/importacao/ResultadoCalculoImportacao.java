package com.felipeboos.gestao_produtos.service.importacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ResultadoCalculoImportacao {

    private final BigDecimal cotacaoMoeda;
    private final BigDecimal precoCustoEmReais;
    private final BigDecimal impostoImportacao;
    private final BigDecimal icmsImportacao;
    private final BigDecimal custoFinalAquisicao;
}
