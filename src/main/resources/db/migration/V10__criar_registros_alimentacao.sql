CREATE TABLE registros_alimentacao (
    id UUID PRIMARY KEY,
    crianca_id UUID NOT NULL REFERENCES criancas(id) ON DELETE CASCADE,
    data_registro DATE NOT NULL,
    tipo_leite VARCHAR(30) NOT NULL,
    estagio_alimentar VARCHAR(50) NOT NULL,
    idade_inicio_alimentacao_complementar_meses INTEGER,
    refeicoes_por_dia INTEGER,
    consome_agua BOOLEAN,
    usa_mamadeira BOOLEAN,
    usa_copo BOOLEAN,
    usa_colher BOOLEAN,
    autoalimentacao BOOLEAN,
    textura_predominante VARCHAR(30) NOT NULL,
    consome_frutas BOOLEAN,
    consome_legumes_verduras BOOLEAN,
    consome_cereais_tuberculos BOOLEAN,
    consome_feijoes_leguminosas BOOLEAN,
    consome_carnes_ovos BOOLEAN,
    ultraprocessados_frequentes BOOLEAN,
    bebidas_adocadas BOOLEAN,
    acucar_adicionado BOOLEAN,
    sal_adicionado BOOLEAN,
    telas_durante_refeicoes BOOLEAN,
    refeicoes_em_familia BOOLEAN,
    rotina_alimentar_regular BOOLEAN,
    seletividade_alimentar BOOLEAN,
    recusa_persistente BOOLEAN,
    engasgos_frequentes BOOLEAN,
    vomitos_recorrentes BOOLEAN,
    constipacao BOOLEAN,
    diarreia_recorrente BOOLEAN,
    dificuldade_ganho_peso_percebida BOOLEAN,
    preocupacao_familia BOOLEAN,
    observacao VARCHAR(1000),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP,
    CONSTRAINT chk_registros_alimentacao_tipo_leite CHECK (
        tipo_leite IN ('LEITE_MATERNO', 'FORMULA_INFANTIL', 'MISTO', 'NAO_CONSOME_LEITE', 'NAO_INFORMADO')
    ),
    CONSTRAINT chk_registros_alimentacao_estagio CHECK (
        estagio_alimentar IN ('APENAS_LEITE', 'INICIANDO_ALIMENTACAO_COMPLEMENTAR', 'ALIMENTACAO_COMPLEMENTAR_ESTABELECIDA', 'COMIDA_DA_FAMILIA', 'NAO_INFORMADO')
    ),
    CONSTRAINT chk_registros_alimentacao_textura CHECK (
        textura_predominante IN ('LIQUIDA', 'PASTOSA', 'AMASSADA', 'PEDACOS_MACIOS', 'COMIDA_DA_FAMILIA', 'NAO_INFORMADO')
    ),
    CONSTRAINT chk_registros_alimentacao_inicio_complementar CHECK (
        idade_inicio_alimentacao_complementar_meses IS NULL OR idade_inicio_alimentacao_complementar_meses BETWEEN 0 AND 24
    ),
    CONSTRAINT chk_registros_alimentacao_refeicoes CHECK (
        refeicoes_por_dia IS NULL OR refeicoes_por_dia BETWEEN 0 AND 10
    )
);

CREATE INDEX idx_registros_alimentacao_crianca_data
    ON registros_alimentacao (crianca_id, data_registro);
