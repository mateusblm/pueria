import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PrivacidadeDialogService {
  readonly aberta = signal(false);

  abrir(): void {
    this.aberta.set(true);
  }

  fechar(): void {
    this.aberta.set(false);
  }
}
