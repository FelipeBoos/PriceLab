package com.felipeboos.gestao_produtos.dto.produto;

import com.felipeboos.gestao_produtos.entity.Moeda;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoUpdateDTO {

    @Size(max = 50, message = "O nome nao pode ter mais que 50 caracteres")
    private String nome;

    @Size(max = 255, message = "A descricao nao pode ter mais que 255 caracteres")
    private String descricao;

    @Positive(message = "O id da categoria deve ser positivo")
    private Long categoriaId;

    @PositiveOrZero(message = "O preco de custo nao pode ser negativo")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precoCusto;

    private Moeda moeda;

    @PositiveOrZero(message = "O preco de venda nao pode ser negativo")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precoVenda;

    @PositiveOrZero(message = "A quantidade em estoque nao pode ser negativa")
    private Integer quantidadeEstoque;

    @PositiveOrZero(message = "A demanda base nao pode ser negativa")
    private Integer demandaBase;

    @PositiveOrZero(message = "O fator de elasticidade nao pode ser negativo")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal fatorElasticidade;

    private Boolean importado;

    private Boolean remessaConforme;

    @PositiveOrZero(message = "O frete internacional nao pode ser negativo")
    @Digits(integer = 10, fraction = 4)
    private BigDecimal freteInternacional;

    @PositiveOrZero(message = "O seguro internacional nao pode ser negativo")
    @Digits(integer = 10, fraction = 4)
    private BigDecimal seguroInternacional;

    @PositiveOrZero(message = "A aliquota de ICMS de importacao nao pode ser negativa")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal aliquotaIcmsImportacao;
}