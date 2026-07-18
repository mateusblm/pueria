package br.com.pueria.pueria.usuarios.aplicacao;

import br.com.pueria.pueria.comum.excecao.RegraDominioException;
import br.com.pueria.pueria.usuarios.dominio.TokenRedefinicaoSenhaRepositorio;
import br.com.pueria.pueria.usuarios.dominio.SessaoAutenticacaoRepositorio;
import br.com.pueria.pueria.usuarios.dominio.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RedefinirSenhaUseCase {
    private final UsuarioRepositorio usuarios;
    private final TokenRedefinicaoSenhaRepositorio tokens;
    private final GeradorTokenRedefinicaoSenha gerador;
    private final CriptografiaSenha criptografia;
    private final SessaoAutenticacaoRepositorio sessoes;

    public RedefinirSenhaUseCase(UsuarioRepositorio usuarios, TokenRedefinicaoSenhaRepositorio tokens,
            GeradorTokenRedefinicaoSenha gerador, CriptografiaSenha criptografia, SessaoAutenticacaoRepositorio sessoes) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.gerador = gerador;
        this.criptografia = criptografia;
        this.sessoes = sessoes;
    }

    public void executar(String tokenPuro, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 8 || novaSenha.length() > 72) {
            throw new RegraDominioException("A senha deve possuir entre 8 e 72 caracteres");
        }
        var token = tokens.buscarPorHash(gerador.calcularHash(tokenPuro))
                .filter(item -> item.estaValidoEm(LocalDateTime.now()))
                .orElseThrow(() -> new RegraDominioException("Este link de redefinição é inválido ou expirou"));
        var usuario = usuarios.buscarPorId(token.usuarioId())
                .orElseThrow(() -> new RegraDominioException("Este link de redefinição é inválido ou expirou"));
        usuarios.salvar(usuario.comSenhaCriptografada(criptografia.criptografar(novaSenha)));
        tokens.salvar(token.marcarComoUsado());
        tokens.invalidarAtivosDoUsuario(usuario.getId());
        sessoes.revogarSessoesAtivasDoUsuario(usuario.getId());
    }
}
