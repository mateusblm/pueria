import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { AuthService } from '../../../core/auth/auth.service';
import { CadastroComponent } from './cadastro.component';

describe('CadastroComponent', () => {
  let authService: { cadastrar: ReturnType<typeof vi.fn> };
  let router: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    authService = { cadastrar: vi.fn() };
    router = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [CadastroComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: {} }
      ]
    }).compileComponents();
  });

  function criarComponente(): CadastroComponent {
    return TestBed.createComponent(CadastroComponent).componentInstance;
  }

  it('bloqueia cadastro com campos obrigatórios ausentes', () => {
    const component = criarComponente();

    component.cadastrar();

    expect(authService.cadastrar).not.toHaveBeenCalled();
    expect(component.erro).toBe('Preencha os dados obrigatórios para criar sua conta.');
  });

  it('bloqueia cadastro quando as senhas não coincidem', () => {
    const component = criarComponente();
    component.form.setValue({
      nome: 'Mateus',
      email: 'mateus@email.com',
      senha: 'senha1234',
      confirmarSenha: 'outrasenha'
    });

    component.cadastrar();

    expect(authService.cadastrar).not.toHaveBeenCalled();
    expect(component.carregando).toBe(false);
    expect(component.erro).toBe('As senhas informadas não coincidem.');
    expect(component.form.controls.confirmarSenha.hasError('senhasDiferentes')).toBe(true);
  });

  it('envia somente os dados aceitos pela API quando o cadastro é válido', () => {
    const component = criarComponente();
    authService.cadastrar.mockReturnValue(of({
      id: 'usuario-1',
      nome: 'Mateus',
      email: 'mateus@email.com'
    }));
    component.form.setValue({
      nome: 'Mateus',
      email: 'mateus@email.com',
      senha: 'senha1234',
      confirmarSenha: 'senha1234'
    });

    component.cadastrar();

    expect(authService.cadastrar).toHaveBeenCalledWith({
      nome: 'Mateus',
      email: 'mateus@email.com',
      senha: 'senha1234'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { cadastro: 'realizado' }
    });
    expect(component.carregando).toBe(false);
  });

  it('exibe mensagem específica e libera o botão quando o e-mail já está cadastrado', () => {
    const component = criarComponente();
    authService.cadastrar.mockReturnValue(throwError(() => new HttpErrorResponse({
      status: 409,
      error: { mensagens: ['E-mail já cadastrado'] }
    })));
    component.form.setValue({
      nome: 'Mateus',
      email: 'mateus@email.com',
      senha: 'senha1234',
      confirmarSenha: 'senha1234'
    });

    component.cadastrar();

    expect(component.erro).toBe('Já existe uma conta com esse e-mail.');
    expect(component.carregando).toBe(false);
  });
});
