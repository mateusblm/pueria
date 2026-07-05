export type OrigemMedidaCrescimento = 'CASA' | 'CONSULTA' | 'ESCOLA_CRECHE' | 'OUTRO';

export interface MedidaCrescimento {
  id: string;
  criancaId: string;
  dataMedicao: string;
  pesoKg?: number | null;
  comprimentoCm?: number | null;
  perimetroCefalicoCm?: number | null;
  origem: OrigemMedidaCrescimento;
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
  observacao?: string | null;
}
