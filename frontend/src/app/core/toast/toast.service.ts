import { Injectable, signal } from '@angular/core';

export type TipoToast = 'sucesso' | 'erro';

export type Toast = {
  id: number;
  mensagem: string;
  tipo: TipoToast;
};

@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly notificacoes = signal<Toast[]>([]);
  private proximoId = 1;

  sucesso(mensagem: string): void {
    this.exibir(mensagem, 'sucesso');
  }

  erro(mensagem: string): void {
    this.exibir(mensagem, 'erro');
  }

  remover(id: number): void {
    this.notificacoes.update((itens) => itens.filter((item) => item.id !== id));
  }

  private exibir(mensagem: string, tipo: TipoToast): void {
    const texto = mensagem.trim();
    if (!texto) {
      return;
    }

    const id = this.proximoId++;
    this.notificacoes.update((itens) => [...itens.filter((item) => item.mensagem !== texto), { id, mensagem: texto, tipo }].slice(-3));
    window.setTimeout(() => this.remover(id), tipo === 'erro' ? 7000 : 4500);
  }
}
