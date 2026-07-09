import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-cadastro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.scss'
})
export class CadastroComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  carregando = false;
  erro = '';
  senhaVisivel = false;
  confirmarSenhaVisivel = false;

  readonly form = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(150)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    senha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
    confirmarSenha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]]
  });

  alternarSenha(): void {
    this.senhaVisivel = !this.senhaVisivel;
  }

  alternarConfirmarSenha(): void {
    this.confirmarSenhaVisivel = !this.confirmarSenhaVisivel;
  }

  cadastrar(): void {
    this.erro = '';
    if (this.form.controls.confirmarSenha.hasError('senhasDiferentes')) {
      this.form.controls.confirmarSenha.setErrors(null);
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro = 'Preencha os dados obrigatórios para criar sua conta.';
      return;
    }

    const valor = this.form.getRawValue();
    if (valor.senha !== valor.confirmarSenha) {
      this.form.controls.confirmarSenha.setErrors({ senhasDiferentes: true });
      this.erro = 'As senhas informadas não coincidem.';
      return;
    }

    this.carregando = true;

    this.authService.cadastrar({
      nome: valor.nome,
      email: valor.email,
      senha: valor.senha
    })
      .pipe(finalize(() => {
        this.carregando = false;
      }))
      .subscribe({
        next: () => void this.router.navigate(['/login'], {
          queryParams: { cadastro: 'realizado' }
        }),
        error: (erro: HttpErrorResponse) => {
          this.erro = this.extrairMensagemErro(erro);
        }
      });
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagens = erro.error?.mensagens;
    const mensagem = Array.isArray(mensagens) && mensagens.length > 0 ? String(mensagens[0]) : '';

    if (erro.status === 409 || (/e-?mail|email/i.test(mensagem) && /existe|cadastrad/i.test(mensagem))) {
      return 'Já existe uma conta com esse e-mail.';
    }

    return mensagem || 'Não foi possível criar a conta agora. Revise os dados e tente novamente.';
  }
}
