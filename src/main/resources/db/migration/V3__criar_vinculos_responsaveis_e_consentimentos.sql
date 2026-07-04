CREATE TABLE responsaveis_criancas (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    crianca_id UUID NOT NULL,
    parentesco VARCHAR(50) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL,

    CONSTRAINT fk_responsaveis_criancas_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_responsaveis_criancas_crianca
        FOREIGN KEY (crianca_id)
        REFERENCES criancas(id),

    CONSTRAINT uk_responsavel_crianca
        UNIQUE (usuario_id, crianca_id)
);

CREATE INDEX idx_responsaveis_criancas_usuario_id
    ON responsaveis_criancas(usuario_id);

CREATE INDEX idx_responsaveis_criancas_crianca_id
    ON responsaveis_criancas(crianca_id);

CREATE INDEX idx_responsaveis_criancas_principal
    ON responsaveis_criancas(crianca_id, principal);

CREATE TABLE consentimentos (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    crianca_id UUID NOT NULL,
    tipo VARCHAR(80) NOT NULL,
    versao_termo VARCHAR(30) NOT NULL,
    aceito BOOLEAN NOT NULL,
    data_aceite TIMESTAMP NOT NULL,

    CONSTRAINT fk_consentimentos_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_consentimentos_crianca
        FOREIGN KEY (crianca_id)
        REFERENCES criancas(id)
);

CREATE INDEX idx_consentimentos_usuario_crianca_tipo
    ON consentimentos(usuario_id, crianca_id, tipo);
