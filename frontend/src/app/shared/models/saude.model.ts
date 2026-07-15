export type TipoRegistroSaude = 'MEDICAMENTO_SUPLEMENTO' | 'INTERCORRENCIA_CLINICA';

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
