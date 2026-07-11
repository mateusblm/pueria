ALTER TABLE medidas_crescimento ADD COLUMN responsavel_medicao VARCHAR(20) NOT NULL DEFAULT 'NAO_INFORMADO';

UPDATE medidas_crescimento SET origem = 'CONSULTORIO' WHERE origem = 'CONSULTA';
UPDATE medidas_crescimento SET origem = 'OUTRO' WHERE origem = 'ESCOLA_CRECHE';
