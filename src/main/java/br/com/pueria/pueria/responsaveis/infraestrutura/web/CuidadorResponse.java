package br.com.pueria.pueria.responsaveis.infraestrutura.web;

import br.com.pueria.pueria.responsaveis.aplicacao.GerenciarCuidadoresUseCase.CuidadorResumo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;

import java.util.UUID;

public record CuidadorResponse(UUID id, String nome, String email, Parentesco parentesco, boolean principal) {
    static CuidadorResponse de(CuidadorResumo cuidador) {
        return new CuidadorResponse(cuidador.id(), cuidador.nome(), cuidador.email(), cuidador.parentesco(), cuidador.principal());
    }
}
