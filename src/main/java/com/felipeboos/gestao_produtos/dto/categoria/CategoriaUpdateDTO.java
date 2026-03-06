package com.felipeboos.gestao_produtos.dto.categoria;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaUpdateDTO {

    @Size(max = 50)
    private String nome;

    @Size(max = 255)
    private String descricao;
}
