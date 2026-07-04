import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

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

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]]
  });

  entrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.carregando = true;
    this.erro = '';

    this.authService.login(this.form.getRawValue()).subscribe({
      next: () => void this.router.navigateByUrl('/app/criancas'),
      error: (erro: HttpErrorResponse) => {
        this.erro = erro.status === 401
          ? 'E-mail ou senha inválidos.'
          : 'Não foi possível entrar agora. Tente novamente.';
        this.carregando = false;
      },
      complete: () => {
        this.carregando = false;
      }
    });
  }
}
