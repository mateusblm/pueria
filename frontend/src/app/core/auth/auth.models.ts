export interface CadastroUsuarioRequest {
  nome: string;
  email: string;
  senha: string;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface SolicitarRedefinicaoSenhaRequest { email: string; }
export interface RedefinirSenhaRequest { token: string; novaSenha: string; }

export interface AuthResponse {
  tipo: 'Bearer';
  token: string;
  expiraEmSegundos: number;
}
