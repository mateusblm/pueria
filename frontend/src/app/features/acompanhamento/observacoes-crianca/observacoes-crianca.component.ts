import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CriancasService } from '../../criancas/criancas.service';
import { Crianca } from '../../../shared/models/crianca.model';
import { AppIconComponent, AppIconName } from '../../../shared/components/app-icon/app-icon.component';
import { catchError, forkJoin, of } from 'rxjs';
import { ModuloHome, ResumoHome, ResumoHomeService } from '../resumo-home.service';

type ModuloObservacao = {
  titulo: string;
  descricao: string;
  rota: string;
  icone: AppIconName;
  tema: string;
  moduloResumo?: ModuloHome;
};

type GrupoObservacoes = {
  titulo: string;
  descricao: string;
  modulos: ModuloObservacao[];
};

@Component({
  selector: 'app-observacoes-crianca',
  imports: [RouterLink, AppIconComponent],
  templateUrl: './observacoes-crianca.component.html',
  styleUrl: './observacoes-crianca.component.scss'
})
export class ObservacoesCriancaComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly criancasService = inject(CriancasService);
  private readonly resumoHomeService = inject(ResumoHomeService);

  readonly crianca = signal<Crianca | null>(null);
  readonly resumoHome = signal<ResumoHome | null>(null);
  readonly carregando = signal(true);
  readonly erro = signal('');
  readonly criancaId = this.route.snapshot.paramMap.get('id') ?? '';

  readonly acessosRapidos: ModuloObservacao[] = [
    { titulo: 'Sono', descricao: 'Padrões de descanso e sinais observados.', rota: 'sono', icone: 'moon', tema: 'sono' },
    { titulo: 'Refeição', descricao: 'Rotina, alimentos e reações observadas.', rota: 'alimentacao', icone: 'salad', tema: 'alimentacao' },
    { titulo: 'Humor', descricao: 'Humor, choro, interação e interesse em brincar.', rota: 'humor-comportamento', icone: 'heart', tema: 'humor' },
    { titulo: 'Eventos', descricao: 'Mudanças ou acontecimentos importantes do período.', rota: 'observacoes-eventos', icone: 'message', tema: 'eventos' }
  ];

  readonly modulosRotina: ModuloObservacao[] = [
    { titulo: 'Sono', descricao: 'Sem registros ainda.', rota: 'sono', icone: 'moon', tema: 'sono', moduloResumo: 'SONO' },
    { titulo: 'Alimentação', descricao: 'Sem registros ainda.', rota: 'alimentacao', icone: 'salad', tema: 'alimentacao', moduloResumo: 'ALIMENTACAO' },
    { titulo: 'Eliminações', descricao: 'Sem registros ainda.', rota: 'transito-intestinal', icone: 'toilet', tema: 'eliminacoes', moduloResumo: 'TRANSITO_INTESTINAL' },
    { titulo: 'Humor e comportamento', descricao: 'Sem registros ainda.', rota: 'humor-comportamento', icone: 'heart', tema: 'humor', moduloResumo: 'HUMOR' },
    { titulo: 'Telas', descricao: 'Sem registros ainda.', rota: 'telas', icone: 'smartphone', tema: 'telas', moduloResumo: 'TELAS' },
    { titulo: 'Observações e eventos', descricao: 'Mudanças ou acontecimentos importantes do período.', rota: 'observacoes-eventos', icone: 'message', tema: 'eventos' },
    { titulo: 'Saúde e cuidados', descricao: 'Sem registros ainda.', rota: 'saude', icone: 'stethoscope', tema: 'saude', moduloResumo: 'SAUDE' }
  ];

  nomeCurto(nome: string): string {
    return nome.trim().split(/\s+/)[0] || nome;
  }

  resumoModulo(modulo: ModuloObservacao): string {
    const area = this.resumoHome()?.areas.find((item) => item.modulo === modulo.moduloResumo);
    return area?.resumo || modulo.descricao;
  }

  moduloTemRegistro(modulo: ModuloObservacao): boolean {
    const area = this.resumoHome()?.areas.find((item) => item.modulo === modulo.moduloResumo);
    return (area?.quantidadeRegistros ?? 0) > 0;
  }

  readonly grupos: GrupoObservacoes[] = [
    {
      titulo: 'Corpo e saúde',
      descricao: 'Informações que ajudam a acompanhar medidas, saúde e eliminações ao longo do tempo.',
      modulos: [
        { titulo: 'Crescimento', descricao: 'Peso, estatura e perímetro cefálico.', rota: 'crescimento', icone: 'chart', tema: 'crescimento' },
        { titulo: 'Eliminações fisiológicas', descricao: 'Fezes, diurese e sinais observados.', rota: 'transito-intestinal', icone: 'toilet', tema: 'eliminacoes' },
        { titulo: 'Saúde e cuidados', descricao: 'Intercorrências, medicamentos e suplementos.', rota: 'saude', icone: 'stethoscope', tema: 'saude' }
      ]
    },
    {
      titulo: 'Rotina e hábitos',
      descricao: 'Registre somente o que ajuda a entender o padrão da criança.',
      modulos: [
        { titulo: 'Sono', descricao: 'Padrões de descanso e sinais observados.', rota: 'sono', icone: 'moon', tema: 'sono' },
        { titulo: 'Alimentação', descricao: 'Rotina, alimentos e reações observadas.', rota: 'alimentacao', icone: 'salad', tema: 'alimentacao' },
        { titulo: 'Telas', descricao: 'Contexto de uso e oportunidades de ajuste.', rota: 'telas', icone: 'smartphone', tema: 'telas' }
      ]
    },
    {
      titulo: 'Contexto da família',
      descricao: 'Registros livres que ajudam a dar sentido às outras observações.',
      modulos: [
        { titulo: 'Humor e comportamento', descricao: 'Humor, choro, interação e interesse em brincar.', rota: 'humor-comportamento', icone: 'heartPulse', tema: 'humor' },
        { titulo: 'Eventos marcantes', descricao: 'Mudanças ou acontecimentos importantes do período.', rota: 'observacoes-eventos', icone: 'message', tema: 'eventos' }
      ]
    }
  ];

  ngOnInit(): void {
    if (!this.criancaId) {
      this.carregando.set(false);
      this.erro.set('Não foi possível identificar a criança.');
      return;
    }
    forkJoin({
      crianca: this.criancasService.buscarPorId(this.criancaId),
      resumo: this.resumoHomeService.carregar(this.criancaId).pipe(catchError(() => of(null)))
    }).subscribe({
      next: ({ crianca, resumo }) => {
        this.crianca.set(crianca);
        this.resumoHome.set(resumo);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível abrir o acompanhamento agora.');
        this.carregando.set(false);
      }
    });
  }
}
