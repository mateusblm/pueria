CREATE TABLE relatos_desenvolvimento (
    id UUID PRIMARY KEY,
    crianca_id UUID NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    registrado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_relatos_desenvolvimento_crianca
        FOREIGN KEY (crianca_id) REFERENCES criancas(id),
    CONSTRAINT chk_relatos_desenvolvimento_tipo
        CHECK (tipo IN ('PERDA_HABILIDADE', 'PREOCUPACAO_FAMILIA'))
);

CREATE INDEX idx_relatos_desenvolvimento_crianca
    ON relatos_desenvolvimento (crianca_id, registrado_em DESC);
