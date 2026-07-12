ALTER TABLE registros_marcos_desenvolvimento
    ADD COLUMN modalidade VARCHAR(40);

UPDATE registros_marcos_desenvolvimento
SET modalidade = 'ACOMPANHAMENTO_ATUAL'
WHERE modalidade IS NULL;

ALTER TABLE registros_marcos_desenvolvimento
    ALTER COLUMN modalidade SET NOT NULL,
    ADD CONSTRAINT chk_registros_marcos_modalidade
        CHECK (modalidade IN ('RETROSPECTIVO', 'ACOMPANHAMENTO_ATUAL'));
