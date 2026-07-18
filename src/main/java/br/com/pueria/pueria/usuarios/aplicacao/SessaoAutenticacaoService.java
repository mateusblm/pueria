package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.comum.excecao.CredenciaisInvalidasException;
import br.com.pueria.pueria.usuarios.dominio.SessaoAutenticacao;
import br.com.pueria.pueria.usuarios.dominio.SessaoAutenticacaoRepositorio;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import br.com.pueria.pueria.usuarios.infraestrutura.seguranca.GeradorTokenSessaoSeguro;
import br.com.pueria.pueria.usuarios.infraestrutura.seguranca.TokenJwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SessaoAutenticacaoService {
    private final SessaoAutenticacaoRepositorio sessoes;
    private final UsuarioRepositorio usuarios;
    private final GeradorTokenSessaoSeguro gerador;
    private final TokenJwtService tokenJwtService;
    private final long refreshExpiracaoDias;

    public SessaoAutenticacaoService(SessaoAutenticacaoRepositorio sessoes, UsuarioRepositorio usuarios,
            GeradorTokenSessaoSeguro gerador, TokenJwtService tokenJwtService,
            @Value("${seguranca.refresh-token.expiracao-dias:30}") long refreshExpiracaoDias) {
        this.sessoes = sessoes; this.usuarios = usuarios; this.gerador = gerador; this.tokenJwtService = tokenJwtService; this.refreshExpiracaoDias = refreshExpiracaoDias;
    }

    @Transactional
    public CredenciaisSessao criar(Usuario usuario) {
        String refreshToken = gerador.gerar();
        sessoes.salvar(SessaoAutenticacao.criar(usuario.getId(), gerador.calcularHash(refreshToken), LocalDateTime.now().plusDays(refreshExpiracaoDias)));
        return new CredenciaisSessao(TokenAutenticacao.bearer(tokenJwtService.gerar(usuario), tokenJwtService.expiracaoEmSegundos()), refreshToken);
    }

    @Transactional
    public CredenciaisSessao renovar(String refreshToken) {
        SessaoAutenticacao sessao = sessoes.buscarPorHash(gerador.calcularHash(refreshToken))
                .orElseThrow(CredenciaisInvalidasException::new);
        if (!sessao.estaAtivaEm(LocalDateTime.now())) {
            sessoes.revogarSessoesAtivasDoUsuario(sessao.usuarioId());
            throw new CredenciaisInvalidasException();
        }
        Usuario usuario = usuarios.buscarPorId(sessao.usuarioId()).filter(Usuario::isAtivo).orElseThrow(CredenciaisInvalidasException::new);
        sessoes.salvar(sessao.revogar());
        return criar(usuario);
    }

    @Transactional
    public void revogar(String refreshToken) {
        sessoes.buscarPorHash(gerador.calcularHash(refreshToken)).filter(sessao -> sessao.estaAtivaEm(LocalDateTime.now())).ifPresent(sessao -> sessoes.salvar(sessao.revogar()));
    }

    public long refreshExpiracaoSegundos() { return refreshExpiracaoDias * 24 * 60 * 60; }

    public record CredenciaisSessao(TokenAutenticacao accessToken, String refreshToken) { }
}
