package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.comum.excecao.CredenciaisInvalidasException;
import br.com.pueria.pueria.usuarios.dominio.Usuario;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CriptografiaSenha criptografiaSenha;
    private final GeradorToken geradorToken;

    public AutenticarUsuarioUseCase(
            UsuarioRepositorio usuarioRepositorio,
            CriptografiaSenha criptografiaSenha,
            GeradorToken geradorToken
    ) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.criptografiaSenha = criptografiaSenha;
        this.geradorToken = geradorToken;
    }

    @Transactional(readOnly = true)
    public TokenAutenticacao executar(LoginComando comando) {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(comando.email())
                .filter(Usuario::isAtivo)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!criptografiaSenha.corresponde(comando.senha(), usuario.getSenhaCriptografada())) {
            throw new CredenciaisInvalidasException();
        }

        return TokenAutenticacao.bearer(geradorToken.gerar(usuario), geradorToken.expiracaoEmSegundos());
    }
}
