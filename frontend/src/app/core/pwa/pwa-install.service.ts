import { Injectable, signal } from '@angular/core';
type EventoInstalacaoPwa = Event & { prompt: () => Promise<void>; userChoice: Promise<{ outcome: 'accepted' | 'dismissed' }> };
@Injectable({ providedIn: 'root' })
export class PwaInstallService {
  readonly instalavel = signal(false);
  private evento?: EventoInstalacaoPwa;
  constructor() { window.addEventListener('beforeinstallprompt', (evento) => { evento.preventDefault(); this.evento = evento as EventoInstalacaoPwa; this.instalavel.set(true); }); window.addEventListener('appinstalled', () => { this.evento = undefined; this.instalavel.set(false); }); }
  async instalar(): Promise<boolean> { if (!this.evento) return false; await this.evento.prompt(); const escolha = await this.evento.userChoice; if (escolha.outcome === 'accepted') { this.evento = undefined; this.instalavel.set(false); } return escolha.outcome === 'accepted'; }
}
