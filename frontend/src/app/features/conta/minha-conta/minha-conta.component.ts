import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { catchError, finalize, of } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/toast/toast.service';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';
import { Usuario } from '../../../shared/models/usuario.model';
import { PwaInstallService } from '../../../core/pwa/pwa-install.service';

@Component({
  selector: 'app-minha-conta',
  imports: [RouterLink, AppIconComponent, ReactiveFormsModule],
  templateUrl: './minha-conta.component.html',
  styleUrl: './minha-conta.component.scss'
})
export class MinhaContaComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  readonly pwaInstall = inject(PwaInstallService);

  readonly usuario = signal<Usuario | null>(null);
  readonly modal = signal<'email' | 'senha' | null>(null);
  readonly salvando = signal(false);
  readonly erro = signal('');
  readonly emailForm = this.fb.nonNullable.group({ email: ['', [Validators.required, Validators.email]], senhaAtual: ['', Validators.required] });
  readonly senhaForm = this.fb.nonNullable.group({ senhaAtual: ['', Validators.required], novaSenha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]], confirmarSenha: ['', Validators.required] });

  ngOnInit(): void {
    this.authService.usuarioAtual().pipe(catchError(() => of(null))).subscribe((usuario) => this.usuario.set(usuario));
  }

  iniciais(nome: string): string {
    return nome.trim().split(/\s+/).slice(0, 2).map((parte) => parte[0]).join('').toUpperCase() || 'P';
  }

  emBreve(): void {
    this.toast.sucesso('Esta funcionalidade estará disponível em breve.');
  }
  async instalarApp(): Promise<void> { if (await this.pwaInstall.instalar()) this.toast.sucesso('Pueria instalado neste dispositivo.'); }

  abrirModal(tipo: 'email' | 'senha'): void { this.erro.set(''); this.modal.set(tipo); if (tipo === 'email') this.emailForm.patchValue({ email: this.usuario()?.email ?? '' }); }
  fecharModal(): void { if (!this.salvando()) this.modal.set(null); }
  salvarEmail(): void { if (this.emailForm.invalid) { this.emailForm.markAllAsTouched(); return; } const v = this.emailForm.getRawValue(); this.enviar(this.authService.atualizarEmail(v.email, v.senhaAtual), 'E-mail atualizado. Entre novamente com seu novo e-mail.'); }
  salvarSenha(): void { const v = this.senhaForm.getRawValue(); if (this.senhaForm.invalid || v.novaSenha !== v.confirmarSenha) { this.erro.set(v.novaSenha !== v.confirmarSenha ? 'As novas senhas não coincidem.' : 'Revise os campos obrigatórios.'); return; } this.enviar(this.authService.atualizarSenha(v.senhaAtual, v.novaSenha), 'Senha atualizada. Entre novamente para continuar.'); }
  private enviar(operacao: ReturnType<AuthService['atualizarEmail']>, mensagem: string): void { this.erro.set(''); this.salvando.set(true); operacao.pipe(finalize(() => this.salvando.set(false))).subscribe({ next: () => { this.authService.sair(); this.toast.sucesso(mensagem); void this.router.navigateByUrl('/login'); }, error: (e: { error?: { mensagem?: string } }) => this.erro.set(e.error?.mensagem ?? 'Não foi possível salvar agora.') }); }
}
