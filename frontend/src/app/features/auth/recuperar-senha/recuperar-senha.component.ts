import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize, TimeoutError } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';

@Component({ selector: 'app-recuperar-senha', imports: [ReactiveFormsModule, RouterLink], templateUrl: './recuperar-senha.component.html', styleUrl: '../login/login.component.scss' })
export class RecuperarSenhaComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  readonly carregando = signal(false);
  readonly enviado = signal(false);
  readonly erro = signal('');
  readonly form = this.formBuilder.nonNullable.group({ email: ['', [Validators.required, Validators.email]] });
  enviar(): void {
    if (this.carregando()) return;
    this.erro.set('');
    if (this.form.invalid) { this.form.markAllAsTouched(); this.erro.set('Informe um e-mail válido para continuar.'); return; }
    this.carregando.set(true);
    this.auth.solicitarRedefinicaoSenha(this.form.getRawValue()).pipe(finalize(() => this.carregando.set(false))).subscribe({
      next: () => this.enviado.set(true),
      error: (erro: unknown) => this.erro.set(erro instanceof TimeoutError ? 'A conexão demorou mais que o esperado. Tente novamente.' : erro instanceof HttpErrorResponse ? 'Não foi possível enviar as instruções agora. Tente novamente.' : 'Não foi possível enviar as instruções agora. Tente novamente.')
    });
  }
}
