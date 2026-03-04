package com.felipeboos.gestao_produtos.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "produtos")
@Entity(name = "produto")
public class Produto extends EntidadeBase {

    @Column(name = "nome", unique = true, nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "precoCusto")
    private BigDecimal precoCusto;

    @Column(name = "precoVenda")
    private BigDecimal precoVenda;

    @Column(name = "quantidadeEstoque")
    private Integer quantidadeEstoque;
}
