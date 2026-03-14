CREATE TABLE estrategias_preco (
    id BIGSERIAL PRIMARY KEY,
    criado_em TIMESTAMP WITH TIME ZONE,
    atualizado_em TIMESTAMP WITH TIME ZONE,
    produto_id BIGINT NOT NULL,
    margem_lucro DECIMAL(5,2),
    percentual_imposto DECIMAL(5,2),
    preco_sugerido NUMERIC(19,2),
    lucro_unitario NUMERIC(19,2),
    demanda_estimada INTEGER,
    lucro_total_estimado NUMERIC(19,2),
    data_simulacao TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_estrategias_preco_produtos FOREIGN KEY (produto_id) REFERENCES produtos(id)
);