CREATE TABLE medidas_crescimento (
    id UUID PRIMARY KEY,
    crianca_id UUID NOT NULL REFERENCES criancas(id) ON DELETE CASCADE,
    data_medicao DATE NOT NULL,
    peso_kg NUMERIC(5,2),
    comprimento_cm NUMERIC(5,1),
    perimetro_cefalico_cm NUMERIC(4,1),
    origem VARCHAR(30) NOT NULL,
    observacao VARCHAR(500),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP,
    CONSTRAINT chk_medidas_crescimento_alguma_medida CHECK (
        peso_kg IS NOT NULL OR comprimento_cm IS NOT NULL OR perimetro_cefalico_cm IS NOT NULL
    ),
    CONSTRAINT chk_medidas_crescimento_origem CHECK (
        origem IN ('CASA', 'CONSULTA', 'ESCOLA_CRECHE', 'OUTRO')
    )
);

CREATE INDEX idx_medidas_crescimento_crianca_data
    ON medidas_crescimento (crianca_id, data_medicao);
