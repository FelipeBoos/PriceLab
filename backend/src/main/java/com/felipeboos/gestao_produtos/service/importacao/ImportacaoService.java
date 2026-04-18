package com.felipeboos.gestao_produtos.service.importacao;

import com.felipeboos.gestao_produtos.entity.Produto;

public interface ImportacaoService {

    ResultadoCalculoImportacao calcular(Produto produto);
}
