import { HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { AuthService } from '../auth/auth.service';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpTestingController;
  let authService: AuthService;
  let router: { url: string; navigate: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    router = { url: '/acompanhamento', navigate: vi.fn() };
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: Router, useValue: router },
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });

    http = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  it('renova a sessão após 401 e repete a requisição com o novo token', () => {
    let resposta: unknown;
    TestBed.inject(AuthService).usuarioAtual().subscribe((valor) => (resposta = valor));

    http.expectOne('/api/usuarios/me').flush(null, {
      status: 401,
      statusText: 'Unauthorized'
    });

    const refresh = http.expectOne('/api/auth/refresh');
    expect(refresh.request.withCredentials).toBe(true);
    refresh.flush({ tipo: 'Bearer', token: 'token-renovado', expiraEmSegundos: 900 });

    const repeticao = http.expectOne('/api/usuarios/me');
    expect(repeticao.request.headers.get('Authorization')).toBe('Bearer token-renovado');
    repeticao.flush({ id: 'usuario-1', nome: 'Mateus', email: 'mateus@email.com' });

    expect(resposta).toEqual({ id: 'usuario-1', nome: 'Mateus', email: 'mateus@email.com' });
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('não tenta renovar requisições de autenticação que retornam 401', () => {
    let erro: unknown;
    TestBed.inject(AuthService).login({ email: 'mateus@email.com', senha: 'errada' }).subscribe({
      error: (valor) => (erro = valor)
    });

    const requisicao = http.expectOne('/api/auth/login');
    requisicao.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(erro).toBeInstanceOf(HttpErrorResponse);
    expect(http.match('/api/auth/refresh')).toHaveLength(0);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('encerra a sessão e redireciona quando a renovação falha', () => {
    authService.login({ email: 'mateus@email.com', senha: 'senha1234' }).subscribe();
    http.expectOne('/api/auth/login').flush({ tipo: 'Bearer', token: 'token-antigo', expiraEmSegundos: 900 });

    TestBed.inject(AuthService).usuarioAtual().subscribe({ error: () => undefined });
    http.expectOne('/api/usuarios/me').flush(null, {
      status: 401,
      statusText: 'Unauthorized'
    });
    http.expectOne('/api/auth/refresh').flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(authService.token).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { sessao: 'expirada', retorno: '/acompanhamento' }
    });
  });
});
