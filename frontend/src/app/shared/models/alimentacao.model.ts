export type TipoLeiteAlimentacao = 'LEITE_MATERNO' | 'FORMULA_INFANTIL' | 'MISTO' | 'NAO_CONSOME_LEITE' | 'NAO_INFORMADO';
export type EstagioAlimentar = 'APENAS_LEITE' | 'INICIANDO_ALIMENTACAO_COMPLEMENTAR' | 'ALIMENTACAO_COMPLEMENTAR_ESTABELECIDA' | 'COMIDA_DA_FAMILIA' | 'NAO_INFORMADO';
export type TexturaAlimentar = 'LIQUIDA' | 'PASTOSA' | 'AMASSADA' | 'PEDACOS_MACIOS' | 'COMIDA_DA_FAMILIA' | 'NAO_INFORMADO';

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
  autoalimentacao?: boolean | null;
  texturaPredominante: TexturaAlimentar;
  consomeFrutas?: boolean | null;
  consomeLegumesVerduras?: boolean | null;
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
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
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
  autoalimentacao?: boolean | null;
  texturaPredominante: TexturaAlimentar;
  consomeFrutas?: boolean | null;
  consomeLegumesVerduras?: boolean | null;
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
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
}
