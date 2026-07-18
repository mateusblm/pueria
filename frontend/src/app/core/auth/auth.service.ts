import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, finalize, shareReplay, tap, timeout } from 'rxjs';

import { Usuario } from '../../shared/models/usuario.model';
import { AuthResponse, CadastroUsuarioRequest, LoginRequest, RedefinirSenhaRequest, SolicitarRedefinicaoSenhaRequest } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenSignal = signal<string | null>(null);
  private renovacaoEmAndamento$?: Observable<AuthResponse>;

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
    return this.http.post<AuthResponse>('/api/auth/login', request, { withCredentials: true }).pipe(
      timeout(15000),
      tap((response) => this.salvarToken(response.token))
    );
  }

  renovarSessao(): Observable<AuthResponse> {
    if (!this.renovacaoEmAndamento$) {
      this.renovacaoEmAndamento$ = this.http
        .post<AuthResponse>('/api/auth/refresh', {}, { withCredentials: true })
        .pipe(
          timeout(15000),
          tap((response) => this.salvarToken(response.token)),
          finalize(() => (this.renovacaoEmAndamento$ = undefined)),
          shareReplay({ bufferSize: 1, refCount: false })
        );
    }

    return this.renovacaoEmAndamento$;
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
    this.limparSessaoLocal();
    this.http.post<void>('/api/auth/logout', {}, { withCredentials: true }).pipe(timeout(15000)).subscribe({ error: () => undefined });
  }

  limparSessaoLocal(): void {
    this.tokenSignal.set(null);
  }

  private salvarToken(token: string): void {
    this.tokenSignal.set(token);
  }
}
