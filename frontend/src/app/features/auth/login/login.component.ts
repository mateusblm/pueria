import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize, TimeoutError } from 'rxjs';

import { AuthService } from '../../../core/auth/auth.service';
import { AppIconComponent } from '../../../shared/components/app-icon/app-icon.component';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, AppIconComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly carregando = signal(false);
  readonly erro = signal('');
  readonly senhaVisivel = signal(false);

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]]
  });

  alternarSenha(): void {
    this.senhaVisivel.update((visivel) => !visivel);
  }

  entrar(): void {
    if (this.carregando()) {
      return;
    }

    this.erro.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.erro.set('Informe e-mail e senha para continuar.');
      return;
    }

    this.carregando.set(true);

    this.authService.login(this.form.getRawValue())
      .pipe(finalize(() => {
        this.carregando.set(false);
      }))
      .subscribe({
        next: () => void this.router.navigateByUrl('/acompanhamento'),
        error: (erro: unknown) => {
          if (erro instanceof TimeoutError) {
            this.erro.set('A conexão demorou mais que o esperado. Verifique sua internet e tente novamente.');
            return;
          }

          this.erro.set(erro instanceof HttpErrorResponse && erro.status === 401
            ? 'E-mail ou senha inválidos.'
            : 'Não foi possível entrar agora. Tente novamente.');
        }
      });
  }
}
