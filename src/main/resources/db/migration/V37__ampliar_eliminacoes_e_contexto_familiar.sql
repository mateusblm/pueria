ALTER TABLE registros_transito_intestinal
    ADD COLUMN IF NOT EXISTS intervalo_diurese_horas INTEGER,
    ADD COLUMN IF NOT EXISTS cor_urina VARCHAR(24),
    ADD COLUMN IF NOT EXISTS aspecto_urina VARCHAR(24),
    ADD COLUMN IF NOT EXISTS cheiro_urina VARCHAR(24),
    ADD COLUMN IF NOT EXISTS diurese_sem_alteracoes BOOLEAN;

ALTER TABLE registros_transito_intestinal
    DROP CONSTRAINT IF EXISTS chk_eliminacoes_intervalo_diurese;
ALTER TABLE registros_transito_intestinal
    ADD CONSTRAINT chk_eliminacoes_intervalo_diurese
    CHECK (intervalo_diurese_horas IS NULL OR intervalo_diurese_horas BETWEEN 0 AND 24);
