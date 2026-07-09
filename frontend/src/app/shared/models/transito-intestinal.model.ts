export type TipoFezesBristol = 'TIPO_1' | 'TIPO_2' | 'TIPO_3' | 'TIPO_4' | 'TIPO_5' | 'TIPO_6' | 'TIPO_7' | 'NAO_INFORMADO';
export type FacilidadeLimpezaFezes = 'FACIL' | 'DIFICIL' | 'NAO_INFORMADO';
export type ClassificacaoFezes = 'ENDURECIDA' | 'ESPERADA' | 'MAIS_MACIA' | 'LIQUIDA' | 'SEM_DADOS';

export interface AnaliseTransitoIntestinal {
  titulo: string;
  resumo: string;
  classificacaoFezes: ClassificacaoFezes;
  rotina: string[];
  conversaConsulta: string[];
  habitosApoio: string[];
}

export interface RegistroTransitoIntestinal {
  id: string;
  criancaId: string;
  dataRegistro: string;
  tipoFezes: TipoFezesBristol;
  evacuacoesPorDia?: number | null;
  facilidadeLimpeza: FacilidadeLimpezaFezes;
  muco?: boolean | null;
  restosAlimentares?: boolean | null;
  raiasSangue?: boolean | null;
  constipacao?: boolean | null;
  diarreia?: boolean | null;
  dorEvacuar?: boolean | null;
  escapeFecal?: boolean | null;
  assaduraFrequente?: boolean | null;
  assaduraVermelhidao?: boolean | null;
  assaduraPontosVermelhos?: boolean | null;
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
  criadoEm: string;
  atualizadoEm?: string | null;
  analise: AnaliseTransitoIntestinal;
}

export interface SalvarRegistroTransitoIntestinalRequest {
  dataRegistro: string;
  tipoFezes: TipoFezesBristol;
  evacuacoesPorDia?: number | null;
  facilidadeLimpeza: FacilidadeLimpezaFezes;
  muco?: boolean | null;
  restosAlimentares?: boolean | null;
  raiasSangue?: boolean | null;
  constipacao?: boolean | null;
  diarreia?: boolean | null;
  dorEvacuar?: boolean | null;
  escapeFecal?: boolean | null;
  assaduraFrequente?: boolean | null;
  assaduraVermelhidao?: boolean | null;
  assaduraPontosVermelhos?: boolean | null;
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
}
