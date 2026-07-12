ALTER TABLE marcos_desenvolvimento
    ADD COLUMN tipo_fonte VARCHAR(40),
    ADD COLUMN versao_catalogo VARCHAR(40),
    ADD COLUMN papel_clinico VARCHAR(40),
    ADD COLUMN alta_relevancia_vigilancia BOOLEAN;

DELETE FROM registros_marcos_desenvolvimento;
DELETE FROM marcos_desenvolvimento;

ALTER TABLE marcos_desenvolvimento
    ALTER COLUMN tipo_fonte SET NOT NULL,
    ALTER COLUMN versao_catalogo SET NOT NULL,
    ALTER COLUMN papel_clinico SET NOT NULL,
    ALTER COLUMN alta_relevancia_vigilancia SET NOT NULL,
    ADD CONSTRAINT chk_marcos_desenvolvimento_tipo_fonte
        CHECK (tipo_fonte IN ('LEGADO_CDC_SBP', 'OMS', 'CDC_CLASSICO', 'CDC_2022')),
    ADD CONSTRAINT chk_marcos_desenvolvimento_papel_clinico
        CHECK (papel_clinico IN ('ACOMPANHAMENTO', 'ATENCAO_PERSISTENTE', 'ALTA_RELEVANCIA'));

CREATE INDEX idx_marcos_desenvolvimento_catalogo
    ON marcos_desenvolvimento (versao_catalogo, idade_meses, area);

INSERT INTO marcos_desenvolvimento (
    id, idade_meses, area, descricao, fonte, tipo_fonte, versao_catalogo,
    papel_clinico, alta_relevancia_vigilancia, ativo
) VALUES
-- 2 meses
('10000000-0000-0000-0000-000000000201', 2, 'SOCIAL_EMOCIONAL', 'Acalma-se no colo ou com a voz de quem cuida.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000202', 2, 'SOCIAL_EMOCIONAL', 'Sorri ao ver o rosto de alguém.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000203', 2, 'LINGUAGEM_COMUNICACAO', 'Faz sons além do choro.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000204', 2, 'LINGUAGEM_COMUNICACAO', 'Reage a sons altos.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000205', 2, 'COGNITIVO', 'Presta atenção a rostos.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000206', 2, 'COGNITIVO', 'Segue objetos com o olhar por curtos períodos.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000207', 2, 'MOTOR', 'Levanta a cabeça brevemente quando está de bruços e acordada.', 'CDC clássico / AAP Bright Futures', 'CDC_CLASSICO', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000208', 2, 'MOTOR', 'Movimenta braços e pernas dos dois lados do corpo.', 'CDC clássico / AAP Bright Futures', 'CDC_CLASSICO', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
-- 4 meses
('10000000-0000-0000-0000-000000000401', 4, 'SOCIAL_EMOCIONAL', 'Sorri espontaneamente para chamar atenção.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000402', 4, 'SOCIAL_EMOCIONAL', 'Começa a imitar expressões faciais.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000403', 4, 'LINGUAGEM_COMUNICACAO', 'Faz arrulhos em uma troca de sons com outra pessoa.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000404', 4, 'LINGUAGEM_COMUNICACAO', 'Vira-se para vozes familiares.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000405', 4, 'COGNITIVO', 'Observa as próprias mãos com interesse.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000406', 4, 'COGNITIVO', 'Segue objetos em movimento de um lado a outro.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000407', 4, 'MOTOR', 'Sustenta a cabeça firme quando está sentada com apoio.', 'CDC clássico / AAP Bright Futures', 'CDC_CLASSICO', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000408', 4, 'MOTOR', 'Rola de bruços para as costas.', 'CDC clássico / AAP Bright Futures', 'CDC_CLASSICO', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
-- 6 meses
('10000000-0000-0000-0000-000000000601', 6, 'SOCIAL_EMOCIONAL', 'Reconhece pessoas familiares e pode reagir diferente a pessoas desconhecidas.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000602', 6, 'SOCIAL_EMOCIONAL', 'Ri alto ou gargalha.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000603', 6, 'LINGUAGEM_COMUNICACAO', 'Faz sons de vogais e consoantes.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000604', 6, 'COGNITIVO', 'Leva objetos à boca para explorar.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000605', 6, 'COGNITIVO', 'Estica o braço para alcançar um objeto.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000606', 6, 'MOTOR', 'Senta sem apoio.', 'OMS, Motor Development Study (2006)', 'OMS', 'NEURO_V2_2026_07', 'ATENCAO_PERSISTENTE', FALSE, TRUE),
('10000000-0000-0000-0000-000000000607', 6, 'MOTOR', 'Sustenta peso nas pernas quando apoiada em pé.', 'OMS, Motor Development Study (2006)', 'OMS', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
-- 9 meses
('10000000-0000-0000-0000-000000000901', 9, 'SOCIAL_EMOCIONAL', 'Pode estranhar pessoas novas.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000902', 9, 'SOCIAL_EMOCIONAL', 'Reage quando é chamada pelo nome.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ALTA_RELEVANCIA', TRUE, TRUE),
('10000000-0000-0000-0000-000000000903', 9, 'LINGUAGEM_COMUNICACAO', 'Balbucia em sequência, como “dadada” ou “bababa”.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000904', 9, 'LINGUAGEM_COMUNICACAO', 'Aponta ou vocaliza para pedir algo.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000905', 9, 'COGNITIVO', 'Procura um objeto parcialmente escondido.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000906', 9, 'COGNITIVO', 'Bate dois objetos um contra o outro.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000000907', 9, 'MOTOR', 'Fica em pé com apoio ou anda se apoiando em móveis.', 'OMS, Motor Development Study (2006)', 'OMS', 'NEURO_V2_2026_07', 'ATENCAO_PERSISTENTE', FALSE, TRUE),
('10000000-0000-0000-0000-000000000908', 9, 'MOTOR', 'Engatinha ou se desloca de alguma forma.', 'OMS, Motor Development Study (2006)', 'OMS', 'NEURO_V2_2026_07', 'ATENCAO_PERSISTENTE', FALSE, TRUE),
-- 12 meses
('10000000-0000-0000-0000-000000001201', 12, 'SOCIAL_EMOCIONAL', 'Brinca de dar e receber objetos.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001202', 12, 'SOCIAL_EMOCIONAL', 'Acena “tchau”.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001203', 12, 'LINGUAGEM_COMUNICACAO', 'Fala uma primeira palavra com intenção de se comunicar.', 'CDC clássico / AAP Bright Futures', 'CDC_CLASSICO', 'NEURO_V2_2026_07', 'ATENCAO_PERSISTENTE', FALSE, TRUE),
('10000000-0000-0000-0000-000000001204', 12, 'LINGUAGEM_COMUNICACAO', 'Aponta para pedir algo.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001205', 12, 'COGNITIVO', 'Coloca e retira objetos de um recipiente.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001206', 12, 'COGNITIVO', 'Imita gestos simples.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001207', 12, 'MOTOR', 'Fica em pé sozinho.', 'OMS, Motor Development Study (2006)', 'OMS', 'NEURO_V2_2026_07', 'ATENCAO_PERSISTENTE', FALSE, TRUE),
('10000000-0000-0000-0000-000000001208', 12, 'MOTOR', 'Anda sozinho, sem apoio.', 'OMS, Motor Development Study (2006)', 'OMS', 'NEURO_V2_2026_07', 'ATENCAO_PERSISTENTE', FALSE, TRUE),
('10000000-0000-0000-0000-000000001209', 12, 'MOTOR', 'Usa a pinça entre polegar e indicador para pegar objetos pequenos.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
-- 15 meses
('10000000-0000-0000-0000-000000001501', 15, 'SOCIAL_EMOCIONAL', 'Demonstra afeto espontaneamente.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001502', 15, 'SOCIAL_EMOCIONAL', 'Ajuda a se vestir.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001503', 15, 'LINGUAGEM_COMUNICACAO', 'Usa pelo menos três palavras isoladas.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001504', 15, 'LINGUAGEM_COMUNICACAO', 'Segue uma instrução simples sem gesto de apoio.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001505', 15, 'COGNITIVO', 'Usa objetos do dia a dia de forma esperada, como copo ou escova.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001506', 15, 'COGNITIVO', 'Empilha pelo menos dois objetos.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001507', 15, 'MOTOR', 'Anda com boa estabilidade, sem cair com frequência.', 'CDC clássico / AAP Bright Futures', 'CDC_CLASSICO', 'NEURO_V2_2026_07', 'ATENCAO_PERSISTENTE', FALSE, TRUE),
('10000000-0000-0000-0000-000000001508', 15, 'MOTOR', 'Sobe alguns degraus baixos com ajuda.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
-- 18 meses
('10000000-0000-0000-0000-000000001801', 18, 'SOCIAL_EMOCIONAL', 'Brinca perto de outras crianças.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001802', 18, 'SOCIAL_EMOCIONAL', 'Aponta para mostrar algo interessante a outra pessoa.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ALTA_RELEVANCIA', TRUE, TRUE),
('10000000-0000-0000-0000-000000001803', 18, 'LINGUAGEM_COMUNICACAO', 'Tem vocabulário de pelo menos dez palavras.', 'CDC clássico / AAP Bright Futures', 'CDC_CLASSICO', 'NEURO_V2_2026_07', 'ATENCAO_PERSISTENTE', FALSE, TRUE),
('10000000-0000-0000-0000-000000001804', 18, 'LINGUAGEM_COMUNICACAO', 'Aponta para uma parte do corpo quando solicitado.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001805', 18, 'COGNITIVO', 'Mostra para que servem objetos comuns.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001806', 18, 'COGNITIVO', 'Aponta ou gesticula para pedir ajuda.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001807', 18, 'MOTOR', 'Sobe e desce de móveis baixos sozinha.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000001808', 18, 'MOTOR', 'Come com colher, mesmo derramando um pouco.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
-- 24 meses
('10000000-0000-0000-0000-000000002401', 24, 'SOCIAL_EMOCIONAL', 'Percebe quando alguém está triste ou machucado.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000002402', 24, 'SOCIAL_EMOCIONAL', 'Brinca de forma mais interativa com outras crianças.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000002403', 24, 'LINGUAGEM_COMUNICACAO', 'Fala frases de duas palavras.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000002404', 24, 'LINGUAGEM_COMUNICACAO', 'Tem vocabulário de aproximadamente cinquenta palavras.', 'CDC clássico / AAP Bright Futures', 'CDC_CLASSICO', 'NEURO_V2_2026_07', 'ATENCAO_PERSISTENTE', FALSE, TRUE),
('10000000-0000-0000-0000-000000002405', 24, 'LINGUAGEM_COMUNICACAO', 'Segue uma instrução de dois passos.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000002406', 24, 'COGNITIVO', 'Faz brincadeira de faz-de-conta com sequência de dois passos.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000002407', 24, 'COGNITIVO', 'Nomeia pelo menos cinco objetos do dia a dia.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000002408', 24, 'MOTOR', 'Corre e chuta uma bola.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE),
('10000000-0000-0000-0000-000000002409', 24, 'MOTOR', 'Constrói uma torre de pelo menos quatro blocos.', 'CDC Learn the Signs. Act Early (2022)', 'CDC_2022', 'NEURO_V2_2026_07', 'ACOMPANHAMENTO', FALSE, TRUE);
