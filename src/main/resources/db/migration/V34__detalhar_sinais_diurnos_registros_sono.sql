ALTER TABLE registros_sono ADD COLUMN dificil_de_ser_acordado BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE registros_sono ADD COLUMN mal_humorado BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE registros_sono ADD COLUMN irritado BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE registros_alimentacao ADD COLUMN alimentado_exclusivamente_por_cuidador BOOLEAN NOT NULL DEFAULT FALSE;
