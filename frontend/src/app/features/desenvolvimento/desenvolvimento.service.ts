import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { EstimuloDesenvolvimento, EventoTrajetoriaDesenvolvimento, MarcoDesenvolvimento, RelatoDesenvolvimento, RegistrarMarcoDesenvolvimentoRequest, RegistrarRelatoDesenvolvimentoRequest } from '../../shared/models/desenvolvimento.model';

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

  listarTrajetoria(criancaId: string): Observable<EventoTrajetoriaDesenvolvimento[]> {
    return this.http.get<EventoTrajetoriaDesenvolvimento[]>(`/api/criancas/${criancaId}/desenvolvimento/trajetoria`)
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

  listarEstimulos(criancaId: string): Observable<EstimuloDesenvolvimento[]> {
    return this.http.get<EstimuloDesenvolvimento[]>(`/api/criancas/${criancaId}/desenvolvimento/estimulos`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  buscarEstimuloParaMarco(criancaId: string, marcoId: string): Observable<EstimuloDesenvolvimento> {
    return this.http.get<EstimuloDesenvolvimento>(`/api/criancas/${criancaId}/desenvolvimento/estimulos/marcos/${marcoId}`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  registrarEstimulo(criancaId: string, estimuloId: string, observacao?: string | null): Observable<void> {
    return this.http.put<void>(`/api/criancas/${criancaId}/desenvolvimento/estimulos/${estimuloId}`, { observacao: observacao ?? null })
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  listarHistoricoEstimulos(criancaId: string): Observable<EstimuloDesenvolvimento[]> {
    return this.http.get<EstimuloDesenvolvimento[]>(`/api/criancas/${criancaId}/desenvolvimento/estimulos/historico`)
      .pipe(timeout({ first: this.tempoLimiteRequisicaoMs }));
  }

  gerarResumoConsulta(criancaId: string): Observable<Blob> { return this.http.get(`/api/criancas/${criancaId}/relatorio-consulta`, { responseType: 'blob' }).pipe(timeout({ first: this.tempoLimiteRequisicaoMs })); }
}
