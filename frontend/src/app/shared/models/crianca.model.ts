export type Sexo = 'MASCULINO' | 'FEMININO';
export type Parentesco = 'MAE' | 'PAI' | 'RESPONSAVEL_LEGAL' | 'AVO' | 'OUTRO';

export interface Crianca {
  id: string;
  nome: string;
  dataNascimento: string;
  sexo: Sexo | null;
  prematura: boolean;
  semanasGestacionais: number | null;
  pesoNascimentoGramas: number | null;
  criadoEm: string;
}

export interface CriarCriancaRequest {
  nome: string;
  dataNascimento: string;
  sexo: Sexo | null;
  prematura: boolean;
  semanasGestacionais: number | null;
  pesoNascimentoGramas: number | null;
  parentesco: Parentesco;
  aceiteConsentimento: boolean;
  versaoTermoConsentimento: string;
}
