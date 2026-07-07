import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { RegistroAlimentacao, SalvarRegistroAlimentacaoRequest } from '../../shared/models/alimentacao.model';

@Injectable({ providedIn: 'root' })
export class AlimentacaoService {
  private readonly tempoLimiteRequisicaoMs = 10000;

  constructor(private readonly http: HttpClient) {}

  listar(criancaId: string): Observable<RegistroAlimentacao[]> {
    return this.http.get<RegistroAlimentacao[]>(`/api/criancas/${criancaId}/alimentacao/registros`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  registrar(criancaId: string, request: SalvarRegistroAlimentacaoRequest): Observable<RegistroAlimentacao> {
    return this.http.post<RegistroAlimentacao>(`/api/criancas/${criancaId}/alimentacao/registros`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  atualizar(criancaId: string, registroId: string, request: SalvarRegistroAlimentacaoRequest): Observable<RegistroAlimentacao> {
    return this.http.put<RegistroAlimentacao>(`/api/criancas/${criancaId}/alimentacao/registros/${registroId}`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }
}
