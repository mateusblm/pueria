import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { MarcoDesenvolvimento, RegistrarMarcoDesenvolvimentoRequest } from '../../shared/models/desenvolvimento.model';

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
}
