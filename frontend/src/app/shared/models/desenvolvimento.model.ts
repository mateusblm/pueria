export type AreaDesenvolvimento = 'SOCIAL_EMOCIONAL' | 'LINGUAGEM_COMUNICACAO' | 'COGNITIVO' | 'MOTOR';

export type StatusMarcoDesenvolvimento = 'OBSERVADO' | 'NAO_TENHO_CERTEZA' | 'AINDA_NAO_OBSERVADO' | 'NAO_AVALIADO';
export type TipoFonteMarcoDesenvolvimento = 'OMS' | 'CDC_CLASSICO' | 'CDC_2022';
export type PapelClinicoMarcoDesenvolvimento = 'ACOMPANHAMENTO' | 'ATENCAO_PERSISTENTE' | 'ALTA_RELEVANCIA';

export interface MarcoDesenvolvimento {
  id: string;
  idadeMeses: number;
  area: AreaDesenvolvimento;
  descricao: string;
  fonte: string;
  tipoFonte: TipoFonteMarcoDesenvolvimento;
  versaoCatalogo: string;
  papelClinico: PapelClinicoMarcoDesenvolvimento;
  altaRelevanciaVigilancia: boolean;
  status: StatusMarcoDesenvolvimento;
  observacao?: string | null;
  registradoEm?: string | null;
}

export interface RegistrarMarcoDesenvolvimentoRequest {
  status: StatusMarcoDesenvolvimento;
  observacao?: string | null;
}
