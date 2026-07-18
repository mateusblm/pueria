export type TipoLeiteAlimentacao = 'LEITE_MATERNO' | 'FORMULA_INFANTIL' | 'MISTO' | 'NAO_CONSOME_LEITE' | 'NAO_INFORMADO';
export type EstagioAlimentar = 'APENAS_LEITE' | 'INICIANDO_ALIMENTACAO_COMPLEMENTAR' | 'ALIMENTACAO_COMPLEMENTAR_ESTABELECIDA' | 'COMIDA_DA_FAMILIA' | 'NAO_INFORMADO';
export type TexturaAlimentar = 'LIQUIDA' | 'PASTOSA' | 'AMASSADA' | 'PEDACOS_MACIOS' | 'COMIDA_DA_FAMILIA' | 'NAO_INFORMADO';
export type TipoOrigemAlimento = 'ORGANICO' | 'CONVENCIONAL' | 'MISTO' | 'NAO_INFORMADO';
export type OrigemPreparoAlimento = 'PREPARO_EM_CASA' | 'PREPARO_NA_ESCOLA_CRECHE' | 'PREPARO_EM_RESTAURANTES' | 'ALIMENTOS_CONGELADOS' | 'MISTO_CASA_RESTAURANTE' | 'NAO_INFORMADO';
export type AceitacaoAlimento = 'BOA' | 'PARCIAL' | 'RECUSOU' | 'NAO_INFORMADA';
export type ClassificacaoGluten = 'CONTEM' | 'NAO_CONTEM' | 'PODE_CONTER_TRACOS' | 'NAO_INFORMADO' | 'NAO_SE_APLICA';
export type SituacaoSinaisOferta = 'NAO_INFORMADO' | 'NENHUM_PERCEBIDO' | 'SINAIS_PERCEBIDOS';
export type GrupoAlimento =
  | 'FRUTA'
  | 'LEGUME_HORTALICA_FRUTO'
  | 'VERDURA_FOLHA'
  | 'RAIZ_TUBERCULO_AMIDO'
  | 'CEREAL_GRAO_MASSA'
  | 'PSEUDOCEREAL_GRAO_ESPECIAL'
  | 'LEGUMINOSA'
  | 'CARNE_AVE'
  | 'PEIXE_FRUTO_MAR'
  | 'OVO'
  | 'LEITE_DERIVADO'
  | 'OLEAGINOSA'
  | 'SEMENTE'
  | 'GORDURA'
  | 'BEBIDA_LIQUIDO';


export interface AlimentoRegistroAlimentacao {
  codigo: string;
  nome: string;
  grupo: GrupoAlimento;
  alergenico?: boolean;
  dataIntroducao?: string | null;
  formaPreparo?: string | null;
  textura?: TexturaAlimentar | null;
  quantidadeAproximada?: string | null;
  aceitacao?: AceitacaoAlimento | null;
  classificacaoGluten?: ClassificacaoGluten | null;
  tipoPeixe?: string | null;
  datasReexposicao?: string[];
  situacaoSinais?: SituacaoSinaisOferta | null;
  repetiuOutroDia?: boolean;
  sintomasPele?: boolean;
  sintomasIntestinais?: boolean;
  sintomasRespiratorios?: boolean;
  alteracaoSono?: boolean;
  alteracaoComportamento?: boolean;
  observacao?: string | null;
}

export interface AnaliseAlimentacao {
  titulo: string;
  resumo: string;
  rotina: string[];
  conversaConsulta: string[];
  habitosApoio: string[];
}

export interface RegistroAlimentacao {
  id: string;
  criancaId: string;
  dataRegistro: string;
  tipoLeite: TipoLeiteAlimentacao;
  estagioAlimentar: EstagioAlimentar;
  idadeInicioAlimentacaoComplementarMeses?: number | null;
  refeicoesPorDia?: number | null;
  consomeAgua?: boolean | null;
  usaMamadeira?: boolean | null;
  usaCopo?: boolean | null;
  usaColher?: boolean | null;
  blwMisto?: boolean | null;
  autoalimentacao?: boolean | null;
  alimentadoExclusivamentePorCuidador?: boolean | null;
  texturaPredominante: TexturaAlimentar;
  consomeFrutas?: boolean | null;
  consomeLegumesVerduras?: boolean | null;
  consomeLegumes?: boolean | null;
  consomeVerduras?: boolean | null;
  consomeCereaisTuberculos?: boolean | null;
  consomeFeijoesLeguminosas?: boolean | null;
  consomeCarnesOvos?: boolean | null;
  ultraprocessadosFrequentes?: boolean | null;
  bebidasAdocadas?: boolean | null;
  acucarAdicionado?: boolean | null;
  salAdicionado?: boolean | null;
  telasDuranteRefeicoes?: boolean | null;
  refeicoesEmFamilia?: boolean | null;
  rotinaAlimentarRegular?: boolean | null;
  seletividadeAlimentar?: boolean | null;
  recusaPersistente?: boolean | null;
  engasgosFrequentes?: boolean | null;
  vomitosRecorrentes?: boolean | null;
  constipacao?: boolean | null;
  diarreiaRecorrente?: boolean | null;
  dificuldadeGanhoPesoPercebida?: boolean | null;
  familiaTranquilaGanhoPesoAtual?: boolean | null;
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
  tipoOrigemAlimento: TipoOrigemAlimento;
  origemPreparoAlimento: OrigemPreparoAlimento;
  alimentosOferecidos: AlimentoRegistroAlimentacao[];
  criadoEm: string;
  atualizadoEm?: string | null;
  analise: AnaliseAlimentacao;
}

export interface SalvarRegistroAlimentacaoRequest {
  dataRegistro: string;
  tipoLeite: TipoLeiteAlimentacao;
  estagioAlimentar: EstagioAlimentar;
  idadeInicioAlimentacaoComplementarMeses?: number | null;
  refeicoesPorDia?: number | null;
  consomeAgua?: boolean | null;
  usaMamadeira?: boolean | null;
  usaCopo?: boolean | null;
  usaColher?: boolean | null;
  blwMisto?: boolean | null;
  autoalimentacao?: boolean | null;
  alimentadoExclusivamentePorCuidador?: boolean | null;
  texturaPredominante: TexturaAlimentar;
  consomeFrutas?: boolean | null;
  consomeLegumesVerduras?: boolean | null;
  consomeLegumes?: boolean | null;
  consomeVerduras?: boolean | null;
  consomeCereaisTuberculos?: boolean | null;
  consomeFeijoesLeguminosas?: boolean | null;
  consomeCarnesOvos?: boolean | null;
  ultraprocessadosFrequentes?: boolean | null;
  bebidasAdocadas?: boolean | null;
  acucarAdicionado?: boolean | null;
  salAdicionado?: boolean | null;
  telasDuranteRefeicoes?: boolean | null;
  refeicoesEmFamilia?: boolean | null;
  rotinaAlimentarRegular?: boolean | null;
  seletividadeAlimentar?: boolean | null;
  recusaPersistente?: boolean | null;
  engasgosFrequentes?: boolean | null;
  vomitosRecorrentes?: boolean | null;
  constipacao?: boolean | null;
  diarreiaRecorrente?: boolean | null;
  dificuldadeGanhoPesoPercebida?: boolean | null;
  familiaTranquilaGanhoPesoAtual?: boolean | null;
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
  tipoOrigemAlimento?: TipoOrigemAlimento;
  origemPreparoAlimento?: OrigemPreparoAlimento;
  alimentosOferecidos?: AlimentoRegistroAlimentacao[];
}
