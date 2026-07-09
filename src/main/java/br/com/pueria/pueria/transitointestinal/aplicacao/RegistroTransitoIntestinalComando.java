package br.com.pueria.pueria.transitointestinal.aplicacao;

import br.com.pueria.pueria.transitointestinal.dominio.DadosTransitoIntestinal;

import java.util.UUID;

public record RegistroTransitoIntestinalComando(UUID criancaId, String emailResponsavel, DadosTransitoIntestinal dados) {}
