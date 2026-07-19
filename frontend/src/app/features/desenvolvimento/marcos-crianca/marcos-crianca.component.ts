import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AreaDesenvolvimento, EstimuloDesenvolvimento, MarcoDesenvolvimento, ModalidadeRegistroMarcoDesenvolvimento, RelatoDesenvolvimento, StatusMarcoDesenvolvimento, TipoRelatoDesenvolvimento } from '../../../shared/models/desenvolvimento.model';
import { DesenvolvimentoService } from '../desenvolvimento.service';
import { AppIconComponent, AppIconName } from '../../../shared/components/app-icon/app-icon.component';
import { CriancasService } from '../../criancas/criancas.service';
import { Crianca } from '../../../shared/models/crianca.model';

type AreaResumo = {
  area: AreaDesenvolvimento;
  label: string;
  pendentes: number;
  total: number;
};

type ResultadoArea = AreaResumo & {
  observados: number;
  duvidas: number;
  aindaNao: number;
};

type HistoricoIdade = {
  idadeMeses: number;
  titulo: string;
  total: number;
  respondidos: number;
  pontosAtencao: number;
};

type TrajetoriaArea = {
  area: AreaDesenvolvimento;
  label: string;
  idades: string[];
  total: number;
};

type ModoTela = 'visao' | 'responder' | 'resultados' | 'experimentos';

@Component({
  selector: 'app-marcos-crianca',
  imports: [RouterLink, FormsModule, DatePipe, AppIconComponent],
  templateUrl: './marcos-crianca.component.html',
  styleUrl: './marcos-crianca.component.scss'
})
export class MarcosCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);

  rotaRetorno(): string[] {
    return this.route.snapshot.queryParamMap.get('origem') === 'perfil'
      ? ['/criancas', this.route.snapshot.paramMap.get('id') ?? '']
      : ['/acompanhamento'];
  }

  textoRetorno(): string {
    return this.route.snapshot.queryParamMap.get('origem') === 'perfil' ? 'Perfil' : 'Acompanhamento';
  }
  private readonly desenvolvimentoService = inject(DesenvolvimentoService);
  private readonly criancasService = inject(CriancasService);

  readonly criancaId = signal('');
  readonly crianca = signal<Crianca | null>(null);
  readonly marcos = signal<MarcoDesenvolvimento[]>([]);
  readonly relatos = signal<RelatoDesenvolvimento[]>([]);
  readonly estimulos = signal<EstimuloDesenvolvimento[]>([]);
  readonly historicoEstimulos = signal<EstimuloDesenvolvimento[]>([]);
  readonly estimuloParaMarco = signal<EstimuloDesenvolvimento | null>(null);
  readonly orientacaoPrioritariaMarco = signal<string | null>(null);
  readonly carregando = signal(true);
  readonly salvandoId = signal<string | null>(null);
  readonly erro = signal('');
  readonly idadeSelecionada = signal<number | null>(null);
  readonly indiceEtapa = signal(0);
  readonly modo = signal<ModoTela>('visao');
  readonly tipoRelato = signal<TipoRelatoDesenvolvimento>('PREOCUPACAO_FAMILIA');
  readonly descricaoRelato = signal('');
  readonly salvandoRelato = signal(false);
  readonly salvandoEstimuloId = signal<string | null>(null);
  readonly entendaAberto = signal(false);

  readonly areas: AreaDesenvolvimento[] = ['SOCIAL_EMOCIONAL', 'LINGUAGEM_COMUNICACAO', 'COGNITIVO', 'MOTOR'];

  readonly tituloIdadeSelecionada = computed(() => {
    const idade = this.idadeSelecionada();
    return idade === null ? '' : this.tituloIdade(idade);
  });

  readonly idadeAtualDisponivel = computed(() => this.historicoPorIdade().at(-1)?.idadeMeses ?? null);

  readonly textoIdadeReferencia = computed(() => {
    const crianca = this.crianca();
    const mesesCorrigidos = this.idadeCorrigidaEmMeses(crianca);
    if (mesesCorrigidos === null || mesesCorrigidos >= 24) {
      return '';
    }
    return `Idade corrigida nesta avaliação: ${this.tituloIdade(mesesCorrigidos)}.`;
  });

  readonly textoSemMarcos = computed(() => {
    const crianca = this.crianca();
    const mesesCorrigidos = this.idadeCorrigidaEmMeses(crianca);
    if (mesesCorrigidos !== null && mesesCorrigidos < 2) {
      return `Pela idade corrigida, ${crianca?.nome ?? 'a criança'} tem ${this.tituloIdade(mesesCorrigidos)}. A primeira etapa aparecerá aos 2 meses de idade corrigida.`;
    }
    return 'Ainda estamos preparando a primeira etapa de acompanhamento desta criança.';
  });

  readonly etapaRetrospectiva = computed(() => {
    const idadeSelecionada = this.idadeSelecionada();
    const idadeAtual = this.idadeAtualDisponivel();
    return idadeSelecionada !== null && idadeAtual !== null && idadeSelecionada < idadeAtual;
  });

  readonly marcosDaIdade = computed(() => {
    const idade = this.idadeSelecionada();
    return idade === null ? [] : this.marcos().filter((marco) => marco.idadeMeses === idade);
  });

  readonly marcoAtual = computed(() => this.marcosDaIdade()[this.indiceEtapa()] ?? null);

  readonly areasResumo = computed<AreaResumo[]>(() => this.areas.map((area) => {
    const marcos = this.marcosDaIdade().filter((marco) => marco.area === area);
    return {
      area,
      label: this.labelArea(area),
      pendentes: marcos.filter((marco) => marco.status === 'NAO_AVALIADO').length,
      total: marcos.length
    };
  }).filter((resumo) => resumo.total > 0));

  readonly progresso = computed(() => {
    const total = this.marcosDaIdade().length;
    const respondidos = this.marcosDaIdade().filter((marco) => marco.status !== 'NAO_AVALIADO').length;
    return { total, respondidos, percentual: total === 0 ? 0 : Math.round((respondidos / total) * 100) };
  });

  readonly pontosDeAtencao = computed(() => this.marcosDaIdade().filter((marco) =>
    marco.status === 'AINDA_NAO_OBSERVADO' || marco.status === 'NAO_TENHO_CERTEZA'
  ));

  readonly marcosComObservacao = computed(() => this.marcosDaIdade().filter((marco) => !!marco.observacao));

  readonly resultadoPorArea = computed<ResultadoArea[]>(() => this.areas.map((area) => {
    const marcos = this.marcosDaIdade().filter((marco) => marco.area === area);
    return {
      area,
      label: this.labelArea(area),
      total: marcos.length,
      pendentes: marcos.filter((marco) => marco.status === 'NAO_AVALIADO').length,
      observados: marcos.filter((marco) => marco.status === 'OBSERVADO').length,
      duvidas: marcos.filter((marco) => marco.status === 'NAO_TENHO_CERTEZA').length,
      aindaNao: marcos.filter((marco) => marco.status === 'AINDA_NAO_OBSERVADO').length
    };
  }).filter((resumo) => resumo.total > 0));

  readonly historicoPorIdade = computed<HistoricoIdade[]>(() => [...new Set(this.marcos().map((marco) => marco.idadeMeses))]
    .sort((a, b) => a - b)
    .map((idadeMeses) => {
      const marcos = this.marcos().filter((marco) => marco.idadeMeses === idadeMeses);
      return {
        idadeMeses,
        titulo: this.tituloIdade(idadeMeses),
        total: marcos.length,
        respondidos: marcos.filter((marco) => marco.status !== 'NAO_AVALIADO').length,
        pontosAtencao: marcos.filter((marco) => marco.status === 'AINDA_NAO_OBSERVADO' || marco.status === 'NAO_TENHO_CERTEZA').length
      };
    }));

  readonly trajetoriaPorArea = computed<TrajetoriaArea[]>(() => this.areas.map((area) => {
    const pontos = this.marcos().filter((marco) => marco.area === area &&
      (marco.status === 'AINDA_NAO_OBSERVADO' || marco.status === 'NAO_TENHO_CERTEZA'));
    const idades = [...new Set(pontos.map((marco) => marco.idadeMeses))];
    return {
      area,
      label: this.labelArea(area),
      idades: idades.map((idade) => this.tituloIdade(idade)),
      total: pontos.length
    };
  }).filter((trajetoria) => trajetoria.idades.length >= 2));

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.erro.set('Criança não encontrada.');
      this.carregando.set(false);
      return;
    }

    this.criancaId.set(id);
    this.carregarMarcos();
  }

  carregarMarcos(): void {
    this.carregando.set(true);
    this.erro.set('');

    this.desenvolvimentoService.listarMarcos(this.criancaId())
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (marcos) => {
          const ordenados = [...marcos].sort((a, b) => a.idadeMeses - b.idadeMeses || this.areas.indexOf(a.area) - this.areas.indexOf(b.area));
          const idadeAtual = ordenados.at(-1)?.idadeMeses ?? null;

          this.marcos.set(ordenados);
          this.idadeSelecionada.set(idadeAtual);
          if (this.route.snapshot.queryParamMap.get('modo') === 'resultados') {
            this.modo.set('resultados');
          } else if (this.route.snapshot.queryParamMap.get('modo') === 'experimentos') {
            this.modo.set('experimentos');
          } else if (this.route.snapshot.queryParamMap.get('modo') === 'responder') {
            this.modo.set('responder');
            this.posicionarPrimeiraPendente();
            this.carregarEstimuloParaMarcoAtual();
          } else {
            this.modo.set('visao');
          }
          this.carregarExperimentosDaFaixa();
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });

    this.desenvolvimentoService.listarRelatos(this.criancaId()).subscribe({
      next: (relatos) => this.relatos.set(relatos),
      error: () => this.erro.set('Não foi possível carregar os relatos de desenvolvimento agora.')
    });

    this.desenvolvimentoService.listarHistoricoEstimulos(this.criancaId()).subscribe({ next: (itens) => this.historicoEstimulos.set(itens) });

    this.criancasService.buscarPorId(this.criancaId()).subscribe({
      next: (crianca) => this.crianca.set(crianca)
    });
  }

  registrar(marco: MarcoDesenvolvimento, status: StatusMarcoDesenvolvimento): void {
    this.salvandoId.set(marco.id);
    this.erro.set('');

    this.desenvolvimentoService.registrarMarco(this.criancaId(), marco.id, {
      status,
      observacao: marco.observacao ?? null
    })
      .pipe(finalize(() => this.salvandoId.set(null)))
      .subscribe({
        next: () => {
          const modalidade: ModalidadeRegistroMarcoDesenvolvimento = this.etapaRetrospectiva()
            ? 'RETROSPECTIVO'
            : 'ACOMPANHAMENTO_ATUAL';
          this.marcos.update((marcos) => marcos.map((item) => item.id === marco.id ? { ...item, status, modalidade } : item));
          this.carregarEstimuloParaMarco({ ...marco, status, modalidade });
          this.carregarExperimentosDaFaixa();
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  salvarObservacao(marco: MarcoDesenvolvimento, observacao: string): void {
    const texto = observacao.trim();
    const valor = texto.length > 0 ? texto : null;

    if ((marco.observacao ?? null) === valor) {
      return;
    }

    this.salvandoId.set(marco.id);
    this.erro.set('');

    this.desenvolvimentoService.registrarMarco(this.criancaId(), marco.id, {
      status: marco.status,
      observacao: valor
    })
      .pipe(finalize(() => this.salvandoId.set(null)))
      .subscribe({
        next: () => this.marcos.update((marcos) => marcos.map((item) => item.id === marco.id ? { ...item, observacao: valor } : item)),
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  abrirResponder(): void {
    this.modo.set('responder');
    if (this.progresso().respondidos < this.progresso().total) {
      this.posicionarPrimeiraPendente();
    }
  }

  abrirVisao(): void {
    this.modo.set('visao');
  }

  abrirEntenda(): void {
    this.entendaAberto.set(true);
  }

  fecharEntenda(): void {
    this.entendaAberto.set(false);
  }

  abrirResultados(): void {
    this.modo.set('resultados');
  }

  abrirExperimentos(): void {
    this.modo.set('experimentos');
  }

  selecionarIdade(idadeMeses: number): void {
    const modoAtual = this.modo();
    this.idadeSelecionada.set(idadeMeses);
    this.indiceEtapa.set(0);
    if (modoAtual === 'responder') {
      this.posicionarPrimeiraPendente();
    }
    this.carregarEstimuloParaMarcoAtual();
    this.carregarExperimentosDaFaixa();
  }

  etapaAnterior(): void {
    this.indiceEtapa.update((indice) => Math.max(0, indice - 1));
    this.carregarEstimuloParaMarcoAtual();
  }

  proximaEtapa(): void {
    this.indiceEtapa.update((indice) => Math.min(this.marcosDaIdade().length - 1, indice + 1));
    this.carregarEstimuloParaMarcoAtual();
  }

  seguirAposSugestao(): void {
    this.avancarDepoisDeResponder();
    this.carregarEstimuloParaMarcoAtual();
  }

  imprimirResumo(): void {
    this.desenvolvimentoService.gerarResumoConsulta(this.criancaId()).subscribe({ next: (pdf) => window.open(URL.createObjectURL(pdf), '_blank'), error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro)) });
  }

  registrarRelato(): void {
    const descricao = this.descricaoRelato().trim();
    if (!descricao) {
      this.erro.set('Conte um pouco do que você percebeu antes de registrar.');
      return;
    }
    this.salvandoRelato.set(true);
    this.erro.set('');
    this.desenvolvimentoService.registrarRelato(this.criancaId(), { tipo: this.tipoRelato(), descricao })
      .pipe(finalize(() => this.salvandoRelato.set(false)))
      .subscribe({
        next: (relato) => {
          this.relatos.update((itens) => [relato, ...itens]);
          this.descricaoRelato.set('');
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  removerRelato(relato: RelatoDesenvolvimento): void {
    this.salvandoRelato.set(true);
    this.desenvolvimentoService.removerRelato(this.criancaId(), relato.id)
      .pipe(finalize(() => this.salvandoRelato.set(false)))
      .subscribe({
        next: () => this.relatos.update((itens) => itens.filter((item) => item.id !== relato.id)),
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  registrarEstimulo(estimulo: EstimuloDesenvolvimento): void {
    this.salvandoEstimuloId.set(estimulo.id);
    this.desenvolvimentoService.registrarEstimulo(this.criancaId(), estimulo.id, estimulo.observacao)
      .pipe(finalize(() => this.salvandoEstimuloId.set(null)))
      .subscribe({
        next: () => {
          const atualizado = { ...estimulo, experimentado: true, experimentadoEm: estimulo.experimentadoEm ?? new Date().toISOString() };
          this.estimulos.update((itens) => itens.map((item) => item.id === estimulo.id ? atualizado : item));
          this.estimuloParaMarco.update((item) => item?.id === estimulo.id ? atualizado : item);
          this.historicoEstimulos.update((itens) => [atualizado, ...itens.filter((item) => item.id !== estimulo.id)]);
          this.carregarExperimentosDaFaixa();
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  salvarObservacaoEstimulo(estimulo: EstimuloDesenvolvimento, observacao: string): void {
    const valor = observacao.trim() || null;
    if (!estimulo.experimentado || valor === (estimulo.observacao ?? null)) return;
    this.salvandoEstimuloId.set(estimulo.id);
    this.desenvolvimentoService.registrarEstimulo(this.criancaId(), estimulo.id, valor)
      .pipe(finalize(() => this.salvandoEstimuloId.set(null)))
      .subscribe({
        next: () => {
          this.estimulos.update((itens) => itens.map((item) => item.id === estimulo.id ? { ...item, observacao: valor } : item));
          this.historicoEstimulos.update((itens) => itens.map((item) => item.id === estimulo.id ? { ...item, observacao: valor } : item));
        }, error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  labelTipoRelato(tipo: TipoRelatoDesenvolvimento): string {
    return tipo === 'PERDA_HABILIDADE' ? 'Perda de habilidade' : 'Preocupação da família';
  }

  textoConviteEstimulo(status: StatusMarcoDesenvolvimento): string {
    if (status === 'OBSERVADO') {
      return '';
    }
    if (status === 'NAO_TENHO_CERTEZA') {
      return 'Uma ideia simples para observar essa habilidade em um momento tranquilo. Você pode retomar a observação nas próximas 4 a 6 semanas, sem testar nem cobrar uma resposta.';
    }
    return 'Uma ideia leve para experimentar no ritmo da criança. Vale observar novamente nas próximas 4 a 6 semanas e levar essa observação à próxima consulta de rotina, especialmente se continuar.';
  }

  textoOrientacaoMarco(marco: MarcoDesenvolvimento): string {
    if (marco.papelClinico === 'ALTA_RELEVANCIA') {
      return 'Este marco tem relevância especial para a vigilância do desenvolvimento. Quando houver dúvida persistente, leve exemplos da rotina para conversar com o pediatra.';
    }
    if (marco.papelClinico === 'ATENCAO_PERSISTENTE') {
      return 'Acompanhe este marco nas atividades do dia a dia e leve suas observações à consulta se a dúvida continuar.';
    }
    return 'Esta pergunta ajuda a organizar as observações da rotina. Uma resposta isolada não confirma nem exclui uma condição.';
  }

  textoFonteMarco(marco: MarcoDesenvolvimento): string {
    const fonte = marco.tipoFonte === 'OMS' ? 'Organização Mundial da Saúde' : 'Centers for Disease Control and Prevention';
    return `${fonte} · ${marco.versaoCatalogo}`;
  }

  labelArea(area: string): string {
    const labels: Record<string, string> = {
      SOCIAL_EMOCIONAL: 'Social',
      LINGUAGEM_COMUNICACAO: 'Linguagem',
      COGNITIVO: 'Cognição',
      MOTOR: 'Movimento'
    };
    return labels[area] ?? area;
  }

  iconeArea(area: AreaDesenvolvimento): AppIconName {
    const icones: Record<AreaDesenvolvimento, AppIconName> = {
      SOCIAL_EMOCIONAL: 'heart',
      LINGUAGEM_COMUNICACAO: 'message',
      COGNITIVO: 'brain',
      MOTOR: 'footprints'
    };
    return icones[area];
  }

  temaArea(area: AreaDesenvolvimento): string {
    const temas: Record<AreaDesenvolvimento, string> = {
      SOCIAL_EMOCIONAL: 'social',
      LINGUAGEM_COMUNICACAO: 'linguagem',
      COGNITIVO: 'cognicao',
      MOTOR: 'movimento'
    };
    return temas[area];
  }

  labelStatus(status: StatusMarcoDesenvolvimento): string {
    const labels: Record<StatusMarcoDesenvolvimento, string> = {
      OBSERVADO: 'Sim, sempre',
      NAO_TENHO_CERTEZA: 'Às vezes',
      AINDA_NAO_OBSERVADO: 'Ainda não',
      NAO_LEMBRO: 'Não lembro',
      NAO_AVALIADO: 'Não respondido'
    };
    return labels[status];
  }

  labelPendencias(pendentes: number): string {
    if (pendentes === 0) {
      return 'Completo';
    }
    return pendentes === 1 ? '1 pendente' : `${pendentes} pendentes`;
  }

  labelPontosAtencao(total: number): string {
    if (total === 0) {
      return 'Sem pontos para conversar';
    }
    return total === 1 ? '1 ponto para conversar' : `${total} pontos para conversar`;
  }

  dataResumo(): string {
    return new Intl.DateTimeFormat('pt-BR').format(new Date());
  }

  textoConduta(): string {
    if (this.relatos().some((relato) => relato.tipo === 'PERDA_HABILIDADE')) {
      return 'Uma perda de habilidade foi registrada. Vale conversar com o pediatra o quanto antes e levar este relato para contextualizar a consulta.';
    }
    if (this.relatos().some((relato) => relato.tipo === 'PREOCUPACAO_FAMILIA')) {
      return 'Há uma preocupação registrada pela família. Leve esse contexto para conversar com o pediatra na próxima oportunidade.';
    }
    if (this.pontosDeAtencao().length === 0) {
      return 'Manter acompanhamento de rotina, brincadeiras responsivas e novas observações conforme a criança cresce.';
    }
    return 'Levar as respostas marcadas como dúvida ou ainda não observadas para a próxima consulta. Se houver perda de habilidades, preocupação persistente ou atraso claro, antecipar contato com o pediatra.';
  }

  classeStatus(status: StatusMarcoDesenvolvimento): string {
    return `marco-status marco-status--${status.toLowerCase().replaceAll('_', '-')}`;
  }

  imagemMarco(marco: MarcoDesenvolvimento, variante: 'desktop' | 'mobile'): string {
    const primeiroNumeroPorIdade: Partial<Record<number, number>> = {
      2: 1,
      4: 9,
      6: 17,
      9: 24,
      12: 32,
      15: 41,
      18: 49,
      24: 57
    };
    const primeiroNumero = primeiroNumeroPorIdade[marco.idadeMeses];
    const sufixoDoId = Number(marco.id.slice(-4));
    const posicaoNaIdade = sufixoDoId - marco.idadeMeses * 100;
    const numeroDaIlustracao = primeiroNumero === undefined ? NaN : primeiroNumero + posicaoNaIdade - 1;

    if (Number.isInteger(numeroDaIlustracao) && numeroDaIlustracao >= 1 && numeroDaIlustracao <= 65) {
      return `/assets/desenvolvimento/marcos/${variante}/${String(numeroDaIlustracao).padStart(2, '0')}.png`;
    }

    return this.imagemArea(marco.area);
  }

  private imagemArea(area: AreaDesenvolvimento): string {
    const imagens: Record<AreaDesenvolvimento, string> = {
      SOCIAL_EMOCIONAL: '/assets/desenvolvimento/social.svg',
      LINGUAGEM_COMUNICACAO: '/assets/desenvolvimento/linguagem.svg',
      COGNITIVO: '/assets/desenvolvimento/cognicao.svg',
      MOTOR: '/assets/desenvolvimento/motor.svg'
    };
    return imagens[area];
  }

  tituloIdade(idadeMeses: number): string {
    if (idadeMeses < 12) {
      return `${idadeMeses} meses`;
    }
    const anos = Math.floor(idadeMeses / 12);
    const meses = idadeMeses % 12;
    if (meses === 0) {
      return `${anos} ${anos === 1 ? 'ano' : 'anos'}`;
    }
    return `${anos} ${anos === 1 ? 'ano' : 'anos'} e ${meses} meses`;
  }

  private adicionarDias(dataIso: string, dias: number): Date {
    const [ano, mes, dia] = dataIso.split('-').map(Number);
    const data = new Date(ano, mes - 1, dia);
    data.setDate(data.getDate() + dias);
    return data;
  }

  private mesesCompletos(inicio: Date, fim: Date): number {
    if (fim < inicio) {
      return 0;
    }
    let meses = (fim.getFullYear() - inicio.getFullYear()) * 12 + fim.getMonth() - inicio.getMonth();
    if (fim.getDate() < inicio.getDate()) {
      meses--;
    }
    return Math.max(0, meses);
  }

  private idadeCorrigidaEmMeses(crianca: Crianca | null): number | null {
    if (!crianca?.prematura || !crianca.semanasGestacionais) {
      return null;
    }
    const diasParaTermo = Math.max(0, 40 * 7 - (crianca.semanasGestacionais * 7 + (crianca.diasGestacionais ?? 0)));
    return this.mesesCompletos(this.adicionarDias(crianca.dataNascimento, diasParaTermo), new Date());
  }

  private posicionarPrimeiraPendente(): void {
    const primeiraPendente = this.marcosDaIdade().findIndex((marco) => marco.status === 'NAO_AVALIADO');
    this.indiceEtapa.set(primeiraPendente >= 0 ? primeiraPendente : 0);
  }

  private carregarEstimuloParaMarcoAtual(): void {
    const marco = this.marcoAtual();
    if (!marco) {
      this.estimuloParaMarco.set(null);
      this.orientacaoPrioritariaMarco.set(null);
      return;
    }
    this.carregarEstimuloParaMarco(marco);
  }

  private carregarExperimentosDaFaixa(): void {
    const idadeMeses = this.idadeSelecionada();
    if (idadeMeses === null) {
      this.estimulos.set([]);
      return;
    }
    this.desenvolvimentoService.listarRecomendacoes(this.criancaId(), idadeMeses).subscribe({
      next: (estimulos) => this.estimulos.set(estimulos),
      error: () => this.estimulos.set([])
    });
  }

  private carregarEstimuloParaMarco(marco: MarcoDesenvolvimento): void {
    this.orientacaoPrioritariaMarco.set(null);
    if (marco.status === 'NAO_AVALIADO' || marco.status === 'NAO_LEMBRO') {
      this.estimuloParaMarco.set(null);
      return;
    }

    const orientacaoPrioritaria = this.orientacaoPrioritariaPara(marco);
    if (orientacaoPrioritaria) {
      this.estimuloParaMarco.set(null);
      this.orientacaoPrioritariaMarco.set(orientacaoPrioritaria);
      return;
    }

    this.estimuloParaMarco.set(null);
    this.desenvolvimentoService.buscarEstimuloParaMarco(this.criancaId(), marco.id).subscribe({
      next: (estimulo) => {
        if (this.marcoAtual()?.id === marco.id) {
          this.estimuloParaMarco.set(estimulo);
        }
      },
      error: () => this.estimuloParaMarco.set(null)
    });
  }

  private orientacaoPrioritariaPara(marco: MarcoDesenvolvimento): string | null {
    if (marco.status !== 'AINDA_NAO_OBSERVADO') {
      return null;
    }

    if (marco.id === '10000000-0000-0000-0000-000000000204') {
      return 'Como a reação a sons altos inesperados ainda não foi observada, vale conversar com o pediatra sobre a audição da criança. Este registro pode ajudar na consulta.';
    }

    if (marco.id === '10000000-0000-0000-0000-000000000208') {
      return 'Se o movimento de braços e pernas dos dois lados ainda não foi observado, ou se você percebe diferença entre os lados, vale conversar com o pediatra. Registre o que percebeu para levar à consulta.';
    }

    return null;
  }

  private avancarDepoisDeResponder(): void {
    const proximaPendente = this.marcosDaIdade().findIndex((marco, indice) =>
      indice > this.indiceEtapa() && marco.status === 'NAO_AVALIADO'
    );

    if (proximaPendente >= 0) {
      this.indiceEtapa.set(proximaPendente);
      return;
    }

    if (this.indiceEtapa() < this.marcosDaIdade().length - 1) {
      this.proximaEtapa();
      return;
    }

    if (this.progresso().respondidos === this.progresso().total) {
      const proximaIdade = this.historicoPorIdade().find((grupo) => grupo.idadeMeses > (this.idadeSelecionada() ?? 0));
      if (proximaIdade) {
        this.selecionarIdade(proximaIdade.idadeMeses);
        return;
      }
      this.modo.set('resultados');
      return;
    }
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;
    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens[0];
    }
    return 'Não foi possível carregar os marcos do desenvolvimento agora.';
  }
}
