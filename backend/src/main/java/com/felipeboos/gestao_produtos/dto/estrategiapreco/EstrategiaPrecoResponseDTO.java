package com.felipeboos.gestao_produtos.dto.estrategiapreco;

import com.felipeboos.gestao_produtos.entity.EstrategiaPreco;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class EstrategiaPrecoResponseDTO {

    private Long id;

    private Long produtoId;
    private String produtoNome;
    private String categoriaNome;
    private BigDecimal precoUnidade;
    private Integer demandaBase;

    private BigDecimal margemLucro;
    private BigDecimal percentualImposto;

    private BigDecimal precoSugerido;
    private Integer demandaEstimada;
    private BigDecimal impostoUnitario;
    private BigDecimal impostoTotal;
    private BigDecimal lucroUnitario;
    private BigDecimal lucroTotalEstimado;

    private Instant dataSimulacao;
    
    private List<String> avisos = new ArrayList<>();

    public static EstrategiaPrecoResponseDTO fromEntity(EstrategiaPreco estrategiaPreco){
        EstrategiaPrecoResponseDTO dto = new EstrategiaPrecoResponseDTO();

        dto.setId(estrategiaPreco.getId());

        if (estrategiaPreco.getProduto() != null) {
            dto.setProdutoId(estrategiaPreco.getProduto().getId());
            dto.setProdutoNome(estrategiaPreco.getProduto().getNome());
            dto.setCategoriaNome(estrategiaPreco.getProduto().getCategoria().getNome());
            dto.setPrecoUnidade(estrategiaPreco.getProduto().getPrecoCusto());
            dto.setDemandaBase(estrategiaPreco.getProduto().getDemandaBase());
        }

        dto.setMargemLucro(estrategiaPreco.getMargemLucro());
        dto.setPercentualImposto(estrategiaPreco.getPercentualImposto());

        dto.setPrecoSugerido(estrategiaPreco.getPrecoSugerido());
        dto.setDemandaEstimada(estrategiaPreco.getDemandaEstimada());
        dto.setLucroUnitario(estrategiaPreco.getLucroUnitario());
        dto.setLucroTotalEstimado(estrategiaPreco.getLucroTotalEstimado());

        dto.setDataSimulacao(estrategiaPreco.getDataSimulacao());

        return dto;
    }
}
