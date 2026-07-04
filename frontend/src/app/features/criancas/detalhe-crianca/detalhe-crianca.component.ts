import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Crianca } from '../../../shared/models/crianca.model';
import { CriancasService } from '../criancas.service';

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

    this.criancasService.buscarPorId(id).subscribe({
      next: (crianca) => {
        this.crianca = crianca;
      },
      error: (erro: HttpErrorResponse) => {
        this.erro = erro.status === 404
          ? 'Criança não encontrada ou não vinculada ao seu usuário.'
          : 'Não foi possível carregar os dados agora.';
        this.carregando = false;
      },
      complete: () => {
        this.carregando = false;
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
}
