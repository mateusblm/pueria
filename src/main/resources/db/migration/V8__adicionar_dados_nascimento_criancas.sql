ALTER TABLE criancas
    ADD COLUMN IF NOT EXISTS dias_gestacionais INTEGER,
    ADD COLUMN IF NOT EXISTS tipo_parto VARCHAR(30),
    ADD COLUMN IF NOT EXISTS comprimento_nascimento_cm NUMERIC(5, 2),
    ADD COLUMN IF NOT EXISTS perimetro_cefalico_nascimento_cm NUMERIC(5, 2),
    ADD COLUMN IF NOT EXISTS apgar_um_minuto INTEGER,
    ADD COLUMN IF NOT EXISTS apgar_cinco_minutos INTEGER,
    ADD COLUMN IF NOT EXISTS uti_neonatal BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS reanimacao_neonatal BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS ictericia_neonatal BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS dificuldade_respiratoria BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS dificuldade_amamentacao BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS observacoes_nascimento VARCHAR(1000);

UPDATE criancas
SET dias_gestacionais = COALESCE(dias_gestacionais, 0),
    tipo_parto = COALESCE(tipo_parto, 'NAO_INFORMADO'),
    comprimento_nascimento_cm = COALESCE(comprimento_nascimento_cm, 50.0),
    perimetro_cefalico_nascimento_cm = COALESCE(perimetro_cefalico_nascimento_cm, 34.0)
WHERE dias_gestacionais IS NULL
   OR tipo_parto IS NULL
   OR comprimento_nascimento_cm IS NULL
   OR perimetro_cefalico_nascimento_cm IS NULL;

ALTER TABLE criancas
    ALTER COLUMN dias_gestacionais SET NOT NULL,
    ALTER COLUMN tipo_parto SET NOT NULL,
    ALTER COLUMN comprimento_nascimento_cm SET NOT NULL,
    ALTER COLUMN perimetro_cefalico_nascimento_cm SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_dias_gestacionais_intervalo'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_dias_gestacionais_intervalo
            CHECK (dias_gestacionais BETWEEN 0 AND 6) NOT VALID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_tipo_parto_valido'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_tipo_parto_valido
            CHECK (tipo_parto IN ('VAGINAL', 'CESAREA', 'VAGINAL_INSTRUMENTADO', 'NAO_INFORMADO')) NOT VALID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_comprimento_nascimento_intervalo'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_comprimento_nascimento_intervalo
            CHECK (comprimento_nascimento_cm BETWEEN 20.0 AND 70.0) NOT VALID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_perimetro_cefalico_nascimento_intervalo'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_perimetro_cefalico_nascimento_intervalo
            CHECK (perimetro_cefalico_nascimento_cm BETWEEN 20.0 AND 50.0) NOT VALID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_apgar_um_minuto_intervalo'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_apgar_um_minuto_intervalo
            CHECK (apgar_um_minuto IS NULL OR apgar_um_minuto BETWEEN 0 AND 10) NOT VALID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_criancas_apgar_cinco_minutos_intervalo'
    ) THEN
        ALTER TABLE criancas
            ADD CONSTRAINT ck_criancas_apgar_cinco_minutos_intervalo
            CHECK (apgar_cinco_minutos IS NULL OR apgar_cinco_minutos BETWEEN 0 AND 10) NOT VALID;
    END IF;
END $$;
