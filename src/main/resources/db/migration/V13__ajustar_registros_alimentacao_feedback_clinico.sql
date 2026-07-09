ALTER TABLE registros_alimentacao
    ADD COLUMN IF NOT EXISTS blw_misto BOOLEAN,
    ADD COLUMN IF NOT EXISTS consome_legumes BOOLEAN,
    ADD COLUMN IF NOT EXISTS consome_verduras BOOLEAN,
    ADD COLUMN IF NOT EXISTS familia_tranquila_ganho_peso_atual BOOLEAN;

UPDATE registros_alimentacao
SET consome_legumes = COALESCE(consome_legumes, consome_legumes_verduras),
    consome_verduras = COALESCE(consome_verduras, consome_legumes_verduras)
WHERE consome_legumes_verduras IS NOT NULL;
