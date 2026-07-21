import { HttpErrorResponse } from '@angular/common/http';

type CorpoErro = {
  mensagens?: unknown;
  erro?: unknown;
};

export function mensagemErroHttp(erro: unknown, fallback: string): string {
  if (!(erro instanceof HttpErrorResponse)) {
    return fallback;
  }

  const corpo = erro.error as CorpoErro | null;
  if (Array.isArray(corpo?.mensagens)) {
    const mensagens = corpo.mensagens
      .filter((mensagem): mensagem is string => typeof mensagem === 'string' && mensagem.trim().length > 0)
      .map((mensagem) => mensagem.trim());
    if (mensagens.length > 0) {
      return mensagens.join(' ');
    }
  }

  return typeof corpo?.erro === 'string' && corpo.erro.trim().length > 0 ? corpo.erro : fallback;
}
