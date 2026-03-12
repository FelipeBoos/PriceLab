package com.felipeboos.gestao_produtos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "estrategias_preco")
@Entity(name = "EstrategiaPreco")
public class EstrategiaPreco extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "margem_lucro", precision = 5, scale = 2)
    private BigDecimal margemLucro;

    @Column(name = "percentual_imposto", precision = 5, scale = 2)
    private BigDecimal percentualImposto;

    @Column(name = "preco_sugerido")
    private BigDecimal precoSugerido;

    @Column(name = "lucro_unitario")
    private BigDecimal lucroUnitario;

    @Column(name= "demanda_estimada")
    private Integer demandaEstimada;

    @Column(name = "lucro_total_estimado")
    private BigDecimal lucroTotalEstimado;

    @Column(name = "data_simulacao")
    private Instant dataSimulacao;
}
