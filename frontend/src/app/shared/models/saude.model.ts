export type TipoRegistroSaude = 'MEDICAMENTO_SUPLEMENTO' | 'INTERCORRENCIA_CLINICA' | 'HUMOR_COMPORTAMENTO' | 'OBSERVACAO_EVENTO_MARCANTE';

export interface RegistroSaude {
  id: string;
  tipo: TipoRegistroSaude;
  dataRegistro: string;
  descricao: string;
  criadoEm: string;
  atualizadoEm?: string | null;
}

export interface SalvarRegistroSaudeRequest {
  tipo: TipoRegistroSaude;
  dataRegistro: string;
  descricao: string;
}
