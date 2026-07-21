import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type EstadoAreaHome = 'SEM_REGISTROS' | 'INICIAL' | 'EM_DIA' | 'ACOMPANHAR' | 'ATENCAO';
export type ModuloHome = 'CRESCIMENTO' | 'NEURODESENVOLVIMENTO' | 'SONO' | 'ALIMENTACAO' | 'HUMOR' | 'TELAS' | 'TRANSITO_INTESTINAL' | 'SAUDE';

export interface AreaResumoHome {
  modulo: ModuloHome;
  estado: EstadoAreaHome;
  resumo: string;
  quantidadeRegistros: number;
  pontosAtencao: number;
  acao: 'VER' | 'REGISTRAR' | 'COMECAR';
}

export interface ResumoHome {
  estado: 'INICIAL' | 'ACOMPANHAR' | 'ATENCAO' | 'TRANQUILO';
  areas: AreaResumoHome[];
}

@Injectable({ providedIn: 'root' })
export class ResumoHomeService {
  private readonly http = inject(HttpClient);

  carregar(criancaId: string): Observable<ResumoHome> {
    return this.http.get<ResumoHome>(`/api/criancas/${criancaId}/home/resumo`);
  }
}
