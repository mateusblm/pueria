package br.com.pueria.pueria.usuarios.dominio;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TokenRedefinicaoSenhaRepositorio {
    TokenRedefinicaoSenha salvar(TokenRedefinicaoSenha token);
    Optional<TokenRedefinicaoSenha> buscarPorHash(String tokenHash);
    boolean existeSolicitacaoDesde(UUID usuarioId, LocalDateTime inicio);
    void invalidarAtivosDoUsuario(UUID usuarioId);
}
