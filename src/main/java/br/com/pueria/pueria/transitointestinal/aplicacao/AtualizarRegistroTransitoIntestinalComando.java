package br.com.pueria.pueria.transitointestinal.aplicacao;

import br.com.pueria.pueria.transitointestinal.dominio.DadosTransitoIntestinal;

import java.util.UUID;

public record AtualizarRegistroTransitoIntestinalComando(UUID criancaId, UUID registroId, String emailResponsavel, DadosTransitoIntestinal dados) {}
