CREATE TABLE tokens_redefinicao_senha (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expira_em TIMESTAMP NOT NULL,
    usado_em TIMESTAMP,
    criado_em TIMESTAMP NOT NULL
);

CREATE INDEX idx_tokens_redefinicao_senha_usuario_ativo
    ON tokens_redefinicao_senha(usuario_id, criado_em DESC)
    WHERE usado_em IS NULL;
