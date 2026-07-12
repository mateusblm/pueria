export type AreaDesenvolvimento = 'SOCIAL_EMOCIONAL' | 'LINGUAGEM_COMUNICACAO' | 'COGNITIVO' | 'MOTOR';

export type StatusMarcoDesenvolvimento = 'OBSERVADO' | 'NAO_TENHO_CERTEZA' | 'AINDA_NAO_OBSERVADO' | 'NAO_LEMBRO' | 'NAO_AVALIADO';
export type TipoFonteMarcoDesenvolvimento = 'OMS' | 'CDC_CLASSICO' | 'CDC_2022';
export type PapelClinicoMarcoDesenvolvimento = 'ACOMPANHAMENTO' | 'ATENCAO_PERSISTENTE' | 'ALTA_RELEVANCIA';
export type ModalidadeRegistroMarcoDesenvolvimento = 'RETROSPECTIVO' | 'ACOMPANHAMENTO_ATUAL';
export type TipoRelatoDesenvolvimento = 'PERDA_HABILIDADE' | 'PREOCUPACAO_FAMILIA';

export interface RelatoDesenvolvimento {
  id: string;
  tipo: TipoRelatoDesenvolvimento;
  descricao: string;
  registradoEm: string;
}

export interface RegistrarRelatoDesenvolvimentoRequest {
  tipo: TipoRelatoDesenvolvimento;
  descricao: string;
}

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
  modalidade?: ModalidadeRegistroMarcoDesenvolvimento | null;
  observacao?: string | null;
  registradoEm?: string | null;
}

export interface RegistrarMarcoDesenvolvimentoRequest {
  status: StatusMarcoDesenvolvimento;
  observacao?: string | null;
}
