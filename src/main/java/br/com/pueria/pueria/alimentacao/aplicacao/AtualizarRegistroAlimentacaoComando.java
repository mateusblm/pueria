package br.com.pueria.pueria.alimentacao.aplicacao;

import br.com.pueria.pueria.alimentacao.dominio.DadosAlimentacao;

import java.util.UUID;

public record AtualizarRegistroAlimentacaoComando(
        UUID criancaId,
        UUID registroId,
        String emailResponsavel,
        DadosAlimentacao dados
) {}
