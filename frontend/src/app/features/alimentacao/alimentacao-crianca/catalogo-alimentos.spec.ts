import { CATALOGO_ALIMENTOS } from './catalogo-alimentos';

describe('Catálogo de alimentos', () => {
  it('mantém códigos únicos para não duplicar alimentos no registro', () => {
    const codigos = CATALOGO_ALIMENTOS.map((alimento) => alimento.codigo);

    expect(new Set(codigos).size).toBe(codigos.length);
  });

  it('inclui todos os grupos previstos para a introdução alimentar', () => {
    const grupos = new Set(CATALOGO_ALIMENTOS.map((alimento) => alimento.grupo));

    expect(grupos.size).toBe(15);
    expect(grupos.has('PSEUDOCEREAL_GRAO_ESPECIAL')).toBe(true);
    expect(grupos.has('OLEAGINOSA')).toBe(true);
    expect(grupos.has('BEBIDA_LIQUIDO')).toBe(true);
    expect(CATALOGO_ALIMENTOS.length).toBeGreaterThan(150);
  });

  it('identifica alimentos que merecem rastreabilidade sem removê-los dos grupos de origem', () => {
    const ovo = CATALOGO_ALIMENTOS.find((alimento) => alimento.codigo === 'ovo');
    const trigo = CATALOGO_ALIMENTOS.find((alimento) => alimento.codigo === 'trigo');

    expect(ovo?.grupo).toBe('OVO');
    expect(ovo?.alergenico).toBe(true);
    expect(trigo?.grupo).toBe('CEREAL_GRAO_MASSA');
    expect(trigo?.alergenico).toBe(true);
  });

  it('mantém a classificação de glúten como dado estruturado', () => {
    const trigo = CATALOGO_ALIMENTOS.find((alimento) => alimento.codigo === 'trigo');
    const arroz = CATALOGO_ALIMENTOS.find((alimento) => alimento.codigo === 'arroz');
    const massa = CATALOGO_ALIMENTOS.find((alimento) => alimento.codigo === 'massa');

    expect(trigo?.classificacaoGluten).toBe('CONTEM');
    expect(arroz?.classificacaoGluten).toBe('NAO_CONTEM');
    expect(massa?.classificacaoGluten).toBe('NAO_INFORMADO');
  });
});
