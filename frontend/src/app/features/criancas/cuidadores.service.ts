import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { ConvidarCuidadorRequest, ConviteCuidador, Cuidador } from '../../shared/models/cuidador.model';

@Injectable({ providedIn: 'root' })
export class CuidadoresService {
  constructor(private readonly http: HttpClient) {}
  listar(criancaId: string): Observable<Cuidador[]> { return this.http.get<Cuidador[]>(`/api/criancas/${criancaId}/cuidadores`).pipe(timeout(10000)); }
  convidar(criancaId: string, request: ConvidarCuidadorRequest): Observable<ConviteCuidador> { return this.http.post<ConviteCuidador>(`/api/criancas/${criancaId}/cuidadores`, request).pipe(timeout(10000)); }
  remover(criancaId: string, vinculoId: string): Observable<void> { return this.http.delete<void>(`/api/criancas/${criancaId}/cuidadores/${vinculoId}`).pipe(timeout(10000)); }
  listarConvites(): Observable<ConviteCuidador[]> { return this.http.get<ConviteCuidador[]>('/api/convites-cuidadores').pipe(timeout(10000)); }
  aceitarConvite(id: string): Observable<void> { return this.http.post<void>(`/api/convites-cuidadores/${id}/aceitar`, {}).pipe(timeout(10000)); }
  recusarConvite(id: string): Observable<void> { return this.http.post<void>(`/api/convites-cuidadores/${id}/recusar`, {}).pipe(timeout(10000)); }
}
