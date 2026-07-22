import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { ConvidarCuidadorRequest, Cuidador } from '../../shared/models/cuidador.model';

@Injectable({ providedIn: 'root' })
export class CuidadoresService {
  constructor(private readonly http: HttpClient) {}
  listar(criancaId: string): Observable<Cuidador[]> { return this.http.get<Cuidador[]>(`/api/criancas/${criancaId}/cuidadores`).pipe(timeout(10000)); }
  convidar(criancaId: string, request: ConvidarCuidadorRequest): Observable<Cuidador> { return this.http.post<Cuidador>(`/api/criancas/${criancaId}/cuidadores`, request).pipe(timeout(10000)); }
  remover(criancaId: string, vinculoId: string): Observable<void> { return this.http.delete<void>(`/api/criancas/${criancaId}/cuidadores/${vinculoId}`).pipe(timeout(10000)); }
}
