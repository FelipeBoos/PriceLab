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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(name = "preco_custo", precision = 19, scale = 4, nullable = false)
    private BigDecimal precoCusto;

    @Enumerated(EnumType.STRING)
    @Column(name = "moeda", nullable = false, length = 3)
    private Moeda moeda;

    @Column(name = "cotacao_moeda", precision = 19, scale = 6, nullable = false)
    private BigDecimal cotacaoMoeda;

    @Column(name = "preco_custo_em_reais", precision = 19, scale = 4, nullable = false)
    private BigDecimal precoCustoEmReais;

    @Column(name = "importado", nullable = false)
    private Boolean importado;

    @Column(name = "remessa_conforme", nullable = false)
    private Boolean remessaConforme;

    @Column(name = "frete_internacional", precision = 19, scale = 4, nullable = false)
    private BigDecimal freteInternacional;

    @Column(name = "seguro_internacional", precision = 19, scale = 4, nullable = false)
    private BigDecimal seguroInternacional;

    @Column(name = "aliquota_icms_importacao", precision = 5, scale = 2, nullable = false)
    private BigDecimal aliquotaIcmsImportacao;

    @Column(name = "imposto_importacao", precision = 19, scale = 4, nullable = false)
    private BigDecimal impostoImportacao;

    @Column(name = "icms_importacao", precision = 19, scale = 4, nullable = false)
    private BigDecimal icmsImportacao;

    @Column(name = "custo_final_aquisicao", precision = 19, scale = 4, nullable = false)
    private BigDecimal custoFinalAquisicao;

    @Column(name = "preco_venda", precision = 19, scale = 4)
    private BigDecimal precoVenda;

    @Column(name = "quantidade_estoque")
    private Integer quantidadeEstoque;

    @Column(name = "demanda_base")
    private Integer demandaBase;

    @Column(name = "fator_elasticidade", precision = 5, scale = 2)
    private BigDecimal fatorElasticidade;
}