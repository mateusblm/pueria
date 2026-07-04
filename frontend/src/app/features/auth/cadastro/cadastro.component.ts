import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

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

  readonly form = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(150)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    senha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]]
  });

  cadastrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.carregando = true;
    this.erro = '';

    this.authService.cadastrar(this.form.getRawValue()).subscribe({
      next: () => void this.router.navigate(['/login'], {
        queryParams: { cadastro: 'realizado' }
      }),
      error: (erro: HttpErrorResponse) => {
        this.erro = erro.status === 409
          ? 'Já existe uma conta com esse e-mail.'
          : 'Não foi possível criar a conta agora. Revise os dados e tente novamente.';
        this.carregando = false;
      },
      complete: () => {
        this.carregando = false;
      }
    });
  }
}
