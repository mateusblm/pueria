import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { AvaliacaoCurvaCrescimento, MedidaCrescimento, SalvarMedidaCrescimentoRequest } from '../../shared/models/crescimento.model';

@Injectable({ providedIn: 'root' })
export class CrescimentoService {
  private readonly tempoLimiteRequisicaoMs = 10000;

  constructor(private readonly http: HttpClient) {}

  listar(criancaId: string): Observable<MedidaCrescimento[]> {
    return this.http.get<MedidaCrescimento[]>(`/api/criancas/${criancaId}/crescimento/medidas`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  listarCurvas(criancaId: string): Observable<AvaliacaoCurvaCrescimento[]> {
    return this.http.get<AvaliacaoCurvaCrescimento[]>(`/api/criancas/${criancaId}/crescimento/medidas/curvas`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  registrar(criancaId: string, request: SalvarMedidaCrescimentoRequest): Observable<MedidaCrescimento> {
    return this.http.post<MedidaCrescimento>(`/api/criancas/${criancaId}/crescimento/medidas`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  atualizar(criancaId: string, medidaId: string, request: SalvarMedidaCrescimentoRequest): Observable<MedidaCrescimento> {
    return this.http.put<MedidaCrescimento>(`/api/criancas/${criancaId}/crescimento/medidas/${medidaId}`, request)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  remover(criancaId: string, medidaId: string): Observable<void> {
    return this.http.delete<void>(`/api/criancas/${criancaId}/crescimento/medidas/${medidaId}`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }
}
