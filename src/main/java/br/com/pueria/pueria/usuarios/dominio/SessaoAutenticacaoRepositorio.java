package br.com.pueria.pueria.usuarios.dominio;

import java.util.Optional;
import java.util.UUID;

public interface SessaoAutenticacaoRepositorio {
    SessaoAutenticacao salvar(SessaoAutenticacao sessao);
    Optional<SessaoAutenticacao> buscarPorHash(String tokenHash);
    void revogarSessoesAtivasDoUsuario(UUID usuarioId);
}
