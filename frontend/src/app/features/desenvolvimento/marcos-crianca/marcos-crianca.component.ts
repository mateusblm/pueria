import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { MarcoDesenvolvimento, StatusMarcoDesenvolvimento } from '../../../shared/models/desenvolvimento.model';
import { DesenvolvimentoService } from '../desenvolvimento.service';

type GrupoMarcos = {
  idadeMeses: number;
  titulo: string;
  marcos: MarcoDesenvolvimento[];
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

  readonly grupos = computed<GrupoMarcos[]>(() => {
    const porIdade = new Map<number, MarcoDesenvolvimento[]>();

    for (const marco of this.marcos()) {
      porIdade.set(marco.idadeMeses, [...(porIdade.get(marco.idadeMeses) ?? []), marco]);
    }

    return [...porIdade.entries()]
      .sort(([a], [b]) => a - b)
      .map(([idadeMeses, marcos]) => ({
        idadeMeses,
        titulo: this.tituloIdade(idadeMeses),
        marcos
      }));
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
        next: (marcos) => this.marcos.set(marcos),
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

  labelArea(area: string): string {
    const labels: Record<string, string> = {
      SOCIAL_EMOCIONAL: 'Social e emocional',
      LINGUAGEM_COMUNICACAO: 'Linguagem',
      COGNITIVO: 'Cognição',
      MOTOR: 'Movimento'
    };
    return labels[area] ?? area;
  }

  labelStatus(status: StatusMarcoDesenvolvimento): string {
    const labels: Record<StatusMarcoDesenvolvimento, string> = {
      OBSERVADO: 'Observado',
      AINDA_NAO_OBSERVADO: 'Acompanhar',
      NAO_AVALIADO: 'Não avaliado'
    };
    return labels[status];
  }

  classeStatus(status: StatusMarcoDesenvolvimento): string {
    return `marco-status marco-status--${status.toLowerCase().replaceAll('_', '-')}`;
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

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;
    if (Array.isArray(mensagens) && mensagens.length > 0) {
      return mensagens[0];
    }
    return 'Não foi possível carregar os marcos do desenvolvimento agora.';
  }
}
