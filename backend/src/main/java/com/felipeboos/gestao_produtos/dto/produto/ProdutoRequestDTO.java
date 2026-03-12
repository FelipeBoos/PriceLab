package com.felipeboos.gestao_produtos.dto.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto eh obrigatorio")
    @Size(max = 50, message = "O nome do produto deve ter no maximo 50 caracteres")
    private String nome;

    @Size(max = 255, message = "A descricao deve ter no maximo 255 caracteres")
    private String descricao;

    @NotNull(message = "A categoria do produto eh obrigatória")
    private Long categoriaId;

    @NotNull(message = "O preco de custo eh obrigatorio")
    @PositiveOrZero(message = "O preco de custo nao pode ser negativo")
    private BigDecimal precoCusto;

    @NotNull(message = "O preco de venda eh obrigatorio")
    @PositiveOrZero(message = "O preco ce venda nao pode ser negativo")
    private BigDecimal precoVenda;

    @NotNull(message = "A quantidade em estoque eh obrigatoria")
    @PositiveOrZero(message = "A quantidade em estoque nao pode ser negativa")
    private Integer quantidadeEstoque;

    @PositiveOrZero(message = "A demanda base nao pode ser negativa")
    private Integer demandaBase;

    @PositiveOrZero(message = "O fator de elasticidade nao pode ser negativo")
    private BigDecimal fatorElasticidade;
}
