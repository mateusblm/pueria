package br.com.pueria.pueria.usuarios.infraestrutura.persistencia;

import br.com.pueria.pueria.usuarios.dominio.SessaoAutenticacao;
import br.com.pueria.pueria.usuarios.dominio.SessaoAutenticacaoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SessaoAutenticacaoRepositorioJpa implements SessaoAutenticacaoRepositorio {

    private final SessaoAutenticacaoJpaRepository repository;

    public SessaoAutenticacaoRepositorioJpa(SessaoAutenticacaoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public SessaoAutenticacao salvar(SessaoAutenticacao sessao) {
        return paraDominio(repository.save(paraJpa(sessao)));
    }

    @Override
    public Optional<SessaoAutenticacao> buscarPorHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(this::paraDominio);
    }

    @Override
    public void revogarSessoesAtivasDoUsuario(UUID usuarioId) {
        var sessoesRevogadas = repository.findByUsuarioIdAndRevogadoEmIsNull(usuarioId).stream()
                .map(this::paraDominio)
                .map(SessaoAutenticacao::revogar)
                .map(this::paraJpa)
                .toList();

        repository.saveAll(sessoesRevogadas);
    }

    private SessaoAutenticacaoJpaEntidade paraJpa(SessaoAutenticacao sessao) {
        return new SessaoAutenticacaoJpaEntidade(
                sessao.id(),
                sessao.usuarioId(),
                sessao.tokenHash(),
                sessao.expiraEm(),
                sessao.revogadoEm(),
                sessao.criadoEm(),
                sessao.ultimoUsoEm()
        );
    }

    private SessaoAutenticacao paraDominio(SessaoAutenticacaoJpaEntidade sessao) {
        return new SessaoAutenticacao(
                sessao.getId(),
                sessao.getUsuarioId(),
                sessao.getTokenHash(),
                sessao.getExpiraEm(),
                sessao.getRevogadoEm(),
                sessao.getCriadoEm(),
                sessao.getUltimoUsoEm()
        );
    }
}
