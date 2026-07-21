ALTER TABLE registros_transito_intestinal
    ADD COLUMN intervalo_diurese_horas INTEGER,
    ADD COLUMN cor_urina VARCHAR(24),
    ADD COLUMN aspecto_urina VARCHAR(24),
    ADD COLUMN cheiro_urina VARCHAR(24),
    ADD COLUMN diurese_sem_alteracoes BOOLEAN;

ALTER TABLE registros_transito_intestinal
    ADD CONSTRAINT chk_eliminacoes_intervalo_diurese
    CHECK (intervalo_diurese_horas IS NULL OR intervalo_diurese_horas BETWEEN 0 AND 24);
