DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_semanas_gestacionais_obrigatorias'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_semanas_gestacionais_obrigatorias
            CHECK (semanas_gestacionais IS NOT NULL) NOT VALID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_peso_nascimento_obrigatorio'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_peso_nascimento_obrigatorio
            CHECK (peso_nascimento_gramas IS NOT NULL) NOT VALID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_semanas_gestacionais_intervalo'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_semanas_gestacionais_intervalo
            CHECK (semanas_gestacionais BETWEEN 22 AND 42) NOT VALID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_peso_nascimento_intervalo'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_peso_nascimento_intervalo
            CHECK (peso_nascimento_gramas BETWEEN 300 AND 7000) NOT VALID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_prematuridade_coerente'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_prematuridade_coerente
            CHECK (
                (prematura = TRUE AND semanas_gestacionais < 37)
                OR
                (prematura = FALSE AND semanas_gestacionais >= 37)
            ) NOT VALID;
    END IF;
END $$;
