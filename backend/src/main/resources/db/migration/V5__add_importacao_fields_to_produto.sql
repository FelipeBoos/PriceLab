ALTER TABLE produtos
ADD COLUMN importado BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN remessa_conforme BOOLEAN,
ADD COLUMN frete_internacional NUMERIC(19,4),
ADD COLUMN seguro_internacional NUMERIC(19,4),
ADD COLUMN aliquota_icms_importacao NUMERIC(5,2),
ADD COLUMN imposto_importacao NUMERIC(19,4),
ADD COLUMN icms_importacao NUMERIC(19,4),
ADD COLUMN custo_final_aquisicao NUMERIC(19,4);

UPDATE produtos
SET
    remessa_conforme = FALSE,
    frete_internacional = 0,
    seguro_internacional = 0,
    aliquota_icms_importacao = 17.00,
    imposto_importacao = 0,
    icms_importacao = 0,
    custo_final_aquisicao = preco_custo_em_reais
WHERE custo_final_aquisicao IS NULL;

ALTER TABLE produtos
ALTER COLUMN frete_internacional SET NOT NULL,
ALTER COLUMN seguro_internacional SET NOT NULL,
ALTER COLUMN aliquota_icms_importacao SET NOT NULL,
ALTER COLUMN imposto_importacao SET NOT NULL,
ALTER COLUMN icms_importacao SET NOT NULL,
ALTER COLUMN custo_final_aquisicao SET NOT NULL;