import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastContainerComponent } from './core/toast/toast-container.component';
import { PrivacidadeDialogService } from './core/privacidade/privacidade-dialog.service';
import { PrivacidadeModalComponent } from './shared/components/privacidade-modal/privacidade-modal.component';
import { PwaInstallService } from './core/pwa/pwa-install.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastContainerComponent, PrivacidadeModalComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private readonly privacidade = inject(PrivacidadeDialogService);
  private readonly pwaInstall = inject(PwaInstallService);

  interceptarAviso(evento: MouseEvent): void {
    const alvo = evento.target as HTMLElement | null;
    if (alvo?.closest('[data-privacy-modal]')) {
      evento.preventDefault();
      this.privacidade.abrir();
    }
  }
}
