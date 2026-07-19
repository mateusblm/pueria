import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CriancasService } from '../../criancas/criancas.service';
import { Crianca } from '../../../shared/models/crianca.model';
import { AppIconComponent, AppIconName } from '../../../shared/components/app-icon/app-icon.component';

type ModuloObservacao = {
  titulo: string;
  descricao: string;
  rota: string;
  icone: AppIconName;
  tema: string;
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

  readonly crianca = signal<Crianca | null>(null);
  readonly carregando = signal(true);
  readonly erro = signal('');
  readonly criancaId = this.route.snapshot.paramMap.get('id') ?? '';

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
    this.criancasService.buscarPorId(this.criancaId).subscribe({
      next: (crianca) => {
        this.crianca.set(crianca);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível abrir o acompanhamento agora.');
        this.carregando.set(false);
      }
    });
  }
}
