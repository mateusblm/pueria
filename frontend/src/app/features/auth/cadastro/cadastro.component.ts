import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize, TimeoutError } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';

@Component({
  selector: 'app-cadastro',
  imports: [ReactiveFormsModule, RouterLink, AppIconComponent],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.scss'
})
export class CadastroComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly carregando = signal(false);
  readonly erro = signal('');
  readonly senhaVisivel = signal(false);
  readonly confirmarSenhaVisivel = signal(false);

  readonly form = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(150)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    senha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
    confirmarSenha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]]
  });

  alternarSenha(): void {
    this.senhaVisivel.update((visivel) => !visivel);
  }

  alternarConfirmarSenha(): void {
    this.confirmarSenhaVisivel.update((visivel) => !visivel);
  }

  cadastrar(): void {
    if (this.carregando()) {
      return;
    }

    this.erro.set('');
    if (this.form.controls.confirmarSenha.hasError('senhasDiferentes')) {
      this.form.controls.confirmarSenha.setErrors(null);
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro.set('Preencha os dados obrigatórios para criar sua conta.');
      return;
    }

    const valor = this.form.getRawValue();
    if (valor.senha !== valor.confirmarSenha) {
      this.form.controls.confirmarSenha.setErrors({ senhasDiferentes: true });
      this.erro.set('As senhas informadas não coincidem.');
      return;
    }

    this.carregando.set(true);

    this.authService.cadastrar({
      nome: valor.nome,
      email: valor.email,
      senha: valor.senha
    })
      .pipe(finalize(() => {
        this.carregando.set(false);
      }))
      .subscribe({
        next: () => void this.router.navigate(['/login'], {
          queryParams: { cadastro: 'realizado' }
        }),
        error: (erro: unknown) => {
          this.erro.set(this.extrairMensagemErro(erro));
        }
      });
  }

  private extrairMensagemErro(erro: unknown): string {
    if (erro instanceof TimeoutError) {
      return 'Não foi possível concluir o cadastro agora. Verifique sua conexão e tente novamente.';
    }

    if (!(erro instanceof HttpErrorResponse)) {
      return 'Não foi possível criar a conta agora. Revise os dados e tente novamente.';
    }

    const mensagem = this.extrairMensagemApi(erro.error);
    if (this.ehEmailJaCadastrado(erro.status, mensagem)) {
      return 'Já existe uma conta com esse e-mail.';
    }

    return mensagem || 'Não foi possível criar a conta agora. Revise os dados e tente novamente.';
  }

  private extrairMensagemApi(error: unknown): string {
    if (typeof error === 'string') {
      return error;
    }

    if (error && typeof error === 'object' && 'mensagens' in error) {
      const mensagens = (error as { mensagens?: unknown }).mensagens;
      if (Array.isArray(mensagens) && mensagens.length > 0) {
        return String(mensagens[0]);
      }
    }

    return '';
  }

  private ehEmailJaCadastrado(status: number, mensagem: string): boolean {
    return status === 409
      || (status === 400 && /e-?mail|email/i.test(mensagem) && /existe|cadastrad/i.test(mensagem));
  }
}
