export type AreaDesenvolvimento = 'SOCIAL_EMOCIONAL' | 'LINGUAGEM_COMUNICACAO' | 'COGNITIVO' | 'MOTOR';

export type StatusMarcoDesenvolvimento = 'OBSERVADO' | 'AINDA_NAO_OBSERVADO' | 'NAO_AVALIADO';

export interface MarcoDesenvolvimento {
  id: string;
  idadeMeses: number;
  area: AreaDesenvolvimento;
  descricao: string;
  fonte: string;
  status: StatusMarcoDesenvolvimento;
  observacao?: string | null;
  registradoEm?: string | null;
}

export interface RegistrarMarcoDesenvolvimentoRequest {
  status: StatusMarcoDesenvolvimento;
  observacao?: string | null;
}
