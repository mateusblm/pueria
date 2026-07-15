CREATE TABLE registros_saude_cuidados (
    id UUID PRIMARY KEY,
    crianca_id UUID NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    data_registro DATE NOT NULL,
    descricao VARCHAR(4000) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP,
    CONSTRAINT fk_registros_saude_cuidados_crianca FOREIGN KEY (crianca_id) REFERENCES criancas(id),
    CONSTRAINT chk_registros_saude_cuidados_tipo CHECK (tipo IN ('MEDICAMENTO_SUPLEMENTO', 'INTERCORRENCIA_CLINICA'))
);

CREATE INDEX idx_registros_saude_cuidados_crianca_data
    ON registros_saude_cuidados (crianca_id, data_registro DESC, criado_em DESC);
