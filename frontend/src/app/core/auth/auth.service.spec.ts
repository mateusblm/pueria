import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  it('salva o access token recebido no login', () => {
    let resposta: unknown;
    service.login({ email: 'mateus@email.com', senha: 'senha1234' }).subscribe((valor) => (resposta = valor));

    const requisicao = http.expectOne('/api/auth/login');
    expect(requisicao.request.withCredentials).toBe(true);
    expect(requisicao.request.body).toEqual({ email: 'mateus@email.com', senha: 'senha1234' });
    requisicao.flush({ tipo: 'Bearer', token: 'access-1', expiraEmSegundos: 900 });

    expect(resposta).toEqual({ tipo: 'Bearer', token: 'access-1', expiraEmSegundos: 900 });
    expect(service.token).toBe('access-1');
    expect(service.estaAutenticado()).toBe(true);
  });

  it('compartilha uma única renovação quando duas chamadas acontecem juntas', () => {
    const primeira = service.renovarSessao();
    const segunda = service.renovarSessao();
    expect(primeira).toBe(segunda);

    let respostas = 0;
    primeira.subscribe(() => respostas++);
    segunda.subscribe(() => respostas++);

    const requisicoes = http.match('/api/auth/refresh');
    expect(requisicoes).toHaveLength(1);
    expect(requisicoes[0].request.withCredentials).toBe(true);
    requisicoes[0].flush({ tipo: 'Bearer', token: 'access-renovado', expiraEmSegundos: 900 });

    expect(respostas).toBe(2);
    expect(service.token).toBe('access-renovado');
    http.verify();
  });

  it('limpa o token local imediatamente ao sair', () => {
    service.login({ email: 'mateus@email.com', senha: 'senha1234' }).subscribe();
    http.expectOne('/api/auth/login').flush({ tipo: 'Bearer', token: 'access-1', expiraEmSegundos: 900 });

    service.sair();

    expect(service.token).toBeNull();
    const requisicao = http.expectOne('/api/auth/logout');
    expect(requisicao.request.withCredentials).toBe(true);
    requisicao.flush(null);
  });

  it('envia o pedido de recuperação de senha sem credenciais de sessão', () => {
    service.solicitarRedefinicaoSenha({ email: 'mateus@email.com' }).subscribe();

    const requisicao = http.expectOne('/api/auth/recuperar-senha');
    expect(requisicao.request.withCredentials).toBe(false);
    expect(requisicao.request.body).toEqual({ email: 'mateus@email.com' });
    requisicao.flush(null);
  });
});
