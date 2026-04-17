package com.felipeboos.gestao_produtos.dto.produto;

import com.felipeboos.gestao_produtos.entity.Moeda;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProdutoCotacaoResponseDTO {

    private final Moeda moeda;
    private final BigDecimal cotacao;
}
