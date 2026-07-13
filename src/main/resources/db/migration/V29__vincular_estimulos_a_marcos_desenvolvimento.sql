CREATE TABLE marcos_estimulos_desenvolvimento (
    marco_id UUID PRIMARY KEY,
    estimulo_id UUID NOT NULL,
    CONSTRAINT fk_marcos_estimulos_marco
        FOREIGN KEY (marco_id) REFERENCES marcos_desenvolvimento(id),
    CONSTRAINT fk_marcos_estimulos_estimulo
        FOREIGN KEY (estimulo_id) REFERENCES estimulos_desenvolvimento(id)
);

INSERT INTO marcos_estimulos_desenvolvimento (marco_id, estimulo_id)
SELECT marco.id, estimulo.id
FROM marcos_desenvolvimento marco
JOIN estimulos_desenvolvimento estimulo
    ON estimulo.area = marco.area
    AND marco.idade_meses BETWEEN estimulo.idade_inicial_meses AND estimulo.idade_final_meses
WHERE marco.versao_catalogo = 'NEURO_V2_2026_07'
  AND estimulo.versao_catalogo = 'ESTIMULOS_V1_2026_07'
  AND estimulo.ativo = TRUE;
