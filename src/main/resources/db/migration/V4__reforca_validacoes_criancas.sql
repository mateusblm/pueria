ALTER TABLE criancas
    ADD CONSTRAINT ck_criancas_semanas_gestacionais_obrigatorias
    CHECK (semanas_gestacionais IS NOT NULL) NOT VALID;

ALTER TABLE criancas
    ADD CONSTRAINT ck_criancas_peso_nascimento_obrigatorio
    CHECK (peso_nascimento_gramas IS NOT NULL) NOT VALID;

ALTER TABLE criancas
    ADD CONSTRAINT ck_criancas_semanas_gestacionais_intervalo
    CHECK (semanas_gestacionais BETWEEN 22 AND 42) NOT VALID;

ALTER TABLE criancas
    ADD CONSTRAINT ck_criancas_peso_nascimento_intervalo
    CHECK (peso_nascimento_gramas BETWEEN 300 AND 7000) NOT VALID;

ALTER TABLE criancas
    ADD CONSTRAINT ck_criancas_prematuridade_coerente
    CHECK (
        (prematura = TRUE AND semanas_gestacionais < 37)
        OR
        (prematura = FALSE AND semanas_gestacionais >= 37)
    ) NOT VALID;
