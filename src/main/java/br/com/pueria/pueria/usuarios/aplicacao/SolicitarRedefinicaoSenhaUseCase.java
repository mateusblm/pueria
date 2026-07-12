package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.usuarios.dominio.TokenRedefinicaoSenha;
import br.com.pueria.pueria.usuarios.dominio.TokenRedefinicaoSenhaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SolicitarRedefinicaoSenhaUseCase {
    private final UsuarioRepositorio usuarios;
    private final TokenRedefinicaoSenhaRepositorio tokens;
    private final GeradorTokenRedefinicaoSenha gerador;
    private final NotificadorRedefinicaoSenha notificador;
    private final int expiracaoMinutos;

    public SolicitarRedefinicaoSenhaUseCase(UsuarioRepositorio usuarios, TokenRedefinicaoSenhaRepositorio tokens,
            GeradorTokenRedefinicaoSenha gerador, NotificadorRedefinicaoSenha notificador,
            @Value("${pueria.redefinicao-senha.expiracao-minutos:15}") int expiracaoMinutos) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.gerador = gerador;
        this.notificador = notificador;
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public void executar(String email) {
        usuarios.buscarPorEmail(email).ifPresent(usuario -> {
            LocalDateTime agora = LocalDateTime.now();
            if (tokens.existeSolicitacaoDesde(usuario.getId(), agora.minusMinutes(1))) {
                return;
            }
            tokens.invalidarAtivosDoUsuario(usuario.getId());
            String tokenPuro = gerador.gerar();
            tokens.salvar(TokenRedefinicaoSenha.criar(usuario.getId(), gerador.calcularHash(tokenPuro), agora.plusMinutes(expiracaoMinutos)));
            notificador.enviar(usuario, tokenPuro);
        });
    }
}
