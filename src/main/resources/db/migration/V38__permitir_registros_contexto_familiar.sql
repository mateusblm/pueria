ALTER TABLE registros_saude_cuidados
    DROP CONSTRAINT chk_registros_saude_cuidados_tipo;

ALTER TABLE registros_saude_cuidados
    ADD CONSTRAINT chk_registros_saude_cuidados_tipo
    CHECK (tipo IN (
        'MEDICAMENTO_SUPLEMENTO',
        'INTERCORRENCIA_CLINICA',
        'HUMOR_COMPORTAMENTO',
        'OBSERVACAO_EVENTO_MARCANTE'
    ));
