import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { RegistroSaude, SalvarRegistroSaudeRequest } from '../../shared/models/saude.model';

@Injectable({ providedIn: 'root' })
export class SaudeService {
  private readonly tempoLimiteRequisicaoMs = 10000;

  constructor(private readonly http: HttpClient) { }

  listar(criancaId: string): Observable<RegistroSaude[]> {
    return this.http.get<RegistroSaude[]>(`/api/criancas/${criancaId}/saude/registros`).pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  registrar(criancaId: string, request: SalvarRegistroSaudeRequest): Observable<RegistroSaude> {
    return this.http.post<RegistroSaude>(`/api/criancas/${criancaId}/saude/registros`, request).pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  atualizar(criancaId: string, registroId: string, request: SalvarRegistroSaudeRequest): Observable<RegistroSaude> {
    return this.http.put<RegistroSaude>(`/api/criancas/${criancaId}/saude/registros/${registroId}`, request).pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  remover(criancaId: string, registroId: string): Observable<void> {
    return this.http.delete<void>(`/api/criancas/${criancaId}/saude/registros/${registroId}`).pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }
}
