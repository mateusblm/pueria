import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { Crianca } from '../../../shared/models/crianca.model';
import { CriancasService } from '../criancas.service';

interface ErroApiResponse {
  status?: number;
  erro?: string;
  mensagens?: string[];
}

@Component({
  selector: 'app-detalhe-crianca',
  imports: [RouterLink],
  templateUrl: './detalhe-crianca.component.html',
  styleUrl: './detalhe-crianca.component.scss'
})
export class DetalheCriancaComponent implements OnInit {
  crianca: Crianca | null = null;
  carregando = true;
  erro = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly criancasService: CriancasService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.erro = 'Criança não encontrada.';
      this.carregando = false;
      return;
    }

    this.carregando = true;
    this.erro = '';

    this.criancasService.buscarPorId(id)
      .pipe(finalize(() => {
        this.carregando = false;
      }))
      .subscribe({
        next: (crianca) => {
          this.crianca = crianca;
        },
        error: (erro: HttpErrorResponse) => {
          this.erro = this.obterMensagemErro(erro);
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

  private obterMensagemErro(erro: HttpErrorResponse): string {
    const corpo = erro.error as ErroApiResponse | string | null;

    if (typeof corpo === 'string' && corpo.trim().length > 0) {
      return corpo;
    }

    if (corpo && typeof corpo === 'object') {
      if (Array.isArray(corpo.mensagens) && corpo.mensagens.length > 0) {
        return corpo.mensagens[0];
      }

      if (corpo.erro) {
        return corpo.erro;
      }
    }

    if (erro.status === 401) {
      return 'Sua sessão expirou. Faça login novamente.';
    }

    if (erro.status === 404) {
      return 'Criança não encontrada ou não vinculada ao seu usuário.';
    }

    if (erro.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique se a API está em execução.';
    }

    return 'Não foi possível carregar os dados agora.';
  }
}
