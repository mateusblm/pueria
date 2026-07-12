ALTER TABLE registros_alimentacao
    ADD COLUMN origem_preparo_alimento VARCHAR(30) NOT NULL DEFAULT 'NAO_INFORMADO';
