export type LocalSono = 'BERCO' | 'CAMA_PROPRIA' | 'CAMA_COMPARTILHADA' | 'QUARTO_DOS_RESPONSAVEIS' | 'QUARTO_DA_PROPRIA_CRIANCA' | 'OUTRO' | 'NAO_INFORMADO';
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
  localSono: LocalSono;
  roncosFrequentes?: boolean | null;
  pausasRespiratoriasPercebidas?: boolean | null;
  sonoAgitado?: boolean | null;
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
  localSono: LocalSono;
  roncosFrequentes?: boolean | null;
  pausasRespiratoriasPercebidas?: boolean | null;
  sonoAgitado?: boolean | null;
  sonolenciaDiurna?: boolean | null;
  irritabilidadeCansaco?: boolean | null;
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
}
