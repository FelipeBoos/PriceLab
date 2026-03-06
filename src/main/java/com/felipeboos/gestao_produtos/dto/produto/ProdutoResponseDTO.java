package com.felipeboos.gestao_produtos.dto.produto;

import com.felipeboos.gestao_produtos.entity.Produto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;

    private Long categoriaId;
    private String categoriaNome;

    private BigDecimal precoCusto;
    private BigDecimal precoVenda;

    private Integer quantidadeEstoque;

    public static ProdutoResponseDTO fromEntity(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();

        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());

        if (produto.getCategoria() != null) {
            dto.setCategoriaId(produto.getCategoria().getId());
            dto.setCategoriaNome(produto.getCategoria().getNome());
        }

        dto.setPrecoCusto(produto.getPrecoCusto());
        dto.setPrecoVenda(produto.getPrecoVenda());
        dto.setQuantidadeEstoque(produto.getQuantidadeEstoque());

        return dto;
    }
}
