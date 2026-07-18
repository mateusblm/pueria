import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, Subject, throwError, TimeoutError } from 'rxjs';
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
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: {
                get: () => null
              }
            }
          }
        }
      ]
    }).compileComponents();
  });

  function criarComponente(): LoginComponent {
    return TestBed.createComponent(LoginComponent).componentInstance;
  }

  it('alterna a visibilidade da senha', () => {
    const component = criarComponente();

    component.alternarSenha();
    expect(component.senhaVisivel()).toBe(true);

    component.alternarSenha();
    expect(component.senhaVisivel()).toBe(false);
  });

  it('bloqueia login com campos obrigatórios ausentes', () => {
    const component = criarComponente();

    component.entrar();

    expect(authService.login).not.toHaveBeenCalled();
    expect(component.erro()).toBe('Informe e-mail e senha para continuar.');
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
    expect(component.carregando()).toBe(false);
  });

  it('exibe erro específico e libera o botão quando as credenciais são inválidas', () => {
    const component = criarComponente();
    authService.login.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 401 })));
    component.form.setValue({
      email: 'mateus@email.com',
      senha: 'senhaerrada'
    });

    component.entrar();

    expect(component.erro()).toBe('E-mail ou senha inválidos.');
    expect(component.carregando()).toBe(false);
  });

  it('atualiza a tela quando a resposta de credenciais inválidas chega depois do envio', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    const resposta = new Subject<never>();
    authService.login.mockReturnValue(resposta);
    component.form.setValue({
      email: 'conta.inexistente@email.com',
      senha: 'senha1234'
    });
    fixture.detectChanges();

    component.entrar();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('button[type="submit"]').textContent).toContain('Entrando...');

    resposta.error(new HttpErrorResponse({ status: 401 }));
    fixture.detectChanges();

    expect(component.carregando()).toBe(false);
    expect(fixture.nativeElement.querySelector('.pueria-message--erro').textContent).toContain('E-mail ou senha inválidos.');
    expect(fixture.nativeElement.querySelector('button[type="submit"]').textContent).toContain('Entrar');
  });

  it('libera o botão quando a tentativa de login expira', () => {
    const component = criarComponente();
    authService.login.mockReturnValue(throwError(() => new TimeoutError()));
    component.form.setValue({
      email: 'mateus@email.com',
      senha: 'senha1234'
    });

    component.entrar();

    expect(component.erro()).toBe('A conexão demorou mais que o esperado. Verifique sua internet e tente novamente.');
    expect(component.carregando()).toBe(false);
  });

  it('ignora novo envio enquanto o login está em andamento', () => {
    const component = criarComponente();
    component.carregando.set(true);
    component.form.setValue({
      email: 'mateus@email.com',
      senha: 'senha1234'
    });

    component.entrar();

    expect(authService.login).not.toHaveBeenCalled();
  });
});
