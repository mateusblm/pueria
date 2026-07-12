import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { MarcoDesenvolvimento, RelatoDesenvolvimento, RegistrarMarcoDesenvolvimentoRequest, RegistrarRelatoDesenvolvimentoRequest } from '../../shared/models/desenvolvimento.model';

@Injectable({ providedIn: 'root' })
export class DesenvolvimentoService {
  private readonly tempoLimiteRequisicaoMs = 10000;

  constructor(private readonly http: HttpClient) {}

  listarMarcos(criancaId: string): Observable<MarcoDesenvolvimento[]> {
    return this.http.get<MarcoDesenvolvimento[]>(`/api/criancas/${criancaId}/desenvolvimento/marcos`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  registrarMarco(criancaId: string, marcoId: string, request: RegistrarMarcoDesenvolvimentoRequest): Observable<void> {
    return this.http.put<void>(`/api/criancas/${criancaId}/desenvolvimento/marcos/${marcoId}`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  listarRelatos(criancaId: string): Observable<RelatoDesenvolvimento[]> {
    return this.http.get<RelatoDesenvolvimento[]>(`/api/criancas/${criancaId}/desenvolvimento/relatos`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  registrarRelato(criancaId: string, request: RegistrarRelatoDesenvolvimentoRequest): Observable<RelatoDesenvolvimento> {
    return this.http.post<RelatoDesenvolvimento>(`/api/criancas/${criancaId}/desenvolvimento/relatos`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  removerRelato(criancaId: string, relatoId: string): Observable<void> {
    return this.http.delete<void>(`/api/criancas/${criancaId}/desenvolvimento/relatos/${relatoId}`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }
}
