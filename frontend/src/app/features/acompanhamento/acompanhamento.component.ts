import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, finalize, forkJoin, map, of, switchMap } from 'rxjs';
import { CriancasService } from '../criancas/criancas.service';
import { DesenvolvimentoService } from '../desenvolvimento/desenvolvimento.service';
import { Crianca } from '../../shared/models/crianca.model';
import { MarcoDesenvolvimento } from '../../shared/models/desenvolvimento.model';

type ResumoCrianca = {
  crianca: Crianca;
  marcos: MarcoDesenvolvimento[];
  erro?: string;
};

@Component({
  selector: 'app-acompanhamento',
  imports: [RouterLink],
  templateUrl: './acompanhamento.component.html',
  styleUrl: './acompanhamento.component.scss'
})
export class AcompanhamentoComponent implements OnInit {
  private readonly criancasService = inject(CriancasService);
  private readonly desenvolvimentoService = inject(DesenvolvimentoService);

  readonly resumos = signal<ResumoCrianca[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal('');

  readonly possuiCriancas = computed(() => this.resumos().length > 0);

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set('');

    this.criancasService.listar()
      .pipe(
        switchMap((criancas) => {
          if (criancas.length === 0) {
            return of([]);
          }

          return forkJoin(criancas.map((crianca) =>
            this.desenvolvimentoService.listarMarcos(crianca.id).pipe(
              map((marcos) => ({ crianca, marcos })),
              catchError(() => of({ crianca, marcos: [], erro: 'Não foi possível carregar o desenvolvimento agora.' }))
            )
          ));
        }),
        finalize(() => this.carregando.set(false))
      )
      .subscribe({
        next: (resumos) => this.resumos.set(resumos),
        error: (erro: HttpErrorResponse) => this.erro.set(this.extrairMensagemErro(erro))
      });
  }

  idadeReferencia(marcos: MarcoDesenvolvimento[]): string {
    const idade = marcos.at(-1)?.idadeMeses;
    return idade === undefined ? 'Sem faixa disponível' : this.tituloIdade(idade);
  }

  progresso(marcos: MarcoDesenvolvimento[]): { total: number; respondidos: number; percentual: number } {
    const idade = marcos.at(-1)?.idadeMeses;
    const marcosDaIdade = idade === undefined ? [] : marcos.filter((marco) => marco.idadeMeses === idade);
    const total = marcosDaIdade.length;
    const respondidos = marcosDaIdade.filter((marco) => marco.status !== 'NAO_AVALIADO').length;

    return { total, respondidos, percentual: total === 0 ? 0 : Math.round((respondidos / total) * 100) };
  }

  pontosAtencao(marcos: MarcoDesenvolvimento[]): number {
    const idade = marcos.at(-1)?.idadeMeses;
    if (idade === undefined) {
      return 0;
    }
    return marcos.filter((marco) =>
      marco.idadeMeses === idade && (marco.status === 'AINDA_NAO_OBSERVADO' || marco.status === 'NAO_TENHO_CERTEZA')
    ).length;
  }

  labelPontosAtencao(total: number): string {
    if (total === 0) {
      return 'Sem pontos de atenção';
    }
    return total === 1 ? '1 ponto de atenção' : `${total} pontos de atenção`;
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
    return 'Não foi possível carregar o acompanhamento agora.';
  }
}
