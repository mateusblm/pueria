import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { RegistroSono, SalvarRegistroSonoRequest } from '../../shared/models/sono.model';

@Injectable({ providedIn: 'root' })
export class SonoService {
  private readonly tempoLimiteRequisicaoMs = 10000;

  constructor(private readonly http: HttpClient) {}

  listar(criancaId: string): Observable<RegistroSono[]> {
    return this.http.get<RegistroSono[]>(`/api/criancas/${criancaId}/sono/registros`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  registrar(criancaId: string, request: SalvarRegistroSonoRequest): Observable<RegistroSono> {
    return this.http.post<RegistroSono>(`/api/criancas/${criancaId}/sono/registros`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  atualizar(criancaId: string, registroId: string, request: SalvarRegistroSonoRequest): Observable<RegistroSono> {
    return this.http.put<RegistroSono>(`/api/criancas/${criancaId}/sono/registros/${registroId}`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }
}
