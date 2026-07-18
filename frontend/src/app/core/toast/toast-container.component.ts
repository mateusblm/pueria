import { Component, inject } from '@angular/core';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-toast-container',
  template: `
    <section class="toast-container" aria-live="polite" aria-atomic="true">
      @for (toast of toastService.notificacoes(); track toast.id) {
        <article class="toast" [class.toast--erro]="toast.tipo === 'erro'" [class.toast--sucesso]="toast.tipo === 'sucesso'" role="status">
          <span class="toast__marca" aria-hidden="true">{{ toast.tipo === 'erro' ? '!' : '✓' }}</span>
          <p>{{ toast.mensagem }}</p>
          <button type="button" aria-label="Fechar notificação" (click)="toastService.remover(toast.id)">×</button>
        </article>
      }
    </section>
  `,
  styleUrl: './toast-container.component.scss'
})
export class ToastContainerComponent {
  readonly toastService = inject(ToastService);
}
