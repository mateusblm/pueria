ALTER TABLE registros_telas ADD COLUMN crianca_escolhe_conteudo_livremente BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE registros_telas_contextos_uso (
    registro_telas_id UUID NOT NULL,
    dispositivo VARCHAR(20) NOT NULL,
    conteudo VARCHAR(40) NOT NULL,
    PRIMARY KEY (registro_telas_id, dispositivo),
    CONSTRAINT fk_registros_telas_contextos_uso_registro
        FOREIGN KEY (registro_telas_id) REFERENCES registros_telas(id) ON DELETE CASCADE
);
