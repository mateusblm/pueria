export type OrigemMedidaCrescimento = 'CASA' | 'CONSULTA' | 'CONSULTORIO' | 'POSTO_SAUDE' | 'HOSPITAL' | 'OUTRO';
export type ResponsavelMedicaoCrescimento = 'FAMILIA' | 'PEDIATRA' | 'ENFERMAGEM' | 'OUTRO' | 'NAO_INFORMADO';

export interface MedidaCrescimento {
  id: string;
  criancaId: string;
  dataMedicao: string;
  pesoKg?: number | null;
  comprimentoCm?: number | null;
  perimetroCefalicoCm?: number | null;
  origem: OrigemMedidaCrescimento;
  responsavelMedicao: ResponsavelMedicaoCrescimento;
  observacao?: string | null;
  criadoEm: string;
  atualizadoEm?: string | null;
}

export interface SalvarMedidaCrescimentoRequest {
  dataMedicao: string;
  pesoKg?: number | null;
  comprimentoCm?: number | null;
  perimetroCefalicoCm?: number | null;
  origem: OrigemMedidaCrescimento;
  responsavelMedicao: ResponsavelMedicaoCrescimento;
  observacao?: string | null;
}

export type IndicadorCurvaCrescimento = 'PESO_IDADE' | 'COMPRIMENTO_IDADE' | 'PERIMETRO_CEFALICO_IDADE';
export type ClassificacaoCurvaCrescimento = 'MUITO_ABAIXO' | 'ABAIXO' | 'FAIXA_ESPERADA' | 'ACIMA' | 'MUITO_ACIMA';

export interface ResultadoCurvaCrescimento {
  indicador: IndicadorCurvaCrescimento;
  titulo: string;
  valor: number;
  unidade: string;
  zScore: number;
  percentil: number;
  classificacao: ClassificacaoCurvaCrescimento;
  classificacaoTitulo: string;
  classificacaoDetalhe: string;
  fonte: string;
}

export interface AvaliacaoCurvaCrescimento {
  medidaId: string;
  dataMedicao: string;
  idadeDias: number;
  idadeCronologicaDias: number;
  idadeCorrigida: boolean;
  criterioIdade: string;
  resultados: ResultadoCurvaCrescimento[];
}
