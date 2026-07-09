CREATE TABLE registros_transito_intestinal (
    id UUID PRIMARY KEY,
    crianca_id UUID NOT NULL REFERENCES criancas(id) ON DELETE CASCADE,
    data_registro DATE NOT NULL,
    tipo_fezes VARCHAR(20) NOT NULL,
    evacuacoes_por_dia INTEGER,
    facilidade_limpeza VARCHAR(20) NOT NULL,
    muco BOOLEAN,
    restos_alimentares BOOLEAN,
    raias_sangue BOOLEAN,
    constipacao BOOLEAN,
    diarreia BOOLEAN,
    dor_evacuar BOOLEAN,
    escape_fecal BOOLEAN,
    assadura_frequente BOOLEAN,
    assadura_vermelhidao BOOLEAN,
    assadura_pontos_vermelhos BOOLEAN,
    preocupacao_familia BOOLEAN,
    observacao VARCHAR(1000),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP,
    CONSTRAINT chk_transito_evacuacoes CHECK (evacuacoes_por_dia IS NULL OR evacuacoes_por_dia BETWEEN 0 AND 30)
);

CREATE INDEX idx_transito_intestinal_crianca_data
    ON registros_transito_intestinal (crianca_id, data_registro DESC);
