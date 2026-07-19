import { Component, HostListener, computed, input, output } from '@angular/core';

@Component({
  selector: 'app-registro-rapido',
  templateUrl: './registro-rapido.component.html',
  styleUrl: './registro-rapido.component.scss'
})
export class RegistroRapidoComponent {
  readonly aberto = input(false);
  readonly titulo = input.required<string>();
  readonly descricao = input('');
  readonly etapa = input(1);
  readonly totalEtapas = input(1);
  readonly tema = input<'padrao' | 'sono'>('padrao');
  readonly fechar = output<void>();
  readonly passos = computed(() => Array.from({ length: this.totalEtapas() }, (_, indice) => indice + 1));

  @HostListener('document:keydown.escape')
  aoPressionarEscape(): void {
    if (this.aberto()) {
      this.fechar.emit();
    }
  }

  fecharAoClicarNoFundo(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fechar.emit();
    }
  }
}
