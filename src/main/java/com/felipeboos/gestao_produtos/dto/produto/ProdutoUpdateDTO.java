package com.felipeboos.gestao_produtos.dto.produto;

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

    @PositiveOrZero(message = "O preco de venda nao pode ser negativo")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precoVenda;

    @PositiveOrZero(message = "A quantidade em estoque nao pode ser negativa")
    private Integer quantidadeEstoque;
}
