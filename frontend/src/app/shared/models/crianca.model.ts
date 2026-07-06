export type Sexo = 'MASCULINO' | 'FEMININO' | 'NAO_INFORMADO';

export type TipoParto = 'VAGINAL' | 'CESAREA' | 'VAGINAL_INSTRUMENTADO' | 'NAO_INFORMADO';

export type Parentesco = 'MAE' | 'PAI' | 'RESPONSAVEL_LEGAL' | 'AVO' | 'OUTRO';

export interface Crianca {
  id: string;
  nome: string;
  dataNascimento: string;
  sexo: Sexo | null;
  prematura: boolean;
  semanasGestacionais: number;
  diasGestacionais: number;
  tipoParto: TipoParto;
  pesoNascimentoGramas: number;
  comprimentoNascimentoCm: number;
  perimetroCefalicoNascimentoCm: number;
  apgarUmMinuto?: number | null;
  apgarCincoMinutos?: number | null;
  utiNeonatal: boolean;
  reanimacaoNeonatal: boolean;
  ictericiaNeonatal: boolean;
  dificuldadeRespiratoria: boolean;
  dificuldadeAmamentacao: boolean;
  observacoesNascimento?: string | null;
  criadoEm: string;
  atualizadoEm?: string | null;
}

export interface CriarCriancaRequest {
  nome: string;
  dataNascimento: string;
  sexo: Sexo | null;
  prematura: boolean;
  semanasGestacionais: number;
  diasGestacionais: number;
  tipoParto: TipoParto;
  pesoNascimentoGramas: number;
  comprimentoNascimentoCm: number;
  perimetroCefalicoNascimentoCm: number;
  apgarUmMinuto?: number | null;
  apgarCincoMinutos?: number | null;
  utiNeonatal: boolean;
  reanimacaoNeonatal: boolean;
  ictericiaNeonatal: boolean;
  dificuldadeRespiratoria: boolean;
  dificuldadeAmamentacao: boolean;
  observacoesNascimento?: string | null;
  parentesco: Parentesco;
  aceiteConsentimento: boolean;
  versaoTermoConsentimento: string;
}

export interface AtualizarCriancaRequest {
  nome: string;
  dataNascimento: string;
  sexo: Sexo | null;
  prematura: boolean;
  semanasGestacionais: number;
  diasGestacionais: number;
  tipoParto: TipoParto;
  pesoNascimentoGramas: number;
  comprimentoNascimentoCm: number;
  perimetroCefalicoNascimentoCm: number;
  apgarUmMinuto?: number | null;
  apgarCincoMinutos?: number | null;
  utiNeonatal: boolean;
  reanimacaoNeonatal: boolean;
  ictericiaNeonatal: boolean;
  dificuldadeRespiratoria: boolean;
  dificuldadeAmamentacao: boolean;
  observacoesNascimento?: string | null;
}
