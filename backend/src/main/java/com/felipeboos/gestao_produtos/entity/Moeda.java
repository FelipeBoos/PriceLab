package com.felipeboos.gestao_produtos.entity;

public enum Moeda {
    BRL("Real"),
    USD("Dólar"),
    EUR("Euro");

    private final String descricao;

    Moeda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
