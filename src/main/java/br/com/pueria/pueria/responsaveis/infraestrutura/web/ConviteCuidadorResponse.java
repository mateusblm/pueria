package br.com.pueria.pueria.responsaveis.infraestrutura.web;

import br.com.pueria.pueria.responsaveis.aplicacao.GerenciarCuidadoresUseCase.ConviteResumo;
import br.com.pueria.pueria.responsaveis.dominio.Parentesco;
import java.util.UUID;

public record ConviteCuidadorResponse(UUID id, UUID criancaId, String nomeCrianca, String convidadoPor, Parentesco parentesco) {
    static ConviteCuidadorResponse de(ConviteResumo convite) { return new ConviteCuidadorResponse(convite.id(), convite.criancaId(), convite.nomeCrianca(), convite.convidadoPor(), convite.parentesco()); }
}
