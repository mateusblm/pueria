package br.com.pueria.pueria.sono.aplicacao;

import br.com.pueria.pueria.sono.dominio.DadosSono;

import java.util.UUID;

public record AtualizarRegistroSonoComando(UUID criancaId, UUID registroId, String emailResponsavel, DadosSono dados) {}
