-- O contexto “Uma ideia para experimentar” já aparece na interface.
UPDATE estimulos_desenvolvimento
SET titulo = REPLACE(titulo, 'Atividade para: ', '')
WHERE versao_catalogo = 'ESTIMULOS_MARCO_V2_2026_07';
