import { HttpErrorResponse } from '@angular/common/http';
import { describe, expect, it } from 'vitest';

import { mensagemErroHttp } from './mensagem-erro';

describe('mensagemErroHttp', () => {
  it('combina mensagens de validação do backend', () => {
    const erro = new HttpErrorResponse({ status: 400, error: { mensagens: ['A data é obrigatória.', 'O valor deve ser positivo.'] } });
    expect(mensagemErroHttp(erro, 'Revise os dados.')).toBe('A data é obrigatória. O valor deve ser positivo.');
  });

  it('ignora itens não textuais da lista de mensagens', () => {
    const erro = new HttpErrorResponse({ status: 400, error: { mensagens: [null, '', 'Campo inválido.'] } });
    expect(mensagemErroHttp(erro, 'Revise os dados.')).toBe('Campo inválido.');
  });

  it('usa o erro legível quando não há lista de mensagens', () => {
    const erro = new HttpErrorResponse({ status: 409, error: { erro: 'Registro duplicado.' } });
    expect(mensagemErroHttp(erro, 'Não foi possível salvar.')).toBe('Registro duplicado.');
  });

  it('usa fallback para erro técnico ou desconhecido', () => {
    expect(mensagemErroHttp(new Error('falha técnica'), 'Tente novamente.')).toBe('Tente novamente.');
  });
});
