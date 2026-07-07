package br.com.pueria.pueria.sono.aplicacao;

import br.com.pueria.pueria.sono.dominio.DadosSono;

import java.util.UUID;

public record RegistroSonoComando(UUID criancaId, String emailResponsavel, DadosSono dados) {}
