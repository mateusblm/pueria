ALTER TABLE registros_alimentacao
    ADD COLUMN tipo_origem_alimento VARCHAR(30) NOT NULL DEFAULT 'NAO_INFORMADO';

UPDATE registros_alimentacao_alimentos SET grupo = 'LEGUME_HORTALICA_FRUTO' WHERE grupo = 'LEGUME';
UPDATE registros_alimentacao_alimentos SET grupo = 'VERDURA_FOLHA' WHERE grupo = 'VERDURA';
UPDATE registros_alimentacao_alimentos SET grupo = 'RAIZ_TUBERCULO_AMIDO' WHERE grupo = 'RAIZ_TUBERCULO';
UPDATE registros_alimentacao_alimentos SET grupo = 'LEGUMINOSA' WHERE grupo = 'FEIJAO_LEGUMINOSA';
UPDATE registros_alimentacao_alimentos SET grupo = 'CEREAL_GRAO_MASSA' WHERE grupo = 'CEREAL';
UPDATE registros_alimentacao_alimentos SET grupo = 'CARNE_AVE' WHERE grupo = 'PROTEINA';

ALTER TABLE registros_alimentacao_alimentos
    ADD COLUMN alergenico BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN data_introducao DATE,
    ADD COLUMN forma_preparo VARCHAR(160),
    ADD COLUMN textura VARCHAR(30),
    ADD COLUMN quantidade_aproximada VARCHAR(80),
    ADD COLUMN aceitacao VARCHAR(30),
    ADD COLUMN repetiu_outro_dia BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN sintomas_pele BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN sintomas_intestinais BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN sintomas_respiratorios BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN alteracao_sono BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN alteracao_comportamento BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN observacao VARCHAR(500);
