CREATE TABLE convites_cuidadores (
    id UUID PRIMARY KEY,
    crianca_id UUID NOT NULL REFERENCES criancas(id) ON DELETE CASCADE,
    convidado_usuario_id UUID NOT NULL REFERENCES usuarios(id),
    criado_por_usuario_id UUID NOT NULL REFERENCES usuarios(id),
    parentesco VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    respondido_em TIMESTAMP NULL,
    CONSTRAINT uk_convite_cuidador_pendente UNIQUE (crianca_id, convidado_usuario_id, estado)
);

CREATE INDEX idx_convites_cuidadores_convidado ON convites_cuidadores(convidado_usuario_id, estado);
