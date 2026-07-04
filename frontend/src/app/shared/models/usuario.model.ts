export interface Usuario {
  id: string;
  nome: string;
  email: string;
  tipo: 'RESPONSAVEL' | 'PROFISSIONAL' | 'ADMINISTRADOR';
}
