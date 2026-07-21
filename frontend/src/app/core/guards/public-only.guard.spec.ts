import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { AuthService } from '../auth/auth.service';
import { publicOnlyGuard } from './public-only.guard';

describe('publicOnlyGuard', () => {
  let authService: { estaAutenticado: ReturnType<typeof vi.fn> };
  let router: { parseUrl: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authService = { estaAutenticado: vi.fn() };
    router = { parseUrl: vi.fn(() => '/acompanhamento') };
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });
  });

  it('permite acesso às rotas públicas sem autenticação', () => {
    authService.estaAutenticado.mockReturnValue(false);

    const resultado = TestBed.runInInjectionContext(() => publicOnlyGuard({} as never, {} as never));

    expect(resultado).toBe(true);
  });

  it('redireciona usuário autenticado para acompanhamento', () => {
    authService.estaAutenticado.mockReturnValue(true);

    const resultado = TestBed.runInInjectionContext(() => publicOnlyGuard({} as never, {} as never));

    expect(resultado).toBe('/acompanhamento');
    expect(router.parseUrl).toHaveBeenCalledWith('/acompanhamento');
  });
});
