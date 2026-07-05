import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AreaDesenvolvimento, MarcoDesenvolvimento, StatusMarcoDesenvolvimento } from '../../../shared/models/desenvolvimento.model';
import { DesenvolvimentoService } from '../desenvolvimento.service';

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

type ModoTela = 'responder' | 'resultados';

@Component({
  selector: 'app-marcos-crianca',
  imports: [RouterLink],
  templateUrl: './marcos-crianca.component.html',
  styleUrl: './marcos-crianca.component.scss'
})
export class MarcosCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly desenvolvimentoService = inject(DesenvolvimentoService);

  readonly criancaId = signal('');
  readonly marcos = signal<MarcoDesenvolvimento[]>([]);
  readonly carregando = signal(true);
  readonly salvandoId = signal<string | null>(null);
  readonly erro = signal('');
  readonly idadeSelecionada = signal<number | null>(null);
  readonly indiceEtapa = signal(0);
  readonly modo = signal<ModoTela>('responder');

  readonly areas: AreaDesenvolvimento[] = ['SOCIAL_EMOCIONAL', 'LINGUAGEM_COMUNICACAO', 'COGNITIVO', 'MOTOR'];

  readonly tituloIdadeSelecionada = computed(() => {
    const idade = this.idadeSelecionada();
    return idade === null ? '' : this.tituloIdade(idade);
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
          this.posicionarPrimeiraPendente();
        },
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
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
          this.marcos.update((marcos) => marcos.map((item) => item.id === marco.id ? { ...item, status } : item));
          this.avancarDepoisDeResponder();
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

  abrirResultados(): void {
    this.modo.set('resultados');
  }

  etapaAnterior(): void {
    this.indiceEtapa.update((indice) => Math.max(0, indice - 1));
  }

  proximaEtapa(): void {
    this.indiceEtapa.update((indice) => Math.min(this.marcosDaIdade().length - 1, indice + 1));
  }

  imprimirResumo(): void {
    window.print();
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

  labelStatus(status: StatusMarcoDesenvolvimento): string {
    const labels: Record<StatusMarcoDesenvolvimento, string> = {
      OBSERVADO: 'Sim',
      NAO_TENHO_CERTEZA: 'Não tenho certeza',
      AINDA_NAO_OBSERVADO: 'Ainda não',
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
      return 'Sem itens';
    }
    return total === 1 ? '1 item' : `${total} itens`;
  }

  dataResumo(): string {
    return new Intl.DateTimeFormat('pt-BR').format(new Date());
  }

  textoConduta(): string {
    if (this.pontosDeAtencao().length === 0) {
      return 'Manter acompanhamento de rotina, brincadeiras responsivas e novas observações conforme a criança cresce.';
    }
    return 'Levar as respostas marcadas como dúvida ou ainda não observadas para a próxima consulta. Se houver perda de habilidades, preocupação persistente ou atraso claro, antecipar contato com o pediatra.';
  }

  classeStatus(status: StatusMarcoDesenvolvimento): string {
    return `marco-status marco-status--${status.toLowerCase().replaceAll('_', '-')}`;
  }

  imagemArea(area: AreaDesenvolvimento): string {
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

  private posicionarPrimeiraPendente(): void {
    const primeiraPendente = this.marcosDaIdade().findIndex((marco) => marco.status === 'NAO_AVALIADO');
    this.indiceEtapa.set(primeiraPendente >= 0 ? primeiraPendente : 0);
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
