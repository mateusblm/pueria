ALTER TABLE registros_alimentacao_alimentos
    ADD COLUMN classificacao_gluten VARCHAR(30) NOT NULL DEFAULT 'NAO_SE_APLICA',
    ADD COLUMN tipo_peixe VARCHAR(120),
    ADD COLUMN datas_reexposicao VARCHAR(2000) NOT NULL DEFAULT '[]',
    ADD COLUMN situacao_sinais VARCHAR(30) NOT NULL DEFAULT 'NAO_INFORMADO';

UPDATE registros_alimentacao_alimentos
SET situacao_sinais = 'SINAIS_PERCEBIDOS'
WHERE sintomas_pele = TRUE
   OR sintomas_intestinais = TRUE
   OR sintomas_respiratorios = TRUE
   OR alteracao_sono = TRUE
   OR alteracao_comportamento = TRUE;
