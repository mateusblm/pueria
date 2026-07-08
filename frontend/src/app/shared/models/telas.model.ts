export type TipoConteudoTela =
  | 'VIDEO_PASSIVO'
  | 'VIDEOCHAMADA'
  | 'EDUCATIVO_INTERATIVO'
  | 'JOGOS'
  | 'MUSICA_AUDIOVISUAL'
  | 'OUTRO'
  | 'NAO_INFORMADO';

export type ClassificacaoTempoTela = 'SEM_DADOS' | 'DENTRO_DA_REFERENCIA' | 'ACIMA_DA_REFERENCIA';

export interface AnaliseTelas {
  titulo: string;
  resumo: string;
  minutosMediosDia?: number | null;
  minutosReferenciaMaximo: number;
  classificacaoTempo: ClassificacaoTempoTela;
  rotina: string[];
  conversaConsulta: string[];
  habitosApoio: string[];
}

export interface RegistroTelas {
  id: string;
  criancaId: string;
  dataRegistro: string;
  minutosDiaSemana?: number | null;
  minutosFimSemana?: number | null;
  tipoConteudoPredominante: TipoConteudoTela;
  telaAoAcordar?: boolean | null;
  telaDuranteRefeicoes?: boolean | null;
  telaAntesDormir?: boolean | null;
  telaParaAcalmar?: boolean | null;
  telaEmSegundoPlano?: boolean | null;
  usoAcompanhadoAdulto?: boolean | null;
  conteudoAdultoSupervisionado?: boolean | null;
  videochamadaFamilia?: boolean | null;
  autoplayAtivo?: boolean | null;
  notificacoesAtivas?: boolean | null;
  dispositivoNoQuarto?: boolean | null;
  brincaAoArLivre?: boolean | null;
  leituraBrincadeiraSemTela?: boolean | null;
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
  minutosMediosDia?: number | null;
  criadoEm: string;
  atualizadoEm?: string | null;
  analise: AnaliseTelas;
}

export interface SalvarRegistroTelasRequest {
  dataRegistro: string;
  minutosDiaSemana?: number | null;
  minutosFimSemana?: number | null;
  tipoConteudoPredominante: TipoConteudoTela;
  telaAoAcordar?: boolean | null;
  telaDuranteRefeicoes?: boolean | null;
  telaAntesDormir?: boolean | null;
  telaParaAcalmar?: boolean | null;
  telaEmSegundoPlano?: boolean | null;
  usoAcompanhadoAdulto?: boolean | null;
  conteudoAdultoSupervisionado?: boolean | null;
  videochamadaFamilia?: boolean | null;
  autoplayAtivo?: boolean | null;
  notificacoesAtivas?: boolean | null;
  dispositivoNoQuarto?: boolean | null;
  brincaAoArLivre?: boolean | null;
  leituraBrincadeiraSemTela?: boolean | null;
  preocupacaoFamilia?: boolean | null;
  observacao?: string | null;
}
