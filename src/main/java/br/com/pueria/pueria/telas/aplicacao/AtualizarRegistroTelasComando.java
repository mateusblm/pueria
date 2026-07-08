package br.com.pueria.pueria.telas.aplicacao;

import br.com.pueria.pueria.telas.dominio.DadosTelas;

import java.util.UUID;

public record AtualizarRegistroTelasComando(UUID criancaId, UUID registroId, String emailResponsavel, DadosTelas dados) {}
