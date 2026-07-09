CREATE TABLE registros_alimentacao_alimentos (
    registro_alimentacao_id UUID NOT NULL,
    codigo VARCHAR(80) NOT NULL,
    nome VARCHAR(120) NOT NULL,
    grupo VARCHAR(40) NOT NULL,
    CONSTRAINT fk_registros_alimentacao_alimentos_registro
        FOREIGN KEY (registro_alimentacao_id)
        REFERENCES registros_alimentacao (id)
        ON DELETE CASCADE,
    CONSTRAINT uk_registros_alimentacao_alimentos_codigo
        UNIQUE (registro_alimentacao_id, codigo)
);

CREATE INDEX idx_registros_alimentacao_alimentos_registro
    ON registros_alimentacao_alimentos (registro_alimentacao_id);

CREATE INDEX idx_registros_alimentacao_alimentos_grupo
    ON registros_alimentacao_alimentos (grupo);
