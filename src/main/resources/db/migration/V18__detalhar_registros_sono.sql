ALTER TABLE registros_sono ADD COLUMN superficie_sono VARCHAR(30) NOT NULL DEFAULT 'NAO_INFORMADA';
ALTER TABLE registros_sono ADD COLUMN ambiente_sono VARCHAR(35) NOT NULL DEFAULT 'NAO_INFORMADO';
ALTER TABLE registros_sono ADD COLUMN ranger_dentes_durante_sono BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE registros_sono ADD COLUMN acorda_bem_disposto BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE registros_sono
SET superficie_sono = CASE local_sono
    WHEN 'BERCO' THEN 'BERCO'
    WHEN 'CAMA_PROPRIA' THEN 'CAMA_PROPRIA'
    WHEN 'CAMA_COMPARTILHADA' THEN 'CAMA_COMPARTILHADA'
    WHEN 'OUTRO' THEN 'OUTRA'
    ELSE 'NAO_INFORMADA'
END,
ambiente_sono = CASE local_sono
    WHEN 'CAMA_COMPARTILHADA' THEN 'QUARTO_DOS_RESPONSAVEIS'
    WHEN 'QUARTO_DOS_RESPONSAVEIS' THEN 'QUARTO_DOS_RESPONSAVEIS'
    WHEN 'QUARTO_DA_PROPRIA_CRIANCA' THEN 'QUARTO_DA_PROPRIA_CRIANCA'
    WHEN 'OUTRO' THEN 'OUTRO'
    ELSE 'NAO_INFORMADO'
END;

CREATE TABLE registros_sono_tipos_despertar (
    registro_sono_id UUID NOT NULL,
    tipo_despertar VARCHAR(45) NOT NULL,
    PRIMARY KEY (registro_sono_id, tipo_despertar),
    CONSTRAINT fk_registros_sono_tipos_despertar_registro
        FOREIGN KEY (registro_sono_id) REFERENCES registros_sono(id) ON DELETE CASCADE
);
