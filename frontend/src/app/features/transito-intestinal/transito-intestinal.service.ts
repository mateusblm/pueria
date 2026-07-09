import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RegistroTransitoIntestinal, SalvarRegistroTransitoIntestinalRequest } from '../../shared/models/transito-intestinal.model';

@Injectable({ providedIn: 'root' })
export class TransitoIntestinalService {
  constructor(private readonly http: HttpClient) {}

  listar(criancaId: string): Observable<RegistroTransitoIntestinal[]> {
    return this.http.get<RegistroTransitoIntestinal[]>(`/api/criancas/${criancaId}/transito-intestinal/registros`);
  }

  registrar(criancaId: string, request: SalvarRegistroTransitoIntestinalRequest): Observable<RegistroTransitoIntestinal> {
    return this.http.post<RegistroTransitoIntestinal>(`/api/criancas/${criancaId}/transito-intestinal/registros`, request);
  }

  atualizar(criancaId: string, registroId: string, request: SalvarRegistroTransitoIntestinalRequest): Observable<RegistroTransitoIntestinal> {
    return this.http.put<RegistroTransitoIntestinal>(`/api/criancas/${criancaId}/transito-intestinal/registros/${registroId}`, request);
  }
}
