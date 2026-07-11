export type Sexo = 'MASCULINO' | 'FEMININO' | 'NAO_INFORMADO';

export type TipoParto = 'VAGINAL' | 'CESAREA' | 'VAGINAL_INSTRUMENTADO' | 'NAO_INFORMADO';

export type StatusTriagemNeonatal = 'REALIZADO' | 'PENDENTE' | 'NAO_INFORMADO';

export type AlimentacaoInicial = 'ALEITAMENTO_MATERNO_EXCLUSIVO' | 'ALEITAMENTO_MISTO' | 'FORMULA_INFANTIL' | 'NAO_INFORMADO';
export type TipoGestacao = 'UNICA' | 'MULTIPLA' | 'NAO_INFORMADO';
export type StatusCondicaoClinica = 'NAO' | 'SIM' | 'EM_INVESTIGACAO' | 'PREFIRO_INFORMAR_DEPOIS';

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
  preNatalRealizado: boolean;
  consultasPreNatal?: number | null;
  diabetesGestacional: boolean;
  hipertensaoGestacional: boolean;
  infeccaoGestacional: boolean;
  sangramentoGestacional: boolean;
  usoAlcoolGestacao: boolean;
  usoTabacoGestacao: boolean;
  outrasExposicoesGestacao: boolean;
  observacoesGestacao?: string | null;
  diasAltaHospitalar?: number | null;
  retornoHospitalarPrimeiraSemana: boolean;
  testePezinho: StatusTriagemNeonatal;
  testeOrelhinha: StatusTriagemNeonatal;
  testeOlhinho: StatusTriagemNeonatal;
  testeCoracaozinho: StatusTriagemNeonatal;
  amamentacaoPrimeiraHora: boolean;
  alimentacaoInicial: AlimentacaoInicial;
  tipoGestacao: TipoGestacao;
  statusT21: StatusCondicaoClinica;
  statusTurner: StatusCondicaoClinica;
  outraCondicaoRelevante: boolean;
  observacoesCondicaoRelevante?: string | null;
  criadoEm: string;
  atualizadoEm?: string | null;
}

interface DadosIniciaisRequest {
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
  preNatalRealizado: boolean;
  consultasPreNatal?: number | null;
  diabetesGestacional: boolean;
  hipertensaoGestacional: boolean;
  infeccaoGestacional: boolean;
  sangramentoGestacional: boolean;
  usoAlcoolGestacao: boolean;
  usoTabacoGestacao: boolean;
  outrasExposicoesGestacao: boolean;
  observacoesGestacao?: string | null;
  diasAltaHospitalar?: number | null;
  retornoHospitalarPrimeiraSemana: boolean;
  testePezinho: StatusTriagemNeonatal;
  testeOrelhinha: StatusTriagemNeonatal;
  testeOlhinho: StatusTriagemNeonatal;
  testeCoracaozinho: StatusTriagemNeonatal;
  amamentacaoPrimeiraHora: boolean;
  alimentacaoInicial: AlimentacaoInicial;
  tipoGestacao?: TipoGestacao;
  statusT21?: StatusCondicaoClinica;
  statusTurner?: StatusCondicaoClinica;
  outraCondicaoRelevante?: boolean;
  observacoesCondicaoRelevante?: string | null;
}

export interface CriarCriancaRequest extends DadosIniciaisRequest {
  parentesco: Parentesco;
  aceiteConsentimento: boolean;
  versaoTermoConsentimento: string;
}

export interface AtualizarCriancaRequest extends DadosIniciaisRequest {}
