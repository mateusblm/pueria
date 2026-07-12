import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize, TimeoutError } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';

@Component({ selector: 'app-redefinir-senha', imports: [ReactiveFormsModule, RouterLink], templateUrl: './redefinir-senha.component.html', styleUrl: '../login/login.component.scss' })
export class RedefinirSenhaComponent {
  private readonly formBuilder = inject(FormBuilder); private readonly auth = inject(AuthService); private readonly route = inject(ActivatedRoute); private readonly router = inject(Router);
  readonly carregando = signal(false); readonly concluido = signal(false); readonly erro = signal(''); readonly token = this.route.snapshot.queryParamMap.get('token') ?? '';
  readonly form = this.formBuilder.nonNullable.group({ senha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]], confirmarSenha: ['', [Validators.required]] });
  redefinir(): void {
    if (this.carregando()) return;
    this.erro.set('');
    const valor = this.form.getRawValue();
    if (!this.token) { this.erro.set('Este link de redefinição é inválido ou expirou.'); return; }
    if (this.form.invalid || valor.senha !== valor.confirmarSenha) { this.form.markAllAsTouched(); this.erro.set(valor.senha !== valor.confirmarSenha ? 'As senhas informadas não coincidem.' : 'A nova senha deve possuir ao menos 8 caracteres.'); return; }
    this.carregando.set(true);
    this.auth.redefinirSenha({ token: this.token, novaSenha: valor.senha }).pipe(finalize(() => this.carregando.set(false))).subscribe({ next: () => { this.concluido.set(true); setTimeout(() => void this.router.navigateByUrl('/login'), 1600); }, error: (erro: unknown) => this.erro.set(erro instanceof TimeoutError ? 'A conexão demorou mais que o esperado. Tente novamente.' : erro instanceof HttpErrorResponse && erro.status === 400 ? 'Este link de redefinição é inválido ou expirou.' : 'Não foi possível redefinir a senha agora. Tente novamente.') });
  }
}
