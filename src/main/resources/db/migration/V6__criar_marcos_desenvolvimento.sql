CREATE TABLE marcos_desenvolvimento (
    id UUID PRIMARY KEY,
    idade_meses INTEGER NOT NULL,
    area VARCHAR(40) NOT NULL,
    descricao VARCHAR(300) NOT NULL,
    fonte VARCHAR(120) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_marcos_desenvolvimento_idade
    ON marcos_desenvolvimento(idade_meses, area);

CREATE TABLE registros_marcos_desenvolvimento (
    id UUID PRIMARY KEY,
    crianca_id UUID NOT NULL,
    marco_id UUID NOT NULL,
    status VARCHAR(40) NOT NULL,
    observacao VARCHAR(500),
    registrado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP,

    CONSTRAINT fk_registros_marcos_crianca
        FOREIGN KEY (crianca_id)
        REFERENCES criancas(id),

    CONSTRAINT fk_registros_marcos_marco
        FOREIGN KEY (marco_id)
        REFERENCES marcos_desenvolvimento(id),

    CONSTRAINT uk_registro_marco_crianca
        UNIQUE (crianca_id, marco_id)
);

CREATE INDEX idx_registros_marcos_crianca
    ON registros_marcos_desenvolvimento(crianca_id);

INSERT INTO marcos_desenvolvimento (id, idade_meses, area, descricao, fonte, ativo) VALUES
('00000000-0000-0000-0000-000000000201', 2, 'SOCIAL_EMOCIONAL', 'Sorri quando alguém fala ou sorri para ela.', 'CDC/SBP - marcos 2 meses', TRUE),
('00000000-0000-0000-0000-000000000202', 2, 'LINGUAGEM_COMUNICACAO', 'Faz sons além do choro.', 'CDC/SBP - marcos 2 meses', TRUE),
('00000000-0000-0000-0000-000000000203', 2, 'COGNITIVO', 'Observa a pessoa cuidadora enquanto ela se movimenta.', 'CDC/SBP - marcos 2 meses', TRUE),
('00000000-0000-0000-0000-000000000204', 2, 'MOTOR', 'Sustenta a cabeça por alguns instantes quando está de bruços.', 'CDC/SBP - marcos 2 meses', TRUE),

('00000000-0000-0000-0000-000000000401', 4, 'SOCIAL_EMOCIONAL', 'Sorri espontaneamente para chamar atenção.', 'CDC/SBP - marcos 4 meses', TRUE),
('00000000-0000-0000-0000-000000000402', 4, 'LINGUAGEM_COMUNICACAO', 'Produz sons como “aaa” ou “ooo”.', 'CDC/SBP - marcos 4 meses', TRUE),
('00000000-0000-0000-0000-000000000403', 4, 'COGNITIVO', 'Olha para as próprias mãos com interesse.', 'CDC/SBP - marcos 4 meses', TRUE),
('00000000-0000-0000-0000-000000000404', 4, 'MOTOR', 'Mantém a cabeça firme sem apoio quando está no colo.', 'CDC/SBP - marcos 4 meses', TRUE),

('00000000-0000-0000-0000-000000000601', 6, 'SOCIAL_EMOCIONAL', 'Reconhece pessoas familiares.', 'CDC/SBP - marcos 6 meses', TRUE),
('00000000-0000-0000-0000-000000000602', 6, 'LINGUAGEM_COMUNICACAO', 'Alterna sons com a pessoa cuidadora, como em uma conversa.', 'CDC/SBP - marcos 6 meses', TRUE),
('00000000-0000-0000-0000-000000000603', 6, 'COGNITIVO', 'Leva objetos à boca para explorar.', 'CDC/SBP - marcos 6 meses', TRUE),
('00000000-0000-0000-0000-000000000604', 6, 'MOTOR', 'Rola da barriga para as costas.', 'CDC/SBP - marcos 6 meses', TRUE),

('00000000-0000-0000-0000-000000000901', 9, 'SOCIAL_EMOCIONAL', 'Pode estranhar ou ficar tímida perto de pessoas desconhecidas.', 'CDC/SBP - marcos 9 meses', TRUE),
('00000000-0000-0000-0000-000000000902', 9, 'LINGUAGEM_COMUNICACAO', 'Faz sons repetidos como “mamama” ou “bababa”.', 'CDC/SBP - marcos 9 meses', TRUE),
('00000000-0000-0000-0000-000000000903', 9, 'COGNITIVO', 'Procura um objeto quando vê que ele caiu.', 'CDC/SBP - marcos 9 meses', TRUE),
('00000000-0000-0000-0000-000000000904', 9, 'MOTOR', 'Consegue sentar sem ajuda.', 'CDC/SBP - marcos 9 meses', TRUE),

('00000000-0000-0000-0000-000000001201', 12, 'SOCIAL_EMOCIONAL', 'Brinca de jogos simples, como bater palminhas.', 'CDC/SBP - marcos 12 meses', TRUE),
('00000000-0000-0000-0000-000000001202', 12, 'LINGUAGEM_COMUNICACAO', 'Acena para dar tchau.', 'CDC/SBP - marcos 12 meses', TRUE),
('00000000-0000-0000-0000-000000001203', 12, 'COGNITIVO', 'Coloca algo dentro de um recipiente.', 'CDC/SBP - marcos 12 meses', TRUE),
('00000000-0000-0000-0000-000000001204', 12, 'MOTOR', 'Puxa o corpo para ficar em pé.', 'CDC/SBP - marcos 12 meses', TRUE),

('00000000-0000-0000-0000-000000001501', 15, 'SOCIAL_EMOCIONAL', 'Imita outras crianças durante a brincadeira.', 'CDC/SBP - marcos 15 meses', TRUE),
('00000000-0000-0000-0000-000000001502', 15, 'LINGUAGEM_COMUNICACAO', 'Tenta falar uma ou duas palavras além de “mamã” ou “papá”.', 'CDC/SBP - marcos 15 meses', TRUE),
('00000000-0000-0000-0000-000000001503', 15, 'COGNITIVO', 'Tenta usar objetos do jeito esperado, como telefone ou copo.', 'CDC/SBP - marcos 15 meses', TRUE),
('00000000-0000-0000-0000-000000001504', 15, 'MOTOR', 'Dá alguns passos sem ajuda.', 'CDC/SBP - marcos 15 meses', TRUE),

('00000000-0000-0000-0000-000000001801', 18, 'SOCIAL_EMOCIONAL', 'Afasta-se para explorar, mas verifica se a pessoa cuidadora está por perto.', 'CDC/SBP - marcos 18 meses', TRUE),
('00000000-0000-0000-0000-000000001802', 18, 'LINGUAGEM_COMUNICACAO', 'Fala três ou mais palavras além de “mamã” ou “papá”.', 'CDC/SBP - marcos 18 meses', TRUE),
('00000000-0000-0000-0000-000000001803', 18, 'COGNITIVO', 'Imita tarefas simples da casa, como varrer.', 'CDC/SBP - marcos 18 meses', TRUE),
('00000000-0000-0000-0000-000000001804', 18, 'MOTOR', 'Anda sem se apoiar em alguém ou em móveis.', 'CDC/SBP - marcos 18 meses', TRUE),

('00000000-0000-0000-0000-000000002401', 24, 'SOCIAL_EMOCIONAL', 'Percebe quando outra pessoa está triste ou machucada.', 'CDC/SBP - marcos 2 anos', TRUE),
('00000000-0000-0000-0000-000000002402', 24, 'LINGUAGEM_COMUNICACAO', 'Fala duas palavras juntas, como “mais água”.', 'CDC/SBP - marcos 2 anos', TRUE),
('00000000-0000-0000-0000-000000002403', 24, 'COGNITIVO', 'Tenta usar botões, interruptores ou controles em brinquedos.', 'CDC/SBP - marcos 2 anos', TRUE),
('00000000-0000-0000-0000-000000002404', 24, 'MOTOR', 'Chuta uma bola.', 'CDC/SBP - marcos 2 anos', TRUE),

('00000000-0000-0000-0000-000000003001', 30, 'SOCIAL_EMOCIONAL', 'Brinca perto de outras crianças e às vezes brinca junto com elas.', 'CDC/SBP - marcos 30 meses', TRUE),
('00000000-0000-0000-0000-000000003002', 30, 'LINGUAGEM_COMUNICACAO', 'Usa cerca de 50 palavras.', 'CDC/SBP - marcos 30 meses', TRUE),
('00000000-0000-0000-0000-000000003003', 30, 'COGNITIVO', 'Segue instruções de duas etapas.', 'CDC/SBP - marcos 30 meses', TRUE),
('00000000-0000-0000-0000-000000003004', 30, 'MOTOR', 'Salta tirando os dois pés do chão.', 'CDC/SBP - marcos 30 meses', TRUE),

('00000000-0000-0000-0000-000000003601', 36, 'SOCIAL_EMOCIONAL', 'Acalma-se em até cerca de 10 minutos após a saída da pessoa cuidadora.', 'CDC/SBP - marcos 3 anos', TRUE),
('00000000-0000-0000-0000-000000003602', 36, 'LINGUAGEM_COMUNICACAO', 'Conversa usando pelo menos duas trocas de fala.', 'CDC/SBP - marcos 3 anos', TRUE),
('00000000-0000-0000-0000-000000003603', 36, 'COGNITIVO', 'Desenha um círculo quando alguém mostra como fazer.', 'CDC/SBP - marcos 3 anos', TRUE),
('00000000-0000-0000-0000-000000003604', 36, 'MOTOR', 'Veste algumas peças de roupa sozinha, como calça ou casaco aberto.', 'CDC/SBP - marcos 3 anos', TRUE),

('00000000-0000-0000-0000-000000004801', 48, 'SOCIAL_EMOCIONAL', 'Finge ser outra pessoa ou personagem durante a brincadeira.', 'CDC/SBP - marcos 4 anos', TRUE),
('00000000-0000-0000-0000-000000004802', 48, 'LINGUAGEM_COMUNICACAO', 'Fala frases com quatro ou mais palavras.', 'CDC/SBP - marcos 4 anos', TRUE),
('00000000-0000-0000-0000-000000004803', 48, 'COGNITIVO', 'Nomeia algumas cores.', 'CDC/SBP - marcos 4 anos', TRUE),
('00000000-0000-0000-0000-000000004804', 48, 'MOTOR', 'Pega uma bola grande na maioria das tentativas.', 'CDC/SBP - marcos 4 anos', TRUE),

('00000000-0000-0000-0000-000000006001', 60, 'SOCIAL_EMOCIONAL', 'Segue regras ou espera sua vez ao brincar com outras crianças.', 'CDC/SBP - marcos 5 anos', TRUE),
('00000000-0000-0000-0000-000000006002', 60, 'LINGUAGEM_COMUNICACAO', 'Conta uma história que ouviu ou inventou.', 'CDC/SBP - marcos 5 anos', TRUE),
('00000000-0000-0000-0000-000000006003', 60, 'COGNITIVO', 'Conta até 10.', 'CDC/SBP - marcos 5 anos', TRUE),
('00000000-0000-0000-0000-000000006004', 60, 'MOTOR', 'Pula em um pé só.', 'CDC/SBP - marcos 5 anos', TRUE);
