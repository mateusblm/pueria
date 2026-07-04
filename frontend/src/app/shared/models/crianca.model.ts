export type Sexo = 'MASCULINO' | 'FEMININO' | 'NAO_INFORMADO';

export type Parentesco = 'MAE' | 'PAI' | 'RESPONSAVEL_LEGAL' | 'AVO' | 'OUTRO';

export interface Crianca {
  id: string;
  nome: string;
  dataNascimento: string;
  sexo: Sexo | null;
  prematura: boolean;
  semanasGestacionais: number;
  pesoNascimentoGramas: number;
  criadoEm: string;
}

export interface CriarCriancaRequest {
  nome: string;
  dataNascimento: string;
  sexo: Sexo | null;
  prematura: boolean;
  semanasGestacionais: number;
  pesoNascimentoGramas: number;
  parentesco: Parentesco;
  aceiteConsentimento: boolean;
  versaoTermoConsentimento: string;
}
