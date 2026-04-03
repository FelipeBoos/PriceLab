-- Adiciona novas colunas
ALTER TABLE produtos
ADD COLUMN moeda VARCHAR(3),
ADD COLUMN cotacao_moeda NUMERIC(19,6),
ADD COLUMN preco_custo_em_reais NUMERIC(19,4);

-- Preenche dados existentes
UPDATE produtos
SET
    moeda = 'BRL',
    cotacao_moeda = 1.0,
    preco_custo_em_reais = preco_custo
WHERE moeda IS NULL;

-- Define como NOT NULL
ALTER TABLE produtos
ALTER COLUMN moeda SET NOT NULL;

ALTER TABLE produtos
ALTER COLUMN cotacao_moeda SET NOT NULL;

ALTER TABLE produtos
ALTER COLUMN preco_custo_em_reais SET NOT NULL;