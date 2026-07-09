import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  carregando = false;
  erro = '';
  senhaVisivel = false;

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]]
  });

  alternarSenha(): void {
    this.senhaVisivel = !this.senhaVisivel;
  }

  entrar(): void {
    this.erro = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro = 'Informe e-mail e senha para continuar.';
      return;
    }

    this.carregando = true;

    this.authService.login(this.form.getRawValue())
      .pipe(finalize(() => {
        this.carregando = false;
      }))
      .subscribe({
        next: () => void this.router.navigateByUrl('/acompanhamento'),
        error: (erro: HttpErrorResponse) => {
          this.erro = erro.status === 401
            ? 'E-mail ou senha inválidos.'
            : 'Não foi possível entrar agora. Tente novamente.';
        }
      });
  }
}
