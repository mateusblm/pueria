import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { RegistroTelas, SalvarRegistroTelasRequest } from '../../shared/models/telas.model';

@Injectable({ providedIn: 'root' })
export class TelasService {
  private readonly tempoLimiteRequisicaoMs = 10000;

  constructor(private readonly http: HttpClient) {}

  listar(criancaId: string): Observable<RegistroTelas[]> {
    return this.http.get<RegistroTelas[]>(`/api/criancas/${criancaId}/telas/registros`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  registrar(criancaId: string, request: SalvarRegistroTelasRequest): Observable<RegistroTelas> {
    return this.http.post<RegistroTelas>(`/api/criancas/${criancaId}/telas/registros`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  atualizar(criancaId: string, registroId: string, request: SalvarRegistroTelasRequest): Observable<RegistroTelas> {
    return this.http.put<RegistroTelas>(`/api/criancas/${criancaId}/telas/registros/${registroId}`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }
}
