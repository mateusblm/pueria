import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap, timeout } from 'rxjs';

import { Usuario } from '../../shared/models/usuario.model';
import { AuthResponse, CadastroUsuarioRequest, LoginRequest, RedefinirSenhaRequest, SolicitarRedefinicaoSenhaRequest } from './auth.models';

const TOKEN_STORAGE_KEY = 'pueria.token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenSignal = signal<string | null>(localStorage.getItem(TOKEN_STORAGE_KEY));

  constructor(private readonly http: HttpClient) {}

  get token(): string | null {
    return this.tokenSignal();
  }

  estaAutenticado(): boolean {
    return Boolean(this.tokenSignal());
  }

  cadastrar(request: CadastroUsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>('/api/auth/cadastro', request).pipe(timeout(15000));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', request).pipe(
      timeout(15000),
      tap((response) => this.salvarToken(response.token))
    );
  }

  solicitarRedefinicaoSenha(request: SolicitarRedefinicaoSenhaRequest): Observable<void> {
    return this.http.post<void>('/api/auth/recuperar-senha', request).pipe(timeout(15000));
  }

  redefinirSenha(request: RedefinirSenhaRequest): Observable<void> {
    return this.http.post<void>('/api/auth/redefinir-senha', request).pipe(timeout(15000));
  }

  usuarioAtual(): Observable<Usuario> {
    return this.http.get<Usuario>('/api/usuarios/me');
  }

  sair(): void {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    this.tokenSignal.set(null);
  }

  private salvarToken(token: string): void {
    localStorage.setItem(TOKEN_STORAGE_KEY, token);
    this.tokenSignal.set(token);
  }
}
