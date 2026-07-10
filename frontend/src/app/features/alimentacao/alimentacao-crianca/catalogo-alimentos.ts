import { AlimentoRegistroAlimentacao, GrupoAlimento } from '../../../shared/models/alimentacao.model';

export interface CatalogoAlimento extends AlimentoRegistroAlimentacao {
  gruposRelacionados?: GrupoAlimento[];
}

const criar = (
  grupo: GrupoAlimento,
  itens: Array<[string, string]>,
  opcoes?: { alergenicos?: string[] }
): CatalogoAlimento[] => itens.map(([codigo, nome]) => ({
  codigo,
  nome,
  grupo,
  alergenico: opcoes?.alergenicos?.includes(codigo) ?? false
}));

export const CATALOGO_ALIMENTOS: CatalogoAlimento[] = [
  ...criar('FRUTA', [
    ['banana', 'Banana'], ['maca', 'Maçã'], ['pera', 'Pera'], ['mamao', 'Mamão'], ['manga', 'Manga'],
    ['abacate', 'Abacate'], ['melancia', 'Melancia'], ['melao', 'Melão'], ['laranja', 'Laranja'],
    ['mexerica', 'Mexerica/tangerina'], ['limao', 'Limão'], ['uva', 'Uva'], ['morango', 'Morango'],
    ['kiwi', 'Kiwi'], ['pessego', 'Pêssego'], ['ameixa', 'Ameixa'], ['nectarina', 'Nectarina'],
    ['caqui', 'Caqui'], ['goiaba', 'Goiaba'], ['figo', 'Figo'], ['roma', 'Romã'], ['jabuticaba', 'Jabuticaba'],
    ['pitanga', 'Pitanga'], ['amora', 'Amora'], ['framboesa', 'Framboesa'], ['mirtilo', 'Mirtilo'],
    ['cereja', 'Cereja'], ['caju', 'Caju'], ['acerola', 'Acerola'], ['carambola', 'Carambola'],
    ['graviola', 'Graviola'], ['pinha', 'Pinha/fruta-do-conde'], ['atemoia', 'Atemoia'], ['sapoti', 'Sapoti'],
    ['jenipapo', 'Jenipapo'], ['cupuacu', 'Cupuaçu'], ['acai', 'Açaí sem xarope'], ['buriti', 'Buriti'],
    ['bacaba', 'Bacaba'], ['murici', 'Murici'], ['cambuci', 'Cambuci'], ['seriguela', 'Seriguela'],
    ['umbu', 'Umbu'], ['araca', 'Araçá'], ['maracuja', 'Maracujá'], ['abacaxi', 'Abacaxi'],
    ['coco', 'Coco'], ['jaca', 'Jaca'], ['lichia', 'Lichia'], ['rambuta', 'Rambutã'], ['tamara', 'Tâmara'],
    ['damasco', 'Damasco'], ['uva-passa', 'Uva-passa'], ['ameixa-seca', 'Ameixa seca']
  ]),
  ...criar('LEGUME_HORTALICA_FRUTO', [
    ['abobrinha', 'Abobrinha'], ['abobora', 'Abóbora'], ['chuchu', 'Chuchu'], ['berinjela', 'Berinjela'],
    ['pepino', 'Pepino'], ['tomate', 'Tomate'], ['tomate-cereja', 'Tomate-cereja'], ['quiabo', 'Quiabo'],
    ['maxixe', 'Maxixe'], ['jilo', 'Jiló'], ['vagem', 'Vagem'], ['ervilha-torta', 'Ervilha-torta'],
    ['milho-verde', 'Milho-verde'], ['palmito', 'Palmito'], ['alcachofra', 'Alcachofra'], ['aspargos', 'Aspargos']
  ]),
  ...criar('VERDURA_FOLHA', [
    ['alface', 'Alface'], ['rucula', 'Rúcula'], ['agriao', 'Agrião'], ['espinafre', 'Espinafre'],
    ['couve', 'Couve'], ['couve-manteiga', 'Couve-manteiga'], ['couve-bruxelas', 'Couve-de-bruxelas'],
    ['acelga', 'Acelga'], ['repolho-branco', 'Repolho branco'], ['repolho-roxo', 'Repolho roxo'],
    ['chicoria', 'Chicória'], ['escarola', 'Escarola'], ['almeirao', 'Almeirão'], ['mostarda', 'Mostarda'],
    ['bertalha', 'Bertalha']
  ]),
  ...criar('RAIZ_TUBERCULO_AMIDO', [
    ['batata-inglesa', 'Batata inglesa'], ['batata-doce', 'Batata-doce'], ['mandioquinha', 'Batata-baroa/mandioquinha'],
    ['mandioca', 'Mandioca/aipim/macaxeira'], ['inhame', 'Inhame'], ['cara', 'Cará'], ['taro', 'Taro'],
    ['nabo', 'Nabo'], ['rabanete', 'Rabanete'], ['beterraba', 'Beterraba'], ['cenoura', 'Cenoura']
  ]),
  ...criar('CEREAL_GRAO_MASSA', [
    ['arroz', 'Arroz'], ['milho', 'Milho'], ['fuba', 'Fubá'], ['canjica', 'Canjica'],
    ['cuscuz-milho', 'Cuscuz de milho'], ['aveia', 'Aveia'], ['trigo', 'Trigo'], ['cevada', 'Cevada'],
    ['centeio', 'Centeio'], ['bulgur', 'Bulgur/triguilho'], ['cuscuz-marroquino', 'Cuscuz marroquino'],
    ['semolina', 'Semolina'], ['farinha-trigo', 'Farinha de trigo'], ['massa-com-gluten', 'Massa com glúten'],
    ['massa-sem-gluten', 'Massa sem glúten'], ['massa-tracos-gluten', 'Massa que pode conter traços de glúten']
  ], { alergenicos: ['trigo', 'farinha-trigo', 'massa-com-gluten'] }),
  ...criar('PSEUDOCEREAL_GRAO_ESPECIAL', [
    ['quinoa', 'Quinoa'], ['amaranto', 'Amaranto'], ['trigo-sarraceno', 'Trigo-sarraceno']
  ]),
  ...criar('LEGUMINOSA', [
    ['feijao', 'Feijão'], ['lentilha', 'Lentilha'], ['grao-de-bico', 'Grão-de-bico'], ['ervilha', 'Ervilha'],
    ['soja', 'Soja']
  ], { alergenicos: ['soja'] }),
  ...criar('CARNE_AVE', [
    ['carne-bovina', 'Carne bovina'], ['carne-suina', 'Carne suína'], ['frango', 'Frango'], ['peru', 'Peru'],
    ['pato', 'Pato'], ['cordeiro', 'Cordeiro'], ['cabrito', 'Cabrito'], ['coelho', 'Coelho']
  ]),
  ...criar('PEIXE_FRUTO_MAR', [
    ['peixe', 'Peixe'], ['camarao', 'Camarão'], ['lula', 'Lula'], ['polvo', 'Polvo'], ['mexilhao', 'Mexilhão'],
    ['ostra', 'Ostra'], ['vieira', 'Vieira'], ['caranguejo', 'Caranguejo'], ['siri', 'Siri']
  ], { alergenicos: ['peixe', 'camarao', 'lula', 'polvo', 'mexilhao', 'ostra', 'vieira', 'caranguejo', 'siri'] }),
  ...criar('OVO', [['ovo', 'Ovo']], { alergenicos: ['ovo'] }),
  ...criar('LEITE_DERIVADO', [
    ['leite-materno', 'Leite materno'], ['formula-infantil', 'Fórmula infantil'], ['leite-vaca', 'Leite de vaca'],
    ['iogurte-natural', 'Iogurte natural integral sem açúcar'], ['queijo', 'Queijo'], ['requeijao', 'Requeijão'],
    ['manteiga', 'Manteiga']
  ], { alergenicos: ['leite-vaca', 'iogurte-natural', 'queijo', 'requeijao', 'manteiga'] }),
  ...criar('OLEAGINOSA', [
    ['amendoim', 'Amendoim'], ['castanha-caju', 'Castanha-de-caju'], ['castanha-para', 'Castanha-do-pará'],
    ['amendoa', 'Amêndoa'], ['noz', 'Noz'], ['avela', 'Avelã'], ['pistache', 'Pistache'],
    ['macadamia', 'Macadâmia'], ['pinhao', 'Pinhão'], ['pecan', 'Pecan']
  ], { alergenicos: ['amendoim', 'castanha-caju', 'castanha-para', 'amendoa', 'noz', 'avela', 'pistache', 'macadamia', 'pecan'] }),
  ...criar('SEMENTE', [['chia', 'Chia'], ['linhaca', 'Linhaça'], ['gergelim', 'Gergelim']], { alergenicos: ['gergelim'] }),
  ...criar('GORDURA', [['azeite-oliva', 'Azeite de oliva'], ['ghee', 'Ghee'], ['outro-oleo', 'Outro óleo']]),
  ...criar('BEBIDA_LIQUIDO', [
    ['agua', 'Água'], ['agua-coco', 'Água de coco'], ['suco-natural', 'Suco natural']
  ])
].map((alimento) => {
  const relacionados: Partial<Record<string, GrupoAlimento[]>> = {
    abacate: ['GORDURA'], coco: ['GORDURA'], manteiga: ['GORDURA'],
    'leite-materno': ['BEBIDA_LIQUIDO'], 'formula-infantil': ['BEBIDA_LIQUIDO']
  };
  return { ...alimento, gruposRelacionados: relacionados[alimento.codigo] };
});

export const ORIENTACOES_GRUPOS: Partial<Record<GrupoAlimento | 'ALERGENICOS', string>> = {
  FRUTA: 'Uva, cereja e outras frutas pequenas ou arredondadas precisam de corte seguro para a idade e a habilidade da criança.',
  CEREAL_GRAO_MASSA: 'O glúten está presente no trigo, centeio e cevada. Sua retirada deve ser orientada após avaliação profissional.',
  PEIXE_FRUTO_MAR: 'Observe o preparo, retire espinhas e ofereça uma textura adequada à idade.',
  LEITE_DERIVADO: 'Antes de 1 ano, leite de vaca não deve substituir o leite materno ou a fórmula como bebida principal.',
  OLEAGINOSA: 'Para crianças pequenas, ofereça em pasta fina, farinha ou preparações adequadas. Não ofereça castanhas inteiras.',
  BEBIDA_LIQUIDO: 'Priorize água e fruta inteira. Sucos não são recomendados como parte habitual da rotina na primeira infância.',
  ALERGENICOS: 'Esta seleção ajuda a lembrar quando e como o alimento foi oferecido. Ela não indica restrição nem diagnóstico de alergia.'
};
