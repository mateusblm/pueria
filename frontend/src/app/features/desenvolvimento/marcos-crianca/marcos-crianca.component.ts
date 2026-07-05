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
  readonly areaSelecionada = signal<AreaDesenvolvimento>('SOCIAL_EMOCIONAL');

  readonly areas: AreaDesenvolvimento[] = ['SOCIAL_EMOCIONAL', 'LINGUAGEM_COMUNICACAO', 'COGNITIVO', 'MOTOR'];

  readonly idadesDisponiveis = computed(() => [...new Set(this.marcos().map((marco) => marco.idadeMeses))].sort((a, b) => a - b));

  readonly tituloIdadeSelecionada = computed(() => {
    const idade = this.idadeSelecionada();
    return idade === null ? '' : this.tituloIdade(idade);
  });

  readonly marcosDaIdade = computed(() => {
    const idade = this.idadeSelecionada();
    return idade === null ? [] : this.marcos().filter((marco) => marco.idadeMeses === idade);
  });

  readonly areasResumo = computed<AreaResumo[]>(() => this.areas.map((area) => {
    const marcos = this.marcosDaIdade().filter((marco) => marco.area === area);
    return {
      area,
      label: this.labelArea(area),
      pendentes: marcos.filter((marco) => marco.status === 'NAO_AVALIADO').length,
      total: marcos.length
    };
  }).filter((resumo) => resumo.total > 0));

  readonly marcosDaArea = computed(() => this.marcosDaIdade().filter((marco) => marco.area === this.areaSelecionada()));

  readonly progresso = computed(() => {
    const total = this.marcosDaIdade().length;
    const respondidos = this.marcosDaIdade().filter((marco) => marco.status !== 'NAO_AVALIADO').length;

    return {
      total,
      respondidos,
      percentual: total === 0 ? 0 : Math.round((respondidos / total) * 100)
    };
  });

  readonly pontosDeAtencao = computed(() => this.marcosDaIdade().filter((marco) =>
    marco.status === 'AINDA_NAO_OBSERVADO' || marco.status === 'NAO_TENHO_CERTEZA'
  ));

  readonly dicasDaArea = computed(() => {
    const dicas: Record<AreaDesenvolvimento, string[]> = {
      SOCIAL_EMOCIONAL: [
        'Observe como a criança responde ao seu rosto, voz e presença durante a rotina.',
        'Inclua momentos curtos de interação olho no olho, colo, conversa e brincadeiras simples.'
      ],
      LINGUAGEM_COMUNICACAO: [
        'Converse narrando o banho, a alimentação e as brincadeiras, dando tempo para a criança responder.',
        'Cante, leia livros curtos e valorize sons, gestos e tentativas de comunicação.'
      ],
      COGNITIVO: [
        'Ofereça objetos seguros com formas, texturas e sons diferentes para exploração supervisionada.',
        'Repita brincadeiras simples e observe curiosidade, atenção compartilhada e tentativa de resolver problemas.'
      ],
      MOTOR: [
        'Garanta períodos seguros de movimento livre, com supervisão, respeitando a idade e o conforto da criança.',
        'Evite longos períodos em cadeirinhas ou telas quando a criança poderia explorar o corpo e o ambiente.'
      ]
    };

    return dicas[this.areaSelecionada()];
  });

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
          const idades = [...new Set(ordenados.map((marco) => marco.idadeMeses))];

          this.marcos.set(ordenados);
          if (idades.length > 0 && (this.idadeSelecionada() === null || !idades.includes(this.idadeSelecionada()!))) {
            this.idadeSelecionada.set(idades[idades.length - 1]);
          }
          if (!this.areasResumo().some((resumo) => resumo.area === this.areaSelecionada())) {
            this.areaSelecionada.set(this.areasResumo()[0]?.area ?? 'SOCIAL_EMOCIONAL');
          }
        },
        error: (erro: HttpErrorResponse) => {
          this.erro.set(this.extrairMensagemErro(erro));
        }
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
        },
        error: (erro: HttpErrorResponse) => {
          this.erro.set(this.extrairMensagemErro(erro));
        }
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
        next: () => {
          this.marcos.update((marcos) => marcos.map((item) => item.id === marco.id ? { ...item, observacao: valor } : item));
        },
        error: (erro: HttpErrorResponse) => {
          this.erro.set(this.extrairMensagemErro(erro));
        }
      });
  }

  selecionarIdade(idadeMeses: number): void {
    this.idadeSelecionada.set(idadeMeses);
    this.areaSelecionada.set(this.marcos()
      .find((marco) => marco.idadeMeses === idadeMeses && marco.area === this.areaSelecionada())?.area
      ?? this.marcos().find((marco) => marco.idadeMeses === idadeMeses)?.area
      ?? 'SOCIAL_EMOCIONAL');
  }

  selecionarArea(area: AreaDesenvolvimento): void {
    this.areaSelecionada.set(area);
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
      NAO_AVALIADO: 'Não registrado'
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
      return 'Sem pontos';
    }
    return total === 1 ? '1 ponto' : `${total} pontos`;
  }

  classeStatus(status: StatusMarcoDesenvolvimento): string {
    return `marco-status marco-status--${status.toLowerCase().replaceAll('_', '-')}`;
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

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;
    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens[0];
    }
    return 'Não foi possível carregar os marcos do desenvolvimento agora.';
  }
}
