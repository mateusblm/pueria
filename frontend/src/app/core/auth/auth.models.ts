export interface CadastroUsuarioRequest {
  nome: string;
  email: string;
  senha: string;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface AuthResponse {
  tipo: 'Bearer';
  token: string;
  expiraEmSegundos: number;
}
