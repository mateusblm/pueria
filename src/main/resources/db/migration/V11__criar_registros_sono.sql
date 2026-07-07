CREATE TABLE registros_sono (
    id UUID PRIMARY KEY,
    crianca_id UUID NOT NULL REFERENCES criancas(id) ON DELETE CASCADE,
    data_registro DATE NOT NULL,
    horario_dormiu TIME,
    horario_acordou TIME,
    quantidade_cochilos INTEGER,
    minutos_cochilos INTEGER,
    despertares_noturnos INTEGER,
    dificuldade_iniciar_sono BOOLEAN,
    rotina_sono_consistente BOOLEAN,
    telas_antes_dormir BOOLEAN,
    local_sono VARCHAR(35) NOT NULL,
    roncos_frequentes BOOLEAN,
    pausas_respiratorias_percebidas BOOLEAN,
    sono_agitado BOOLEAN,
    sonolencia_diurna BOOLEAN,
    irritabilidade_cansaco BOOLEAN,
    preocupacao_familia BOOLEAN,
    observacao VARCHAR(1000),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP,
    CONSTRAINT chk_registros_sono_local CHECK (
        local_sono IN ('BERCO', 'CAMA_PROPRIA', 'CAMA_COMPARTILHADA', 'QUARTO_DOS_RESPONSAVEIS', 'OUTRO', 'NAO_INFORMADO')
    ),
    CONSTRAINT chk_registros_sono_horarios_completos CHECK (
        (horario_dormiu IS NULL AND horario_acordou IS NULL)
        OR (horario_dormiu IS NOT NULL AND horario_acordou IS NOT NULL AND horario_dormiu <> horario_acordou)
    ),
    CONSTRAINT chk_registros_sono_cochilos CHECK (
        quantidade_cochilos IS NULL OR quantidade_cochilos BETWEEN 0 AND 12
    ),
    CONSTRAINT chk_registros_sono_minutos_cochilos CHECK (
        minutos_cochilos IS NULL OR minutos_cochilos BETWEEN 0 AND 1200
    ),
    CONSTRAINT chk_registros_sono_despertares CHECK (
        despertares_noturnos IS NULL OR despertares_noturnos BETWEEN 0 AND 30
    )
);

CREATE INDEX idx_registros_sono_crianca_data
    ON registros_sono (crianca_id, data_registro);
