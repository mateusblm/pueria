import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, finalize, forkJoin, of } from 'rxjs';
import { RegistroAlimentacao } from '../../../shared/models/alimentacao.model';
import { AvaliacaoCurvaCrescimento } from '../../../shared/models/crescimento.model';
import { Crianca } from '../../../shared/models/crianca.model';
import { MarcoDesenvolvimento } from '../../../shared/models/desenvolvimento.model';
import { RegistroSono } from '../../../shared/models/sono.model';
import { RegistroTelas } from '../../../shared/models/telas.model';
import { RegistroTransitoIntestinal } from '../../../shared/models/transito-intestinal.model';
import { AlimentacaoService } from '../../alimentacao/alimentacao.service';
import { CrescimentoService } from '../../crescimento/crescimento.service';
import { DesenvolvimentoService } from '../../desenvolvimento/desenvolvimento.service';
import { SonoService } from '../../sono/sono.service';
import { TelasService } from '../../telas/telas.service';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';
import { TransitoIntestinalService } from '../../transito-intestinal/transito-intestinal.service';
import { CriancasService } from '../criancas.service';

type EstadoModulo = 'ok' | 'atencao' | 'pendente' | 'indisponivel';

type ModuloResumo = {
  titulo: string;
  subtitulo: string;
  estado: EstadoModulo;
  valor: string;
  detalhe: string;
  acao: string;
  rota: string[];
};

type PainelAcao = {
  titulo: string;
  texto: string;
};

type PainelAcompanhamento = {
  frase: string;
  prioridades: string[];
  acompanharCasa: PainelAcao[];
  completarApp: PainelAcao[];
  conversarConsulta: PainelAcao[];
  pontosFortes: PainelAcao[];
  periodo: string;
  modulos: ModuloResumo[];
};

@Component({
  selector: 'app-detalhe-crianca',
  imports: [RouterLink, AppIconComponent],
  templateUrl: './detalhe-crianca.component.html',
  styleUrl: './detalhe-crianca.component.scss'
})
export class DetalheCriancaComponent implements OnInit {
  readonly crianca = signal<Crianca | null>(null);
  readonly marcos = signal<MarcoDesenvolvimento[]>([]);
  readonly curvasCrescimento = signal<AvaliacaoCurvaCrescimento[]>([]);
  readonly registrosAlimentacao = signal<RegistroAlimentacao[]>([]);
  readonly registrosTransitoIntestinal = signal<RegistroTransitoIntestinal[]>([]);
  readonly registrosSono = signal<RegistroSono[]>([]);
  readonly registrosTelas = signal<RegistroTelas[]>([]);
  readonly errosModulos = signal<Record<string, string>>({});
  readonly carregando = signal(true);
  readonly removendo = signal(false);
  readonly confirmandoRemocao = signal(false);
  readonly erro = signal('');

  readonly ultimoRegistroAlimentacao = computed(() => this.maisRecente(this.registrosAlimentacao(), (item) => item.dataRegistro));
  readonly ultimoRegistroTransitoIntestinal = computed(() => this.maisRecente(this.registrosTransitoIntestinal(), (item) => item.dataRegistro));
  readonly ultimoRegistroSono = computed(() => this.maisRecente(this.registrosSono(), (item) => item.dataRegistro));
  readonly ultimoRegistroTelas = computed(() => this.maisRecente(this.registrosTelas(), (item) => item.dataRegistro));
  readonly ultimaAvaliacaoCrescimento = computed(() => this.maisRecente(this.curvasCrescimento(), (item) => item.dataMedicao));
  readonly painel = computed(() => this.montarPainel());

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly criancasService: CriancasService,
    private readonly desenvolvimentoService: DesenvolvimentoService,
    private readonly crescimentoService: CrescimentoService,
    private readonly alimentacaoService: AlimentacaoService,
    private readonly transitoIntestinalService: TransitoIntestinalService,
    private readonly sonoService: SonoService,
    private readonly telasService: TelasService
  ) {}

  ngOnInit(): void {
    this.carregarCrianca();
  }

  carregarCrianca(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.erro.set('Criança não encontrada.');
      this.carregando.set(false);
      return;
    }

    this.carregando.set(true);
    this.erro.set('');
    this.errosModulos.set({});

    forkJoin({
      crianca: this.criancasService.buscarPorId(id),
      marcos: this.desenvolvimentoService.listarMarcos(id).pipe(catchError(() => {
        this.registrarErroModulo('desenvolvimento', 'Não foi possível carregar o desenvolvimento agora.');
        return of([]);
      })),
      curvas: this.crescimentoService.listarCurvas(id).pipe(catchError(() => {
        this.registrarErroModulo('crescimento', 'Não foi possível carregar o crescimento agora.');
        return of([]);
      })),
      alimentacao: this.alimentacaoService.listar(id).pipe(catchError(() => {
        this.registrarErroModulo('alimentacao', 'Não foi possível carregar a alimentação agora.');
        return of([]);
      })),
      transitoIntestinal: this.transitoIntestinalService.listar(id).pipe(catchError(() => {
        this.registrarErroModulo('transitoIntestinal', 'Não foi possível carregar o trânsito intestinal agora.');
        return of([]);
      })),
      sono: this.sonoService.listar(id).pipe(catchError(() => {
        this.registrarErroModulo('sono', 'Não foi possível carregar o sono agora.');
        return of([]);
      })),
      telas: this.telasService.listar(id).pipe(catchError(() => {
        this.registrarErroModulo('telas', 'NÃ£o foi possÃ­vel carregar telas agora.');
        return of([]);
      }))
    })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: ({ crianca, marcos, curvas, alimentacao, transitoIntestinal, sono, telas }) => {
          this.crianca.set(crianca);
          this.marcos.set(marcos);
          this.curvasCrescimento.set(curvas);
          this.registrosAlimentacao.set(alimentacao);
          this.registrosTransitoIntestinal.set(transitoIntestinal);
          this.registrosSono.set(sono);
          this.registrosTelas.set(telas);
        },
        error: (erro: HttpErrorResponse) => {
          this.erro.set(erro.status === 404
            ? 'Criança não encontrada ou não vinculada à sua conta.'
            : 'Não foi possível carregar os dados agora.');
        }
      });
  }

  private registrarErroModulo(modulo: string, mensagem: string): void {
    this.errosModulos.update((erros) => ({ ...erros, [modulo]: mensagem }));
  }

  private montarPainel(): PainelAcompanhamento {
    const crianca = this.crianca();
    if (!crianca) {
      return {
        frase: '',
        prioridades: [],
        acompanharCasa: [],
        completarApp: [],
        conversarConsulta: [],
        pontosFortes: [],
        periodo: '',
        modulos: []
      };
    }

    const modulos = [
      this.resumoDesenvolvimento(crianca),
      this.resumoCrescimento(crianca),
      this.resumoAlimentacao(crianca),
      this.resumoTransitoIntestinal(crianca),
      this.resumoSono(crianca),
      this.resumoTelas(crianca)
    ];
    const prioridades = this.prioridadesDoPainel(modulos, crianca);
    const acompanharCasa = this.acoesParaAcompanharEmCasa();
    const completarApp = this.acoesParaCompletarNoApp(modulos);
    const conversarConsulta = this.acoesParaConsulta(crianca, modulos);
    const pontosFortes = this.pontosFortesDoPainel(modulos);
    const frase = prioridades.length > 0
      ? 'Há pontos úteis para acompanhar com mais atenção nos próximos registros.'
      : 'Os registros disponíveis não mostram pontos prioritários neste momento.';

    return {
      frase,
      prioridades,
      acompanharCasa,
      completarApp,
      conversarConsulta,
      pontosFortes,
      periodo: this.periodoPainel(),
      modulos
    };
  }

  private resumoDesenvolvimento(crianca: Crianca): ModuloResumo {
    if (this.errosModulos()['desenvolvimento']) {
      return this.moduloIndisponivel('Desenvolvimento', 'Marcos da idade', ['/criancas', crianca.id, 'desenvolvimento']);
    }

    const marcos = this.marcos();
    if (marcos.length === 0) {
      return {
        titulo: 'Desenvolvimento',
        subtitulo: 'Marcos da idade',
        estado: 'pendente',
        valor: 'Sem faixa carregada',
        detalhe: 'Abra os marcos para iniciar o acompanhamento da idade atual.',
        acao: 'Abrir marcos',
        rota: ['/criancas', crianca.id, 'desenvolvimento']
      };
    }

    const idade = marcos.at(-1)?.idadeMeses;
    const marcosDaIdade = idade === undefined ? [] : marcos.filter((marco) => marco.idadeMeses === idade);
    const respondidos = marcosDaIdade.filter((marco) => marco.status !== 'NAO_AVALIADO').length;
    const pontosConsulta = marcosDaIdade.filter((marco) => marco.status === 'AINDA_NAO_OBSERVADO' || marco.status === 'NAO_TENHO_CERTEZA').length;
    const total = marcosDaIdade.length;

    return {
      titulo: 'Desenvolvimento',
      subtitulo: `Faixa: ${idade === undefined ? 'idade atual' : this.tituloIdade(idade)}`,
      estado: total === 0 ? 'pendente' : pontosConsulta > 0 ? 'atencao' : respondidos === total ? 'ok' : 'pendente',
      valor: total === 0 ? 'Sem marcos' : `${respondidos}/${total}`,
      detalhe: pontosConsulta > 0
        ? `${pontosConsulta} ${pontosConsulta === 1 ? 'ponto pode ajudar na conversa com o pediatra.' : 'pontos podem ajudar na conversa com o pediatra.'}`
        : total > 0 && respondidos === total
          ? 'Faixa atual preenchida.'
          : 'Continue a sequência da idade atual.',
      acao: total > 0 && respondidos > 0 ? 'Continuar' : 'Iniciar',
      rota: ['/criancas', crianca.id, 'desenvolvimento']
    };
  }

  private resumoCrescimento(crianca: Crianca): ModuloResumo {
    if (this.errosModulos()['crescimento']) {
      return this.moduloIndisponivel('Crescimento', 'Curvas OMS', ['/criancas', crianca.id, 'crescimento']);
    }

    const avaliacao = this.ultimaAvaliacaoCrescimento();
    if (!avaliacao) {
      return {
        titulo: 'Crescimento',
        subtitulo: 'Curvas OMS',
        estado: 'pendente',
        valor: 'Sem medida',
        detalhe: 'Registre peso, comprimento ou perímetro cefálico para ver a evolução.',
        acao: 'Registrar',
        rota: ['/criancas', crianca.id, 'crescimento']
      };
    }

    const foraDaFaixa = avaliacao.resultados.filter((resultado) => resultado.classificacao !== 'FAIXA_ESPERADA');
    return {
      titulo: 'Crescimento',
      subtitulo: `Última medida: ${this.formatarData(avaliacao.dataMedicao)}`,
      estado: foraDaFaixa.length > 0 ? 'atencao' : 'ok',
      valor: foraDaFaixa.length > 0 ? `${foraDaFaixa.length} atenção` : 'Na faixa',
      detalhe: foraDaFaixa.length > 0
        ? 'Há medida fora da faixa esperada; observe junto da trajetória.'
        : 'Últimas curvas avaliadas estão dentro da faixa esperada.',
      acao: 'Ver curvas',
      rota: ['/criancas', crianca.id, 'crescimento']
    };
  }

  private resumoAlimentacao(crianca: Crianca): ModuloResumo {
    if (this.errosModulos()['alimentacao']) {
      return this.moduloIndisponivel('Alimentação', 'Rotina alimentar', ['/criancas', crianca.id, 'alimentacao']);
    }

    const registro = this.ultimoRegistroAlimentacao();
    if (!registro) {
      return {
        titulo: 'Alimentação',
        subtitulo: 'Rotina alimentar',
        estado: 'pendente',
        valor: 'Sem registro',
        detalhe: 'Registre a rotina para observar variedade, hábitos e pontos para consulta.',
        acao: 'Registrar',
        rota: ['/criancas', crianca.id, 'alimentacao']
      };
    }

    const pontosConsulta = registro.analise.conversaConsulta.length;
    return {
      titulo: 'Alimentação',
      subtitulo: `Último registro: ${this.formatarData(registro.dataRegistro)}`,
      estado: pontosConsulta > 0 ? 'atencao' : 'ok',
      valor: pontosConsulta > 0 ? `${pontosConsulta} ponto(s)` : 'Registrada',
      detalhe: pontosConsulta > 0 ? registro.analise.resumo : 'Rotina alimentar registrada para acompanhamento.',
      acao: 'Ver rotina',
      rota: ['/criancas', crianca.id, 'alimentacao']
    };
  }

  private resumoTransitoIntestinal(crianca: Crianca): ModuloResumo {
    if (this.errosModulos()['transitoIntestinal']) {
      return this.moduloIndisponivel('Trânsito intestinal', 'Fezes e assaduras', ['/criancas', crianca.id, 'transito-intestinal']);
    }

    const registro = this.ultimoRegistroTransitoIntestinal();
    if (!registro) {
      return {
        titulo: 'Trânsito intestinal',
        subtitulo: 'Fezes e assaduras',
        estado: 'pendente',
        valor: 'Sem registro',
        detalhe: 'Registre aspecto das fezes, frequência e sinais associados.',
        acao: 'Registrar',
        rota: ['/criancas', crianca.id, 'transito-intestinal']
      };
    }

    const pontosConsulta = registro.analise.conversaConsulta.length;
    return {
      titulo: 'Trânsito intestinal',
      subtitulo: `Último registro: ${this.formatarData(registro.dataRegistro)}`,
      estado: pontosConsulta > 0 ? 'atencao' : 'ok',
      valor: this.labelClassificacaoFezes(registro.analise.classificacaoFezes),
      detalhe: pontosConsulta > 0 ? registro.analise.resumo : 'Registro intestinal salvo para acompanhar padrões ao longo dos dias.',
      acao: 'Ver registro',
      rota: ['/criancas', crianca.id, 'transito-intestinal']
    };
  }

  private resumoSono(crianca: Crianca): ModuloResumo {
    if (this.errosModulos()['sono']) {
      return this.moduloIndisponivel('Sono', 'Descanso em 24h', ['/criancas', crianca.id, 'sono']);
    }

    const registro = this.ultimoRegistroSono();
    if (!registro) {
      return {
        titulo: 'Sono',
        subtitulo: 'Descanso em 24h',
        estado: 'pendente',
        valor: 'Sem registro',
        detalhe: 'Registre sono noturno, cochilos e despertares para acompanhar a rotina.',
        acao: 'Registrar',
        rota: ['/criancas', crianca.id, 'sono']
      };
    }

    const atencao = registro.analise.classificacaoDuracao !== 'FAIXA_ESPERADA' && registro.analise.classificacaoDuracao !== 'SEM_DADOS';
    return {
      titulo: 'Sono',
      subtitulo: `Último registro: ${this.formatarData(registro.dataRegistro)}`,
      estado: atencao || registro.analise.conversaConsulta.length > 0 ? 'atencao' : 'ok',
      valor: this.formatarDuracao(registro.minutosSonoTotal24h),
      detalhe: registro.analise.resumo,
      acao: 'Ver sono',
      rota: ['/criancas', crianca.id, 'sono']
    };
  }

  private resumoTelas(crianca: Crianca): ModuloResumo {
    if (this.errosModulos()['telas']) {
      return this.moduloIndisponivel('Telas', 'Tempo e contexto', ['/criancas', crianca.id, 'telas']);
    }

    const registro = this.ultimoRegistroTelas();
    if (!registro) {
      return {
        titulo: 'Telas',
        subtitulo: 'Tempo e contexto',
        estado: 'pendente',
        valor: 'Sem registro',
        detalhe: 'Registre quando a tela aparece para observar sono, refeicoes e brincadeira junto da rotina.',
        acao: 'Registrar',
        rota: ['/criancas', crianca.id, 'telas']
      };
    }

    const atencao = registro.analise.classificacaoTempo === 'ACIMA_DA_REFERENCIA' || registro.analise.conversaConsulta.length > 0;
    return {
      titulo: 'Telas',
      subtitulo: `Ultimo registro: ${this.formatarData(registro.dataRegistro)}`,
      estado: atencao ? 'atencao' : 'ok',
      valor: this.formatarDuracaoTela(registro.minutosMediosDia),
      detalhe: registro.analise.resumo,
      acao: 'Ver rotina',
      rota: ['/criancas', crianca.id, 'telas']
    };
  }

  private moduloIndisponivel(titulo: string, subtitulo: string, rota: string[]): ModuloResumo {
    return {
      titulo,
      subtitulo,
      estado: 'indisponivel',
      valor: 'Indisponível',
      detalhe: 'Não foi possível carregar estes dados agora.',
      acao: 'Abrir',
      rota
    };
  }

  private prioridadesDoPainel(modulos: ModuloResumo[], crianca: Crianca): string[] {
    const prioridades = modulos
      .filter((modulo) => modulo.estado === 'atencao')
      .map((modulo) => `${modulo.titulo}: ${modulo.detalhe}`);

    if (modulos.some((modulo) => modulo.estado === 'pendente')) {
      prioridades.push('Complete os módulos sem registro para que o Pueria consiga cruzar melhor rotina, crescimento e desenvolvimento.');
    }
    if (crianca.prematura) {
      prioridades.push('Prematuridade registrada: mantenha esse contexto nas conversas de crescimento e desenvolvimento.');
    }

    return prioridades.slice(0, 4);
  }

  private acoesParaAcompanharEmCasa(): PainelAcao[] {
    const acoes: PainelAcao[] = [];
    const ultimosSono = this.ultimos(this.registrosSono(), 5, (item) => item.dataRegistro);
    const ultimosAlimentacao = this.ultimos(this.registrosAlimentacao(), 5, (item) => item.dataRegistro);
    const ultimosIntestinal = this.ultimos(this.registrosTransitoIntestinal(), 5, (item) => item.dataRegistro);
    const ultimosTelas = this.ultimos(this.registrosTelas(), 5, (item) => item.dataRegistro);

    const sonoForaFaixa = ultimosSono.filter((registro) =>
      registro.analise.classificacaoDuracao === 'ABAIXO_DA_FAIXA'
      || registro.analise.classificacaoDuracao === 'ACIMA_DA_FAIXA'
    ).length;
    if (sonoForaFaixa >= 2) {
      acoes.push({
        titulo: 'Sono',
        texto: `Em ${sonoForaFaixa} dos últimos ${ultimosSono.length} registros, o sono ficou fora da faixa esperada. Registre mais alguns dias e observe horários, cochilos e despertares.`
      });
    }

    const telasSono = ultimosSono.filter((registro) => registro.telasAntesDormir).length;
    if (telasSono >= 2) {
      acoes.push({
        titulo: 'Rotina antes de dormir',
        texto: 'Telas perto do sono apareceram em mais de um registro. Vale observar se retirar telas antes de dormir melhora início do sono ou despertares.'
      });
    }

    const despertares = ultimosSono.filter((registro) => (registro.despertaresNoturnos ?? 0) >= 3).length;
    if (despertares >= 2) {
      acoes.push({
        titulo: 'Despertares',
        texto: 'Despertares frequentes se repetiram. Anote se acontecem em horários parecidos e se há fome, desconforto, tela, luz ou mudança de rotina.'
      });
    }

    const baixaVariedade = ultimosAlimentacao.filter((registro) =>
      registro.consomeFrutas === false
      || registro.consomeLegumesVerduras === false
      || registro.consomeCarnesOvos === false
    ).length;
    if (baixaVariedade >= 2) {
      acoes.push({
        titulo: 'Variedade alimentar',
        texto: 'A variedade apareceu como ponto a observar em mais de um registro. Planeje uma oferta por vez e registre aceitação sem transformar em pressão.'
      });
    }

    const telasAlimentacao = ultimosAlimentacao.filter((registro) => registro.telasDuranteRefeicoes).length;
    if (telasAlimentacao >= 2) {
      acoes.push({
        titulo: 'Refeições',
        texto: 'Telas durante refeições se repetiram. Observe se refeições sem tela ajudam na atenção aos sinais de fome, saciedade e interação.'
      });
    }

    const fezesForaEsperado = ultimosIntestinal.filter((registro) =>
      registro.analise.classificacaoFezes === 'ENDURECIDA'
      || registro.analise.classificacaoFezes === 'LIQUIDA'
      || registro.constipacao
      || registro.diarreia
    ).length;
    if (fezesForaEsperado >= 2) {
      acoes.push({
        titulo: 'Trânsito intestinal',
        texto: 'Fezes endurecidas, líquidas ou evacuação difícil apareceram mais de uma vez. Observe hidratação, alimentos recentes e repetição do padrão.'
      });
    }

    const telasAcimaReferencia = ultimosTelas.filter((registro) => registro.analise.classificacaoTempo === 'ACIMA_DA_REFERENCIA').length;
    if (telasAcimaReferencia >= 2) {
      acoes.push({
        titulo: 'Telas',
        texto: `Em ${telasAcimaReferencia} dos ultimos ${ultimosTelas.length} registros, o tempo ficou acima da referencia. Observe quais momentos da rotina sao mais faceis de ajustar primeiro.`
      });
    }

    const telasContextoSonoRefeicao = ultimosTelas.filter((registro) =>
      registro.telaAntesDormir || registro.telaDuranteRefeicoes || registro.telaParaAcalmar
    ).length;
    if (telasContextoSonoRefeicao >= 2) {
      acoes.push({
        titulo: 'Contexto das telas',
        texto: 'Telas perto do sono, nas refeicoes ou para acalmar apareceram mais de uma vez. Esses contextos costumam ser bons pontos de comeco para mudancas pequenas.'
      });
    }

    if (acoes.length === 0 && (ultimosSono.length > 0 || ultimosAlimentacao.length > 0 || ultimosIntestinal.length > 0 || ultimosTelas.length > 0)) {
      acoes.push({
        titulo: 'Rotina',
        texto: 'Os últimos registros não mostram repetição clara de um ponto modificável. Continue registrando para confirmar a estabilidade da rotina.'
      });
    }

    return acoes.slice(0, 4);
  }

  private acoesParaCompletarNoApp(modulos: ModuloResumo[]): PainelAcao[] {
    const acoes: PainelAcao[] = [];
    const pendentes = modulos.filter((modulo) => modulo.estado === 'pendente');

    if (pendentes.length > 0) {
      acoes.push({
        titulo: 'Registros pendentes',
        texto: `Complete ${pendentes.map((modulo) => modulo.titulo.toLowerCase()).join(', ')} para o Pueria cruzar melhor rotina, crescimento e desenvolvimento.`
      });
    }

    const marcos = this.marcos();
    const idade = marcos.at(-1)?.idadeMeses;
    const marcosDaIdade = idade === undefined ? [] : marcos.filter((marco) => marco.idadeMeses === idade);
    const total = marcosDaIdade.length;
    const respondidos = marcosDaIdade.filter((marco) => marco.status !== 'NAO_AVALIADO').length;
    if (total > 0 && respondidos < total) {
      acoes.push({
        titulo: 'Desenvolvimento',
        texto: `Faltam ${total - respondidos} marco(s) da faixa atual. Responder essa etapa melhora a leitura integrada do acompanhamento.`
      });
    }

    if (this.registrosSono().length > 0 && this.registrosSono().length < 3) {
      acoes.push({
        titulo: 'Sono',
        texto: 'Com 3 ou mais registros de sono, o painel consegue enxergar melhor se existe padrão ou se foi apenas um dia diferente.'
      });
    }

    if (this.registrosAlimentacao().length > 0 && this.registrosAlimentacao().length < 3) {
      acoes.push({
        titulo: 'Alimentação',
        texto: 'Com mais registros de alimentação, fica mais fácil diferenciar preferência ocasional de padrão persistente da rotina.'
      });
    }

    if (this.registrosTransitoIntestinal().length > 0 && this.registrosTransitoIntestinal().length < 3) {
      acoes.push({
        titulo: 'Trânsito intestinal',
        texto: 'Com mais alguns registros, fica mais fácil diferenciar uma mudança pontual de um padrão intestinal.'
      });
    }

    if (this.registrosTelas().length > 0 && this.registrosTelas().length < 3) {
      acoes.push({
        titulo: 'Telas',
        texto: 'Com mais alguns registros de telas, fica mais facil diferenciar um dia pontual de um padrao da rotina.'
      });
    }

    return acoes.slice(0, 4);
  }

  private acoesParaConsulta(crianca: Crianca, modulos: ModuloResumo[]): PainelAcao[] {
    const acoes: PainelAcao[] = [];
    const ultimosSono = this.ultimos(this.registrosSono(), 5, (item) => item.dataRegistro);
    const ultimosAlimentacao = this.ultimos(this.registrosAlimentacao(), 5, (item) => item.dataRegistro);
    const ultimosIntestinal = this.ultimos(this.registrosTransitoIntestinal(), 5, (item) => item.dataRegistro);
    const ultimosTelas = this.ultimos(this.registrosTelas(), 5, (item) => item.dataRegistro);

    if (crianca.prematura) {
      acoes.push({
        titulo: 'Contexto de nascimento',
        texto: 'Prematuridade registrada. Mantenha esse dado presente nas conversas sobre crescimento e desenvolvimento.'
      });
    }

    const roncosOuPausas = ultimosSono.filter((registro) => registro.roncosFrequentes || registro.pausasRespiratoriasPercebidas).length;
    if (roncosOuPausas > 0) {
      acoes.push({
        titulo: 'Sono',
        texto: 'Roncos frequentes ou pausas respiratórias percebidas foram registrados. Vale levar esse ponto ao pediatra, especialmente se persistirem.'
      });
    }

    const sonolenciaOuIrritabilidade = ultimosSono.filter((registro) => registro.sonolenciaDiurna || registro.irritabilidadeCansaco).length;
    if (sonolenciaOuIrritabilidade >= 2) {
      acoes.push({
        titulo: 'Comportamento e descanso',
        texto: 'Sonolência, irritabilidade ou cansaço apareceram em mais de um registro. Leve junto do histórico de sono para contextualizar.'
      });
    }

    const sinaisAlimentares = ultimosAlimentacao.filter((registro) =>
      registro.engasgosFrequentes
      || registro.vomitosRecorrentes
      || registro.dificuldadeGanhoPesoPercebida
      || registro.preocupacaoFamilia
    ).length;
    if (sinaisAlimentares > 0) {
      acoes.push({
        titulo: 'Alimentação',
        texto: 'Há sinal alimentar registrado que pode ajudar a conversa na consulta. Leve exemplos, frequência e quando acontece.'
      });
    }

    const sinaisIntestinais = ultimosIntestinal.filter((registro) =>
      registro.raiasSangue
      || registro.muco
      || registro.dorEvacuar
      || registro.escapeFecal
      || registro.assaduraFrequente
      || registro.assaduraPontosVermelhos
      || registro.preocupacaoFamilia
      || registro.analise.conversaConsulta.length > 0
    ).length;
    if (sinaisIntestinais > 0) {
      acoes.push({
        titulo: 'Trânsito intestinal',
        texto: 'Há sinais intestinais ou de pele registrados. Leve frequência, aspecto das fezes e presença de assaduras para a consulta.'
      });
    }

    const preocupacaoTelas = ultimosTelas.filter((registro) => registro.preocupacaoFamilia || registro.analise.conversaConsulta.length > 0).length;
    if (preocupacaoTelas > 0) {
      acoes.push({
        titulo: 'Telas',
        texto: 'Ha contexto de telas que pode valer conversa na consulta. Leve tempo aproximado, momento do dia e o que a familia ja tentou ajustar.'
      });
    }

    const modulosAtencao = modulos.filter((modulo) => modulo.estado === 'atencao');
    if (modulosAtencao.length >= 2) {
      acoes.push({
        titulo: 'Visão integrada',
        texto: 'Mais de um módulo está marcado para observar. Use o painel como resumo para orientar a próxima conversa com o pediatra.'
      });
    }

    return acoes.slice(0, 4);
  }

  private pontosFortesDoPainel(modulos: ModuloResumo[]): PainelAcao[] {
    const pontos = modulos
      .filter((modulo) => modulo.estado === 'ok')
      .map((modulo) => ({
        titulo: modulo.titulo,
        texto: modulo.detalhe
      }));

    if (pontos.length === 0 && modulos.some((modulo) => modulo.estado === 'pendente')) {
      pontos.push({
        titulo: 'Começo do acompanhamento',
        texto: 'Os módulos iniciais já estão organizados. O próximo ganho vem de completar registros e acompanhar evolução.'
      });
    }

    return pontos.slice(0, 3);
  }

  private periodoPainel(): string {
    const datas = [
      ...this.registrosSono().map((registro) => registro.dataRegistro),
      ...this.registrosAlimentacao().map((registro) => registro.dataRegistro),
      ...this.registrosTransitoIntestinal().map((registro) => registro.dataRegistro),
      ...this.registrosTelas().map((registro) => registro.dataRegistro),
      ...this.curvasCrescimento().map((avaliacao) => avaliacao.dataMedicao)
    ].sort();

    if (datas.length === 0) {
      return 'Sem registros longitudinais ainda';
    }

    const primeira = datas[0];
    const ultima = datas.at(-1) ?? primeira;
    return primeira === ultima
      ? `Registro de ${this.formatarData(ultima)}`
      : `${this.formatarData(primeira)} a ${this.formatarData(ultima)}`;
  }

  private maisRecente<T>(itens: T[], data: (item: T) => string): T | null {
    return [...itens].sort((a, b) => data(b).localeCompare(data(a)))[0] ?? null;
  }

  private ultimos<T>(itens: T[], total: number, data: (item: T) => string): T[] {
    return [...itens].sort((a, b) => data(b).localeCompare(data(a))).slice(0, total);
  }

  private tituloIdade(idadeMeses: number): string {
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

  formatarDuracao(minutos?: number | null): string {
    if (minutos === null || minutos === undefined) {
      return 'Sem duração';
    }
    const horas = Math.floor(minutos / 60);
    const resto = minutos % 60;
    if (resto === 0) {
      return `${horas}h`;
    }
    return `${horas}h${resto.toString().padStart(2, '0')}`;
  }

  formatarDuracaoTela(minutos?: number | null): string {
    if (minutos === null || minutos === undefined) {
      return 'Sem tempo';
    }
    if (minutos < 60) {
      return `${minutos} min`;
    }
    const horas = Math.floor(minutos / 60);
    const resto = minutos % 60;
    return resto === 0 ? `${horas}h` : `${horas}h${resto.toString().padStart(2, '0')}`;
  }

  classeEstado(estado: EstadoModulo): string {
    const classes: Record<EstadoModulo, string> = {
      ok: 'painel-modulo--ok',
      atencao: 'painel-modulo--atencao',
      pendente: 'painel-modulo--pendente',
      indisponivel: 'painel-modulo--indisponivel'
    };
    return classes[estado];
  }

  labelEstado(estado: EstadoModulo): string {
    const labels: Record<EstadoModulo, string> = {
      ok: 'Acompanhado',
      atencao: 'Observar',
      pendente: 'Pendente',
      indisponivel: 'Indisponível'
    };
    return labels[estado];
  }

  labelClassificacaoFezes(classificacao: string): string {
    const labels: Record<string, string> = {
      ENDURECIDA: 'Endurecida',
      ESPERADA: 'Na faixa esperada',
      MAIS_MACIA: 'Mais macia',
      LIQUIDA: 'Líquida',
      SEM_DADOS: 'Sem dados'
    };
    return labels[classificacao] ?? classificacao;
  }

  abrirConfirmacaoRemocao(): void {
    this.confirmandoRemocao.set(true);
    this.erro.set('');
  }

  cancelarRemocao(): void {
    this.confirmandoRemocao.set(false);
  }

  confirmarRemocao(): void {
    const crianca = this.crianca();
    if (!crianca) {
      return;
    }

    this.removendo.set(true);
    this.erro.set('');

    this.criancasService.remover(crianca.id)
      .pipe(finalize(() => this.removendo.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/criancas');
        },
        error: (erro: HttpErrorResponse) => {
          this.confirmandoRemocao.set(false);
          const mensagens = erro.error?.mensagens;
          this.erro.set(Array.isArray(mensagens) && mensagens.length > 0
            ? mensagens[0]
            : 'Não foi possível remover o perfil agora.');
        }
      });
  }

  calcularIdade(dataNascimento: string): string {
    const nascimento = new Date(`${dataNascimento}T00:00:00`);
    const hoje = new Date();
    let anos = hoje.getFullYear() - nascimento.getFullYear();
    let meses = hoje.getMonth() - nascimento.getMonth();

    if (hoje.getDate() < nascimento.getDate()) {
      meses -= 1;
    }

    if (meses < 0) {
      anos -= 1;
      meses += 12;
    }

    if (anos <= 0) {
      return `${meses} ${meses === 1 ? 'mês' : 'meses'}`;
    }

    return `${anos} ${anos === 1 ? 'ano' : 'anos'} e ${meses} ${meses === 1 ? 'mês' : 'meses'}`;
  }

  formatarData(data: string): string {
    return new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(`${data}T00:00:00Z`));
  }

  formatarPeso(pesoGramas: number): string {
    const pesoKg = pesoGramas / 1000;
    return `${pesoKg.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    })} kg (${pesoGramas.toLocaleString('pt-BR')} g)`;
  }

  formatarMedida(valor: number, unidade: string): string {
    return `${valor.toLocaleString('pt-BR', {
      minimumFractionDigits: 1,
      maximumFractionDigits: 2
    })} ${unidade}`;
  }

  formatarIdadeGestacional(semanas: number, dias: number): string {
    return `${semanas} semanas${dias > 0 ? ` e ${dias} dias` : ''}`;
  }

  formatarApgar(valor?: number | null): string {
    return valor == null ? 'Não informado' : `${valor}/10`;
  }

  labelSexo(sexo: string | null): string {
    const labels: Record<string, string> = {
      FEMININO: 'Feminino',
      MASCULINO: 'Masculino',
      NAO_INFORMADO: 'Não informado'
    };
    return sexo ? labels[sexo] ?? sexo : 'Não informado';
  }

  labelTipoParto(tipoParto: string): string {
    const labels: Record<string, string> = {
      VAGINAL: 'Vaginal',
      CESAREA: 'Cesárea',
      VAGINAL_INSTRUMENTADO: 'Vaginal com instrumento',
      NAO_INFORMADO: 'Não informado'
    };
    return labels[tipoParto] ?? tipoParto;
  }

  labelTipoGestacao(tipoGestacao: string): string {
    const labels: Record<string, string> = {
      UNICA: 'Única',
      MULTIPLA: 'Múltipla',
      NAO_INFORMADO: 'Não informado'
    };
    return labels[tipoGestacao] ?? tipoGestacao;
  }

  labelStatusCondicao(status: string): string {
    const labels: Record<string, string> = {
      NAO: 'Não',
      SIM: 'Sim',
      EM_INVESTIGACAO: 'Em investigação',
      PREFIRO_INFORMAR_DEPOIS: 'Prefiro informar depois'
    };
    return labels[status] ?? status;
  }

  labelTriagem(status: string): string {
    const labels: Record<string, string> = {
      REALIZADO: 'Realizado',
      PENDENTE: 'Pendente',
      NAO_INFORMADO: 'Não informado'
    };
    return labels[status] ?? status;
  }

  labelAlimentacaoInicial(alimentacao: string): string {
    const labels: Record<string, string> = {
      ALEITAMENTO_MATERNO_EXCLUSIVO: 'Aleitamento materno exclusivo',
      ALEITAMENTO_MISTO: 'Aleitamento misto',
      FORMULA_INFANTIL: 'Fórmula infantil',
      NAO_INFORMADO: 'Não informado'
    };
    return labels[alimentacao] ?? alimentacao;
  }

  listarIntercorrencias(crianca: Crianca): string {
    const pontos = [
      crianca.utiNeonatal ? 'UTI neonatal' : '',
      crianca.reanimacaoNeonatal ? 'reanimação ao nascer' : '',
      crianca.ictericiaNeonatal ? 'icterícia neonatal' : '',
      crianca.dificuldadeRespiratoria ? 'dificuldade respiratória' : '',
      crianca.dificuldadeAmamentacao ? 'dificuldade para mamar' : ''
    ].filter(Boolean);

    return pontos.length > 0 ? pontos.join(', ') : 'Sem intercorrências registradas';
  }

  listarPontosGestacao(crianca: Crianca): string {
    const pontos = [
      crianca.diabetesGestacional ? 'diabetes gestacional' : '',
      crianca.hipertensaoGestacional ? 'pressão alta ou pré-eclâmpsia' : '',
      crianca.infeccaoGestacional ? 'infecção importante' : '',
      crianca.sangramentoGestacional ? 'sangramento importante' : '',
      crianca.usoAlcoolGestacao ? 'exposição a álcool' : '',
      crianca.usoTabacoGestacao ? 'exposição a tabaco' : '',
      crianca.outrasExposicoesGestacao ? 'outras exposições relevantes' : ''
    ].filter(Boolean);

    return pontos.length > 0 ? pontos.join(', ') : 'Sem intercorrências registradas';
  }
}
