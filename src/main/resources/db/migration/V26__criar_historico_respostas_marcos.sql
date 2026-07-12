CREATE TABLE historico_respostas_marcos_desenvolvimento (
    id UUID PRIMARY KEY, crianca_id UUID NOT NULL, marco_id UUID NOT NULL,
    status_anterior VARCHAR(40), status_novo VARCHAR(40) NOT NULL,
    observacao_anterior VARCHAR(500), observacao_nova VARCHAR(500),
    modalidade VARCHAR(40) NOT NULL, registrado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_historico_marcos_crianca FOREIGN KEY (crianca_id) REFERENCES criancas(id),
    CONSTRAINT fk_historico_marcos_marco FOREIGN KEY (marco_id) REFERENCES marcos_desenvolvimento(id)
);
CREATE INDEX idx_historico_marcos_crianca_marco ON historico_respostas_marcos_desenvolvimento (crianca_id, marco_id, registrado_em DESC);
