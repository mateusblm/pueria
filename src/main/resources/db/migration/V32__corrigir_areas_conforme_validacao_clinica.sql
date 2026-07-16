-- Classificações confirmadas pela dra. durante a validação clínica.
UPDATE marcos_desenvolvimento
SET area = 'LINGUAGEM_COMUNICACAO'
WHERE id = '10000000-0000-0000-0000-000000002407'
  AND versao_catalogo = 'NEURO_V2_2026_07';

-- O marco “Acena tchau” já é social; mantém-se a atividade na mesma área.
UPDATE estimulos_desenvolvimento
SET area = 'SOCIAL_EMOCIONAL'
WHERE id = '30000000-0000-0000-0000-000000000033'
  AND versao_catalogo = 'ESTIMULOS_MARCO_V2_2026_07';
