import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize, TimeoutError } from 'rxjs';
import { Crianca } from '../../../shared/models/crianca.model';
import { CriancasService } from '../criancas.service';

@Component({
  selector: 'app-minhas-criancas',
  imports: [RouterLink],
  templateUrl: './minhas-criancas.component.html',
  styleUrl: './minhas-criancas.component.scss'
})
export class MinhasCriancasComponent implements OnInit {
  readonly criancas = signal<Crianca[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal('');

  constructor(private readonly criancasService: CriancasService) {}

  ngOnInit(): void {
    this.carregarCriancas();
  }

  carregarCriancas(): void {
    this.carregando.set(true);
    this.erro.set('');

    this.criancasService.listar()
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (criancas) => {
          this.criancas.set(criancas);
        },
        error: (erro: unknown) => {
          this.erro.set(this.extrairMensagemErro(erro));
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

  private extrairMensagemErro(erro: unknown): string {
    if (erro instanceof HttpErrorResponse) {
      const mensagens = erro.error?.mensagens;
      if (Array.isArray(mensagens) && mensagens.length > 0) {
        return mensagens[0];
      }

      if (erro.status === 0) {
        return 'Não foi possível carregar as informações agora. Verifique sua conexão e tente novamente.';
      }
    }

    if (erro instanceof TimeoutError) {
      return 'O carregamento demorou mais que o esperado. Tente novamente em instantes.';
    }

    return 'Não foi possível carregar as crianças agora.';
  }
}
