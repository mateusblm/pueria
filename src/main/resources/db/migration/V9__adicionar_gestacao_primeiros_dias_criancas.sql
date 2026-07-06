ALTER TABLE criancas
    ADD COLUMN IF NOT EXISTS pre_natal_realizado BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS consultas_pre_natal INTEGER,
    ADD COLUMN IF NOT EXISTS diabetes_gestacional BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS hipertensao_gestacional BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS infeccao_gestacional BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS sangramento_gestacional BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS uso_alcool_gestacao BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS uso_tabaco_gestacao BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS outras_exposicoes_gestacao BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS observacoes_gestacao VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS dias_alta_hospitalar INTEGER,
    ADD COLUMN IF NOT EXISTS retorno_hospitalar_primeira_semana BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS teste_pezinho VARCHAR(20) NOT NULL DEFAULT 'NAO_INFORMADO',
    ADD COLUMN IF NOT EXISTS teste_orelhinha VARCHAR(20) NOT NULL DEFAULT 'NAO_INFORMADO',
    ADD COLUMN IF NOT EXISTS teste_olhinho VARCHAR(20) NOT NULL DEFAULT 'NAO_INFORMADO',
    ADD COLUMN IF NOT EXISTS teste_coracaozinho VARCHAR(20) NOT NULL DEFAULT 'NAO_INFORMADO',
    ADD COLUMN IF NOT EXISTS amamentacao_primeira_hora BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS alimentacao_inicial VARCHAR(40) NOT NULL DEFAULT 'NAO_INFORMADO';

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_consultas_pre_natal_intervalo') THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_consultas_pre_natal_intervalo
            CHECK (consultas_pre_natal IS NULL OR consultas_pre_natal BETWEEN 0 AND 60) NOT VALID;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_dias_alta_hospitalar_intervalo') THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_dias_alta_hospitalar_intervalo
            CHECK (dias_alta_hospitalar IS NULL OR dias_alta_hospitalar BETWEEN 0 AND 365) NOT VALID;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_testes_triagem_neonatal_validos') THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_testes_triagem_neonatal_validos
            CHECK (
                teste_pezinho IN ('REALIZADO', 'PENDENTE', 'NAO_INFORMADO')
                AND teste_orelhinha IN ('REALIZADO', 'PENDENTE', 'NAO_INFORMADO')
                AND teste_olhinho IN ('REALIZADO', 'PENDENTE', 'NAO_INFORMADO')
                AND teste_coracaozinho IN ('REALIZADO', 'PENDENTE', 'NAO_INFORMADO')
            ) NOT VALID;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_alimentacao_inicial_valida') THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_alimentacao_inicial_valida
            CHECK (alimentacao_inicial IN ('ALEITAMENTO_MATERNO_EXCLUSIVO', 'ALEITAMENTO_MISTO', 'FORMULA_INFANTIL', 'NAO_INFORMADO')) NOT VALID;
    END IF;
END $$;
