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
