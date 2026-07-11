ALTER TABLE medidas_crescimento ADD COLUMN responsavel_medicao VARCHAR(20) NOT NULL DEFAULT 'NAO_INFORMADO';

ALTER TABLE medidas_crescimento DROP CONSTRAINT IF EXISTS chk_medidas_crescimento_origem;

UPDATE medidas_crescimento SET origem = 'CONSULTORIO' WHERE origem = 'CONSULTA';
UPDATE medidas_crescimento SET origem = 'OUTRO' WHERE origem = 'ESCOLA_CRECHE';

ALTER TABLE medidas_crescimento ADD CONSTRAINT chk_medidas_crescimento_origem CHECK (
    origem IN ('CASA', 'CONSULTORIO', 'POSTO_SAUDE', 'HOSPITAL', 'OUTRO')
);
