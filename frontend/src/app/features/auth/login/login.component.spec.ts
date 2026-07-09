import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { AuthService } from '../../../core/auth/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let authService: { login: ReturnType<typeof vi.fn> };
  let router: { navigateByUrl: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    authService = { login: vi.fn() };
    router = { navigateByUrl: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: {} }
      ]
    }).compileComponents();
  });

  function criarComponente(): LoginComponent {
    return TestBed.createComponent(LoginComponent).componentInstance;
  }

  it('alterna a visibilidade da senha', () => {
    const component = criarComponente();

    component.alternarSenha();
    expect(component.senhaVisivel).toBe(true);

    component.alternarSenha();
    expect(component.senhaVisivel).toBe(false);
  });

  it('bloqueia login com campos obrigatórios ausentes', () => {
    const component = criarComponente();

    component.entrar();

    expect(authService.login).not.toHaveBeenCalled();
    expect(component.erro).toBe('Informe e-mail e senha para continuar.');
  });

  it('navega para acompanhamento quando o login é válido', () => {
    const component = criarComponente();
    authService.login.mockReturnValue(of({
      tipo: 'Bearer',
      token: 'token',
      expiraEmSegundos: 3600
    }));
    component.form.setValue({
      email: 'mateus@email.com',
      senha: 'senha1234'
    });

    component.entrar();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'mateus@email.com',
      senha: 'senha1234'
    });
    expect(router.navigateByUrl).toHaveBeenCalledWith('/acompanhamento');
    expect(component.carregando).toBe(false);
  });

  it('exibe erro específico e libera o botão quando as credenciais são inválidas', () => {
    const component = criarComponente();
    authService.login.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 401 })));
    component.form.setValue({
      email: 'mateus@email.com',
      senha: 'senhaerrada'
    });

    component.entrar();

    expect(component.erro).toBe('E-mail ou senha inválidos.');
    expect(component.carregando).toBe(false);
  });
});
