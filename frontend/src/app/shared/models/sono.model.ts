export type SuperficieSono = 'BERCO' | 'CAMA_PROPRIA' | 'CAMA_COMPARTILHADA' | 'OUTRA' | 'NAO_INFORMADA';
export type AmbienteSono = 'QUARTO_DOS_RESPONSAVEIS' | 'QUARTO_DA_PROPRIA_CRIANCA' | 'OUTRO' | 'NAO_INFORMADO';
export type TipoDespertarNoturno = 'ACORDA_E_MAMA' | 'ACORDA_SEM_SE_ALIMENTAR' | 'VOLTA_A_DORMIR_RAPIDO' | 'DEMORA_PARA_VOLTAR_A_DORMIR';
export type ClassificacaoDuracaoSono = 'SEM_DADOS' | 'ABAIXO_DA_FAIXA' | 'FAIXA_ESPERADA' | 'ACIMA_DA_FAIXA';

export interface AnaliseSono {
  titulo: string;
  resumo: string;
  minutosSonoTotal24h?: number | null;
  minutosSonoEsperadoMinimo: number;
  minutosSonoEsperadoMaximo: number;
  classificacaoDuracao: ClassificacaoDuracaoSono;
  rotina: string[];
  conversaConsulta: string[];
  habitosApoio: string[];
}

export interface RegistroSono {
  id: string;
  criancaId: string;
  dataRegistro: string;
  horarioDormiu?: string | null;
  horarioAcordou?: string | null;
  quantidadeCochilos?: number | null;
  minutosCochilos?: number | null;
  despertaresNoturnos?: number | null;
  dificuldadeIniciarSono?: boolean | null;
  rotinaSonoConsistente?: boolean | null;
  telasAntesDormir?: boolean | null;
  superficieSono: SuperficieSono;
  ambienteSono: AmbienteSono;
  tiposDespertarNoturno: TipoDespertarNoturno[];
  roncosFrequentes?: boolean | null;
  pausasRespiratoriasPercebidas?: boolean | null;
  sonoAgitado?: boolean | null;
  rangerDentesDuranteSono?: boolean | null;
  acordaBemDisposto?: boolean | null;
  sonolenciaDiurna?: boolean | null;
  irritabilidadeCansaco?: boolean | null;
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
  minutosSonoNoturno?: number | null;
  minutosSonoTotal24h?: number | null;
  criadoEm: string;
  atualizadoEm?: string | null;
  analise: AnaliseSono;
}

export interface SalvarRegistroSonoRequest {
  dataRegistro: string;
  horarioDormiu?: string | null;
  horarioAcordou?: string | null;
  quantidadeCochilos?: number | null;
  minutosCochilos?: number | null;
  despertaresNoturnos?: number | null;
  dificuldadeIniciarSono?: boolean | null;
  rotinaSonoConsistente?: boolean | null;
  telasAntesDormir?: boolean | null;
  superficieSono: SuperficieSono;
  ambienteSono: AmbienteSono;
  tiposDespertarNoturno: TipoDespertarNoturno[];
  roncosFrequentes?: boolean | null;
  pausasRespiratoriasPercebidas?: boolean | null;
  sonoAgitado?: boolean | null;
  rangerDentesDuranteSono?: boolean | null;
  acordaBemDisposto?: boolean | null;
  sonolenciaDiurna?: boolean | null;
  irritabilidadeCansaco?: boolean | null;
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
}
