package br.com.pueria.pueria.alimentacao.aplicacao;

import br.com.pueria.pueria.alimentacao.dominio.DadosAlimentacao;

import java.util.UUID;

public record RegistroAlimentacaoComando(
        UUID criancaId,
        String emailResponsavel,
        DadosAlimentacao dados
) {}
