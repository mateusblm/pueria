CREATE TABLE criancas (
    id UUID PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    nome_normalizado VARCHAR(150) NOT NULL,
    data_nascimento DATE NOT NULL,
    sexo VARCHAR(20),
    prematura BOOLEAN NOT NULL,
    semanas_gestacionais INTEGER,
    peso_nascimento_gramas INTEGER,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP
);

CREATE INDEX idx_criancas_nome_normalizado ON criancas(nome_normalizado);
CREATE INDEX idx_criancas_data_nascimento ON criancas(data_nascimento);
