CREATE TABLE sessoes_autenticacao (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expira_em TIMESTAMP NOT NULL,
    revogado_em TIMESTAMP,
    criado_em TIMESTAMP NOT NULL,
    ultimo_uso_em TIMESTAMP
);

CREATE INDEX idx_sessoes_autenticacao_usuario ON sessoes_autenticacao(usuario_id);
