package br.com.pueria.pueria.usuarios.infraestrutura.persistencia;

import br.com.pueria.pueria.usuarios.dominio.TokenRedefinicaoSenha;
import br.com.pueria.pueria.usuarios.dominio.TokenRedefinicaoSenhaRepositorio;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TokenRedefinicaoSenhaRepositorioJpa implements TokenRedefinicaoSenhaRepositorio {
    private final TokenRedefinicaoSenhaJpaRepository repository;
    public TokenRedefinicaoSenhaRepositorioJpa(TokenRedefinicaoSenhaJpaRepository repository) { this.repository = repository; }

    @Override public TokenRedefinicaoSenha salvar(TokenRedefinicaoSenha token) { return paraDominio(repository.save(paraJpa(token))); }
    @Override public Optional<TokenRedefinicaoSenha> buscarPorHash(String tokenHash) { return repository.findByTokenHash(tokenHash).map(this::paraDominio); }
    @Override public boolean existeSolicitacaoDesde(UUID usuarioId, LocalDateTime inicio) { return repository.existsByUsuarioIdAndCriadoEmAfter(usuarioId, inicio); }
    @Override public void invalidarAtivosDoUsuario(UUID usuarioId) { repository.saveAll(repository.findByUsuarioIdAndUsadoEmIsNull(usuarioId).stream().map(this::paraDominio).map(TokenRedefinicaoSenha::marcarComoUsado).map(this::paraJpa).toList()); }

    private TokenRedefinicaoSenhaJpaEntidade paraJpa(TokenRedefinicaoSenha token) { return new TokenRedefinicaoSenhaJpaEntidade(token.id(), token.usuarioId(), token.tokenHash(), token.expiraEm(), token.usadoEm(), token.criadoEm()); }
    private TokenRedefinicaoSenha paraDominio(TokenRedefinicaoSenhaJpaEntidade token) { return new TokenRedefinicaoSenha(token.getId(), token.getUsuarioId(), token.getTokenHash(), token.getExpiraEm(), token.getUsadoEm(), token.getCriadoEm()); }
}
