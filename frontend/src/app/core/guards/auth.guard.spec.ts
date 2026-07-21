import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { firstValueFrom, of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { AuthService } from '../auth/auth.service';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let authService: {
    estaAutenticado: ReturnType<typeof vi.fn>;
    renovarSessao: ReturnType<typeof vi.fn>;
    limparSessaoLocal: ReturnType<typeof vi.fn>;
  };
  let router: { parseUrl: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authService = {
      estaAutenticado: vi.fn(),
      renovarSessao: vi.fn(),
      limparSessaoLocal: vi.fn()
    };
    router = { parseUrl: vi.fn(() => '/login') };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });
  });

  it('permite acesso imediato quando existe access token', () => {
    authService.estaAutenticado.mockReturnValue(true);

    const resultado = TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));

    expect(resultado).toBe(true);
    expect(authService.renovarSessao).not.toHaveBeenCalled();
  });

  it('renova a sessão antes de permitir acesso sem token local', async () => {
    authService.estaAutenticado.mockReturnValue(false);
    authService.renovarSessao.mockReturnValue(of({ token: 'novo-token' }));

    const resultado = TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));

    expect(await firstValueFrom(resultado as ReturnType<typeof of>)).toBe(true);
    expect(authService.renovarSessao).toHaveBeenCalledOnce();
  });

  it('limpa a sessão e redireciona quando a renovação falha', async () => {
    authService.estaAutenticado.mockReturnValue(false);
    authService.renovarSessao.mockReturnValue(throwError(() => new Error('sessão inválida')));

    const resultado = TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));

    expect(await firstValueFrom(resultado as ReturnType<typeof of>)).toBe('/login');
    expect(authService.limparSessaoLocal).toHaveBeenCalledOnce();
    expect(router.parseUrl).toHaveBeenCalledWith('/login');
  });
});
