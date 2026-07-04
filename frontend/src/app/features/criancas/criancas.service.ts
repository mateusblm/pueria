import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { AtualizarCriancaRequest, Crianca, CriarCriancaRequest } from '../../shared/models/crianca.model';

@Injectable({ providedIn: 'root' })
export class CriancasService {
  private readonly tempoLimiteRequisicaoMs = 10000;

  constructor(private readonly http: HttpClient) {}

  listar(): Observable<Crianca[]> {
    return this.http.get<Crianca[]>('/api/criancas')
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  criar(request: CriarCriancaRequest): Observable<Crianca> {
    return this.http.post<Crianca>('/api/criancas', request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  buscarPorId(id: string): Observable<Crianca> {
    return this.http.get<Crianca>(`/api/criancas/${id}`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  atualizar(id: string, request: AtualizarCriancaRequest): Observable<Crianca> {
    return this.http.put<Crianca>(`/api/criancas/${id}`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  remover(id: string): Observable<void> {
    return this.http.delete<void>(`/api/criancas/${id}`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }
}
