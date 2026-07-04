import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Crianca, CriarCriancaRequest } from '../../shared/models/crianca.model';

@Injectable({
  providedIn: 'root'
})
export class CriancasService {
  constructor(private readonly http: HttpClient) {}

  criar(request: CriarCriancaRequest): Observable<Crianca> {
    return this.http.post<Crianca>('/api/criancas', request);
  }

  buscarPorId(id: string): Observable<Crianca> {
    return this.http.get<Crianca>(`/api/criancas/${id}`);
  }
}
