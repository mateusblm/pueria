import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AppIconComponent, AppIconName } from '../../../shared/components/app-icon/app-icon.component';
import { BrandMarkComponent } from '../../../shared/components/brand-mark/brand-mark.component';

type Modulo = {
  titulo: string;
  descricao: string;
  icone: AppIconName;
  classe: string;
};

@Component({
  selector: 'app-pagina-inicial',
  imports: [RouterLink, AppIconComponent, BrandMarkComponent],
  templateUrl: './pagina-inicial.component.html',
  styleUrl: './pagina-inicial.component.scss'
})
export class PaginaInicialComponent {
  readonly modulos: Modulo[] = [
    { titulo: 'Neurodesenvolvimento', descricao: 'Marcos por idade, observações da família e atividades que apoiam a conversa com o pediatra.', icone: 'brain', classe: 'modulo--neuro' },
    { titulo: 'Crescimento', descricao: 'Peso, comprimento e perímetro cefálico nas curvas de referência, com leitura clara da trajetória.', icone: 'chart', classe: 'modulo--crescimento' },
    { titulo: 'Alimentação', descricao: 'Variedade, introdução alimentar e aceitação registrados sem transformar as refeições em prova.', icone: 'salad', classe: 'modulo--alimentacao' },
    { titulo: 'Trânsito intestinal', descricao: 'Aspecto, frequência e sinais observados ao longo dos dias, com a escala de Bristol como apoio visual.', icone: 'toilet', classe: 'modulo--eliminacao' },
    { titulo: 'Sono', descricao: 'Registros rápidos para perceber duração, despertares e padrões de descanso em 24 horas.', icone: 'moon', classe: 'modulo--sono' },
    { titulo: 'Telas', descricao: 'Tempo e contexto de uso para decisões mais conscientes conforme a idade da criança.', icone: 'smartphone', classe: 'modulo--telas' },
    { titulo: 'Saúde e cuidados', descricao: 'Uso diário, intercorrências e lembretes organizados para não deixar informações importantes passarem.', icone: 'heartPulse', classe: 'modulo--saude' }
  ];
}
