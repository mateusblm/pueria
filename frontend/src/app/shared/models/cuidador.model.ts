import { Parentesco } from './crianca.model';

export interface Cuidador {
  id: string;
  nome: string;
  email: string;
  parentesco: Parentesco;
  principal: boolean;
}

export interface ConvidarCuidadorRequest {
  email: string;
  parentesco?: Parentesco;
}

export interface ConviteCuidador {
  id: string;
  criancaId: string;
  nomeCrianca: string;
  convidadoPor: string;
  parentesco: Parentesco;
}
