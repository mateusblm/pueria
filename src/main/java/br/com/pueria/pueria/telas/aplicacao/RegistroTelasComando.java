package br.com.pueria.pueria.telas.aplicacao;

import br.com.pueria.pueria.telas.dominio.DadosTelas;

import java.util.UUID;

public record RegistroTelasComando(UUID criancaId, String emailResponsavel, DadosTelas dados) {}
